// File: data/remote/ApiService.java
package com.example.newsapp.data.remote;

import com.example.newsapp.data.model.NewsResponse; // Import khuôn model
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// bản thiết kế lệnh gọi api
public interface ApiService {

    // GET https://newsapi.org/v2/top-headlines?country=us&apiKey=YOUR_API_KEY
    @GET("v2/top-headlines")
    Call<NewsResponse> getTopHeadlines(
            @Query("country") String country,
            @Query("apiKey") String apiKey
    );
    // << THÊM PHƯƠMGM THỨC MỚI ĐỂ TÌM KIẾM >>
    // GET https://newsapi.org/v2/everything?q=keyword&apiKey=YOUR_API_KEY
    @GET("v2/everything")
    Call<NewsResponse> searchForNews(
            @Query("q") String keyword, // "q" là tham số cho từ khóa tìm kiếm
            @Query("apiKey") String apiKey
    );
}