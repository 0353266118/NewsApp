// File: ui/search/SearchViewModel.java
package com.example.newsapp.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.newsapp.data.model.Article;
import com.example.newsapp.data.model.NewsResponse; // << Cần import NewsResponse
import com.example.newsapp.data.repository.NewsRepository;
import java.util.List;

public class SearchViewModel extends ViewModel {

    private NewsRepository newsRepository;
    private MutableLiveData<List<Article>> searchResultsLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public SearchViewModel() {
        newsRepository = NewsRepository.getInstance();
    }

    public LiveData<List<Article>> getSearchResultsLiveData() {
        return searchResultsLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void searchNews(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            // Nếu từ khóa rỗng, không làm gì cả
            searchResultsLiveData.postValue(null); // Xóa kết quả cũ
            return;
        }
        isLoading.postValue(true);

        // Gọi đúng phương thức searchNews từ Repository
        // Nó trả về LiveData<NewsResponse>
        LiveData<NewsResponse> newsResponseLiveData = newsRepository.searchNews(keyword);

        // Quan sát kết quả từ Repository
        newsResponseLiveData.observeForever(newsResponse -> {
            if (newsResponse != null && newsResponse.getArticles() != null) {
                // Lấy danh sách Articles từ NewsResponse và cập nhật LiveData của ViewModel
                searchResultsLiveData.postValue(newsResponse.getArticles());
            } else {
                // Xử lý trường hợp lỗi hoặc không có kết quả
                searchResultsLiveData.postValue(null);
            }
            isLoading.postValue(false);
        });
    }
}