package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.adapter.TaiKhoanAdapter;
import com.example.tuan17.models.TaiKhoan;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class DoiMatKhau_Activity extends AppCompatActivity {

    ArrayList<TaiKhoan> mangTK;
    TaiKhoanAdapter adapter;
    String spn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doi_mat_khau);

        Button btndoimk = findViewById(R.id.btnDoi);
        EditText tendn = findViewById(R.id.tdn);
        EditText matkhau = findViewById(R.id.mk);
        EditText nhaplaimatkhau = findViewById(R.id.mk2);
        Spinner spinner = findViewById(R.id.quyen);
        TextView ql = findViewById(R.id.ql);

        // Chuyển về Login
        ql.setOnClickListener(v -> {
            Intent a = new Intent(DoiMatKhau_Activity.this, Login_Activity.class);
            startActivity(a);
        });

        // Chỉ có 1 quyền "user"
        ArrayList<String> ar = new ArrayList<>();
        ar.add("user");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, ar);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spn = ar.get(i);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        // Danh sách tài khoản (nếu cần)
        mangTK = new ArrayList<>();
        adapter = new TaiKhoanAdapter(getApplicationContext(), R.layout.ds_taikhoan, mangTK);

        // Xử lý đổi mật khẩu
        btndoimk.setOnClickListener(view -> {
            String username = tendn.getText().toString().trim();
            String password = matkhau.getText().toString().trim();
            String nhaplaimk = nhaplaimatkhau.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || nhaplaimk.isEmpty()) {
                Toast.makeText(DoiMatKhau_Activity.this, "Không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(nhaplaimk)) {
                Toast.makeText(DoiMatKhau_Activity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi API đổi mật khẩu
            String url = "http://10.0.2.2:3000/taikhoan/doimatkhau"; // Thay đổi IP nếu cần

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getBoolean("success")) {
                                Toast.makeText(DoiMatKhau_Activity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Login_Activity.class));
                            } else {
                                Toast.makeText(DoiMatKhau_Activity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DoiMatKhau_Activity.this, "Lỗi xử lý JSON", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(DoiMatKhau_Activity.this, "Không kết nối được server", Toast.LENGTH_SHORT).show();
                    }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("password", password);
                    return params;
                }
            };

            Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
        });
    }
}
