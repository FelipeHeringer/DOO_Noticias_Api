package com.fag.doo_noticias_api.models;

import java.util.*;

public class SearchHistory {
    private Map<String,Object> newsWanted;

    public SearchHistory (){
        this.newsWanted = new HashMap<>();
    }

    public void addSearchOnHistory(String name, List<News> newsFound){
        newsWanted.put(name,newsFound);
    }

    public News searchNewsById(int id){
        @SuppressWarnings("unchecked")
        News found = newsWanted.values().stream()
                .flatMap(obj -> ((List<News>)obj).stream())
                .filter(news -> news.getId() == id)
                .findFirst()
                .orElse(null);

        return found;
        
    }

    public Map<String, Object> getSeries_wanted() {
        return newsWanted;
    }
}
