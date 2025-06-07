package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tuan17.adapter.SanPhamAdapter;
import com.example.tuan17.database.Database;
import com.example.tuan17.database.SanPhamDB;
import com.example.tuan17.helper.BottomBar_Admin_Helper;
import com.example.tuan17.models.SanPham;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Sanpham_admin_Activity extends AppCompatActivity {

    private ListView lv;
    private FloatingActionButton addButton;
    private ArrayList<SanPham> mangSP;
    private SanPhamAdapter adapter;

    private SanPhamDB sanPhamDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sanpham_admin);
        sanPhamDB = new SanPhamDB(this);

        lv = findViewById(R.id.listtk);
        addButton = findViewById(R.id.btnthem);

        mangSP = new ArrayList<>();
        adapter = new SanPhamAdapter(Sanpham_admin_Activity.this, mangSP, true);
        lv.setAdapter(adapter);
        loadData();
        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ThemSanPham_Activity.class);
            startActivity(intent);
        });
        BottomBar_Admin_Helper.setupBottomBar(this);
    }


    private void loadData() {
        mangSP.clear();
        mangSP.addAll(sanPhamDB.getAllSanPham());
        adapter.notifyDataSetChanged();
    }
    private byte[] convertBitmapToByteArray(int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}