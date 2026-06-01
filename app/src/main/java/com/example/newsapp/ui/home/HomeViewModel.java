// File: ui/home/HomeViewModel.java
package com.example.newsapp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData; // << Import mới
import androidx.lifecycle.ViewModel;
import com.example.newsapp.data.model.NewsResponse;
import com.example.newsapp.data.repository.NewsRepository;

public class HomeViewModel extends ViewModel {

    private NewsRepository newsRepository;
    // << Chuyển thành MutableLiveData để có thể cập nhật từ nhiều nguồn >>
    private MutableLiveData<NewsResponse> newsResponseLiveData;

    public HomeViewModel() {
        newsRepository = NewsRepository.getInstance();
        newsResponseLiveData = new MutableLiveData<>(); // Khởi tạo một lần
        fetchTopHeadlines(); // Tải tin tức mặc định khi ViewModel được tạo
    }

    // << Phương thức để Activity có thể quan sát >>
    public LiveData<NewsResponse> getNewsResponseLiveData() {
        return newsResponseLiveData;
    }

    // << Phương thức để tải tin tức hàng đầu >>
    public void fetchTopHeadlines() {
        // Lấy LiveData từ Repository và gán vào LiveData của ViewModel
        // Chúng ta dùng postValue thay vì setValue vì không chắc chắn hàm này được gọi từ luồng nào
        newsRepository.getTopHeadlines("us").observeForever(newsResponse -> {
            newsResponseLiveData.postValue(newsResponse);
        });
    }

    // << Phương thức để tìm kiếm tin tức >>
    public void searchNews(String keyword) {
        newsRepository.searchNews(keyword).observeForever(newsResponse -> {
            newsResponseLiveData.postValue(newsResponse);
        });
    }
}