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
import com.example.newsapp.ui.home.OnArticleClickListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.io.Serializable;

public class BookmarkActivity extends AppCompatActivity implements OnArticleClickListener {

    private BookmarkViewModel bookmarkViewModel;
    private RecyclerView recyclerViewBookmarks;
    private BookmarkAdapter bookmarkAdapter; // Sử dụng adapter riêng
    private BottomNavigationView bottomNavigationView;

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
    }

    private void setupRecyclerView() {
        // Tạo và gán BookmarkAdapter riêng biệt
        bookmarkAdapter = new BookmarkAdapter(this);
        bookmarkAdapter.setOnArticleClickListener(this);
        recyclerViewBookmarks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBookmarks.setAdapter(bookmarkAdapter);
    }

    private void setupBottomNav() {
        // Đánh dấu icon Bookmark là đang được chọn khi mở màn hình
        bottomNavigationView.setSelectedItemId(R.id.navigation_bookmark);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Nếu bấm vào tab đang được chọn, không làm gì cả
            if (itemId == bottomNavigationView.getSelectedItemId()) {
                return false;
            }

            if (itemId == R.id.navigation_home) {
                // Quay về Home
                Intent intent = new Intent(BookmarkActivity.this, HomeActivity.class);
                // Các flag này để đảm bảo không tạo ra một HomeActivity mới nếu nó đã tồn tại
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                // Thêm overridePendingTransition để tắt hiệu ứng chuyển cảnh, tạo cảm giác như chuyển tab
                overridePendingTransition(0, 0);
                return true; // Trả về true để BottomNav cập nhật item được chọn
            }

            // Với các tab khác, tạm thời không làm gì
            Toast.makeText(this, "Chức năng này sẽ được phát triển sau", Toast.LENGTH_SHORT).show();
            return false; // Trả về false để không chọn các tab chưa có chức năng
        });
    }

    private void observeViewModel() {
        bookmarkViewModel.getBookmarksLiveData().observe(this, bookmarks -> {
            if (bookmarks != null) {
                Log.d("BookmarkActivity", "Số lượng bookmark nhận được: " + bookmarks.size());
                // Khi có danh sách bookmark mới từ Firestore, cập nhật lên BookmarkAdapter
                bookmarkAdapter.setArticles(bookmarks);
                // TODO: Thêm logic hiển thị thông báo "Danh sách trống" nếu bookmarks.isEmpty()
            } else {
                Log.e("BookmarkActivity", "Lỗi khi lấy bookmarks, danh sách là null.");
                // TODO: Thêm logic hiển thị thông báo lỗi
            }
        });
    }

    @Override
    public void onArticleClick(Article article) {
        Intent intent = new Intent(this, DetailActivity.class);
        // Gửi cả đối tượng Article đi, ép kiểu thành Serializable
        intent.putExtra("ARTICLE_OBJECT", (Serializable) article);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Tắt hiệu ứng chuyển cảnh khi thoát khỏi Activity này
        overridePendingTransition(0, 0);
    }
}