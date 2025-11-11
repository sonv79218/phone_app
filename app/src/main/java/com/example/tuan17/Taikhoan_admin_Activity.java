package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.adapter.TaiKhoanAdapter;
//import com.example.tuan17.database.TaiKhoanDB;
import com.example.tuan17.helper.BottomBar_Admin_Helper;
import com.example.tuan17.models.TaiKhoan;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Taikhoan_admin_Activity extends AppCompatActivity {

//    TaiKhoanDB taiKhoanDB;
    ListView lv;
    List<TaiKhoan> mangTK;
    TaiKhoanAdapter adapter;
    FloatingActionButton dauconggocphai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taikhoan_admin);

//        taiKhoanDB = new TaiKhoanDB(this);
        lv = findViewById(R.id.productGroupList);
        dauconggocphai = findViewById(R.id.addProductGroupButton);
        BottomBar_Admin_Helper.setupBottomBar(this);

        mangTK = new ArrayList<>();
        adapter = new TaiKhoanAdapter(this, R.layout.ds_taikhoan, mangTK);
        lv.setAdapter(adapter);

        dauconggocphai.setOnClickListener(view -> {
            Intent a = new Intent(getApplicationContext(), ThemTaiKhoan_Activity.class);
            startActivity(a);
        });

        loadTaiKhoanFromAPI();
    }

    private void loadTaiKhoanFromAPI() {
        String url = "http://10.0.2.2:3000/taikhoan/all";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray dataArray = response.getJSONArray("data");
                        mangTK.clear();

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject obj = dataArray.getJSONObject(i);

                            int id = obj.getInt("id");
                            String tendn = obj.getString("tendn");
                            String matkhau = obj.getString("matkhau");
                            String email = obj.optString("email", "");
                            String sdt = obj.optString("sdt", "");
                            String hoten = obj.optString("hoten", "");
                            String diachi = obj.optString("diachi", "");
                            String quyen = obj.getString("quyen");
                            String ngaytao = obj.optString("ngaytao", "");
                            int trangthai = obj.getInt("trangthai");

                            TaiKhoan tk = new TaiKhoan(id, tendn, matkhau, email, sdt, hoten, diachi, quyen, ngaytao, trangthai);
                            mangTK.add(tk);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi định dạng dữ liệu!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Lỗi kết nối API!", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }
}
