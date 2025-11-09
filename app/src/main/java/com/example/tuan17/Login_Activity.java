package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login_Activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = findViewById(R.id.btnLogin);
        EditText tdn = findViewById(R.id.tdn);
        EditText mk = findViewById(R.id.mk);
        TextView dangki = findViewById(R.id.dangki);
        TextView qmk = findViewById(R.id.qmk);
        qmk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DoiMatKhau_Activity.class);
                startActivity(intent);
            }
        });
        // Chuyển đến activity đăng ký tài khoản
        dangki.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Signup_Activity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            String username = tdn.getText().toString().trim(); // lấy tên đăng nhập
            String password = mk.getText().toString().trim(); // lấy mật khẩu

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(Login_Activity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "http://10.0.2.2:3000/taikhoan/login"; // Nếu test máy ảo // tạo url gọi /
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, // tạo string request/ tạo 1 yêu cầu HTTP post
                    response -> {
                // xử lý khi server phản hồi
                        try {
                            JSONObject json = new JSONObject(response); // chuyển response thành đối tượng JSONObject
                            Log.d("json" ,json.toString());
                            if (json.getBoolean("success")) {
                                // Lưu thông tin người dùng // xử lý response
                                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("token", json.getString("token"));
                                editor.putString("tendn", json.getString("username"));
                                editor.putInt("user_id", json.getInt("user_id"));
                                editor.putBoolean("isLoggedIn", true);
                                editor.apply();
//                                getUserInfo(json.getString("token"));


                                // Chuyển đến activity tương ứng
                                String quyen = json.getString("role");
                                Intent intent;
                                if (quyen.equals("admin")) {
                                    intent = new Intent(Login_Activity.this, TrangchuAdmin_Activity.class);
                                    Toast.makeText(this, "Đăng nhập Admin", Toast.LENGTH_SHORT).show();
                                } else if (quyen.equals("user")) {
                                    intent = new Intent(Login_Activity.this, TrangchuNgdung_Activity.class);
                                    Toast.makeText(this, "Đăng nhập User", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Quyền không xác định", Toast.LENGTH_SHORT).show();
                                    return;
                                }

//                                startAutoLogoutTimer();
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Login_Activity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Login_Activity.this, "Lỗi xử lý JSON", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(Login_Activity.this, "Không kết nối được server", Toast.LENGTH_SHORT).show();
                    }) {
// gửi dữ liệu leen server
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("password", password);
                    return params;
                }
            };
// thêm request vào hàng đợi
            Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
        });

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String token = prefs.getString("token", null);
        if (token != null) {
            checkTokenValidity(token);
            return; // Dừng xử lý tiếp
        }
    }

    private void checkTokenValidity(String token) {
        String url = "http://10.0.2.2:3000/taikhoan/userinfo";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONObject user = json.getJSONObject("user");
                            String role = user.getString("role");

                            Intent intent;
                            if (role.equals("admin")) {
                                intent = new Intent(Login_Activity.this, TrangchuAdmin_Activity.class);
                            } else {
                                intent = new Intent(Login_Activity.this, TrangchuNgdung_Activity.class);
                            }

                            startActivity(intent);
                            finish(); // kết thúc login
                        }
                    } catch (JSONException e) {
                        e.printStackTrace(); // Token sai định dạng
                    }
                },
                error -> {
                    Log.e("AutoLogin", "Token hết hạn hoặc không hợp lệ"); // Token sai/hết hạn
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

}