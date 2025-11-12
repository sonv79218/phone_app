package com.example.tuan17.adapter;

import static android.content.ContentValues.TAG;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.DanhGiaActivity;
import com.example.tuan17.XemDanhGiaActivity;
import com.example.tuan17.R;
import com.example.tuan17.helper.SharedPrefHelper;
import com.example.tuan17.models.ChiTietDonHang;

import org.json.JSONException;
import java.util.List;

public class ChiTietDonHangAdapter extends ArrayAdapter<ChiTietDonHang> {
    private Context context;
    public ChiTietDonHangAdapter(Context context, List<ChiTietDonHang> details) {
        super(context, 0, details);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChiTietDonHang detail = getItem(position);
        int masp = detail.getMasp();
        int id_donhang = detail.getId_chitiet();
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ds_chitietdonhang, parent, false);
        }
//        lấy các thành phần giao diện
//        TextView tvID_dathang = convertView.findViewById(R.id.txt_Iddathang);
        TextView tvMaSp = convertView.findViewById(R.id.txtMasp);
        TextView tvTenSp = convertView.findViewById(R.id.txtTensp); // Thêm TextView cho tên sản phẩm
//        TextView tvSoLuong = convertView.findViewById(R.id.txtSoLuong);
//        TextView tvDonGia = convertView.findViewById(R.id.txtGia);
        ImageView ivAnh = convertView.findViewById(R.id.imgsp);
        Button danhGia = convertView.findViewById(R.id.btnDanhGia);
        // lấy id người mua, mã đơn hàng, và sản phẩm
        int userId = SharedPrefHelper.getUserId(getContext());
//        hiển thị giao diện
        daDanhGia(userId, masp, id_donhang, exists -> {
            if (exists) {
                // Người dùng đã đánh giá
                danhGia.setText("Xem đánh giá");
                danhGia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), XemDanhGiaActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("sanPhamId", masp);
                        intent.putExtra("chitietdonhangId", detail.getId_chitiet());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent);
                    }
                });
            } else {
                // Chưa đánh giá
                danhGia.setText("Đánh giá");
                danhGia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), DanhGiaActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("sanPhamId", masp);
                        intent.putExtra("chitietdonhangId", detail.getId_chitiet());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent);
                    }
                });
            }
        });
        // Hiển thị ID đơn hàng
//        tvID_dathang.setText(String.valueOf(id_donhang));

        // Hiển thị mã sản phẩm
        tvMaSp.setText(String.valueOf(masp)); // Hiển thị mã sản phẩm
        getTenSanPhamByMaSp(String.valueOf(masp), tenSp -> {
            if (tenSp != null) {
                tvTenSp.setText(tenSp);
            } else {
                tvTenSp.setText("Không xác định");
            }
        });
//        tvSoLuong.setText(String.valueOf(detail.getSoLuong()));
//        tvDonGia.setText(String.valueOf(detail.getDonGia()));
        byte[] anhByteArray = detail.getAnh();
        if (anhByteArray != null && anhByteArray.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(anhByteArray, 0, anhByteArray.length);
            ivAnh.setImageBitmap(bitmap);
            Log.d(TAG, "getView: "+bitmap);
        } else {
            ivAnh.setImageResource(R.drawable.vest); // Hình ảnh mặc định nếu không có
        }
        return convertView;
    }

    public interface TenSanPhamCallback {
        void onTenSanPhamReceived(String tenSanPham);
    }

    private void getTenSanPhamByMaSp(String masp, TenSanPhamCallback callback) {
        String url = "http://10.0.2.2:3000/sanpham/tensp/" + masp;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String tenSp = response.getString("tensp");
                        callback.onTenSanPhamReceived(tenSp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onTenSanPhamReceived(null);
                    }
                },
                error -> {
                    error.printStackTrace();
                    callback.onTenSanPhamReceived(null);
                }
        );
        Volley.newRequestQueue(context).add(request);
    }
    public void daDanhGia(int userId, int sanPhamId, int chiTietDonHangId, DaDanhGiaCallback callback) {
        String url = "http://10.0.2.2:3000/danhgia/check?user_id=" + userId
                + "&masp=" + sanPhamId + "&id_chitietdonhang=" + chiTietDonHangId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        boolean exists = response.getBoolean("exists");
                        callback.onResult(exists);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onResult(false);
                    }
                },
                error -> {
                    error.printStackTrace();
                    callback.onResult(false);
                }
        );

        Volley.newRequestQueue(context).add(request);
    }

    // Interface callback
    public interface DaDanhGiaCallback {
        void onResult(boolean exists);
    }



}