package com.example.newsapp.ui.bookmark;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import com.example.newsapp.data.model.Article;
import com.example.newsapp.data.repository.NewsRepository;
import java.util.List;

public class BookmarkViewModel extends ViewModel {
    private final NewsRepository newsRepository;
    private final MutableLiveData<List<Article>> bookmarksLiveData = new MutableLiveData<>();

    public BookmarkViewModel() {
        newsRepository = NewsRepository.getInstance();
        // Tải dữ liệu lần đầu tiên khi khởi tạo ViewModel
        fetchBookmarks();
    }

    public LiveData<List<Article>> getBookmarksLiveData() {
        return bookmarksLiveData;
    }

    // HÀM LÀM MỚI DỮ LIỆU: Gọi lại lệnh .get() từ Repository
    public void fetchBookmarks() {
        LiveData<List<Article>> repoData = newsRepository.getBookmarks();

        repoData.observeForever(new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                bookmarksLiveData.setValue(articles);

                // BẮT BUỘC: Hủy lắng nghe ngay lập tức để giải phóng bộ nhớ và tránh vòng lặp
                repoData.removeObserver(this);
            }
        });
    }
}