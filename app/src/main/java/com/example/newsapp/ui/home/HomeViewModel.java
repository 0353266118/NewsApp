// File: ui/home/HomeViewModel.java
package com.example.newsapp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData; // << Import mới
import androidx.lifecycle.ViewModel;

import com.example.newsapp.data.model.Article;
import com.example.newsapp.data.model.NewsResponse;
import com.example.newsapp.data.repository.NewsRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private NewsRepository newsRepository;
    // << Chuyển thành MutableLiveData để có thể cập nhật từ nhiều nguồn >>
    private MutableLiveData<NewsResponse> newsResponseLiveData;
    private MutableLiveData<Article> trendingNewsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Article>> latestNewsLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    // ... (Hàm khởi tạo)

    public LiveData<Article> getTrendingNewsLiveData() { return trendingNewsLiveData; }
    public LiveData<List<Article>> getLatestNewsLiveData() { return latestNewsLiveData; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public HomeViewModel() {
        newsRepository = NewsRepository.getInstance();
        newsResponseLiveData = new MutableLiveData<>(); // Khởi tạo một lần
        fetchTopHeadlines(); // Tải tin tức mặc định khi ViewModel được tạo
    }

    // << Phương thức để Activity có thể quan sát >>
    public LiveData<NewsResponse> getNewsResponseLiveData() {
        return newsResponseLiveData;
    }

    public void fetchTopHeadlines() {
        // << Khi bắt đầu tải, thông báo "đang loading" >>
        isLoading.postValue(true);

        newsRepository.getTopHeadlines("us").observeForever(newsResponse -> {
            if (newsResponse != null && newsResponse.getArticles() != null && !newsResponse.getArticles().isEmpty()) {
                trendingNewsLiveData.postValue(newsResponse.getArticles().get(0));
                latestNewsLiveData.postValue(newsResponse.getArticles().subList(1, newsResponse.getArticles().size()));
            }
            // << Dù thành công hay thất bại, thông báo "hết loading" >>
            isLoading.postValue(false);
        });
    }

    public void searchNews(String keyword) {
        // << Tương tự, quản lý trạng thái loading cho việc tìm kiếm >>
        isLoading.postValue(true);
        newsRepository.searchNews(keyword).observeForever(newsResponse -> {
            if (newsResponse != null) {
                trendingNewsLiveData.postValue(null);
                latestNewsLiveData.postValue(newsResponse.getArticles());
            }
            isLoading.postValue(false);
        });
    }

}