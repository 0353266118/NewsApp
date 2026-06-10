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
    private final Context context;
    private OnArticleClickListener listener; // << SỬ DỤNG INTERFACE CHUNG

    // << XÓA BỎ HOÀN TOÀN ĐỊNH NGHĨA INTERFACE CŨ Ở ĐÂY >>

    public void setOnArticleClickListener(OnArticleClickListener listener) {
        this.listener = listener;
    }

    public NewsAdapter(Context context) {
        this.context = context;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article currentArticle = articles.get(position);
        holder.bind(currentArticle);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewArticle;
        TextView textViewTitle, textViewSource;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewArticle = itemView.findViewById(R.id.image_view_article);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewSource = itemView.findViewById(R.id.text_view_source);
        }

        // Tách logic bind ra một hàm riêng cho sạch sẽ
        void bind(Article article) {
            textViewTitle.setText(article.getTitle());
            if (article.getSource() != null) {
                textViewSource.setText(article.getSource().getName());
            }

            Glide.with(context)
                    .load(article.getUrlToImage())
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_error)
                    .into(imageViewArticle);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    // Lấy vị trí một cách an toàn
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onArticleClick(articles.get(position));
                    }
                }
            });
        }
    }
}