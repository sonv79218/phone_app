package com.example.tuan17.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuan17.R;
import com.example.tuan17.models.DanhGia;

import java.util.List;

public class DanhGiaAdapter extends RecyclerView.Adapter<DanhGiaAdapter.DanhGiaViewHolder> {

    private Context context;
    private List<DanhGia> danhGiaList;

    public DanhGiaAdapter(Context context, List<DanhGia> danhGiaList) {
        this.context = context;
        this.danhGiaList = danhGiaList;
    }

    @NonNull
    @Override
    public DanhGiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_danhgia, parent, false);
        return new DanhGiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DanhGiaViewHolder holder, int position) {
        DanhGia danhGia = danhGiaList.get(position);
        holder.ratingBar.setRating(danhGia.getRating());
        holder.tvComment.setText(danhGia.getComment());
        holder.tvNgay.setText("Ngày đánh giá: " + danhGia.getNgayDanhGia());
    }

    @Override
    public int getItemCount() {
        return danhGiaList.size();
    }

    static class DanhGiaViewHolder extends RecyclerView.ViewHolder {
        RatingBar ratingBar;
        TextView tvComment, tvNgay;

        public DanhGiaViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.ratingBarItem);
            tvComment = itemView.findViewById(R.id.tvCommentItem);
            tvNgay = itemView.findViewById(R.id.tvNgayItem);
        }
    }
}
