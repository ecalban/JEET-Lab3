<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>CountryWeb 🗺️</title>
        <meta charset="UTF-8">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body class="bg-light">
        <div class="container mt-5">
            <h2 class="text-center mb-4">Get basic information about countries!</h2>

            <div class="row mb-4 justify-content-center">
                <div class="col-md-6 text-center">
                    <form action="countries" method="GET" class="d-flex shadow-sm p-3 bg-white rounded">
                        <label class="me-2 align-self-center">Enter Country Name:</label>
                        <input type="text" name="name" class="form-control me-2" placeholder="e.g. Turkey" required>
                        <button type="submit" class="btn btn-primary">Search</button>
                    </form>
                </div>
            </div>

            <div class="shadow p-3 mb-5 bg-body rounded">
                <table class="table table-sm table-striped table-bordered table-hover align-middle">
                    <thead class="table-dark text-center">
                        <tr>
                            <th>#</th>
                            <th>Country Name</th>
                            <th>Original Name</th>
                            <th>Capital</th>
                            <th>TLD</th>
                            <th>Flag</th>
                            <th>CoA</th>
                            <th>Map</th>
                        </tr>
                    </thead>
                    <tbody id="countryTableBody">
                        <c:forEach var="country" items="${countriesList}" varStatus="status">
                            <tr>
                                <td class="text-center">${status.count}</td>
                                <td><strong>${country.name.common}</strong></td>
                                <td>
                                    <%-- Native Name karmaşık bir yapıdadır, ilkini alıyoruz --%>
                                    <c:forEach var="nativeName" items="${country.name.nativeName}" begin="0" end="0">

                                        ${nativeName.value.common}
                                    </c:forEach>
                                </td>
                                <td>${country.capital[0]}</td>
                                <td class="text-center">${country.tld[0]}</td>
                                <td class="text-center">
                                    <img src="${country.flags.png}" width="50" class="border">
                                </td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${not empty country.coatOfArms.png}">
                                            <img src="${country.coatOfArms.png}" width="40">
                                        </c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-center">
                                    <a href="${country.maps.openStreetMaps}" target="_blank" class="btn btn-sm btn-outline-info">View Map</a>
                                </td>
                            </tr>
                        </c:forEach>
                        <%-- Liste boşsa (ilk açılış veya hata durumu) --%>
                        <c:if test="${empty countriesList}">
                            <tr>
                                <td colspan="8" class="text-center p-4 text-muted">

                                    Please search for a country to see results.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
            <strong>Recent Searches: </strong>
            <c:forEach var="prevSearch" items="${history}">
                <a href="countries?name=${prevSearch}"class="badge bg-secondary text-decoration-none me-1">
                    ${prevSearch}
                </a>
            </c:forEach>
        </div>
    </body>
</html>
