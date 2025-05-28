package com.fag.doo_noticias_api.dto;

import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import com.fag.doo_noticias_api.models.News;

public class NewsParser {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public List<News> parseNewsList(String jsonResponse) {
        if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try (StringReader reader = new StringReader(jsonResponse);
                JsonReader jsonReader = Json.createReader(reader)) {
            JsonObject jsonObject = jsonReader.readObject();
            List<News> newsList = new ArrayList<>();

            JsonArray newsItems = jsonObject.getJsonArray("items");
            newsItems.forEach(jsonValue -> {
                if (jsonValue.getValueType() == JsonValue.ValueType.OBJECT) {
                    JsonObject newsData = (JsonObject) jsonValue;

                    if (newsData != null) {
                        News news = parseNews(newsData);
                        newsList.add(news);
                    }

                }
            });

            return newsList;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

    }

    private News parseNews(JsonObject newsData) {
        News news = new News();
        news.setId(getIntValue(newsData, "id"));
        news.setTitle(getStringValue(newsData, "titulo"));
        news.setIntroduction(getStringValue(newsData, "introducao"));

        String publicationDateStr = getStringValue(newsData, "data_publicacao");
        if (publicationDateStr != null) {
            news.setPublicationDate(java.sql.Date.valueOf(LocalDate.parse(publicationDateStr, DATE_FORMATTER)));
        }

        news.setFullNewsLink(getStringValue(newsData, "link"));
        news.setType(getStringValue(newsData, "tipo"));
        news.setNewsEditorial(getStringValue(newsData, "editorias"));

        return news;

    }

    private Integer getIntValue(JsonObject jsonObject, String key) {
        if (jsonObject.containsKey(key) && !jsonObject.isNull(key)) {
            JsonValue value = jsonObject.get(key);
            if (value != null && value.getValueType() != JsonValue.ValueType.NULL) {
                return jsonObject.getInt(key);
            }
        }
        return null;
    }

    private String getStringValue(JsonObject jsonObject, String key) {
        if (jsonObject.containsKey(key)) {
            JsonValue value = jsonObject.get(key);
            if (value != null && value.getValueType() != JsonValue.ValueType.NULL) {
                return jsonObject.getString(key);
            }
        }
        return null;
    }
}
