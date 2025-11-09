package com.example.tuan17.adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.tuan17.database.DanhGiaDB;
import com.example.tuan17.database.DatabaseHelper;
import com.example.tuan17.R;
import com.example.tuan17.helper.SharedPrefHelper;
import com.example.tuan17.models.ChiTietDonHang;

import org.json.JSONException;

import java.io.InputStream;
import java.net.URL;
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
//        Log.d("masanpham", "getView: "+ masp);
//        Log.d("id chi tiêt", "getView: "+ id_donhang);
//        int productId = detail.getId_dathang();
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ds_chitietdonhang, parent, false);
        }

        TextView tvID_dathang = convertView.findViewById(R.id.txt_Iddathang);
        TextView tvMaSp = convertView.findViewById(R.id.txtMasp);
        TextView tvTenSp = convertView.findViewById(R.id.txtTensp); // Thêm TextView cho tên sản phẩm
        TextView tvSoLuong = convertView.findViewById(R.id.txtSoLuong);
        TextView tvDonGia = convertView.findViewById(R.id.txtGia);
        ImageView ivAnh = convertView.findViewById(R.id.imgsp);
        Button danhGia = convertView.findViewById(R.id.btnDanhGia);
        // lấy id người mua, mã đơn hàng, và sản phẩm
        int userId = SharedPrefHelper.getUserId(getContext());
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

        //DanhGiaDB db = new DanhGiaDB(getContext());

      //  boolean daDanhGia = db.daDanhGia(userId, masp, detail.getId_chitiet());
//        boolean daDanhGia = db.daDanhGia(userId, masp, detail.getId_chitiet());

//        if (daDanhGia) {
//            danhGia.setText("Xem đánh giá");
//            danhGia.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getContext(), XemDanhGiaActivity.class);
//                    intent.putExtra("userId", userId);
//                    intent.putExtra("sanPhamId", masp);
//                    intent.putExtra("chitietdonhangId", detail.getId_chitiet());
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    getContext().startActivity(intent);
//                }
//            });
//        } else {
//            danhGia.setText("Đánh giá");
//            danhGia.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getContext(), DanhGiaActivity.class);
//                    intent.putExtra("userId", userId);
//                    intent.putExtra("sanPhamId", masp);
//                    intent.putExtra("chitietdonhangId", detail.getId_chitiet());
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    getContext().startActivity(intent);
//                }
//            });
//        }




        // Hiển thị ID đơn hàng
        tvID_dathang.setText(String.valueOf(id_donhang));
        // Hiển thị mã sản phẩm
        tvMaSp.setText(String.valueOf(masp)); // Hiển thị mã sản phẩm

        // Lấy tên sản phẩm từ DatabaseHelper
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
//        String tenSanPham = dbHelper.getTenSanPhamByMaSp(masp);
//        getTenSanPhamByMaSp(masp, tenSanpham);

        getTenSanPhamByMaSp(String.valueOf(masp), tenSp -> {
            if (tenSp != null) {
                tvTenSp.setText(tenSp);
            } else {
                tvTenSp.setText("Không xác định");
            }
        });

//        tvTenSp.setText(tenSanPham != null ? tenSanPham : "Không tìm thấy tên sản phẩm");

        // Hiển thị số lượng và đơn giá
        tvSoLuong.setText(String.valueOf(detail.getSoLuong()));
        tvDonGia.setText(String.valueOf(detail.getDonGia()));

        // Tải ảnh từ byte[]
        Log.d(TAG, "getView: "+detail.getAnh());
        if (detail.getAnh() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(detail.getAnh(), 0, detail.getAnh().length);
            ivAnh.setImageBitmap(bitmap);
            Log.d(TAG, "getView: "+bitmap);
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