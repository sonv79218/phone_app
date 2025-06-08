package com.example.tuan17.adapter;

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

public class DanhGiaAdapter extends RecyclerView.Adapter<DanhGiaAdapter.ViewHolder> {
    private List<DanhGia> danhGiaList;

    public DanhGiaAdapter(List<DanhGia> danhGiaList) {
        this.danhGiaList = danhGiaList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUserId, txtComment, txtRating, txtNgay;

        public ViewHolder(View view) {
            super(view);
            txtUserId = view.findViewById(R.id.txtUserId);
            txtComment = view.findViewById(R.id.txtComment);
            txtRating = view.findViewById(R.id.txtRating);
            txtNgay = view.findViewById(R.id.txtNgayDanhGia);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_danhgia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DanhGia dg = danhGiaList.get(position);
        holder.txtUserId.setText("Người dùng ID: " + dg.getUserId());
        holder.txtComment.setText("Nhận xét: " + dg.getComment());
        holder.txtRating.setText("Số sao: " + dg.getRating());
        holder.txtNgay.setText("Ngày: " + dg.getNgayDanhGia());
    }

    @Override
    public int getItemCount() {
        return danhGiaList.size();
    }
}
