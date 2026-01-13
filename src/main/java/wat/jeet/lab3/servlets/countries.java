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
import java.io.IOException;
import java.io.PrintWriter;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;

@WebServlet(name = "countries", urlPatterns = {"/countries"})
public class countries extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Parametreyi al (index.jsp'deki input name="name" ile aynı olmalı)
        String countryName = request.getParameter("name");
        if (countryName == null || countryName.isEmpty()) {
            countryName = "Poland"; // Varsayılan
        }

        String apiEndpointUrl = "https://restcountries.com/v3.1";
        String apiResource = "name/" + countryName;

        try {
            Client client = createJerseyClient();
            WebTarget target = client.target(apiEndpointUrl).path(apiResource);

            // 2. API'den veriyi çek
            String jsonResponse = target.request(MediaType.APPLICATION_JSON).get(String.class);

            // 3. JSON -> List<Map> dönüşümü (Jackson)
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> countriesList = mapper.readValue(jsonResponse,
                    new TypeReference<List<Map<String, Object>>>() {
            });

            // 4. Veriyi Request'e ekle ve JSP'ye gönder
            request.setAttribute("countriesList", countriesList);
            request.getRequestDispatcher("index.jsp").forward(request, response);

        } catch (Exception e) {
            // Hata durumunda boş liste gönder veya hata mesajı set et
            request.setAttribute("error", "Country not found: " + countryName);
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

    // Kılavuzdaki SSL Bypass Metodu (Hatanın çözümü burasıdır)
    private Client createJerseyClient() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new java.security.SecureRandom());

            return ClientBuilder.newBuilder()
                    .sslContext(sslContext)
                    .hostnameVerifier((hostname, session) -> true)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Jersey Client oluşturulamadı", e);
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
