package com.fag.doo_noticias_api.models;

import java.util.*;

import org.bson.Document;

public class User {
    private String name;
    private String cpf;
    private List<News> favoriteNews;
    private List<News> newsToReadLater;
    private List<News> newsRead;

    public User(String name, String cpf) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("O Nome não pode estar vazio");
        }

        if (cpf == null || !cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            throw new IllegalArgumentException("O cpf deve estar de acordo com o formato XXX.XXX.XXX-XX");
        }

        this.name = name;
        this.cpf = cpf;
        this.favoriteNews = new ArrayList<>();
        this.newsToReadLater = new ArrayList<>();
        this.newsRead = new ArrayList<>();
    }

    public User(Document doc) {
        this.name = doc.getString("user_name");
        this.cpf = doc.getString("user_cpf");

        List<Document> favDocs = doc.getList("user_favorite_news", Document.class);
        this.favoriteNews = new ArrayList<>();
        if (favDocs != null) {
            for (Document d : favDocs) {
                this.favoriteNews.add(new News(d));
            }
        }

        List<Document> readLaterDocs = doc.getList("user_new_read_later", Document.class);
        this.newsToReadLater = new ArrayList<>();
        if (readLaterDocs != null) {
            for (Document d : readLaterDocs) {
                this.newsToReadLater.add(new News(d));
            }
        }

        List<Document> docsRead = doc.getList("user_news_read", Document.class);
        this.newsRead = new ArrayList<>();
        if (docsRead != null) {
            for (Document d : docsRead) {
                this.newsRead.add(new News(d));
            }
        }
    }

    public Map<String, Object> toJson() {
        Map<String, Object> userJson = new HashMap<>();
        userJson.put("user_name", name);
        userJson.put("user_cpf", cpf);
        userJson.put("user_favorite_news", favoriteNews);
        userJson.put("user_news_read_later", newsToReadLater);
        userJson.put("user_news_read", newsRead);

        return userJson;
    }

    public void addNewsOnfavoriteList(News news) {
        this.favoriteNews.add(news);
    }
    public void addNewsOnNewsToReadLaterList(News news) {
        this.newsToReadLater.add(news);
    }
    public void addNewsOnNewsRead(News news) {
        this.newsRead.add(news);
    }

    public void removeNewsFromFavoriteList(News news) {
        this.favoriteNews.remove(news);
    }

    public void removeNewsFromNewsToReadLaterList(News news) {
        this.newsToReadLater.remove(news);
    }

    public void removeNewsFromNewsReadList(News news) {
        this.newsRead.remove(news);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public List<News> getFavoriteNews() {
        return favoriteNews;
    }

    public List<News> getNewsToReadLater() {
        return newsToReadLater;
    }

    public List<News> getNewsRead() {
        return newsRead;
    }
}
