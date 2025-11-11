package com.example.tuan17.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.GioHangManager;
import com.example.tuan17.Login_Activity;
import com.example.tuan17.R;
import com.example.tuan17.models.ChiTietSanPham;
import com.example.tuan17.util.ImageLoader;

import org.json.JSONException;

import java.io.File;
import java.util.function.Consumer;

public class ProductDetailFragment extends Fragment {
    private ChiTietSanPham chiTietSanPham;
    private GioHangManager gioHangManager;
    String masp;
    Button btndathang, btnaddcart;
    ImageView star1, star2, star3, star4, star5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_chi_tiet_san_pham, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvXemDanhGia = view.findViewById(R.id.tv_danhgia_title);
        btndathang = view.findViewById(R.id.btndathang);
        btnaddcart = view.findViewById(R.id.btnaddcart);
        star1 = view.findViewById(R.id.star1);
        star2 = view.findViewById(R.id.star2);
        star3 = view.findViewById(R.id.star3);
        star4 = view.findViewById(R.id.star4);
        star5 = view.findViewById(R.id.star5);

        TextView tensp = view.findViewById(R.id.tensp);
        ImageView anh = view.findViewById(R.id.imgsp);
        TextView dongia = view.findViewById(R.id.dongia);
        TextView mota = view.findViewById(R.id.mota);
        TextView ghichu = view.findViewById(R.id.ghichu);
        TextView soluongkho = view.findViewById(R.id.soluongkho);

        gioHangManager = GioHangManager.getInstance();

        Bundle args = getArguments();
        if (args != null) {
            masp = args.getString("masp");
            String tenspStr = args.getString("tensp");
            float dongiaFloat = args.getFloat("dongia");
            String motaStr = args.getString("mota");
            String ghichuStr = args.getString("ghichu");
            int soluongkhoInt = args.getInt("soluongkho");
            String maso = args.getString("maso");
//            byte[] anh = args.getByteArray("anh");
String imagePath = args.getString("picurl");

            chiTietSanPham = new ChiTietSanPham(masp, tenspStr, dongiaFloat, motaStr, ghichuStr, soluongkhoInt, maso, imagePath);

            tensp.setText(tenspStr);
            ghichu.setText(ghichuStr);
            dongia.setText(String.valueOf(dongiaFloat));
            mota.setText(motaStr != null ? motaStr : "Không có dữ liệu");
            soluongkho.setText(String.valueOf(soluongkhoInt));

            // Load ảnh từ file path
            ImageLoader.loadFromFile(anh, imagePath, R.drawable.vest);


            if (masp != null) {
                try {
                    int maSpInt = Integer.parseInt(masp);
                    tinhTrungBinhSoSao(maSpInt, avgRating -> {
                        displayStarRating(avgRating, star1, star2, star3, star4, star5);
                    });
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        tvXemDanhGia.setOnClickListener(v -> {
            if (masp != null && !masp.isEmpty()) {
                try {
                    int id = Integer.parseInt(masp);
                    Intent intent = new Intent(getActivity(), com.example.tuan17.DanhSachDanhGiaActivity.class);
                    intent.putExtra("masp", id);
                    startActivity(intent);
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "Mã sản phẩm không hợp lệ!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Không có mã sản phẩm!", Toast.LENGTH_SHORT).show();
            }
        });

        btnaddcart.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", getActivity().MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

            if (!isLoggedIn) {
                Intent loginIntent = new Intent(getActivity(), Login_Activity.class);
                startActivity(loginIntent);
            } else {
                gioHangManager.addItem(chiTietSanPham);
                Toast.makeText(getActivity(), "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
            }
        });

        btndathang.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", getActivity().MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

            if (!isLoggedIn) {
                Intent loginIntent = new Intent(getActivity(), Login_Activity.class);
                startActivity(loginIntent);
            } else {
                gioHangManager.addItem(chiTietSanPham);
                // Navigate to CartFragment
                CartFragment fragment = new CartFragment();
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                }
            }
        });
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
                            callback.accept(0f);
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

        Volley.newRequestQueue(getActivity()).add(request);
    }
}

