package com.example.tuan17;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.tuan17.fragments.HomeFragment;
import com.example.tuan17.helper.BottomBar_Helper;

public class UserMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);
        
        // Load mặc định: Trang chủ
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
        
        BottomBar_Helper.setupBottomBar(this);
    }
}

