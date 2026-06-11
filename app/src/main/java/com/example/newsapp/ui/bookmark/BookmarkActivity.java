package com.example.newsapp.ui.bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.R;
import com.example.newsapp.data.model.Article;
import com.example.newsapp.ui.detail.DetailActivity;
import com.example.newsapp.ui.home.HomeActivity;
import com.example.newsapp.ui.home.OnArticleClickListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;
import java.util.List;

public class BookmarkActivity extends AppCompatActivity implements OnArticleClickListener {

    private BookmarkViewModel bookmarkViewModel;
    private RecyclerView recyclerViewBookmarks;
    private BookmarkAdapter bookmarkAdapter;
    private BottomNavigationView bottomNavigationView;
    private ProgressBar progressBar;
    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        bookmarkViewModel = new ViewModelProvider(this).get(BookmarkViewModel.class);

        initViews();
        setupRecyclerView();
        setupBottomNav();
        observeViewModel();
    }

    private void initViews() {
        recyclerViewBookmarks = findViewById(R.id.recycler_view_bookmarks);
        bottomNavigationView = findViewById(R.id.bottom_navigation_bookmark);
        progressBar = findViewById(R.id.progress_bar_bookmark);
        tvMessage = findViewById(R.id.tv_bookmark_message);
    }

    private void setupRecyclerView() {
        bookmarkAdapter = new BookmarkAdapter(this);
        bookmarkAdapter.setOnArticleClickListener(this);
        recyclerViewBookmarks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBookmarks.setAdapter(bookmarkAdapter);
    }

    private void setupBottomNav() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_bookmark);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_bookmark) {
                return false;
            }

            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(BookmarkActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }

            Toast.makeText(this, "Chức năng này sẽ được phát triển sau", Toast.LENGTH_SHORT).show();
            return false;
        });
    }

    private void observeViewModel() {
        showLoadingState();

        bookmarkViewModel.getBookmarksLiveData().observe(this, bookmarks -> {
            progressBar.setVisibility(View.GONE);

            if (bookmarks == null) {
                Log.e("BookmarkActivity", "Lỗi khi lấy bookmarks từ Firestore.");
                showErrorState("Error loading bookmarks. Please check your connection or Firestore rules.");
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

    // THAY ĐỔI TẠI HÀM ONRESUME ĐỂ LÀM MỚI DANH SÁCH TỰ ĐỘNG
    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_bookmark);
        }

        // ÉP APP GỌI FIRESTORE LẤY DATA MỚI KHI QUAY LẠI TAB NÀY
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