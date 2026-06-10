// File: ui/home/HomeViewModel.java
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
import androidx.lifecycle.Observer;
import java.util.ArrayList;

public class HomeViewModel extends ViewModel {

    private NewsRepository newsRepository;
    // << 1. KHAI BÁO CÁC BIẾN CÒN THIẾU Ở ĐÂY >>
    private AuthRepository authRepository;
    private LiveData<FirebaseUser> userLiveData;

    private MutableLiveData<List<Article>> trendingNewsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Article>> latestNewsLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<List<String>> categoriesLiveData = new MutableLiveData<>();

    public HomeViewModel() {
        newsRepository = NewsRepository.getInstance();
        authRepository = AuthRepository.getInstance(); // Dòng này giờ sẽ hết lỗi
        userLiveData = authRepository.getUserLiveData(); // Dòng này giờ sẽ hết lỗi
        fetchTopHeadlines();
        fetchCategories();
    }

    // --- Getters cho Activity quan sát ---
    public LiveData<List<Article>> getTrendingNewsLiveData() { return trendingNewsLiveData; }
    public LiveData<List<Article>> getLatestNewsLiveData() { return latestNewsLiveData; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<List<String>> getCategoriesLiveData() { return categoriesLiveData; }
    // Cung cấp LiveData người dùng cho Activity
    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    // --- Các phương thức hành động ---

    // Lấy tin tức chung (trang chủ)
    public void fetchTopHeadlines() {
        isLoading.setValue(true);
        LiveData<NewsResponse> response = newsRepository.getTopHeadlines("us", null);
        response.observeForever(new Observer<NewsResponse>() {
            @Override
            public void onChanged(NewsResponse newsResponse) {
                if (newsResponse != null && newsResponse.getArticles() != null && !newsResponse.getArticles().isEmpty()) {

                    // Lấy 5 bài báo đầu tiên cho Trending
                    int trendingCount = Math.min(5, newsResponse.getArticles().size());
                    trendingNewsLiveData.setValue(newsResponse.getArticles().subList(0, trendingCount));

                    // Lấy phần còn lại cho Latest
                    if (newsResponse.getArticles().size() > 5) {
                        latestNewsLiveData.setValue(newsResponse.getArticles().subList(5, newsResponse.getArticles().size()));
                    } else {
                        latestNewsLiveData.setValue(new ArrayList<>()); // Trả về danh sách rỗng nếu không còn
                    }

                } else {
                    trendingNewsLiveData.setValue(null);
                    latestNewsLiveData.setValue(null);
                }
                isLoading.setValue(false);
                response.removeObserver(this);
            }
        });
    }

    // Lấy tin tức theo thể loại
    public void fetchNewsByCategory(String category) {
        isLoading.postValue(true);
        // Khi xem theo thể loại, ẩn khu vực trending đi
        trendingNewsLiveData.postValue(null);

        newsRepository.getTopHeadlines("us", category).observeForever(newsResponse -> {
            if (newsResponse != null) {
                latestNewsLiveData.postValue(newsResponse.getArticles());
            } else {
                latestNewsLiveData.postValue(null);
            }
            isLoading.postValue(false);
        });
    }
    // Lấy danh sách các thể loại từ Repository
    public void fetchCategories() {
        // << THAY ĐỔI DANH SÁCH THỂ LOẠI >>
        List<String> categories = newsRepository.getNewsCategories();
        categoriesLiveData.setValue(categories);
    }

    // (Phương thức này sẽ dành cho SearchActivity)
    public void searchNews(String keyword) {
        // ...
    }
}