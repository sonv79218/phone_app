package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.tuan17.adapter.TaiKhoanAdapter;
import com.example.tuan17.database.Database;
import com.example.tuan17.database.TaiKhoanDB;
import com.example.tuan17.models.TaiKhoan;

import java.util.ArrayList;

public class ThemTaiKhoan_Activity extends AppCompatActivity {
//    Database database;

    TaiKhoanDB taiKhoanDB;


    ArrayList<TaiKhoan> mangTK;
    TaiKhoanAdapter adapter;

    RadioButton admin;
    RadioButton user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_tai_khoan);
        taiKhoanDB = new TaiKhoanDB(this);
        Button btnadd = findViewById(R.id.btnadd);
        EditText tendn = findViewById(R.id.tdn);
        EditText matkhau = findViewById(R.id.mk);
        admin= findViewById(R.id.admin);
        user= findViewById(R.id.user);
        mangTK = new ArrayList<>();
        adapter = new TaiKhoanAdapter(getApplicationContext(), R.layout.ds_taikhoan, mangTK);
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = tendn.getText().toString();
                String password = matkhau.getText().toString();
                String quyen = "";
                if(admin.isChecked()){
                    quyen="admin";
                }
                if (user.isChecked()) {
                    quyen = "user";}


                boolean success = taiKhoanDB.addTaiKhoan(username, password, quyen);
                if (success) {
                    Toast.makeText(ThemTaiKhoan_Activity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ThemTaiKhoan_Activity.this, "Lỗi khi thêm tài khoản", Toast.LENGTH_SHORT).show();
                }
//                taiKhoanDB.addTaiKhoan(username,password,quyen);
//                // Thêm tài khoản vào cơ sở dữ liệu
//                Toast.makeText(ThemTaiKhoan_Activity.this, "Thêm dữ liệu thành công", Toast.LENGTH_SHORT).show();
//                // Chuyển đến Activity thứ hai
                Intent intent = new Intent(ThemTaiKhoan_Activity.this, Taikhoan_admin_Activity.class);
                startActivity(intent);
            }
        });

    }
}