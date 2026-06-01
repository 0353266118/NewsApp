// File: data/remote/RetrofitClient.java
package com.example.newsapp.data.remote;

import com.example.newsapp.utils.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor; // Import mới
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofitInstance = null;

    public static Retrofit getClient() {
        if (retrofitInstance == null) {

            // --- BẮT ĐẦU PHẦN NÂNG CẤP ---

            // 1. Tạo Interceptor
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // BODY để xem chi tiết nhất

            // 2. Tạo OkHttpClient và gắn Interceptor vào
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            // --- KẾT THÚC PHẦN NÂNG CẤP ---

            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient) // 3. Bảo Retrofit dùng OkHttpClient đã được nâng cấp
                    .build();
        }
        return retrofitInstance;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}