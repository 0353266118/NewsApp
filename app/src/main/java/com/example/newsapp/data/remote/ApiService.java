// File: data/remote/ApiService.java
package com.example.newsapp.data.remote;

import com.example.newsapp.data.model.NewsResponse; // Import khuôn model
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// bản thiết kế lệnh gọi api
public interface ApiService {

    @GET("v2/top-headlines")
    Call<NewsResponse> getTopHeadlines(
            @Query("country") String country,
            @Query("category") String category, // << THÊM THAM SỐ NÀY VÀO
            @Query("apiKey") String apiKey
    );

    // Phương thức searchForNews vẫn giữ nguyên
    @GET("v2/everything")
    Call<NewsResponse> searchForNews(
            @Query("q") String keyword,
            @Query("apiKey") String apiKey
    );
}