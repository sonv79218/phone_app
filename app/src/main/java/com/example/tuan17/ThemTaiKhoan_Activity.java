package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.adapter.TaiKhoanAdapter;
import com.example.tuan17.database.Database;
//import com.example.tuan17.database.TaiKhoanDB;
import com.example.tuan17.models.TaiKhoan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ThemTaiKhoan_Activity extends AppCompatActivity {
//    Database database;

//    TaiKhoanDB taiKhoanDB;


    ArrayList<TaiKhoan> mangTK;
    TaiKhoanAdapter adapter;

    RadioButton admin;
    RadioButton user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_tai_khoan);
//        taiKhoanDB = new TaiKhoanDB(this);
        Button btnadd = findViewById(R.id.btnadd);

        admin= findViewById(R.id.admin);
        user= findViewById(R.id.user);
        mangTK = new ArrayList<>();
        adapter = new TaiKhoanAdapter(getApplicationContext(), R.layout.ds_taikhoan, mangTK);
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNguoiDung();
//                String username = tendn.getText().toString();
//                String password = matkhau.getText().toString();
//                String quyen = "";
//                if(admin.isChecked()){
//                    quyen="admin";
//                }
//                if (user.isChecked()) {
//                    quyen = "user";}


//                boolean success = taiKhoanDB.addTaiKhoan(username, password, quyen);
//                if (success) {
//                    Toast.makeText(ThemTaiKhoan_Activity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(ThemTaiKhoan_Activity.this, "Lỗi khi thêm tài khoản", Toast.LENGTH_SHORT).show();
//                }
//                taiKhoanDB.addTaiKhoan(username,password,quyen);
//                // Thêm tài khoản vào cơ sở dữ liệu
//                Toast.makeText(ThemTaiKhoan_Activity.this, "Thêm dữ liệu thành công", Toast.LENGTH_SHORT).show();
//                // Chuyển đến Activity thứ hai
//                Intent intent = new Intent(ThemTaiKhoan_Activity.this, Taikhoan_admin_Activity.class);
//                startActivity(intent);
            }
        });

    }
    private void addNguoiDung() {
        EditText tendn = findViewById(R.id.tdn);
        EditText matkhau = findViewById(R.id.mk);

        String usernameStr = tendn.getText().toString().trim();
        String passwordStr = matkhau.getText().toString().trim();
        String quyenStr = admin.isChecked() ? "admin" : user.isChecked() ? "user" : "";

        // Kiểm tra dữ liệu
        if (usernameStr.isEmpty() || passwordStr.isEmpty() || quyenStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:3000/taikhoan";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Thêm người dùng thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, Taikhoan_admin_Activity.class));
                    finish();
                },
                error -> {
                    Toast.makeText(this, "Lỗi khi thêm tài khoản!", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("tendn", usernameStr);
                data.put("matkhau", passwordStr);
                data.put("quyen", quyenStr);
                return data;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

}