// File: ui/bookmark/BookmarkAdapter.java
package com.example.newsapp.ui.bookmark;

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

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {

    private List<Article> articles = new ArrayList<>();
    private final Context context;
    private OnArticleClickListener listener;

    // Interface để xử lý sự kiện click, giống hệt NewsAdapter
    public interface OnArticleClickListener {
        void onArticleClick(Article article);
    }

    public void setOnArticleClickListener(OnArticleClickListener listener) {
        this.listener = listener;
    }

    public BookmarkAdapter(Context context) {
        this.context = context;
    }

    // Phương thức để cập nhật danh sách bài báo từ Activity
    public void setArticles(List<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged(); // Báo cho RecyclerView biết dữ liệu đã thay đổi
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng layout item_article chung
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new BookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
        // Lấy bài báo tại vị trí tương ứng và hiển thị dữ liệu
        Article currentArticle = articles.get(position);
        holder.bind(currentArticle);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    // Lớp ViewHolder để giữ các View của một item
    class BookmarkViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewArticle;
        TextView textViewTitle;
        TextView textViewSource;

        public BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewArticle = itemView.findViewById(R.id.image_view_article);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewSource = itemView.findViewById(R.id.text_view_source);
        }

        // Hàm để gán dữ liệu vào các View
        void bind(Article article) {
            textViewTitle.setText(article.getTitle());
            if (article.getSource() != null) {
                textViewSource.setText(article.getSource().getName());
            }

            // Dùng Glide để tải hình ảnh, có cả placeholder và error
            Glide.with(context)
                    .load(article.getUrlToImage())
                    .placeholder(R.drawable.ic_image_placeholder) // Ảnh giữ chỗ
                    .error(R.drawable.ic_image_error)         // Ảnh báo lỗi
                    .into(imageViewArticle);

            // Bắt sự kiện click vào toàn bộ item
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onArticleClick(articles.get(position));
                    }
                }
            });
        }
    }
}