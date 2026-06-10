// File: ui/detail/DetailActivity.java
package com.example.newsapp.ui.detail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.newsapp.R;
import com.example.newsapp.data.model.Article;
import com.example.newsapp.data.repository.AuthRepository;
import com.example.newsapp.ui.auth.LoginActivity;

import java.io.Serializable;

public class DetailActivity extends AppCompatActivity {

    private DetailViewModel detailViewModel;
    private Toolbar toolbar;
    private WebView webView;
    private ProgressBar progressBar;
    private Article currentArticle;
    private boolean isBookmarked = false;
    private MenuItem bookmarkMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        currentArticle = (Article) getIntent().getSerializableExtra("ARTICLE_OBJECT");

        if (currentArticle == null || currentArticle.getUrl() == null) {
            Toast.makeText(this, "Error: Article data is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupWebView();
        webView.loadUrl(currentArticle.getUrl());

        observeViewModel();
        handleOnBackPressed(); // << SỬA LỖI 1: GỌI HÀM NÀY Ở ĐÂY
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_detail);
        webView = findViewById(R.id.web_view_detail);
        progressBar = findViewById(R.id.progress_bar_detail);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // << SỬA LỖI 2: VÔ HIỆU HÓA HOÀN TOÀN TIÊU ĐỀ >>
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
            // << SỬA LỖI 2: XÓA BỎ HOÀN TOÀN onReceivedTitle >>
        });
    }

    private void observeViewModel() {
        if (AuthRepository.getInstance().getCurrentUser() != null) {
            detailViewModel.isBookmarked(currentArticle.getUrl()).observe(this, bookmarked -> {
                this.isBookmarked = bookmarked;
                updateBookmarkIcon();
            });
            detailViewModel.getBookmarkActionResult().observe(this, success -> {
                // Có thể hiển thị Toast ở đây nếu muốn
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        bookmarkMenuItem = menu.findItem(R.id.action_bookmark);
        updateBookmarkIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_bookmark) {
            if (AuthRepository.getInstance().getCurrentUser() != null) {
                detailViewModel.toggleBookmark(currentArticle, this.isBookmarked);
            } else {
                Toast.makeText(this, "Please login to save articles", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateBookmarkIcon() {
        if (bookmarkMenuItem != null) {
            if (isBookmarked) {
                bookmarkMenuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_bookmark_filled));
            } else {
                bookmarkMenuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_bookmark_border));
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    private void handleOnBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}