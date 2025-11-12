package com.example.tuan17;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
//import com.example.tuan17.database.DanhGiaDB;
import com.example.tuan17.helper.BottomBar_Helper;
import com.example.tuan17.models.ChiTietSanPham;
import com.example.tuan17.models.DanhGia;
import com.example.tuan17.util.ImageLoader;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ChiTietSanPham_Activity extends AppCompatActivity {

    ArrayList<DanhGia> danhGiaList = new ArrayList<>();
     String masp, tendn;
  Button btndathang, btnaddcart;
    private ChiTietSanPham chiTietSanPham;
    private GioHangManager gioHangManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_san_pham);
//        DanhGiaDB danhGiaDB = new DanhGiaDB(this);
        TextView tvXemDanhGia = findViewById(R.id.tv_danhgia_title);

        tvXemDanhGia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (masp != null && !masp.isEmpty()) {
                    try {
                        int id = Integer.parseInt(masp);
                        Intent intent = new Intent(ChiTietSanPham_Activity.this, DanhSachDanhGiaActivity.class);
                        intent.putExtra("masp", id);
                        startActivity(intent);
                    } catch (NumberFormatException e) {
                        Toast.makeText(ChiTietSanPham_Activity.this, "Mã sản phẩm không hợp lệ!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChiTietSanPham_Activity.this, "Không có mã sản phẩm!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Khởi tạo các thành phần giao diện
        btndathang = findViewById(R.id.btndathang);
        btnaddcart = findViewById(R.id.btnaddcart);

        //sao
        // Khai báo biến ImageView
        ImageView star1 = findViewById(R.id.star1);
        ImageView star2 = findViewById(R.id.star2);
        ImageView star3 = findViewById(R.id.star3);
        ImageView star4 = findViewById(R.id.star4);
        ImageView star5 = findViewById(R.id.star5);

        TextView tensp = findViewById(R.id.tensp);
        ImageView anh = findViewById(R.id.imgsp);
        TextView dongia = findViewById(R.id.dongia);
        TextView mota = findViewById(R.id.mota);
        TextView ghichu = findViewById(R.id.ghichu);
        TextView soluongkho = findViewById(R.id.soluongkho);// giá trị để check

//        TextView danhgia = findViewById(R.id.danhgia);
        gioHangManager = GioHangManager.getInstance(); // Sử dụng singleton
        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
            // Nhận chi tiết sản phẩm nếu có
            chiTietSanPham = intent.getParcelableExtra("chitietsanpham");

            if (chiTietSanPham != null) {
                masp = chiTietSanPham.getMasp(); // Lấy mã sản phẩm từ chi tiết
                Log.d("ChiTietSanPham", "masp = " + masp);

                // Lấy điểm trung bình sao (float)
                int maSpInt = Integer.parseInt(masp);
                // Hàm hiển thị sao
//
                tinhTrungBinhSoSao(maSpInt, avgRating -> {
                    displayStarRating(avgRating, star1, star2, star3, star4, star5);
                });
// hiển thị các thuộc tính
                tensp.setText(chiTietSanPham.getTensp());
                ghichu.setText(chiTietSanPham.getGhichu());
                dongia.setText(chiTietSanPham.getDongia() != null ? String.valueOf(chiTietSanPham.getDongia()) : "Không có dữ liệu");
                mota.setText(chiTietSanPham.getMota() != null ? chiTietSanPham.getMota() : "Không có dữ liệu");
                soluongkho.setText(String.valueOf(chiTietSanPham.getSoluongkho()));
                // Load ảnh từ file path
                String imagePath = chiTietSanPham.getAnh();
                ImageLoader.loadFromFile(anh, imagePath, R.drawable.vest);

            } else {
                tensp.setText("Không có dữ liệu");
            }



        // Kiểm tra trạng thái đăng nhập và thêm sản phẩm vào giỏ hàng
        btnaddcart.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

            if (!isLoggedIn) {
                Intent loginIntent = new Intent(getApplicationContext(), Login_Activity.class);
                startActivity(loginIntent);
            } else {
                gioHangManager.addItem(chiTietSanPham); // Gọi phương thức addItem

                Toast.makeText(ChiTietSanPham_Activity.this, "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
            }
        });
        // Kiểm tra trạng thái đăng nhập và thêm sản phẩm vào giỏ hàng
        btndathang.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

            if (!isLoggedIn) {
                Intent loginIntent = new Intent(getApplicationContext(), Login_Activity.class);
                startActivity(loginIntent);
            } else {
                gioHangManager.addItem(chiTietSanPham); // Gọi phương thức addItem
                Intent intent1=new Intent(ChiTietSanPham_Activity.this,GioHang_Activity.class);
                startActivity(intent1);
            }
        });
        // Các nút điều hướng
        BottomBar_Helper.setupBottomBar(this);

    }


    private void displayStarRating(float rating, ImageView... stars) {
        for (int i = 0; i < stars.length; i++) {
            if (rating >= i + 1) {
                stars[i].setImageResource(R.drawable.star_full);
            } else if (rating > i && rating < i + 1) {
                stars[i].setImageResource(R.drawable.star_half);
            } else {
                stars[i].setImageResource(R.drawable.star_empty);
            }
        }
    }
    public void tinhTrungBinhSoSao(int masp, final Consumer<Float> callback) {
        String url = "http://10.0.2.2:3000/danhgia/trungbinh?masp=" + masp;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            float avg = (float) response.getDouble("avgRating");
                            callback.accept(avg);
                        } else {
                            callback.accept(0f); // không có đánh giá
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.accept(0f);
                    }
                },
                error -> {
                    error.printStackTrace();
                    callback.accept(0f);
                });

        Volley.newRequestQueue(this).add(request);
    }


}