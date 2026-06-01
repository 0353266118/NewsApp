// File: ui/detail/DetailActivity.java
package com.example.newsapp.ui.detail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient; // << Import mới
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar; // << Import mới
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

        // 1. Ánh xạ các view
        toolbar = findViewById(R.id.toolbar_detail);
        webView = findViewById(R.id.web_view_detail);
        progressBar = findViewById(R.id.progress_bar_detail);

        // 2. Setup Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false); // << Tắt tiêu đề mặc định
        }

        // 3. Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        String url = intent.getStringExtra("ARTICLE_URL");
        String title = intent.getStringExtra("ARTICLE_TITLE"); // << Nhận thêm tiêu đề

        // 4. Cập nhật tiêu đề trên Toolbar
        if (title != null) {
            toolbar.setTitle(title);
        }

        // 5. Setup và tải WebView
        if (url != null && !url.isEmpty()) {
            setupWebView();
            webView.loadUrl(url);
        }
    }

    private void setupWebView() {
        // Bật JavaScript
        webView.getSettings().setJavaScriptEnabled(true);

        // Client này xử lý các sự kiện chính của trang (bắt đầu tải, tải xong, lỗi)
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE); // Hiển thị ProgressBar khi bắt đầu tải
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE); // Ẩn ProgressBar khi tải xong
            }
        });

        // Client này xử lý các sự kiện của trình duyệt (tiến trình, thay đổi tiêu đề...)
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress); // Cập nhật tiến trình của ProgressBar
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                // Cập nhật tiêu đề của Toolbar khi trang web cung cấp tiêu đề mới
                if (title != null && !title.isEmpty()) {
                    toolbar.setTitle(title);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Xử lý nút back của điện thoại để quay lại trang trước trong WebView nếu có thể
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack(); // Nếu WebView có thể quay lại, thì ưu tiên quay lại trang trước
        } else {
            super.onBackPressed(); // Nếu không, thì thoát Activity
        }
    }
}