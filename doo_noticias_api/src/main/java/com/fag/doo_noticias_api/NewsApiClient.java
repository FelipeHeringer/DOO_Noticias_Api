package com.fag.doo_noticias_api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fag.doo_noticias_api.dto.NewsParser;
import com.fag.doo_noticias_api.models.News;

public class NewsApiClient {
    private static final String baseUrl = "http://servicodados.ibge.gov.br/api/v3/noticias/";
    private HttpClient httpClient;
    private NewsParser newsParser;

    public NewsApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.newsParser = new NewsParser();
    }

    public List<News> searchNewsByTitleOrKeyWord(String searchTerm) {
        String validSearchTerm = validateSearchTerm(searchTerm);

        try{
            String searchUrl = baseUrl + "?busca=" + validSearchTerm + "&qtd=3";
            String jsonResponse = makeRequest(searchUrl);

            return newsParser.parseNewsList(jsonResponse);
        }catch (Exception e){
            throw new RuntimeException("Falha na busca por séries");
        }

    }

    public List<News> searchNewsByDate(String date){
        String validDateFormat = validDateFormat(date);

        try {
            String searchUrl = baseUrl + "?ate=" + validDateFormat + "&qtd=3";
            String jsonResponse = makeRequest(searchUrl);

            return newsParser.parseNewsList(jsonResponse);
        } catch (Exception e) {
            throw new RuntimeException("Falha na busca por séries");
        }

    }

    private String makeRequest(String endpoint) throws Exception{
        URI endpointUrl = new URI(endpoint);
        HttpRequest request = HttpRequest.newBuilder(endpointUrl)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Failed to fetch data from API. Status code: " + response.statusCode());
        }

        return response.body();
    }

    private String validDateFormat(String date) {
        if(date == null || date.trim().isEmpty()){
            throw new IllegalArgumentException("O campo da data não pode estar nulo ou vazio");
        }

        String[] dateParts = date.split("/");
        if(dateParts.length != 3){
            throw new IllegalArgumentException("A data deve estar no formato dd/MM/yyyy");
        }

        return dateParts[1] + "-" + dateParts[0] + "-" + dateParts[2];
    }

    private String validateSearchTerm(String searchTerm) {
        String searchTermFormatted = searchTerm.replaceAll("\\s", "+");

        if(searchTerm == null || searchTerm.trim().isEmpty()){
            throw new IllegalArgumentException("O campo da busca não pode estar nulo ou vazio");
        }

        return searchTermFormatted;
    }

}
