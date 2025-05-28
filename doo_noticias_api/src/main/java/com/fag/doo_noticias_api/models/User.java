package com.fag.doo_noticias_api.models;

import java.util.*;

public class User {
    private String name;
    private String cpf;
    private List<News> favoriteNews;
    private List<News> newsToReadLater;
    private List<News> newsRead;

    public User(String name, String cpf){
        if(name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("O Nome não pode estar vazio");
        }

        if(cpf == null || !cpf.matches("\\d{3}\\.\\\\d{3}\\.\\d{3}-\\d{2}")){
            throw new IllegalArgumentException("O cpf deve estar de acordo com o formato XXX.XXX.XXX-XX");
        }

        this.name = name;
        this.cpf = cpf;
        this.favoriteNews = new ArrayList<>();
        this.newsToReadLater = new ArrayList<>();
        this.newsRead = new ArrayList<>();
    }

    public Map<String,Object> toJson(){
        Map<String,Object> userJson = new HashMap<>();
        userJson.put("user_name",name);
        userJson.put("user_cpf",cpf);
        userJson.put("user_favorite_news",favoriteNews);
        userJson.put("user_new_read_later",newsToReadLater);
        userJson.put("user_readed_news",newsRead);
        
        return userJson;
    }
}
