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
import com.example.newsapp.ui.home.OnArticleClickListener; // Quan trọng: Import interface chung
import java.util.ArrayList;
import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {

    private List<Article> articles = new ArrayList<>();
    private final Context context;
    private OnArticleClickListener listener; // Sử dụng interface chung, không định nghĩa lại ở đây

    /**
     * Hàm khởi tạo của Adapter
     * @param context Context của Activity hoặc Fragment gọi nó, cần cho Glide
     */
    public BookmarkAdapter(Context context) {
        this.context = context;
    }

    /**
     * Phương thức để Activity hoặc Fragment đăng ký lắng nghe sự kiện click
     * @param listener Đối tượng (thường là Activity/Fragment) implement OnArticleClickListener
     */
    public void setOnArticleClickListener(OnArticleClickListener listener) {
        this.listener = listener;
    }

    /**
     * Phương thức để cập nhật danh sách bài báo từ ViewModel và thông báo cho RecyclerView
     * @param articles Danh sách các bài báo mới
     */
    public void setArticles(List<Article> articles) {
        this.articles.clear();
        if (articles != null) {
            this.articles.addAll(articles);
        }
        notifyDataSetChanged(); // Báo cho RecyclerView biết dữ liệu đã thay đổi để vẽ lại
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // "Thổi" layout item_article.xml thành một View
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new BookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
        // Lấy bài báo tại vị trí tương ứng và yêu cầu ViewHolder hiển thị dữ liệu
        Article currentArticle = articles.get(position);
        holder.bind(currentArticle);
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng item trong danh sách
        return articles.size();
    }

    /**
     * Lớp ViewHolder chịu trách nhiệm giữ các tham chiếu đến View của một item
     * và gán dữ liệu vào chúng.
     */
    class BookmarkViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewArticle;
        TextView textViewTitle;
        TextView textViewSource;

        public BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các View một lần duy nhất tại đây để tối ưu hiệu năng
            imageViewArticle = itemView.findViewById(R.id.image_view_article);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewSource = itemView.findViewById(R.id.text_view_source);
        }

        /**
         * Hàm này được gọi trong onBindViewHolder để gán dữ liệu của một Article
         * cụ thể vào các View đã được ánh xạ.
         * @param article Đối tượng bài báo cần hiển thị
         */
        void bind(Article article) {
            // Gán dữ liệu text
            textViewTitle.setText(article.getTitle());
            if (article.getSource() != null) {
                textViewSource.setText(article.getSource().getName());
            } else {
                textViewSource.setText("Unknown Source"); // Xử lý trường hợp source bị null
            }

            // Dùng Glide để tải hình ảnh từ URL
            Glide.with(context)
                    .load(article.getUrlToImage())
                    .placeholder(R.drawable.ic_image_placeholder) // Ảnh hiển thị trong lúc chờ tải
                    .error(R.drawable.ic_image_error)         // Ảnh hiển thị khi URL null hoặc tải lỗi
                    .into(imageViewArticle);

            // Bắt sự kiện click vào toàn bộ item
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    // Lấy vị trí của item một cách an toàn để tránh lỗi khi danh sách thay đổi
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Gọi đến phương thức onArticleClick của Activity/Fragment
                        listener.onArticleClick(articles.get(position));
                    }
                }
            });
        }
    }
}