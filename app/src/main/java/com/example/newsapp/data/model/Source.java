// File: data/model/Source.java
package com.example.newsapp.data.model;

import com.google.gson.annotations.SerializedName;

// model của nguồn tin
public class Source {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
