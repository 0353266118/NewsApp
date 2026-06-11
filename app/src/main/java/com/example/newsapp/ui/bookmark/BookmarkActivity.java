package com.example.newsapp.ui.bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.R;
import com.example.newsapp.data.model.Article;
import com.example.newsapp.ui.detail.DetailActivity;
import com.example.newsapp.ui.home.OnArticleClickListener;

import java.io.Serializable;
import java.util.List;

public class BookmarkActivity extends AppCompatActivity implements OnArticleClickListener {

    private BookmarkViewModel bookmarkViewModel;
    private RecyclerView recyclerViewBookmarks;
    private BookmarkAdapter bookmarkAdapter;
    private ImageView btnBack; // Thay đổi từ BottomNav sang ImageView
    private ProgressBar progressBar;
    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        bookmarkViewModel = new ViewModelProvider(this).get(BookmarkViewModel.class);

        initViews();
        setupRecyclerView();
        setupNavigation(); // Thiết lập sự kiện nút Back thay cho BottomNav cũ
        observeViewModel();
    }

    private void initViews() {
        recyclerViewBookmarks = findViewById(R.id.recycler_view_bookmarks);
        btnBack = findViewById(R.id.btn_back_bookmark); // Ánh xạ nút Back
        progressBar = findViewById(R.id.progress_bar_bookmark);
        tvMessage = findViewById(R.id.tv_bookmark_message);
    }

    private void setupRecyclerView() {
        bookmarkAdapter = new BookmarkAdapter(this);
        bookmarkAdapter.setOnArticleClickListener(this);
        recyclerViewBookmarks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBookmarks.setAdapter(bookmarkAdapter);
    }

    private void setupNavigation() {
        // Khi bấm vào mũi tên, kết thúc Activity này để tự động quay lại HomeActivity dưới Stack
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0); // Giữ hiệu ứng chuyển mượt mà
        });
    }

    private void observeViewModel() {
        showLoadingState();

        bookmarkViewModel.getBookmarksLiveData().observe(this, bookmarks -> {
            progressBar.setVisibility(View.GONE);

            if (bookmarks == null) {
                Log.e("BookmarkActivity", "Lỗi khi lấy bookmarks từ Firestore.");
                showErrorState("Error loading bookmarks. Please check your connection.");
                return;
            }

            if (bookmarks.isEmpty()) {
                Log.d("BookmarkActivity", "Danh sách bookmark trống.");
                showEmptyState("Your bookmark list is empty.\nArticles you save will appear here.");
            } else {
                Log.d("BookmarkActivity", "Hiển thị " + bookmarks.size() + " bookmarks.");
                showContentState(bookmarks);
            }
        });
    }

    private void showLoadingState() {
        progressBar.setVisibility(View.VISIBLE);
        tvMessage.setVisibility(View.GONE);
        recyclerViewBookmarks.setVisibility(View.GONE);
    }

    private void showEmptyState(String message) {
        progressBar.setVisibility(View.GONE);
        recyclerViewBookmarks.setVisibility(View.GONE);
        tvMessage.setText(message);
        tvMessage.setVisibility(View.VISIBLE);
    }

    private void showErrorState(String message) {
        showEmptyState(message);
    }

    private void showContentState(List<Article> bookmarks) {
        progressBar.setVisibility(View.GONE);
        tvMessage.setVisibility(View.GONE);
        recyclerViewBookmarks.setVisibility(View.VISIBLE);
        bookmarkAdapter.setArticles(bookmarks);
    }

    @Override
    public void onArticleClick(Article article) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("ARTICLE_OBJECT", (Serializable) article);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Đã loại bỏ các lệnh setup vị trí nút BottomNav ở đây
        if (bookmarkViewModel != null) {
            showLoadingState();
            bookmarkViewModel.fetchBookmarks();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}