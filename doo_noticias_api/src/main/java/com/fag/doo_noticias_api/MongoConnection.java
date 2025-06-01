package com.fag.doo_noticias_api;

import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.fag.doo_noticias_api.models.News;
import com.fag.doo_noticias_api.models.User;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

public class MongoConnection {
    MongoClient mongoClient;
    MongoDatabase newsDb;
    MongoCollection<Document> newsCollection;

    public MongoConnection(){
        this.mongoClient = new MongoClient("localhost", 27017);
        this.newsDb = mongoClient.getDatabase("news_database");
        this.newsCollection = newsDb.getCollection("news_collection");
    }

    public void putUserOnMongo(Map<String, Object> userJson) {
        Document user_docs = new Document(userJson);
        newsCollection.insertOne(user_docs);
    }

    public User getUserFromMongoByCpf(String cpf) {
        Bson filter = Filters.eq("user_cpf", cpf);
        Document doc = newsCollection.find(filter).first();

        if (doc != null) {
            return new User(doc);
        }

        return null;
    }

    public void updateUserNameByCpf(String cpf, String newName) {
        Bson filter = Filters.eq("user_cpf", cpf);
        Bson update = Updates.set("user_name", newName);
        newsCollection.updateOne(filter, update);
    }

    public  void updateNewsReadList(String cpf, News news) {
        Bson filter = Filters.eq("user_cpf", cpf);
        Bson update = Updates.addToSet("user_news_read", news.toJson());
        newsCollection.updateOne(filter, update);
    }

    public void updateNewsToReadLaterList(String cpf, News news) {
        Bson filter = Filters.eq("user_cpf", cpf);
        Bson update = Updates.addToSet("user_new_read_later", news.toJson());
        newsCollection.updateOne(filter, update);
    }

    public void updateFavoriteNewsList(String cpf, News news) {
        Bson filter = Filters.eq("user_cpf", cpf);
        Bson update = Updates.addToSet("user_favorite_news", news.toJson());
        newsCollection.updateOne(filter, update);
    }

    public void removeNewsFromFavoriteList(String cpf, News news) {
        Bson filter = Filters.eq("user_cpf", cpf);
        Bson update = Updates.pull("user_favorite_news", news.toJson());
        newsCollection.updateOne(filter, update);
    }

    public void removeNewsFromReadLaterList(String cpf, News news) {
        Bson filter = Filters.eq("user_cpf", cpf);
        Bson update = Updates.pull("user_new_read_later", news.toJson());
        newsCollection.updateOne(filter, update);
    }

    public void removeNewsFromReadList(String cpf, News news) {
        Bson filter = Filters.eq("user_cpf", cpf);
        Bson update = Updates.pull("user_news_read", news.toJson());
        newsCollection.updateOne(filter, update);
    }
    
}
