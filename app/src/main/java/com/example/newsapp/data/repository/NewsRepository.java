// File: data/repository/NewsRepository.java
package com.example.newsapp.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.newsapp.data.model.Article;
import com.example.newsapp.data.model.NewsResponse;
import com.example.newsapp.data.remote.ApiService;
import com.example.newsapp.data.remote.RetrofitClient;
import com.example.newsapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsRepository {

    private static final String TAG = "NewsRepository";
    private static NewsRepository instance;
    private ApiService apiService;
    private final FirebaseFirestore db;
    private final FirebaseAuth firebaseAuth;

    // Hàm khởi tạo là private để ngăn tạo đối tượng từ bên ngoài
    private NewsRepository() {
        // Lấy ApiService từ RetrofitClient
        apiService = RetrofitClient.getApiService();
        db = FirebaseFirestore.getInstance(); // Khởi tạo Firestore
        firebaseAuth = FirebaseAuth.getInstance();
    }

    // Phương thức để lấy thể hiện duy nhất của Repository
    public static synchronized NewsRepository getInstance() {
        if (instance == null) {
            instance = new NewsRepository();
        }
        return instance;
    }
    // Phương thức lấy tin hàng đầu/theo thể loại
    public LiveData<NewsResponse> getTopHeadlines(String country, String category) {
        final MutableLiveData<NewsResponse> data = new MutableLiveData<>();

        apiService.getTopHeadlines(country, category, Constants.API_KEY)
                .enqueue(new Callback<NewsResponse>() {
                    @Override
                    public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                        if (response.isSuccessful()) {
                            data.setValue(response.body());
                        } else {
                            data.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<NewsResponse> call, Throwable t) {
                        data.setValue(null);
                        Log.e(TAG, "onFailure: getTopHeadlines failed", t);
                    }
                });

        return data;
    }


    public LiveData<NewsResponse> searchNews(String keyword) {
        final MutableLiveData<NewsResponse> data = new MutableLiveData<>();
        apiService.searchForNews(keyword, Constants.API_KEY)
                .enqueue(new Callback<NewsResponse>() {
                    @Override
                    public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                        if (response.isSuccessful()) {
                            data.setValue(response.body());
                        } else {
                            data.setValue(null);
                        }
                    }
                    @Override
                    public void onFailure(Call<NewsResponse> call, Throwable t) {
                        data.setValue(null);
                        Log.e(TAG, "onFailure: searchNews failed", t);
                    }
                });
        return data;
    }
    // << THÊM PHƯƠNG THỨC MỚI >>

    public List<String> getNewsCategories() {
        // << SỬ DỤNG LẠI DANH SÁCH ĐẦY ĐỦ >>
        return Arrays.asList(
                "All", "Business", "Entertainment", "General",
                "Health", "Science", "Sports", "Technology"
        );
    }
    // --- CÁC PHƯƠNG THỨC MỚI CHO BOOKMARK ---

    // Lấy ID của người dùng hiện tại một cách an toàn
    private String getCurrentUserId() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    // Lưu một bài báo vào Firestore
    public void addBookmark(Article article, MutableLiveData<Boolean> success) {
        String userId = getCurrentUserId();
        if (userId == null || article == null || article.getUrl() == null) {
            success.postValue(false);
            return;
        }

        // Dùng URL làm ID của document để tránh trùng lặp
        String documentId = String.valueOf(article.getUrl().hashCode());

        db.collection("users").document(userId)
                .collection("bookmarks").document(documentId)
                .set(article) // Firestore tự động chuyển đổi object Article thành dữ liệu
                .addOnSuccessListener(aVoid -> success.postValue(true))
                .addOnFailureListener(e -> success.postValue(false));
    }

    // Xóa một bài báo khỏi Firestore
    public void removeBookmark(String articleUrl, MutableLiveData<Boolean> success) {
        String userId = getCurrentUserId();
        if (userId == null || articleUrl == null) {
            success.postValue(false);
            return;
        }
        String documentId = String.valueOf(articleUrl.hashCode());
        db.collection("users").document(userId)
                .collection("bookmarks").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> success.postValue(true))
                .addOnFailureListener(e -> success.postValue(false));
    }

    // Lấy toàn bộ danh sách bài báo đã lưu
    public LiveData<List<Article>> getBookmarks() {
        MutableLiveData<List<Article>> bookmarksLiveData = new MutableLiveData<>();
        String userId = getCurrentUserId();

        if (userId == null) {
            // Nếu chưa đăng nhập, trả về ngay một danh sách rỗng
            bookmarksLiveData.postValue(new ArrayList<>());
            return bookmarksLiveData;
        }

        // Lắng nghe sự thay đổi trên collection 'bookmarks'
        db.collection("users").document(userId)
                .collection("bookmarks")
                .addSnapshotListener((snapshots, error) -> {
                    // Xử lý lỗi kết nối
                    if (error != null) {
                        Log.e("NewsRepository", "Listen failed.", error);
                        bookmarksLiveData.postValue(null); // Gửi tín hiệu lỗi về ViewModel
                        return;
                    }

                    // Xử lý khi có dữ liệu mới
                    List<Article> bookmarks = new ArrayList<>();
                    if (snapshots != null && !snapshots.isEmpty()) {
                        for (QueryDocumentSnapshot doc : snapshots) {
                            // Chuyển đổi tài liệu Firestore thành đối tượng Article
                            Article article = doc.toObject(Article.class);
                            bookmarks.add(article);
                        }
                    }
                    // Cập nhật LiveData với danh sách mới (dù là rỗng hay có dữ liệu)
                    bookmarksLiveData.postValue(bookmarks);
                });

        return bookmarksLiveData;
    }

    // Kiểm tra xem một bài báo cụ thể đã được lưu chưa
    public LiveData<Boolean> isBookmarked(String articleUrl) {
        MutableLiveData<Boolean> isBookmarkedLiveData = new MutableLiveData<>();
        String userId = getCurrentUserId();
        if (userId == null || articleUrl == null) {
            isBookmarkedLiveData.postValue(false);
            return isBookmarkedLiveData;
        }
        String documentId = String.valueOf(articleUrl.hashCode());
        db.collection("users").document(userId)
                .collection("bookmarks").document(documentId)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        isBookmarkedLiveData.postValue(false);
                        return;
                    }
                    isBookmarkedLiveData.postValue(snapshot != null && snapshot.exists());
                });
        return isBookmarkedLiveData;
    }

}