// File: ui/home/CategoryAdapter.java
package com.example.newsapp.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newsapp.R;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<String> categories;
    private OnCategoryClickListener listener;
    // Biến để lưu vị trí của item đang được chọn, mặc định là 0 (item "All")
    private int selectedPosition = 0;

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }

    public CategoryAdapter(List<String> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.bind(category);

        // << LOGIC MỚI 1: CẬP NHẬT TRẠNG THÁI GIAO DIỆN >>
        // Dựa vào selectedPosition để quyết định item này có được "chọn" hay không
        holder.itemView.setSelected(selectedPosition == position);
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
        }

        // Sửa lại hàm bind để nó không cần listener nữa
        public void bind(final String category) {
            tvCategoryName.setText(category);

            // << LOGIC MỚI 2: XỬ LÝ SỰ KIỆN CLICK >>
            itemView.setOnClickListener(v -> {
                // Lấy vị trí hiện tại của item được click
                int clickedPosition = getAdapterPosition();

                // Nếu click vào một item chưa được chọn
                if (clickedPosition != RecyclerView.NO_POSITION && clickedPosition != selectedPosition) {
                    // Thông báo cho Activity biết
                    listener.onCategoryClick(category);

                    // Cập nhật lại giao diện
                    // 1. Báo cho item cũ (vừa bị bỏ chọn) vẽ lại
                    notifyItemChanged(selectedPosition);
                    // 2. Cập nhật vị trí được chọn mới
                    selectedPosition = clickedPosition;
                    // 3. Báo cho item mới (vừa được chọn) vẽ lại
                    notifyItemChanged(selectedPosition);
                }
            });
        }
    }
}