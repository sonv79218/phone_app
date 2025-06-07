package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.tuan17.adapter.TaiKhoanAdapter;
import com.example.tuan17.database.Database;
import com.example.tuan17.database.TaiKhoanDB;
import com.example.tuan17.helper.BottomBar_Admin_Helper;
import com.example.tuan17.models.TaiKhoan;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class Taikhoan_admin_Activity extends AppCompatActivity {


    TaiKhoanDB taiKhoanDB;
    ListView lv;
    int vitri;
    List<TaiKhoan> mangTK;
    TaiKhoanAdapter adapter;

    FloatingActionButton dauconggocphai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taikhoan_admin);
        taiKhoanDB = new TaiKhoanDB(this);
        dauconggocphai = findViewById(R.id.btnthem);
        lv = findViewById(R.id.listtk);
        BottomBar_Admin_Helper.setupBottomBar(this);

        dauconggocphai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a=new Intent(getApplicationContext(),ThemTaiKhoan_Activity.class);
                startActivity(a);
            }
        });

        mangTK = taiKhoanDB.getAllTaiKhoan();
        adapter = new TaiKhoanAdapter(getApplicationContext(), R.layout.ds_taikhoan, mangTK);
        lv.setAdapter(adapter);
    }

}