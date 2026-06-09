// File: ui/home/TrendingAdapter.java
package com.example.newsapp.ui.home;

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

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.TrendingViewHolder> {

    private List<Article> trendingArticles = new ArrayList<>();
    private NewsAdapter.OnArticleClickListener listener; // Tái sử dụng interface từ NewsAdapter

    public TrendingAdapter(NewsAdapter.OnArticleClickListener listener) {
        this.listener = listener;
    }

    public void setTrendingArticles(List<Article> articles) {
        this.trendingArticles = articles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trending_banner, parent, false);
        return new TrendingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingViewHolder holder, int position) {
        Article article = trendingArticles.get(position);
        holder.bind(article);
    }

    @Override
    public int getItemCount() {
        return trendingArticles.size();
    }

    class TrendingViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvSource, tvTitle;

        public TrendingViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_trending_banner_image);
            tvSource = itemView.findViewById(R.id.tv_trending_banner_source);
            tvTitle = itemView.findViewById(R.id.tv_trending_banner_title);
        }

        void bind(Article article) {
            tvTitle.setText(article.getTitle());
            if (article.getSource() != null) {
                tvSource.setText(article.getSource().getName());
            }

            Glide.with(itemView.getContext())
                    .load(article.getUrlToImage())
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_error)
                    .into(ivImage);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onArticleClick(article);
                }
            });
        }
    }
}