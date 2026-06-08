// File: data/repository/NewsRepository.java
package com.example.newsapp.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.newsapp.data.model.NewsResponse;
import com.example.newsapp.data.remote.ApiService;
import com.example.newsapp.data.remote.RetrofitClient;
import com.example.newsapp.utils.Constants;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsRepository {

    private static final String TAG = "NewsRepository";
    private static NewsRepository instance;
    private ApiService apiService;

    // Hàm khởi tạo là private để ngăn tạo đối tượng từ bên ngoài
    private NewsRepository() {
        // Lấy ApiService từ RetrofitClient
        apiService = RetrofitClient.getApiService();
    }

    // Phương thức để lấy thể hiện duy nhất của Repository
    public static synchronized NewsRepository getInstance() {
        if (instance == null) {
            instance = new NewsRepository();
        }
        return instance;
    }
    // Phương thức lấy tin hàng đầu/theo thể loại
    public LiveData<NewsResponse> getTopHeadlines(String country, String category) {
        final MutableLiveData<NewsResponse> data = new MutableLiveData<>();

        apiService.getTopHeadlines(country, category, Constants.API_KEY)
                .enqueue(new Callback<NewsResponse>() {
                    @Override
                    public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                        if (response.isSuccessful()) {
                            data.setValue(response.body());
                        } else {
                            data.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<NewsResponse> call, Throwable t) {
                        data.setValue(null);
                        Log.e(TAG, "onFailure: getTopHeadlines failed", t);
                    }
                });

        return data;
    }


    public LiveData<NewsResponse> searchNews(String keyword) {
        final MutableLiveData<NewsResponse> data = new MutableLiveData<>();
        apiService.searchForNews(keyword, Constants.API_KEY)
                .enqueue(new Callback<NewsResponse>() {
                    @Override
                    public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                        if (response.isSuccessful()) {
                            data.setValue(response.body());
                        } else {
                            data.setValue(null);
                        }
                    }
                    @Override
                    public void onFailure(Call<NewsResponse> call, Throwable t) {
                        data.setValue(null);
                        Log.e(TAG, "onFailure: searchNews failed", t);
                    }
                });
        return data;
    }
    // << THÊM PHƯƠNG THỨC MỚI >>

    public List<String> getNewsCategories() {
        // << SỬ DỤNG LẠI DANH SÁCH ĐẦY ĐỦ >>
        return Arrays.asList(
                "All", "Business", "Entertainment", "General",
                "Health", "Science", "Sports", "Technology"
        );
    }
}