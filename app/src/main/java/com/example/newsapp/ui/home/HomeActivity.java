// File: ui/home/HomeActivity.java
package com.example.newsapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newsapp.R;
import com.example.newsapp.data.model.Article;
import com.example.newsapp.data.model.NewsResponse;
import com.example.newsapp.ui.detail.DetailActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

// Lời hứa được ký ở đây
public class HomeActivity extends AppCompatActivity implements NewsAdapter.OnArticleClickListener {

    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // << 1. TÌM TOOLBAR VÀ ĐĂNG KÝ NÓ >>
        Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar); // Dòng này báo cho Activity biết đây là thanh hành động chính

        // 2. Khởi tạo ViewModel một cách chính xác
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // 3. Ánh xạ các view từ layout
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recycler_view_news);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recycler_view_news);

        setupRecyclerView();
        observeViewModel();
    }

    private void setupRecyclerView() {
        newsAdapter = new NewsAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(newsAdapter);
        newsAdapter.setOnArticleClickListener(this); // Đăng ký listener
    }

    private void observeViewModel() {
        progressBar.setVisibility(View.VISIBLE);
        homeViewModel.getNewsResponseLiveData().observe(this, newsResponse -> { // Dùng lambda cho gọn
            progressBar.setVisibility(View.GONE);
            if (newsResponse != null && newsResponse.getArticles() != null) {
                newsAdapter.setArticles(newsResponse.getArticles());
            } else {
                // Xử lý lỗi
            }
        });
    }

    // << LỜI HỨA ĐƯỢC THỰC HIỆN Ở ĐÂY >>
    // Phương thức này giờ đã nằm trực tiếp trong HomeActivity
    @Override
    public void onArticleClick(Article article) {
        Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
        // Đính kèm 2 "gói hàng"
        intent.putExtra("ARTICLE_URL", article.getUrl());
        intent.putExtra("ARTICLE_TITLE", article.getTitle()); // << Gửi thêm tiêu đề
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);

        // << 2. Lấy SearchView và cấu hình nó >>
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint("Nhập từ khóa...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // Được gọi khi người dùng nhấn nút tìm kiếm trên bàn phím
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.isEmpty()) {
                    homeViewModel.searchNews(query);
                    searchView.clearFocus(); // Ẩn bàn phím
                }
                return true;
            }

            // Được gọi mỗi khi người dùng thay đổi văn bản
            @Override
            public boolean onQueryTextChange(String newText) {
                return false; // Chúng ta không làm gì ở đây để tránh gọi API liên tục
            }
        });

        // << 3. Xử lý khi người dùng đóng ô tìm kiếm >>
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true; // Cho phép mở
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Khi ô tìm kiếm được đóng, tải lại tin tức hàng đầu
                homeViewModel.fetchTopHeadlines();
                return true; // Cho phép đóng
            }
        });

        return true;
    }



}