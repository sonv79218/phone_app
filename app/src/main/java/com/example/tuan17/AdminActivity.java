package com.example.tuan17;  // đổi theo package của bạn

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.tuan17.helper.BottomBar_Admin_Helper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        // Load mặc định: Trang chủ
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragment_container, new AdminHomeFragment())
                .commit();
        BottomBar_Admin_Helper.setupBottomBar(this);

            // Hiệu ứng chuyển nhẹ
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.admin_fragment_container, selectedFragment)
                    .commit();

            return true;
        });
    }
}
