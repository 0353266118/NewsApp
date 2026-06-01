// File: data/repository/NewsRepository.java
package com.example.newsapp.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.newsapp.data.model.NewsResponse;
import com.example.newsapp.data.remote.ApiService;
import com.example.newsapp.data.remote.RetrofitClient;
import com.example.newsapp.utils.Constants;

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

    // Phương thức chính để ViewModel gọi và lấy dữ liệu
    public LiveData<NewsResponse> getTopHeadlines(String country) {
        // 1. Tạo một "hộp chứa" dữ liệu có thể thay đổi
        final MutableLiveData<NewsResponse> data = new MutableLiveData<>();

        // 2. Gọi API thông qua ApiService
        apiService.getTopHeadlines(country, Constants.API_KEY)
                .enqueue(new Callback<NewsResponse>() {
                    @Override
                    public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                        // 3. Nếu gọi thành công
                        if (response.isSuccessful()) {
                            // Bỏ dữ liệu nhận được vào "hộp chứa"
                            data.setValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<NewsResponse> call, Throwable t) {
                        // 4. Nếu gọi thất bại
                        // Có thể set data là null hoặc một đối tượng lỗi tùy chỉnh
                        data.setValue(null);
                        Log.e(TAG, "onFailure: " + t.getMessage());
                    }
                });

        // 5. Trả về "hộp chứa" ngay lập tức
        return data;
    }
    // << THÊM PHƯƠNG THỨC MỚI >>
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
                        Log.e(TAG, "onFailure: Search failed", t);
                    }
                });
        return data;
    }

}