package com.example.tuan17.helper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageButton;

import com.example.tuan17.DonHang_admin_Activity;
import com.example.tuan17.Login_Activity;
import com.example.tuan17.Nhomsanpham_admin_Actvity;
import com.example.tuan17.R;
import com.example.tuan17.Sanpham_admin_Activity;
import com.example.tuan17.Taikhoan_admin_Activity;
import com.example.tuan17.TrangCaNhan_admin_Activity;
import com.example.tuan17.TrangchuAdmin_Activity;

public class BottomBar_Admin_Helper {

    public static void setupBottomBar(Activity activity) {
        ImageButton btntrangchu = activity.findViewById(R.id.btntrangchu);
        btntrangchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(activity, TrangchuAdmin_Activity.class);
                activity.startActivity(a);
            }
        });

        ImageButton btncanhan = activity.findViewById(R.id.btncanhan);
        btncanhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = activity.getSharedPreferences("MyPrefs", activity.MODE_PRIVATE);
                boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

                Intent intent = isLoggedIn
                        ? new Intent(activity, TrangCaNhan_admin_Activity.class)
                        : new Intent(activity, Login_Activity.class);

                activity.startActivity(intent);
            }
        });

        ImageButton btndonhang = activity.findViewById(R.id.btndonhang);
        btndonhang.setOnClickListener(view -> {
            Intent a = new Intent(activity, DonHang_admin_Activity.class);
            activity.startActivity(a);
        });

        ImageButton btnsanpham = activity.findViewById(R.id.btnsanpham);
        btnsanpham.setOnClickListener(view -> {
            Intent a = new Intent(activity, Sanpham_admin_Activity.class);
            activity.startActivity(a);
        });

        ImageButton btnnhomsp = activity.findViewById(R.id.btnnhomsp);
        btnnhomsp.setOnClickListener(view -> {
            Intent a = new Intent(activity, Nhomsanpham_admin_Actvity.class);
            activity.startActivity(a);
        });

        ImageButton btntaikhoan = activity.findViewById(R.id.btntaikhoan);
        btntaikhoan.setOnClickListener(view -> {
            Intent a = new Intent(activity, Taikhoan_admin_Activity.class);
            activity.startActivity(a);
        });
    }
}
