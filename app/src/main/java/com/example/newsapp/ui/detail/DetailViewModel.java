package com.example.newsapp.ui.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.newsapp.data.model.Article;
import com.example.newsapp.data.repository.NewsRepository;

public class DetailViewModel extends ViewModel {
    private NewsRepository newsRepository;
    private MutableLiveData<Boolean> bookmarkActionResult = new MutableLiveData<>();

    public DetailViewModel() {
        newsRepository = NewsRepository.getInstance();
    }

    // LiveData để theo dõi trạng thái đã lưu hay chưa
    public LiveData<Boolean> isBookmarked(String articleUrl) {
        return newsRepository.isBookmarked(articleUrl);
    }

    // LiveData để thông báo kết quả của hành động lưu/xóa
    public LiveData<Boolean> getBookmarkActionResult() {
        return bookmarkActionResult;
    }

    // Logic toggleBookmark được đơn giản hóa
// Phương thức này phải dứt khoát, không có bất kỳ listener nào bên trong
    public void toggleBookmark(Article article, boolean isCurrentlyBookmarked) {
        if (article == null || article.getUrl() == null) return;

        if (isCurrentlyBookmarked) {
            // Nếu đang được lưu -> thì gọi lệnh XÓA
            newsRepository.removeBookmark(article.getUrl(), bookmarkActionResult);
        } else {
            // Nếu chưa được lưu -> thì gọi lệnh THÊM
            newsRepository.addBookmark(article, bookmarkActionResult);
        }
    }
}