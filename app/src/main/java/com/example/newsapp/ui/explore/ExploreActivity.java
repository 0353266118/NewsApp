package com.example.newsapp.ui.explore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newsapp.R;
import com.example.newsapp.data.model.Article;
import com.example.newsapp.ui.detail.DetailActivity;
import com.example.newsapp.ui.home.HomeViewModel;
import com.example.newsapp.ui.home.NewsAdapter;
import com.example.newsapp.ui.home.OnArticleClickListener;
import java.io.Serializable;

public class ExploreActivity extends AppCompatActivity implements OnArticleClickListener {

    private HomeViewModel exploreViewModel;
    private RecyclerView recyclerViewExplore;
    private NewsAdapter exploreAdapter;
    private ProgressBar progressBar;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        // Tái sử dụng HomeViewModel
        exploreViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        initViews();
        setupRecyclerView();
        setupListeners();
        observeViewModel();

        // TỰ ĐỘNG GỌI TIN TỔNG HỢP DIỆN RỘNG KHI MỞ MÀN HÌNH
        // Toán tử OR giúp quét qua nhiều chủ đề báo chí Việt Nam cùng lúc
        exploreViewModel.searchNews("Việt Nam OR thời sự OR công nghệ OR thị trường");
    }

    private void initViews() {
        recyclerViewExplore = findViewById(R.id.recycler_view_explore);
        progressBar = findViewById(R.id.progress_bar_explore);
        btnBack = findViewById(R.id.btn_back_explore);
    }

    private void setupRecyclerView() {
        exploreAdapter = new NewsAdapter(this);
        exploreAdapter.setOnArticleClickListener(this);
        recyclerViewExplore.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewExplore.setAdapter(exploreAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            finish(); // Kết thúc màn hình phụ để rút về HomeActivity
            overridePendingTransition(0, 0);
        });
    }

    private void observeViewModel() {
        exploreViewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading != null && isLoading ? View.VISIBLE : View.GONE);
        });

        // Nhận mớ dữ liệu đa góc nhìn ngẫu nhiên từ NewsAPI đổ thẳng lên RecyclerView
        exploreViewModel.getLatestNewsLiveData().observe(this, articles -> {
            if (articles != null) {
                exploreAdapter.setArticles(articles);
            }
        });
    }

    @Override
    public void onArticleClick(Article article) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("ARTICLE_OBJECT", (Serializable) article);
        startActivity(intent);
    }
}