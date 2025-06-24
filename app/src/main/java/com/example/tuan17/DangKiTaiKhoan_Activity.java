package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.adapter.TaiKhoanAdapter;
import com.example.tuan17.database.Database;
import com.example.tuan17.database.TaiKhoanDB;
import com.example.tuan17.models.TaiKhoan;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DangKiTaiKhoan_Activity extends AppCompatActivity {

//    Database database;

//    TaiKhoanDB taiKhoanDB;

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
                Intent a=new Intent(DangKiTaiKhoan_Activity.this,Login_Activity.class);
                startActivity(a);
            }
        });
        ArrayList<String> ar = new ArrayList<>();
        ar.add("user");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, ar);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spn = ar.get(i);
            }


            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        mangTK = new ArrayList<>();
        adapter = new TaiKhoanAdapter(getApplicationContext(), R.layout.ds_taikhoan, mangTK);

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = tendn.getText().toString().trim();
                String password = matkhau.getText().toString().trim();
                String nhaplaimk = nhaplaimatkhau.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty() || nhaplaimk.isEmpty()) {
                    Toast.makeText(DangKiTaiKhoan_Activity.this, "Không được để trống", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(nhaplaimk)) {
                    Toast.makeText(DangKiTaiKhoan_Activity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Gửi request đến Node.js
                String url = "http://10.0.2.2:3000/taikhoan/register"; // dùng IP máy nếu chạy thiết bị thật

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        response -> {
                            try {
                                JSONObject json = new JSONObject(response);
                                if (json.getBoolean("success")) {
                                    Toast.makeText(DangKiTaiKhoan_Activity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), Login_Activity.class));
                                } else {
                                    Toast.makeText(DangKiTaiKhoan_Activity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {
                            error.printStackTrace();
                            Toast.makeText(DangKiTaiKhoan_Activity.this, "Lỗi kết nối đến server", Toast.LENGTH_SHORT).show();
                        }) {
                    // gửi dữ liệu
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", username);
                        params.put("password", password);
                        params.put("role", spn); // "user"
                        return params;
                    }
                };

                Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
            }
        });

//        btnadd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String username = tendn.getText().toString().trim();
//                String password = matkhau.getText().toString().trim();
//                String nhaplaimk = nhaplaimatkhau.getText().toString().trim();
//
//                // Kiểm tra xem tên đăng nhập và mật khẩu có rỗng không
//                if (username.isEmpty() || password.isEmpty() || nhaplaimk.isEmpty()) {
//                    Toast.makeText(DangKiTaiKhoan_Activity.this, "Tên đăng nhập và mật khẩu không được để trống!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                // Kiểm tra xem mật khẩu và mật khẩu xác nhận có trùng nhau không
//                if (!password.equals(nhaplaimk)) {
//                    Toast.makeText(DangKiTaiKhoan_Activity.this, "Mật khẩu không khớp, vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                // Kiểm tra xem username có tồn tại trong cơ sở dữ liệu không
//                Cursor cursor = taiKhoanDB.GetData("SELECT * FROM taikhoan WHERE tendn = '" + username + "'");
//                if (cursor.getCount() > 0) {
//                    Toast.makeText(DangKiTaiKhoan_Activity.this, "Tên đăng nhập đã tồn tại, vui lòng chọn tên khác!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//              boolean success =   taiKhoanDB.dangky(username,password);
//                // Thêm tài khoản vào cơ sở dữ liệu
////                database.QueryData("INSERT INTO taikhoan VALUES('" + username + "', '" + password + "', '" + spn + "')");
//                if(success){
//                    Toast.makeText(DangKiTaiKhoan_Activity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(getApplicationContext(), Login_Activity.class);
//                    startActivity(intent);
//                }
//                else {
//                    Toast.makeText(DangKiTaiKhoan_Activity.this, "Đăng ký không thành công", Toast.LENGTH_SHORT).show();
//                }
////                Toast.makeText(DangKiTaiKhoan_Activity.this, "Đăng kí tài khoản thành công", Toast.LENGTH_LONG).show();
////                // Chuyển đến Activity thứ hai
////                Intent intent = new Intent(getApplicationContext(), Login_Activity.class);
////                startActivity(intent);
//            }
//        });
    }
}