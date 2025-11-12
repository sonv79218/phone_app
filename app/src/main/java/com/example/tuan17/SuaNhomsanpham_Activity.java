package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.tuan17.adapter.NhomSanPhamAdapter;
//import com.example.tuan17.database.Database;
import com.example.tuan17.models.NhomSanPham;

import java.util.ArrayList;

public class SuaNhomsanpham_Activity extends AppCompatActivity {
//    Database database;
    ArrayList<NhomSanPham> mangNSP;
    NhomSanPhamAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua_nhomsanpham);
        mangNSP = new ArrayList<>();
        adapter = new NhomSanPhamAdapter(SuaNhomsanpham_Activity.this, mangNSP, true);

    }
}