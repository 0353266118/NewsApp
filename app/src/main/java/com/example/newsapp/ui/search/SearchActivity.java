// File: ui/search/SearchActivity.java
package com.example.newsapp.ui.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.example.newsapp.ui.home.OnArticleClickListener;
import java.io.Serializable;

public class SearchActivity extends AppCompatActivity implements OnArticleClickListener {

    private SearchViewModel searchViewModel;
    private EditText etSearch;
    private ImageView ivClearSearch;
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter; // Tái sử dụng NewsAdapter
    private ProgressBar progressBar;
    private TextView tvPlaceholder;
    private ImageView ivBack;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        initViews();
        setupRecyclerView();
        setupSearchListeners();
        observeViewModel();

        // Tự động focus và bật bàn phím khi màn hình mở
        showKeyboard();
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        ivClearSearch = findViewById(R.id.iv_clear_search);
        recyclerView = findViewById(R.id.recycler_view_search_results);
        progressBar = findViewById(R.id.progress_bar_search);
        tvPlaceholder = findViewById(R.id.tv_search_placeholder);
        ivBack = findViewById(R.id.iv_back);
    }

    private void setupRecyclerView() {
        newsAdapter = new NewsAdapter(this);
        newsAdapter.setOnArticleClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(newsAdapter);

        // Thêm listener để tự động ẩn bàn phím khi người dùng bắt đầu cuộn
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

    private void setupSearchListeners() {
        // Nút back
        ivBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Nút xóa text
        ivClearSearch.setOnClickListener(v -> etSearch.setText(""));

        // Lắng nghe sự thay đổi text trong EditText để thực hiện tìm kiếm
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Hủy bỏ lần tìm kiếm trước đó nếu người dùng vẫn đang gõ
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
                // Hiển thị/ẩn nút xóa
                ivClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Kỹ thuật Debounce: Tạo một công việc tìm kiếm mới,
                // sẽ được thực thi sau 500ms kể từ lần gõ phím cuối cùng.
                searchRunnable = () -> searchViewModel.searchNews(s.toString());
                handler.postDelayed(searchRunnable, 500); // Delay 0.5 giây
            }
        });
    }

    private void observeViewModel() {
        // Quan sát trạng thái loading
        searchViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null && isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                tvPlaceholder.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        // Quan sát kết quả tìm kiếm
        searchViewModel.getSearchResultsLiveData().observe(this, articles -> {
            if (articles != null && !articles.isEmpty()) {
                tvPlaceholder.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                newsAdapter.setArticles(articles);
            } else {
                recyclerView.setVisibility(View.GONE);
                // Chỉ hiển thị thông báo nếu không đang loading
                if (progressBar.getVisibility() == View.GONE) {
                    tvPlaceholder.setVisibility(View.VISIBLE);
                    // Hiển thị thông báo phù hợp
                    if (etSearch.getText().toString().isEmpty()) {
                        tvPlaceholder.setText("Tìm kiếm bài báo bạn muốn đọc");
                    } else {
                        tvPlaceholder.setText("Không tìm thấy kết quả.");
                    }
                }
            }
        });
    }

    @Override
    public void onArticleClick(Article article) {
        Intent intent = new Intent(this, DetailActivity.class);
        // Gửi cả đối tượng Article đi, ép kiểu thành Serializable để đảm bảo an toàn
        intent.putExtra("ARTICLE_OBJECT", (Serializable) article);
        startActivity(intent);
    }

    // --- Các hàm tiện ích ---
    private void showKeyboard() {
        etSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}