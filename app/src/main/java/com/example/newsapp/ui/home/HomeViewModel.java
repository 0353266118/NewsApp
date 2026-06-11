package com.example.newsapp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import com.example.newsapp.data.model.Article;
import com.example.newsapp.data.model.NewsResponse;
import com.example.newsapp.data.repository.AuthRepository;
import com.example.newsapp.data.repository.NewsRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final NewsRepository newsRepository;
    private final AuthRepository authRepository;

    private final MutableLiveData<List<Article>> trendingNewsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Article>> latestNewsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<List<String>> categoriesLiveData = new MutableLiveData<>();
    private final LiveData<FirebaseUser> userLiveData;

    // << ĐÃ XÓA BIẾN TOÀN CỤC currentNewsObserver TẠI ĐÂY >>

    public HomeViewModel() {
        newsRepository = NewsRepository.getInstance();
        authRepository = AuthRepository.getInstance();
        userLiveData = authRepository.getUserLiveData();
        fetchCategories();
        fetchTopHeadlines();
    }

    // --- Getters ---
    public LiveData<List<Article>> getTrendingNewsLiveData() { return trendingNewsLiveData; }
    public LiveData<List<Article>> getLatestNewsLiveData() { return latestNewsLiveData; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<List<String>> getCategoriesLiveData() { return categoriesLiveData; }
    public LiveData<FirebaseUser> getUserLiveData() { return userLiveData; }

    // --- Actions ---

    public void fetchTopHeadlines() {
        isLoading.setValue(true);
        LiveData<NewsResponse> response = newsRepository.searchNews("Việt Nam");

        // SỬA ĐỔI THÀNH ANONYMOUS CLASS ĐỂ CHỐNG LỖI CHỒNG LUỒNG MẠNG
        response.observeForever(new Observer<NewsResponse>() {
            @Override
            public void onChanged(NewsResponse newsResponse) {
                if (newsResponse != null && newsResponse.getArticles() != null && !newsResponse.getArticles().isEmpty()) {
                    int trendingCount = Math.min(5, newsResponse.getArticles().size());
                    trendingNewsLiveData.setValue(newsResponse.getArticles().subList(0, trendingCount));
                    if (newsResponse.getArticles().size() > 5) {
                        latestNewsLiveData.setValue(newsResponse.getArticles().subList(5, newsResponse.getArticles().size()));
                    } else {
                        latestNewsLiveData.setValue(new ArrayList<>());
                    }
                } else {
                    trendingNewsLiveData.setValue(null);
                    latestNewsLiveData.setValue(null);
                }
                isLoading.setValue(false);

                // Tự hủy chính xác instance này sau khi cập nhật UI xong
                response.removeObserver(this);
            }
        });
    }

    public void searchNews(String keyword) {
        isLoading.setValue(true);
        trendingNewsLiveData.setValue(null);
        LiveData<NewsResponse> response = newsRepository.searchNews(keyword);

        // SỬA ĐỔI THÀNH ANONYMOUS CLASS ĐỂ CHỐNG LỖI CHỒNG LUỒNG MẠNG
        response.observeForever(new Observer<NewsResponse>() {
            @Override
            public void onChanged(NewsResponse newsResponse) {
                if (newsResponse != null) {
                    latestNewsLiveData.setValue(newsResponse.getArticles());
                } else {
                    latestNewsLiveData.setValue(null);
                }
                isLoading.setValue(false);

                // Tự hủy chính xác instance này
                response.removeObserver(this);
            }
        });
    }

    public void fetchCategories() {
        categoriesLiveData.setValue(newsRepository.getNewsCategories());
    }
}