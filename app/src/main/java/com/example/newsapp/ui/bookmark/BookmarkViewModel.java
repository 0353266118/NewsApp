// File: ui/bookmark/BookmarkViewModel.java
package com.example.newsapp.ui.bookmark;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.newsapp.data.model.Article;
import com.example.newsapp.data.repository.NewsRepository;
import java.util.List;

public class BookmarkViewModel extends ViewModel {
    private final NewsRepository newsRepository;
    private final LiveData<List<Article>> bookmarksLiveData;

    public BookmarkViewModel() {
        // Lấy Repository
        newsRepository = NewsRepository.getInstance();
        // Ngay khi ViewModel được tạo, nó sẽ bắt đầu lấy và lắng nghe danh sách bookmark
        bookmarksLiveData = newsRepository.getBookmarks();
    }

    // Cung cấp LiveData cho Activity quan sát
    public LiveData<List<Article>> getBookmarksLiveData() {
        return bookmarksLiveData;
    }
}