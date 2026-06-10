// File: ui/search/SearchActivity.java
package com.example.newsapp.ui.search;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newsapp.R;
import com.example.newsapp.data.model.Article;
import com.example.newsapp.ui.detail.DetailActivity;
import com.example.newsapp.ui.home.NewsAdapter;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;


public class SearchActivity extends AppCompatActivity implements NewsAdapter.OnArticleClickListener {

    private SearchViewModel searchViewModel;
    private EditText etSearch;
    private ImageView ivClearSearch;
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar;
    private TextView tvPlaceholder;
    private ImageView ivBack;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        initViews();
        setupRecyclerView();
        setupSearch();
        observeViewModel();
        // Thêm sự kiện click cho nút back
        ivBack.setOnClickListener(v -> {
            // Cách đơn giản và hiệu quả nhất là gọi hành động back của hệ thống
            getOnBackPressedDispatcher().onBackPressed();
        });
        // Tự động focus và bật bàn phím
        etSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        ivClearSearch = findViewById(R.id.iv_clear_search);
        recyclerView = findViewById(R.id.recycler_view_search_results);
        progressBar = findViewById(R.id.progress_bar_search);
        tvPlaceholder = findViewById(R.id.tv_search_placeholder);
        ivBack = findViewById(R.id.iv_back); // Ánh xạ nút back
    }

    private void setupRecyclerView() {
        newsAdapter = new NewsAdapter(this);
        newsAdapter.setOnArticleClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(newsAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    hideKeyboard();
                }
            }
        });
    }

    // Hàm tiện ích để ẩn bàn phím
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setupSearch() {
        ivClearSearch.setOnClickListener(v -> etSearch.setText(""));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Kỹ thuật Debounce: Hủy bỏ lần tìm kiếm trước nếu người dùng vẫn đang gõ
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
                ivClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Tạo một lần tìm kiếm mới, sẽ được thực thi sau 500ms
                searchRunnable = () -> searchViewModel.searchNews(s.toString());
                handler.postDelayed(searchRunnable, 500); // Delay 0.5 giây
            }
        });
    }

    private void observeViewModel() {
        searchViewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading) {
                tvPlaceholder.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        searchViewModel.getSearchResultsLiveData().observe(this, articles -> {
            if (articles != null && !articles.isEmpty()) {
                tvPlaceholder.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                newsAdapter.setArticles(articles);
            } else {
                recyclerView.setVisibility(View.GONE);
                // Chỉ hiển thị placeholder nếu không đang loading và không có kết quả
                if (progressBar.getVisibility() == View.GONE) {
                    tvPlaceholder.setVisibility(View.VISIBLE);
                    tvPlaceholder.setText("Không tìm thấy kết quả.");
                }
            }
        });
    }

    @Override
    public void onArticleClick(Article article) {
        Intent intent = new Intent(this, DetailActivity.class);

        // << THAY ĐỔI CÁCH GỬI DỮ LIỆU >>
        // Xóa 2 dòng cũ:
        // intent.putExtra("ARTICLE_URL", article.getUrl());
        // intent.putExtra("ARTICLE_TITLE", article.getTitle());

        // Thêm dòng mới: Gửi cả đối tượng Article
        intent.putExtra("ARTICLE_OBJECT", article);

        startActivity(intent);
    }
}