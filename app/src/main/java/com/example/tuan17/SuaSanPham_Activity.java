package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.tuan17.adapter.SanPhamAdapter;
import com.example.tuan17.models.SanPham;

import java.util.ArrayList;

public class SuaSanPham_Activity extends AppCompatActivity {



    ArrayList<SanPham> mangBS;
    SanPhamAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua_san_pham);
        mangBS = new ArrayList<>();

        adapter = new SanPhamAdapter(this, mangBS, true);

    }
}