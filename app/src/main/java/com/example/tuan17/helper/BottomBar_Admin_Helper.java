package com.example.tuan17.helper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageButton;

import com.example.tuan17.AdminActivity;
import com.example.tuan17.Login_Activity;
import com.example.tuan17.Nhomsanpham_admin_Actvity;
import com.example.tuan17.R;
import com.example.tuan17.Sanpham_admin_Activity;
import com.example.tuan17.Taikhoan_admin_Activity;
import com.example.tuan17.fragments.AdminCategoryManagementFragment;
import com.example.tuan17.fragments.AdminHomeFragment;
import com.example.tuan17.fragments.AdminOrderFragment;
import com.example.tuan17.fragments.AdminProductManagementFragment;
import com.example.tuan17.fragments.AdminProfileFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class BottomBar_Admin_Helper {

    public static void setupBottomBar(Activity activity) {
        ImageButton btntrangchu = activity.findViewById(R.id.btntrangchu);
        ImageButton btncanhan = activity.findViewById(R.id.btncanhan);
        ImageButton btndonhang = activity.findViewById(R.id.btndonhang);
        ImageButton btnsanpham = activity.findViewById(R.id.btnsanpham);
        ImageButton btnnhomsp = activity.findViewById(R.id.btnnhomsp);
        ImageButton btntaikhoan = activity.findViewById(R.id.btntaikhoan);

        if (activity instanceof AdminActivity) {
            FragmentManager fragmentManager = ((AdminActivity) activity).getSupportFragmentManager();

            btntrangchu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new AdminHomeFragment();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                            .replace(R.id.admin_fragment_container, fragment)
                            .commit();
                }
            });

            btncanhan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences sharedPreferences = activity.getSharedPreferences("MyPrefs", activity.MODE_PRIVATE);
                    boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

                    if (isLoggedIn) {
                        Fragment fragment = new AdminProfileFragment();
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                .replace(R.id.admin_fragment_container, fragment)
                                .commit();
                    } else {
                        Intent intent = new Intent(activity, Login_Activity.class);
                        activity.startActivity(intent);
                    }
                }
            });

            btndonhang.setOnClickListener(view -> {
                Fragment fragment = new AdminOrderFragment();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.admin_fragment_container, fragment)
                        .commit();
            });

            btnsanpham.setOnClickListener(view -> {
                Fragment fragment = new AdminProductManagementFragment();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.admin_fragment_container, fragment)
                        .commit();
            });

            btnnhomsp.setOnClickListener(view -> {
                Fragment fragment = new AdminCategoryManagementFragment();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.admin_fragment_container, fragment)
                        .commit();
            });

            btntaikhoan.setOnClickListener(view -> {
                Fragment fragment = new AdminProfileFragment();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.admin_fragment_container, fragment)
                        .commit();
            });
        }
    }
}
