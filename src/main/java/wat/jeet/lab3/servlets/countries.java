package wat.jeet.lab3.servlets;
 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
 
@WebServlet(name = "countries", urlPatterns = {"/countries"})
public class countries extends HttpServlet {
 
    // Part 4.1: Arama geçmişini tutan statik liste
    private static final List<String> searchHistory = new CopyOnWriteArrayList<>();
 
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
 
        // Parametreyi al ve varsayılan değer belirle
        String countryName = request.getParameter("name");
        if (countryName == null || countryName.isEmpty()) {
            countryName = "Poland";
        }
 
        String apiEndpointUrl = "https://restcountries.com/v3.1";
        String apiResource = "name/" + countryName;
 
        try {
            // SSL Bypass özellikli istemciyi oluştur
            Client client = createJerseyClient();
            WebTarget target = client.target(apiEndpointUrl).path(apiResource);
 
            // API'den veriyi String olarak çek
            String jsonResponse = target.request(MediaType.APPLICATION_JSON).get(String.class);
 
            // JSON'ı Java Listesine dönüştür
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> countriesList = mapper.readValue(jsonResponse, 
                    new TypeReference<List<Map<String, Object>>>() {});
 
            // Arama geçmişini güncelle
            if (!searchHistory.contains(countryName)) {
                searchHistory.add(countryName);
            }
 
            // Verileri Request objesine ekle (Forward'dan önce!)
            request.setAttribute("countriesList", countriesList);
            request.setAttribute("history", searchHistory);
 
            // JSP sayfasına yönlendir
            request.getRequestDispatcher("index.jsp").forward(request, response);
 
        } catch (Exception e) {
            // Hata durumunda geçmişi koru ve kullanıcıyı uyar
            request.setAttribute("history", searchHistory);
            request.setAttribute("error", "Country not found: " + countryName);
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }
 
    private Client createJerseyClient() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                @Override public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                @Override public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                @Override public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
            }}, new java.security.SecureRandom());
 
            return ClientBuilder.newBuilder()
                    .sslContext(sslContext)
                    .hostnameVerifier((hostname, session) -> true)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Client Error", e);
        }
    }
 
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}