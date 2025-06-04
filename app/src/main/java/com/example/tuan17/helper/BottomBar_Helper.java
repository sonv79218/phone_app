package com.example.tuan17.helper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ImageButton;

import com.example.tuan17.DonHang_admin_Activity;
import com.example.tuan17.GioHang_Activity;
import com.example.tuan17.Login_Activity;
import com.example.tuan17.R;
import com.example.tuan17.TimKiemSanPham_Activity;
import com.example.tuan17.TrangCaNhan_admin_Activity;
import com.example.tuan17.TrangchuNgdung_Activity;

public class BottomBar_Helper {
    public static void setupBottomBar(Activity activity) {
        ImageButton btnTrangChu = activity.findViewById(R.id.btntrangchu);
        ImageButton btnDonHang = activity.findViewById(R.id.btndonhang);
        ImageButton btnGioHang = activity.findViewById(R.id.btncart);
        ImageButton btnCaNhan = activity.findViewById(R.id.btncanhan);
        ImageButton btnTimKiem = activity.findViewById(R.id.btntimkiem); // nếu có

        btnTrangChu.setOnClickListener(view -> {
            Intent intent = new Intent(activity, TrangchuNgdung_Activity.class);
            activity.startActivity(intent);
        });

        btnDonHang.setOnClickListener(view -> {
            Intent intent = new Intent(activity, DonHang_admin_Activity.class);
            activity.startActivity(intent);
        });

        btnGioHang.setOnClickListener(view -> {
            Intent intent = new Intent(activity, GioHang_Activity.class);
            activity.startActivity(intent);
        });

        btnCaNhan.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = activity.getSharedPreferences("MyPrefs", activity.MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

            Intent intent = isLoggedIn
                    ? new Intent(activity, TrangCaNhan_admin_Activity.class)
                    : new Intent(activity, Login_Activity.class);

            activity.startActivity(intent);
        });

        if (btnTimKiem != null) {
            btnTimKiem.setOnClickListener(view -> {
                Intent intent = new Intent(activity, TimKiemSanPham_Activity.class);
                activity.startActivity(intent);
            });
        }
    }
}
