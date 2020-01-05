package com.uom.happycelebrate.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Card {

    private String id;
    private String designer_id;
    private String image_url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesigner_id() {
        return designer_id;
    }

    public void setDesigner_id(String designer_id) {
        this.designer_id = designer_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVatagory() {
        return vatagory;
    }

    public void setVatagory(String vatagory) {
        this.vatagory = vatagory;
    }

    private String description;
    private String vatagory;


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("designer_id", designer_id);
        result.put("description", description);
        result.put("vatagory", vatagory);
        result.put("image_url",image_url);

        return result;
    }

    @Exclude
    public Card toCard(Map card) {

        Card c = new Card();
        c.setId(card.get("id").toString());
        c.setDesigner_id(card.get("designer_id").toString());
        c.setVatagory(card.get("vatagory").toString());
        c.setImage_url(card.get("image_url").toString());
        c.setDescription(card.get("description").toString());

        return c;
    }

}
