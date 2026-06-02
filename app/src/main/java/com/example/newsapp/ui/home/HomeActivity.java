// File: ui/home/HomeActivity.java
package com.example.newsapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

public class HomeActivity extends AppCompatActivity implements NewsAdapter.OnArticleClickListener {

    private HomeViewModel homeViewModel;
    private RecyclerView recyclerViewNews;
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigationView;

    // Các view mới cho khu vực Trending
    private MaterialCardView cardTrending;
    private ImageView ivTrendingImage;
    private TextView tvTrendingCategory, tvTrendingTitle;
    private RecyclerView recyclerViewCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Khởi tạo ViewModel một cách chính xác
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Ánh xạ các view từ layout
        initViews();

        // Cấu hình các thành phần
        setupRecyclerView();
        setupBottomNav();
        observeViewModel();
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        recyclerViewNews = findViewById(R.id.recycler_view_news);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        cardTrending = findViewById(R.id.card_trending);
        ivTrendingImage = findViewById(R.id.iv_trending_image);
        tvTrendingCategory = findViewById(R.id.tv_trending_category);
        tvTrendingTitle = findViewById(R.id.tv_trending_title);
        recyclerViewCategories = findViewById(R.id.recycler_view_categories);
    }

    private void setupRecyclerView() {
        newsAdapter = new NewsAdapter(this);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(this));
        // Tắt tính năng cuộn của RecyclerView để NestedScrollView quản lý
        recyclerViewNews.setNestedScrollingEnabled(false);
        recyclerViewNews.setAdapter(newsAdapter);
        newsAdapter.setOnArticleClickListener(this);
    }

    private void setupBottomNav() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // Đang ở Home rồi, không làm gì cả
                return true;
            } else if (itemId == R.id.navigation_explore) {
                Toast.makeText(this, "Chức năng Khám phá", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.navigation_bookmark) {
                Toast.makeText(this, "Chức năng Đánh dấu", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.navigation_profile) {
                Toast.makeText(this, "Chức năng Cá nhân", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void observeViewModel() {
        // Observer cho trạng thái loading
        homeViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null && isLoading) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        // Observer cho tin tức Trending
        homeViewModel.getTrendingNewsLiveData().observe(this, article -> {
            if (article != null) {
                cardTrending.setVisibility(View.VISIBLE);
                tvTrendingTitle.setText(article.getTitle());
                if (article.getSource() != null) {
                    tvTrendingCategory.setText(article.getSource().getName());
                }
                Glide.with(this).load(article.getUrlToImage()).into(ivTrendingImage);

                // Thêm sự kiện click cho thẻ Trending
                cardTrending.setOnClickListener(v -> onArticleClick(article));
            } else {
                cardTrending.setVisibility(View.GONE);
            }
        });

        // Observer cho danh sách tin tức Latest
        homeViewModel.getLatestNewsLiveData().observe(this, articles -> {
            if (articles != null) {
                newsAdapter.setArticles(articles);
            }
        });

        // TODO: Setup cho RecyclerView Categories
        // setupCategories();
    }

    @Override
    public void onArticleClick(Article article) {
        Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
        intent.putExtra("ARTICLE_URL", article.getUrl());
        intent.putExtra("ARTICLE_TITLE", article.getTitle());
        startActivity(intent);
    }
}