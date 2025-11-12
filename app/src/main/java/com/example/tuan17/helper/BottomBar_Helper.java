package com.example.tuan17.helper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ImageButton;

import com.example.tuan17.Login_Activity;
import com.example.tuan17.R;
import com.example.tuan17.fragments.SearchFragment;
import com.example.tuan17.UserMainActivity;
import com.example.tuan17.fragments.CartFragment;
import com.example.tuan17.fragments.HomeFragment;
import com.example.tuan17.fragments.OrderFragment;
import com.example.tuan17.fragments.ProfileFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class BottomBar_Helper {
    public static void setupBottomBar(Activity activity) {
        ImageButton btnTrangChu = activity.findViewById(R.id.btntrangchu);
        ImageButton btnDonHang = activity.findViewById(R.id.btndonhang);
        ImageButton btnGioHang = activity.findViewById(R.id.btncart);
        ImageButton btnCaNhan = activity.findViewById(R.id.btncanhan);
        ImageButton btnTimKiem = activity.findViewById(R.id.btntimkiem);

        if (activity instanceof UserMainActivity) {
            FragmentManager fragmentManager = ((UserMainActivity) activity).getSupportFragmentManager();

            btnTrangChu.setOnClickListener(view -> {
                Fragment fragment = new HomeFragment();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right
                        )
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            });

            btnDonHang.setOnClickListener(view -> {
                Fragment fragment = new OrderFragment();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right
                        )
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            });

            btnGioHang.setOnClickListener(view -> {
                Fragment fragment = new CartFragment();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right
                        )
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            });

            btnCaNhan.setOnClickListener(view -> {
                SharedPreferences sharedPreferences = activity.getSharedPreferences("MyPrefs", activity.MODE_PRIVATE);
                boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

                if (isLoggedIn) {
                    Fragment fragment = new ProfileFragment();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_left,
                                    R.anim.slide_in_left,
                                    R.anim.slide_out_right
                            )
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                } else {
                    Intent intent = new Intent(activity, Login_Activity.class);
                    activity.startActivity(intent);
                }
            });

            if (btnTimKiem != null) {
                btnTimKiem.setOnClickListener(view -> {
                    Fragment fragment = new SearchFragment();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_left,
                                    R.anim.slide_in_left,
                                    R.anim.slide_out_right
                            )
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                });
            }
        }
    }
}
