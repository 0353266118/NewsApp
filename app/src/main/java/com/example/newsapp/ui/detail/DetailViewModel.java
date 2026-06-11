// File: ui/detail/DetailViewModel.java
package com.example.newsapp.ui.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import com.example.newsapp.data.model.Article;
import com.example.newsapp.data.repository.NewsRepository;

public class DetailViewModel extends ViewModel {
    private final NewsRepository newsRepository;
    private final MutableLiveData<Boolean> bookmarkActionResult = new MutableLiveData<>();

    public DetailViewModel() {
        newsRepository = NewsRepository.getInstance();
    }

    public LiveData<Boolean> checkBookmarkStatus(String articleUrl) {
        return newsRepository.isBookmarked(articleUrl);
    }

    public LiveData<Boolean> getBookmarkActionResult() {
        return bookmarkActionResult;
    }

    public void toggleBookmark(Article article) {
        if (article == null || article.getUrl() == null) {
            bookmarkActionResult.setValue(false);
            return;
        }
        final LiveData<Boolean> isBookmarkedOnce = newsRepository.isBookmarked(article.getUrl());
        final Observer<Boolean> singleTimeObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isCurrentlyBookmarked) {
                if (isCurrentlyBookmarked != null) {
                    if (isCurrentlyBookmarked) {
                        newsRepository.removeBookmark(article.getUrl(), bookmarkActionResult);
                    } else {
                        newsRepository.addBookmark(article, bookmarkActionResult);
                    }
                }
                isBookmarkedOnce.removeObserver(this);
            }
        };
        isBookmarkedOnce.observeForever(singleTimeObserver);
    }
}