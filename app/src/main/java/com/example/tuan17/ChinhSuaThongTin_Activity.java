package com.example.tuan17;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChinhSuaThongTin_Activity extends AppCompatActivity {

    EditText edtHoten, edtEmail, edtSdt, edtDiachi;
    Button btnLuu;
    int userId;
    String urlGet, urlUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chinh_sua_thong_tin);

        edtHoten = findViewById(R.id.edtHoTen);
        edtEmail = findViewById(R.id.edtEmail);
        edtSdt = findViewById(R.id.edtSdt);
        edtDiachi = findViewById(R.id.edtDiaChi);
        btnLuu = findViewById(R.id.btnLuuThongTin);

        SharedPreferences sp = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sp.getInt("user_id", -1);
        urlGet = "http://10.0.2.2:3000/taikhoan/thongtin?id=" + userId;
        urlUpdate = "http://10.0.2.2:3000/taikhoan/capnhat";

        // Load dữ liệu hiện tại
        loadUserInfo();

        btnLuu.setOnClickListener(v -> {
            String hoten = edtHoten.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String sdt = edtSdt.getText().toString().trim();
            String diachi = edtDiachi.getText().toString().trim();

            capNhatThongTin(hoten, email, sdt, diachi);
        });
    }

    private void loadUserInfo() {
        StringRequest request = new StringRequest(Request.Method.GET, urlGet,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONObject user = json.getJSONObject("user");

                            edtHoten.setText(user.optString("hoten", ""));
                            edtEmail.setText(user.optString("email", ""));
                            edtSdt.setText(user.optString("sdt", ""));
                            edtDiachi.setText(user.optString("diachi", ""));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi khi lấy thông tin", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void capNhatThongTin(String hoten, String email, String sdt, String diachi) {
        StringRequest request = new StringRequest(Request.Method.POST, urlUpdate,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, TrangCaNhan_admin_Activity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> m = new HashMap<>();
                m.put("id", String.valueOf(userId));
                m.put("hoten", hoten);
                m.put("email", email);
                m.put("sdt", sdt);
                m.put("diachi", diachi);
                return m;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
