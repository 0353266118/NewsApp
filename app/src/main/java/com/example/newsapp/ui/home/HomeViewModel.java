// File: ui/home/HomeViewModel.java
package com.example.newsapp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.newsapp.data.model.Article;
import com.example.newsapp.data.repository.NewsRepository;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private NewsRepository newsRepository;
    private MutableLiveData<Article> trendingNewsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Article>> latestNewsLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<List<String>> categoriesLiveData = new MutableLiveData<>();

    public HomeViewModel() {
        newsRepository = NewsRepository.getInstance();
        fetchTopHeadlines();
        fetchCategories();
    }

    // --- Getters cho Activity quan sát ---
    public LiveData<Article> getTrendingNewsLiveData() { return trendingNewsLiveData; }
    public LiveData<List<Article>> getLatestNewsLiveData() { return latestNewsLiveData; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<List<String>> getCategoriesLiveData() { return categoriesLiveData; }

    // --- Các phương thức hành động ---

    // Lấy tin tức chung (trang chủ)
    public void fetchTopHeadlines() {
        isLoading.postValue(true);
        // Mặc định gọi tin tức chung (category là null)
        newsRepository.getTopHeadlines("us", null).observeForever(newsResponse -> {
            if (newsResponse != null && newsResponse.getArticles() != null && !newsResponse.getArticles().isEmpty()) {
                trendingNewsLiveData.postValue(newsResponse.getArticles().get(0));
                latestNewsLiveData.postValue(newsResponse.getArticles().subList(1, newsResponse.getArticles().size()));
            } else {
                trendingNewsLiveData.postValue(null);
                latestNewsLiveData.postValue(null);
            }
            isLoading.postValue(false);
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