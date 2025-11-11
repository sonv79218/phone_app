package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.adapter.NhomSanPhamAdapter;
//import com.example.tuan17.database.NhomSanPhamDB;
import com.example.tuan17.helper.BottomBar_Admin_Helper;
import com.example.tuan17.models.NhomSanPham;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Nhomsanpham_admin_Actvity extends AppCompatActivity {
    private ListView lv;
    private FloatingActionButton addButton;
    private ArrayList<NhomSanPham> mangNSP;
    private NhomSanPhamAdapter adapter;
    String url = "http://10.0.2.2:3000/nhomsanpham";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhomsanpham_admin_actvity);
        lv = findViewById(R.id.productGroupList);
        addButton = findViewById(R.id.addProductGroupButton);
        mangNSP = new ArrayList<>();
        adapter = new NhomSanPhamAdapter(Nhomsanpham_admin_Actvity.this, mangNSP, true);
        lv.setAdapter(adapter);
        loadData();
        BottomBar_Admin_Helper.setupBottomBar(this);
        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ThemNhomSanPham_Activity.class);
            startActivity(intent);
        });
    }
    private void loadData() {
        String url = "http://10.0.2.2:3000/nhomsanpham/all";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        mangNSP.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String maso = obj.optString("maso", "");
                            String tennsp = obj.optString("tennsp", "");
                            // Xử lý picurl có thể null
                            String picurl = obj.optString("picurl", null);
                            if (picurl != null && (picurl.equals("null") || picurl.isEmpty())) {
                                picurl = null;
                            }
                            NhomSanPham nsp = new NhomSanPham(maso, tennsp, picurl);
                            mangNSP.add(nsp);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Lỗi kết nối API", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                });
        Volley.newRequestQueue(this).add(request);

    }
}