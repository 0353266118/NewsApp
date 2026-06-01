// File: ui/home/NewsAdapter.java
package com.example.newsapp.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newsapp.R;
import com.example.newsapp.data.model.Article;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ArticleViewHolder> {

    private List<Article> articles = new ArrayList<>();
    private Context context;
    private OnArticleClickListener listener;

    // << 2. Tạo interface ở ngay bên trong Adapter >>
    public interface OnArticleClickListener {
        void onArticleClick(Article article);
    }

    // << 3. Tạo một phương thức để Activity có thể "đăng ký" lắng nghe >>
    public void setOnArticleClickListener(OnArticleClickListener listener) {
        this.listener = listener;
    }


    public NewsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo ra một view mới (cái khung) từ file item_article.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        // Lấy dữ liệu từ một bài báo và đặt nó vào các view trong khung
        Article currentArticle = articles.get(position);
        holder.textViewTitle.setText(currentArticle.getTitle());
        holder.textViewSource.setText(currentArticle.getSource().getName());
        // << 4. Bắt sự kiện click vào toàn bộ item >>
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onArticleClick(currentArticle);
            }
        });

        // Dùng Glide để tải hình ảnh từ URL
        Glide.with(context)
                .load(currentArticle.getUrlToImage())
                .placeholder(R.drawable.ic_launcher_background) // Ảnh hiển thị trong lúc chờ tải
                .error(R.drawable.ic_launcher_background) // Ảnh hiển thị khi lỗi
                .into(holder.imageViewArticle);
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng bài báo trong danh sách
        return articles.size();
    }

    // Phương thức để cập nhật danh sách bài báo và thông báo cho RecyclerView
    public void setArticles(List<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged(); // Quan trọng: Báo cho RecyclerView biết dữ liệu đã thay đổi để vẽ lại
    }

    // Lớp nội để giữ các view của một item
    class ArticleViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewArticle;
        TextView textViewTitle;
        TextView textViewSource;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewArticle = itemView.findViewById(R.id.image_view_article);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewSource = itemView.findViewById(R.id.text_view_source);
        }
    }
}