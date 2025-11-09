package com.example.tuan17;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.helper.BottomBar_Admin_Helper;
import com.example.tuan17.helper.BottomBar_Helper;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TrangCaNhan_admin_Activity extends AppCompatActivity {
    String tendn;
    Integer id;
    ActivityResultLauncher<Intent> chinhSuaLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_ca_nhan_admin);
        TextView tvHoten = findViewById(R.id.tvHoTen);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvSdt = findViewById(R.id.tvSdt);
        TextView tvDiachi = findViewById(R.id.tvDiaChi);
        TextView tvQuyen = findViewById(R.id.tvQuyen);
        TextView tvNgaytao = findViewById(R.id.tvNgayTao);
        Button dangxuat = findViewById(R.id.btndangxuat);
        TextView textTendn = findViewById(R.id.tendn); // TextView hiển thị tên đăng nhập
        Button doimk = findViewById(R.id.btnDoiMatKhau);
        Button suatt = findViewById(R.id.btnChinhSua);
        // Lấy giá trị tendn từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        tendn = sharedPreferences.getString("tendn", null);
        id = sharedPreferences.getInt("user_id",-1);
        // Nếu SharedPreferences không có, lấy từ Intent
        if (tendn == null) {
            tendn = getIntent().getStringExtra("tendn");
        }

        // Kiểm tra giá trị tendn
        if (tendn != null) {
            textTendn.setText(tendn);
        } else {
            // Chưa đăng nhập, chuyển đến trang login
            Intent intent = new Intent(TrangCaNhan_admin_Activity.this, Login_Activity.class);
            startActivity(intent);
            finish(); // Kết thúc activity nếu chưa đăng nhập
            return;
        }

        String url = "http://10.0.2.2:3000/taikhoan/thongtin?id=" + id;

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);//pasrse chuỗi response
                        if (json.getBoolean("success")) {
                            JSONObject user = json.getJSONObject("user");
                            tvHoten.setText(getSafe(user, "hoten"));
                            tvEmail.setText(getSafe(user, "email"));
                            tvSdt.setText(getSafe(user, "sdt"));
                            tvDiachi.setText(getSafe(user, "diachi"));
                            tvQuyen.setText(getSafe(user, "quyen"));
                            tvNgaytao.setText(formatDate(user.optString("ngaytao")));


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }},
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Lỗi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
        );
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);


        BottomBar_Admin_Helper.setupBottomBar(this);

        dangxuat.setOnClickListener(v -> {
            new AlertDialog.Builder(TrangCaNhan_admin_Activity.this)
                    .setTitle("Đăng Xuất")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        // Xóa trạng thái đăng nhập
                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putBoolean("isLoggedIn", false);
//                        editor.putString("tendn", null);
                        editor.clear();
                        editor.apply();

                        // Quay lại Activity chính
                        Intent intent = new Intent(getApplicationContext(), Login_Activity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
//                        finish(); // Kết thúc activity
                        finishAffinity();
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });
        doimk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DoiMatKhau_Activity.class);
                startActivity(intent);
            }
        });

        suatt.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChinhSuaThongTin_Activity.class);
            startActivity(intent);
        });
    }



    private String getSafe(JSONObject obj, String key) {
        try {
            String value = obj.optString(key, "").trim();
            if (value.equals("null") || value.isEmpty()) {
                return "Không có thông tin";
            }
            return value;
        } catch (Exception e) {
            return "Không có thông tin";
        }
    }

    private String formatDate(String isoDate) {
        try {
            if (isoDate == null || isoDate.equals("null") || isoDate.isEmpty()) return "Không có thông tin";

            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = isoFormat.parse(isoDate);
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return displayFormat.format(date);
        } catch (Exception e) {
            return "Không có thông tin";
        }
    }


}
