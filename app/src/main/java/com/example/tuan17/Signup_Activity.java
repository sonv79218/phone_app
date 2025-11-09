package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.adapter.TaiKhoanAdapter;
import com.example.tuan17.models.TaiKhoan;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Signup_Activity extends AppCompatActivity {


    ArrayList<TaiKhoan> mangTK;
    TaiKhoanAdapter adapter;

    String spn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ki_tai_khoan);
//        taiKhoanDB = new TaiKhoanDB(this);
        Button btnadd = findViewById(R.id.btnDangki);
        EditText tendn = findViewById(R.id.tdn);
        EditText matkhau = findViewById(R.id.mk);
        EditText nhaplaimatkhau = findViewById(R.id.nhaplaimk);
        Spinner spinner = findViewById(R.id.quyen);
        TextView ql=findViewById(R.id.ql);
        ql.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a=new Intent(Signup_Activity.this,Login_Activity.class);
                startActivity(a);
            }
        });

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = tendn.getText().toString().trim();
                String password = matkhau.getText().toString().trim();
                String nhaplaimk = nhaplaimatkhau.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty() || nhaplaimk.isEmpty()) {
                    Toast.makeText(Signup_Activity.this, "Không được để trống", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(nhaplaimk)) {
                    Toast.makeText(Signup_Activity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Gửi request đến Node.js
                String url = "http://10.0.2.2:3000/taikhoan/register"; // dùng IP máy nếu chạy thiết bị thật

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        response -> {
                            try {
                                JSONObject json = new JSONObject(response);
                                if (json.getBoolean("success")) {
                                    Toast.makeText(Signup_Activity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), Login_Activity.class));
                                } else {
                                    Toast.makeText(Signup_Activity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {
                            error.printStackTrace();
                            Toast.makeText(Signup_Activity.this, "Lỗi kết nối đến server", Toast.LENGTH_SHORT).show();
                        }) {
                    // gửi dữ liệu
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", username);
                        params.put("password", password);
                        return params;
                    }
                };

                Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
            }
        });


    }
}