// File: ui/bookmark/BookmarkActivity.java
package com.example.newsapp.ui.bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newsapp.R;
import com.example.newsapp.data.model.Article;
import com.example.newsapp.ui.detail.DetailActivity;
import com.example.newsapp.ui.home.HomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.io.Serializable;

// Implement interface của BookmarkAdapter
public class BookmarkActivity extends AppCompatActivity implements BookmarkAdapter.OnArticleClickListener {

    private BookmarkViewModel bookmarkViewModel;
    private RecyclerView recyclerViewBookmarks;
    private BookmarkAdapter bookmarkAdapter; // Sử dụng adapter mới
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        // 1. Khởi tạo ViewModel
        bookmarkViewModel = new ViewModelProvider(this).get(BookmarkViewModel.class);

        // 2. Ánh xạ Views
        initViews();

        // 3. Cấu hình các thành phần
        setupRecyclerView();
        setupBottomNav();

        // 4. Bắt đầu quan sát dữ liệu
        observeViewModel();
    }

    private void initViews() {
        recyclerViewBookmarks = findViewById(R.id.recycler_view_bookmarks);
        bottomNavigationView = findViewById(R.id.bottom_navigation_bookmark);
    }

    private void setupRecyclerView() {
        // Tạo và gán BookmarkAdapter mới
        bookmarkAdapter = new BookmarkAdapter(this);
        bookmarkAdapter.setOnArticleClickListener(this);
        recyclerViewBookmarks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBookmarks.setAdapter(bookmarkAdapter);
    }

    private void setupBottomNav() {
        // Đánh dấu icon Bookmark là đang được chọn
        bottomNavigationView.setSelectedItemId(R.id.navigation_bookmark);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_bookmark) {
                // Đang ở màn hình này rồi, không làm gì cả
                return true;
            } else if (itemId == R.id.navigation_home) {
                // Quay về Home
                Intent intent = new Intent(BookmarkActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                // Thêm overridePendingTransition để tắt hiệu ứng chuyển cảnh, tạo cảm giác như chuyển tab
                overridePendingTransition(0, 0);
                return true;
            }
            // Các tab khác có thể hiển thị Toast
            Toast.makeText(this, "Chức năng này sẽ được phát triển sau", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private void observeViewModel() {
        bookmarkViewModel.getBookmarksLiveData().observe(this, bookmarks -> {
            if (bookmarks != null) {
                Log.d("BookmarkActivity", "Số lượng bookmark nhận được: " + bookmarks.size());
                // Khi có danh sách bookmark mới từ Firestore, cập nhật lên BookmarkAdapter
                bookmarkAdapter.setArticles(bookmarks);
            } else {
                Log.e("BookmarkActivity", "Lỗi khi lấy bookmarks, danh sách là null.");
            }
        });
    }

    @Override
    public void onArticleClick(Article article) {
        Intent intent = new Intent(this, DetailActivity.class);
        // Đảm bảo Article đã implement Serializable
        intent.putExtra("ARTICLE_OBJECT", (Serializable) article);
        startActivity(intent);
    }
}