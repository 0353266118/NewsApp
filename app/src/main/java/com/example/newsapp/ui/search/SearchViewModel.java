// File: ui/search/SearchViewModel.java
package com.example.newsapp.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.newsapp.data.model.Article;
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
        if (keyword == null || keyword.isEmpty()) {
            return;
        }
        isLoading.postValue(true);
        newsRepository.searchNews(keyword).observeForever(newsResponse -> {
            if (newsResponse != null) {
                searchResultsLiveData.postValue(newsResponse.getArticles());
            }
            isLoading.postValue(false);
        });
    }
}