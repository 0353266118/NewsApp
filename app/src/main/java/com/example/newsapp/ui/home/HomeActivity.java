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
//        cardTrending = findViewById(R.id.card_trending);
//        ivTrendingImage = findViewById(R.id.iv_trending_image);
//        tvTrendingCategory = findViewById(R.id.tv_trending_category);
//        tvTrendingTitle = findViewById(R.id.tv_trending_title);
    }

    private void setupListeners() {
        // Sự kiện click cho thanh tìm kiếm
        searchBar.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
//            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
//                    HomeActivity.this,
//                    Pair.create(searchBar, "search_transition")
//            );
//            startActivity(intent, options.toBundle());
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
    }

    // Xử lý sự kiện click vào bài báo
    @Override
    public void onArticleClick(Article article) {
        Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
        intent.putExtra("ARTICLE_URL", article.getUrl());
        intent.putExtra("ARTICLE_TITLE", article.getTitle());
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
}