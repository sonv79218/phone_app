package com.example.tuan17.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tuan17.DanhGiaActivity;
import com.example.tuan17.XemDanhGiaActivity;
import com.example.tuan17.database.DatabaseHelper;
import com.example.tuan17.R;
import com.example.tuan17.database.SanPhamDB;
import com.example.tuan17.models.ChiTietDonHang;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class ChiTietDonHang_admin_adapter extends ArrayAdapter<ChiTietDonHang> {
    public ChiTietDonHang_admin_adapter(Context context, List<ChiTietDonHang> details) {
        super(context, 0, details);
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ChiTietDonHang detail = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ds_chitietdonhang_admin, parent, false);
        }
        Button xemdanhgia = convertView.findViewById(R.id.btnXemDanhGia);
        TextView tvID_dathang = convertView.findViewById(R.id.txt_Iddathang);
        TextView tvMaSp = convertView.findViewById(R.id.txtMasp);
        TextView tvTenSp = convertView.findViewById(R.id.txtTensp); // Thêm TextView cho tên sản phẩm
        TextView tvSoLuong = convertView.findViewById(R.id.txtSoLuong);
        TextView tvDonGia = convertView.findViewById(R.id.txtGia);
        ImageView ivAnh = convertView.findViewById(R.id.imgsp);
        // nếu mà đc bấm thì chuyển sang trang xem đánh giá - ghi mã đơn hàng và ghi id sp l đc
//        xemdanhgia.setOnClickListener();
        xemdanhgia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), XemDanhGiaActivity.class);
                DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                int userId = dbHelper.getUserIdByChiTietDonHangId(detail.getId_chitiet());
                intent.putExtra("userId", userId);
                intent.putExtra("sanPhamId", detail.getMasp());
                intent.putExtra("chitietdonhangId", detail.getId_chitiet());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }});
        // Hiển thị ID đơn hàng
        tvID_dathang.setText(String.valueOf(detail.getId_dathang()));

        // Hiển thị mã sản phẩm
        int masp = detail.getMasp();
        tvMaSp.setText(String.valueOf(masp)); // Hiển thị mã sản phẩm

        // Lấy tên sản phẩm từ Database
        SanPhamDB sanPhamDB = new SanPhamDB(getContext());
        String tenSanPham = sanPhamDB.getTenSanPhamByMaSp(masp);
        tvTenSp.setText(tenSanPham != null ? tenSanPham : "Không tìm thấy tên sản phẩm");

        // Hiển thị số lượng và đơn giá
        tvSoLuong.setText(String.valueOf(detail.getSoLuong()));
        tvDonGia.setText(String.valueOf(detail.getDonGia()));

        // Tải ảnh từ byte[]
        if (detail.getAnh() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(detail.getAnh(), 0, detail.getAnh().length);
            ivAnh.setImageBitmap(bitmap);
        } else {
            ivAnh.setImageResource(R.drawable.vest); // Hình ảnh mặc định nếu không có
        }

        return convertView;
    }



    private class LoadImage extends AsyncTask<String, Void, Bitmap> {

        private ImageView imageView;

        public LoadImage(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream input = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            }
        }
    }
}