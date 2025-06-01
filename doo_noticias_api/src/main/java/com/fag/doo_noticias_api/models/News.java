package com.fag.doo_noticias_api.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

public class News {
    private Integer id;
    private String title;
    private String introduction;
    private Date publicationDate;
    private String fullNewsLink;
    private String type;
    private String newsEditorial;

    public News() {

    }

    public News(Document doc) {
        this.id = doc.getInteger("news_id");
        this.title = doc.getString("news_title");
        this.introduction = doc.getString("news_introduction");

        String publicationDateString = doc.getString("news_publication_date");

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            this.publicationDate = dateFormat.parse(publicationDateString);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format", e);
        }

        this.fullNewsLink = doc.getString("news_full_link");
        this.type = doc.getString("news_type");
        this.newsEditorial = doc.getString("news_editorial");
    }

    public Map<String, Object> toJson() {
        Map<String, Object> newsJson = new HashMap<>();
        newsJson.put("news_id", id);
        newsJson.put("news_title", title);
        newsJson.put("news_introduction", introduction);
        try {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String publicationDateString = formatter.format((Date) publicationDate);
            newsJson.put("news_publication_date", publicationDateString);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        newsJson.put("news_full_link", fullNewsLink);
        newsJson.put("news_type", type);
        newsJson.put("news_editorial", newsEditorial);

        return newsJson;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getIntroduction() {
        return introduction;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public String getFullNewsLink() {
        return fullNewsLink;
    }

    public String getType() {
        return type;
    }

    public String getNewsEditorial() {
        return newsEditorial;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public void setFullNewsLink(String fullNewsLink) {
        this.fullNewsLink = fullNewsLink;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNewsEditorial(String newsEditorial) {
        this.newsEditorial = newsEditorial;
    }

}
