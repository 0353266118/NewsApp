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
    private final ApiService apiService;
    private final FirebaseFirestore db;
    private final FirebaseAuth firebaseAuth;

    private NewsRepository() {
        apiService = RetrofitClient.getApiService();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public static synchronized NewsRepository getInstance() {
        if (instance == null) {
            instance = new NewsRepository();
        }
        return instance;
    }

    // --- Các phương thức lấy tin tức từ NewsAPI ---
    public LiveData<NewsResponse> getTopHeadlines(String country, String category) {
        final MutableLiveData<NewsResponse> data = new MutableLiveData<>();
        apiService.getTopHeadlines(country, category, Constants.API_KEY)
                .enqueue(new Callback<NewsResponse>() {
                    @Override
                    public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                        data.setValue(response.isSuccessful() ? response.body() : null);
                    }
                    @Override
                    public void onFailure(Call<NewsResponse> call, Throwable t) {
                        Log.e(TAG, "onFailure: getTopHeadlines failed", t);
                        data.setValue(null);
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
                        data.setValue(response.isSuccessful() ? response.body() : null);
                    }
                    @Override
                    public void onFailure(Call<NewsResponse> call, Throwable t) {
                        Log.e(TAG, "onFailure: searchNews failed", t);
                        data.setValue(null);
                    }
                });
        return data;
    }

    public List<String> getNewsCategories() {
        return Arrays.asList("All", "Business", "Entertainment", "General", "Health", "Science", "Sports", "Technology");
    }

    // --- Các phương thức cho Firebase Auth & Firestore ---
    private String getCurrentUserId() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    public void addBookmark(Article article, MutableLiveData<Boolean> success) {
        String userId = getCurrentUserId();
        if (userId == null || article == null || article.getUrl() == null) {
            success.postValue(false);
            return;
        }

        // THÊM DÒNG NÀY: Ghi nhận thời gian lưu bằng miligiây hệ thống
        article.setBookmarkedAt(System.currentTimeMillis());

        String documentId = String.valueOf(article.getUrl().hashCode());
        db.collection("users").document(userId)
                .collection("bookmarks").document(documentId)
                .set(article)
                .addOnSuccessListener(aVoid -> success.postValue(true))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding bookmark", e);
                    success.postValue(false);
                });
    }

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
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error removing bookmark", e);
                    success.postValue(false);
                });
    }

    // PHIÊN BẢN AN TOÀN: Dùng .get() để lấy dữ liệu một lần, không lắng nghe liên tục
    public LiveData<List<Article>> getBookmarks() {
        MutableLiveData<List<Article>> bookmarksLiveData = new MutableLiveData<>();
        String userId = getCurrentUserId();
        if (userId == null) {
            bookmarksLiveData.postValue(new ArrayList<>());
            return bookmarksLiveData;
        }
        db.collection("users").document(userId)
                .collection("bookmarks")
                .orderBy("bookmarkedAt", com.google.firebase.firestore.Query.Direction.DESCENDING) // << THÊM LỆNH XẾP GIẢM DẦN
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Article> bookmarks = new ArrayList<>();
                    if (queryDocumentSnapshots != null) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            bookmarks.add(doc.toObject(Article.class));
                        }
                    }
                    bookmarksLiveData.postValue(bookmarks);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting bookmarks", e);
                    bookmarksLiveData.postValue(null);
                });
        return bookmarksLiveData;
    }

    // PHIÊN BẢN AN TOÀN: Dùng .get() để lấy dữ liệu một lần, không lắng nghe liên tục
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
                .get() // << THAY ĐỔI QUAN TRỌNG
                .addOnSuccessListener(documentSnapshot -> {
                    isBookmarkedLiveData.postValue(documentSnapshot != null && documentSnapshot.exists());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking bookmark status", e);
                    isBookmarkedLiveData.postValue(false);
                });
        return isBookmarkedLiveData;
    }
}