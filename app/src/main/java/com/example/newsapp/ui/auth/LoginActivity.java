// File: ui/auth/LoginActivity.java
package com.example.newsapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.newsapp.R;

public class LoginActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView tvGoToRegister;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Khởi tạo ViewModel (chỉ một lần)
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // 2. Ánh xạ tất cả các View từ layout
        initViews();

        // 3. Thiết lập các sự kiện click
        setupListeners();

        // 4. Bắt đầu quan sát trạng thái từ ViewModel
        observeAuthState();
    }

    // Hàm này chỉ để ánh xạ, giúp onCreate gọn gàng
    private void initViews() {
        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar_login);
        tvGoToRegister = findViewById(R.id.tv_go_to_register);
        ivBack = findViewById(R.id.iv_back);
    }

    // Hàm này chỉ để cài đặt sự kiện click
    private void setupListeners() {
        // Sự kiện cho nút Login
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            // Nên có một hàm kiểm tra đầu vào đơn giản
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }
            authViewModel.login(email, password);
        });

        // Sự kiện cho dòng chữ "Register"
        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Sự kiện cho nút Back
        ivBack.setOnClickListener(v -> finish());
    }

    // Hàm này chỉ để quan sát LiveData
    private void observeAuthState() {
        authViewModel.getAuthState().observe(this, authResource -> {
            // Kiểm tra để chắc chắn authResource không bị null
            if (authResource == null) return;

            // Dựa vào trạng thái để cập nhật giao diện
            switch (authResource.status) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    btnLogin.setEnabled(false); // Vô hiệu hóa nút để tránh click nhiều lần
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    // Đăng nhập thành công, đóng màn hình này lại để quay về màn hình trước đó
                    finish();
                    break;
                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true); // Kích hoạt lại nút khi có lỗi
                    Toast.makeText(this, "Login Failed: " + authResource.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }
}