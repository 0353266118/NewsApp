// File: ui/detail/DetailActivity.java
package com.example.newsapp.ui.detail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.newsapp.R;

public class DetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Ánh xạ các view
        toolbar = findViewById(R.id.toolbar_detail);
        webView = findViewById(R.id.web_view_detail);
        progressBar = findViewById(R.id.progress_bar_detail);

        // Cấu hình Toolbar
        setupToolbar();

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        String url = intent.getStringExtra("ARTICLE_URL");
        String title = intent.getStringExtra("ARTICLE_TITLE");

        // Cập nhật tiêu đề ban đầu cho Toolbar
        if (title != null) {
            toolbar.setTitle(title);
        }

        // Cấu hình và tải WebView
        if (url != null && !url.isEmpty()) {
            setupWebView();
            webView.loadUrl(url);
        }

        // Xử lý nút back bằng cách làm mới
        handleOnBackPressed();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Tắt tiêu đề mặc định để tự quản lý
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

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (title != null && !title.isEmpty()) {
                    toolbar.setTitle(title);
                }
            }
        });
    }

    // Xử lý khi người dùng bấm nút back trên Toolbar
    @Override
    public boolean onSupportNavigateUp() {
        // Lệnh này sẽ kích hoạt OnBackPressedDispatcher
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    // Phương thức mới để xử lý nút back của hệ thống
    private void handleOnBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* Bật mặc định */) {
            @Override
            public void handleOnBackPressed() {
                // Nếu WebView có thể quay lại trang trước, thì ưu tiên làm việc đó
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    // Nếu không, thì tắt callback này đi để tránh vòng lặp vô hạn
                    setEnabled(false);
                    // Và gọi hành động back mặc định (thoát Activity)
                    // Lưu ý: Cần gọi lại getOnBackPressedDispatcher().onBackPressed() thay vì super.onBackPressed()
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        };
        // Thêm callback vào dispatcher của Activity
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}