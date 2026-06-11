// File: ui/detail/DetailActivity.java
package com.example.newsapp.ui.detail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
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
        handleOnBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBookmarkStatus();
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
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }
        });
    }

    private void observeViewModel() {
        if (AuthRepository.getInstance().getCurrentUser() != null) {
            detailViewModel.getBookmarkActionResult().observe(this, success -> {
                if (success != null && success) {
                    checkBookmarkStatus();
                } else if (success != null) {
                    Toast.makeText(this, "Action failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void checkBookmarkStatus() {
        if (AuthRepository.getInstance().getCurrentUser() != null) {
            detailViewModel.checkBookmarkStatus(currentArticle.getUrl()).observe(this, this::updateBookmarkIcon);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        bookmarkMenuItem = menu.findItem(R.id.action_bookmark);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        checkBookmarkStatus(); // Kiểm tra lại trạng thái mỗi khi menu sắp được hiển thị
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_bookmark) {
            if (AuthRepository.getInstance().getCurrentUser() != null) {
                detailViewModel.toggleBookmark(currentArticle);
            } else {
                Toast.makeText(this, "Please login to save articles", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            }
            return true;
        } else if (id == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateBookmarkIcon(Boolean isBookmarked) {
        if (bookmarkMenuItem != null) {
            if (isBookmarked != null && isBookmarked) {
                bookmarkMenuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_bookmark_filled));
            } else {
                bookmarkMenuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_bookmark_border));
            }
        }
    }

    private void handleOnBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}