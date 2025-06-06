package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.tuan17.adapter.TaiKhoanAdapter;
import com.example.tuan17.database.Database;
import com.example.tuan17.helper.BottomBar_Admin_Helper;
import com.example.tuan17.models.TaiKhoan;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Taikhoan_admin_Activity extends AppCompatActivity {


    Database database;
    ListView lv;
    int vitri;
    ArrayList<TaiKhoan> mangTK;
    TaiKhoanAdapter adapter;
    FloatingActionButton dauconggocphai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taikhoan_admin);
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
        mangTK = new ArrayList<>();
        adapter = new TaiKhoanAdapter(getApplicationContext(), R.layout.ds_taikhoan, mangTK);
        lv.setAdapter(adapter);
        database = new Database(this, "banhang.db", null, 1);
        database.QueryData("CREATE TABLE IF NOT EXISTS taikhoan(tendn VARCHAR(20) PRIMARY KEY, matkhau VARCHAR(50), quyen VARCHAR(50))");
        // Thêm 2 dòng dữ liệu
//        database.QueryData("INSERT  INTO taikhoan VALUES ('admin', '1234', 'admin')");
//        database.QueryData("INSERT  INTO taikhoan VALUES ('bac2', '1111', 'user')");
//        database.QueryData("INSERT  INTO taikhoan VALUES ('bac3', '1111', 'user')");
//        database.QueryData("INSERT  INTO taikhoan VALUES ('bac4', '1111', 'bacsi')");
        Loaddulieutaikhoan();


    }
    private void Loaddulieutaikhoan() {
        Cursor dataCongViec = database.GetData("SELECT * FROM taikhoan");
        mangTK.clear();
        while (dataCongViec.moveToNext()) {
            String tdn = dataCongViec.getString(0);
            String mk= dataCongViec.getString(1);
            String q = dataCongViec.getString(2);
            mangTK.add(new TaiKhoan(tdn, mk, q));
        }
        adapter.notifyDataSetChanged();
    }
}