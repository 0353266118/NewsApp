// File: ui/home/HomeActivity.java
package com.example.newsapp.ui.home;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newsapp.R;
import com.example.newsapp.data.model.Article;
import com.example.newsapp.data.repository.AuthRepository;
import com.example.newsapp.ui.auth.LoginActivity;
import com.example.newsapp.ui.bookmark.BookmarkActivity;
import com.example.newsapp.ui.detail.DetailActivity;
import com.example.newsapp.ui.search.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements NewsAdapter.OnArticleClickListener, CategoryAdapter.OnCategoryClickListener {

    private HomeViewModel homeViewModel;
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar;
    private CategoryAdapter categoryAdapter;

    // Các view trên màn hình
    private RecyclerView recyclerViewNews, recyclerViewCategories;
    private BottomNavigationView bottomNavigationView;
    private MaterialCardView searchBar, cardTrending;
    private ImageView ivTrendingImage;
    private TextView tvTrendingCategory, tvTrendingTitle;
    private LinearLayout layoutTrending;
    private ViewPager2 viewPagerTrending; // Thêm biến cho ViewPager2
    private TrendingAdapter trendingAdapter; // Thêm biến cho Adapter mới
    private AuthRepository authRepository;
    private TextView tvGreeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 1. Khởi tạo ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // 2. Ánh xạ tất cả các View
        initViews();

        // 3. Cấu hình các thành phần
        setupListeners();
        setupRecyclerViews();
        authRepository = AuthRepository.getInstance();
        setupBottomNav();

        // 4. Bắt đầu quan sát dữ liệu
        observeViewModel();
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        recyclerViewNews = findViewById(R.id.recycler_view_news);
        recyclerViewCategories = findViewById(R.id.recycler_view_categories);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchBar = findViewById(R.id.search_bar);

        // Views của khu vực Trending
        viewPagerTrending = findViewById(R.id.view_pager_trending); // Ánh xạ ViewPager2
        layoutTrending = findViewById(R.id.layout_trending);

        tvGreeting = findViewById(R.id.tv_greeting);
    }

    private void setupListeners() {
        // Sự kiện click cho thanh tìm kiếm
        searchBar.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);

            startActivity(intent);
        });

        // Sự kiện cho Bottom Navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                return true;
            } else if (itemId == R.id.navigation_explore || itemId == R.id.navigation_bookmark || itemId == R.id.navigation_profile) {
                Toast.makeText(this, "Chức năng này sẽ được phát triển sau", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerViews() {
        // Setup cho RecyclerView tin tức chính
        newsAdapter = new NewsAdapter(this);
        newsAdapter.setOnArticleClickListener(this);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNews.setNestedScrollingEnabled(false);
        recyclerViewNews.setAdapter(newsAdapter);

        // Setup cho RecyclerView thể loại
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // << 3. KHÔNG gán adapter ở đây nữa, vì chúng ta chưa có dữ liệu
        trendingAdapter = new TrendingAdapter(this); // `this` vì HomeActivity implement OnArticleClickListener
        viewPagerTrending.setAdapter(trendingAdapter);
    }

    private void observeViewModel() {
        // Observer cho trạng thái loading
        homeViewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading != null && isLoading ? View.VISIBLE : View.GONE);
        });

        // Observer cho tin tức Trending
        homeViewModel.getTrendingNewsLiveData().observe(this, articles -> {
            if (articles != null && !articles.isEmpty()) {
                layoutTrending.setVisibility(View.VISIBLE);
                trendingAdapter.setTrendingArticles(articles); // Đưa danh sách vào adapter mới
            } else {
                layoutTrending.setVisibility(View.GONE);
            }
        });

        // Observer cho danh sách tin tức Latest
        homeViewModel.getLatestNewsLiveData().observe(this, articles -> {
            if (articles != null) {
                newsAdapter.setArticles(articles);
            }
        });

        // Observer cho danh sách thể loại
        homeViewModel.getCategoriesLiveData().observe(this, categories -> {
            if (categories != null && !categories.isEmpty()) {
                // << 4. LOGIC MỚI: Chỉ tạo adapter nếu nó chưa tồn tại >>
                if (categoryAdapter == null) {
                    categoryAdapter = new CategoryAdapter(categories, this);
                    recyclerViewCategories.setAdapter(categoryAdapter);
                } else {
                    // Nếu adapter đã tồn tại, chỉ cần cập nhật dữ liệu (nếu cần)
                    // Hiện tại danh sách thể loại là cố định nên không cần làm gì
                }
            }
        });
        // << THÊM OBSERVER MỚI CHO TRẠNG THÁI NGƯỜI DÙNG >>
        homeViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                // Nếu có người dùng, hiển thị lời chào
                String displayName = firebaseUser.getDisplayName();
                String email = firebaseUser.getEmail();

                // Ưu tiên hiển thị tên, nếu không có thì hiển thị phần đầu của email
                String greetingName = (displayName != null && !displayName.isEmpty()) ? displayName : email.split("@")[0];

                tvGreeting.setText("Hello, " + greetingName + "!");
                tvGreeting.setVisibility(View.VISIBLE);
            } else {
                // Nếu không có người dùng, ẩn lời chào đi
                tvGreeting.setVisibility(View.GONE);
            }
        });

    }

    // Xử lý sự kiện click vào bài báo
    @Override
    public void onArticleClick(Article article) {
        Intent intent = new Intent(HomeActivity.this, DetailActivity.class);

        intent.putExtra("ARTICLE_OBJECT", article);

        startActivity(intent);
    }

    // Xử lý sự kiện click vào thể loại
    @Override
    public void onCategoryClick(String category) {
        Toast.makeText(this, "Tải tin tức về: " + category, Toast.LENGTH_SHORT).show();
        // "General" là một thể loại hợp lệ của API, còn "All" là do chúng ta tự định nghĩa
        if (category.equalsIgnoreCase("All")) {
            homeViewModel.fetchTopHeadlines();
        } else {
            homeViewModel.fetchNewsByCategory(category);
        }
    }
    private void setupBottomNav() {
        // Đảm bảo icon Home đang được chọn
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                return true;
            } else if (itemId == R.id.navigation_bookmark) {
                // KIỂM TRA ĐĂNG NHẬP
                if (authRepository.getCurrentUser() != null) {
                    // Nếu đã đăng nhập, mở BookmarkActivity
                    startActivity(new Intent(this, BookmarkActivity.class));
                } else {
                    // Nếu chưa, yêu cầu đăng nhập
                    Toast.makeText(this, "Vui lòng đăng nhập để sử dụng chức năng này", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                }
                return true;
            }
            // ... các tab khác
            return false;
        });
    }
}