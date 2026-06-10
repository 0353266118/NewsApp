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
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import com.example.newsapp.R;
import com.example.newsapp.data.model.Article;
import com.example.newsapp.data.repository.AuthRepository;
import com.example.newsapp.ui.auth.LoginActivity;
import com.example.newsapp.ui.bookmark.BookmarkActivity;
import com.example.newsapp.ui.detail.DetailActivity;
import com.example.newsapp.ui.profile.ProfileActivity;
import com.example.newsapp.ui.search.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import java.io.Serializable;
import java.util.List;
import com.example.newsapp.ui.home.OnArticleClickListener;

public class HomeActivity extends AppCompatActivity implements OnArticleClickListener, CategoryAdapter.OnCategoryClickListener {

    private HomeViewModel homeViewModel;
    private NewsAdapter newsAdapter;
    private CategoryAdapter categoryAdapter;
    private TrendingAdapter trendingAdapter;
    private ProgressBar progressBar;
    private AuthRepository authRepository;

    // Các view trên màn hình
    private RecyclerView recyclerViewNews, recyclerViewCategories;
    private BottomNavigationView bottomNavigationView;
    private MaterialCardView searchBar;
    private LinearLayout layoutTrending;
    private ViewPager2 viewPagerTrending;
    private TextView tvGreeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        authRepository = AuthRepository.getInstance();
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        initViews();
        setupRecyclerViews();
        setupListeners(); // Đổi vị trí setupListeners và setupBottomNav
        setupBottomNav();
        observeViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Khi quay lại màn hình này, luôn đảm bảo tab Home được chọn
        // để tránh trường hợp người dùng bấm back từ Bookmark/Profile về
        // mà tab cũ vẫn sáng.
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        recyclerViewNews = findViewById(R.id.recycler_view_news);
        recyclerViewCategories = findViewById(R.id.recycler_view_categories);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchBar = findViewById(R.id.search_bar);
        layoutTrending = findViewById(R.id.layout_trending);
        viewPagerTrending = findViewById(R.id.view_pager_trending);
        tvGreeting = findViewById(R.id.tv_greeting);
    }

    private void setupListeners() {
        searchBar.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    HomeActivity.this,
                    Pair.create(searchBar, "search_transition")
            );
            startActivity(intent, options.toBundle());
        });
    }

    private void setupRecyclerViews() {
        // Setup cho RecyclerView tin tức chính (Latest)
        newsAdapter = new NewsAdapter(this);
        newsAdapter.setOnArticleClickListener(this);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNews.setNestedScrollingEnabled(false);
        recyclerViewNews.setAdapter(newsAdapter);

        // Setup cho RecyclerView thể loại
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Setup cho ViewPager2 Trending
        trendingAdapter = new TrendingAdapter(this);
        viewPagerTrending.setAdapter(trendingAdapter);
    }

    private void setupBottomNav() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Chỉ xử lý khi người dùng bấm vào một tab KHÁC tab Home
            if (item.getItemId() == R.id.navigation_home) {
                return true; // Đã ở Home rồi, không làm gì cả
            }

            int itemId = item.getItemId();
            if (itemId == R.id.navigation_bookmark) {
                navigateTo(BookmarkActivity.class);
            } else if (itemId == R.id.navigation_profile) {
                navigateTo(ProfileActivity.class);
            } else if (itemId == R.id.navigation_explore) {
                Toast.makeText(this, "Explore feature is coming soon!", Toast.LENGTH_SHORT).show();
            }

            // Trả về false để không hiển thị hiệu ứng chọn item trên BottomNav
            // Vì chúng ta sẽ chuyển sang Activity mới, không ở lại đây
            return false;
        });
    }

    // Hàm tiện ích để điều hướng, có kiểm tra đăng nhập
    private void navigateTo(Class<?> activityClass) {
        if (authRepository.getCurrentUser() != null) {
            startActivity(new Intent(this, activityClass));
        } else {
            Toast.makeText(this, "Please login to use this feature", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
        }
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
                trendingAdapter.setTrendingArticles(articles);
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
                if (categoryAdapter == null) {
                    categoryAdapter = new CategoryAdapter(categories, this);
                    recyclerViewCategories.setAdapter(categoryAdapter);
                }
            }
        });

        // Observer cho trạng thái người dùng
        homeViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                String displayName = firebaseUser.getDisplayName();
                String email = firebaseUser.getEmail();
                String greetingName = (displayName != null && !displayName.isEmpty()) ? displayName : (email != null ? email.split("@")[0] : "User");
                tvGreeting.setText("Hello, " + greetingName + "!");
                tvGreeting.setVisibility(View.VISIBLE);
            } else {
                tvGreeting.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onArticleClick(Article article) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("ARTICLE_OBJECT", (Serializable) article);
        startActivity(intent);
    }

    @Override
    public void onCategoryClick(String category) {
        Toast.makeText(this, "Loading news for: " + category, Toast.LENGTH_SHORT).show();
        if (category.equalsIgnoreCase("All")) {
            homeViewModel.fetchTopHeadlines();
        } else {
            homeViewModel.fetchNewsByCategory(category);
        }
    }
}