package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.adapter.SanPhamAdapter;

import com.example.tuan17.helper.BottomBar_Admin_Helper;
import com.example.tuan17.models.SanPham;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Sanpham_admin_Activity extends AppCompatActivity {

    private ListView lv;
    private FloatingActionButton addButton;
    private ArrayList<SanPham> mangSP;
    private SanPhamAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sanpham_admin);
        lv = findViewById(R.id.productGroupList);
        addButton = findViewById(R.id.addProductGroupButton);
        mangSP = new ArrayList<>();
        adapter = new SanPhamAdapter(Sanpham_admin_Activity.this, mangSP, true);
        lv.setAdapter(adapter);
        loadData();
        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ThemSanPham_Activity.class);
            startActivity(intent);
        });
        BottomBar_Admin_Helper.setupBottomBar(this);
    }
private void loadData() {
    String url = "http://10.0.2.2:3000/sanpham/all";

    StringRequest request = new StringRequest(Request.Method.GET, url,
            response -> {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    mangSP.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        // Xử lý masp: nếu không có thì dùng maso, nếu maso cũng null thì dùng index
                        String masp = obj.optString("masp", null);
                        if (masp == null || masp.equals("null")) {
                            Object masoObj = obj.opt("maso");
                            if (masoObj != null && !masoObj.toString().equals("null")) {
                                masp = String.valueOf(masoObj);
                            } else {
                                masp = "SP" + i;
                            }
                        }
                        
                        String tensp = obj.optString("tensp", "");
                        // Xử lý dongia: có thể là string hoặc number
                        float dongia = 0;
                        if (obj.has("dongia") && !obj.isNull("dongia")) {
                            if (obj.get("dongia") instanceof String) {
                                dongia = Float.parseFloat(obj.getString("dongia"));
                            } else {
                                dongia = (float) obj.getDouble("dongia");
                            }
                        }
                        String mota = obj.optString("mota", "");
                        String ghichu = obj.optString("ghichu", "");
                        // Xử lý soluongkho: có thể là string hoặc number
                        int soluongkho = 0;
                        if (obj.has("soluongkho") && !obj.isNull("soluongkho")) {
                            if (obj.get("soluongkho") instanceof String) {
                                soluongkho = Integer.parseInt(obj.getString("soluongkho"));
                            } else {
                                soluongkho = obj.getInt("soluongkho");
                            }
                        }
                        // Xử lý maso có thể null
                        Object masoObj = obj.opt("maso");
                        String maso = (masoObj != null && !masoObj.toString().equals("null")) ? String.valueOf(masoObj) : null;
                        // Xử lý picurl có thể null
                        String anh = obj.optString("picurl", null);
                        if (anh != null && (anh.equals("null") || anh.isEmpty())) {
                            anh = null;
                        }

//                        byte[] imageBytes = Base64.decode(anhBase64, Base64.DEFAULT);

                        // Tạo sản phẩm theo constructor đầy đủ
                        SanPham sp = new SanPham(
                                masp, tensp, dongia, mota, ghichu,
                                soluongkho, maso, anh
                        );

                        mangSP.add(sp);
                    }

                    adapter.notifyDataSetChanged(); // cập nhật hiển thị

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Lỗi định dạng JSON", Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                error.printStackTrace();
                Toast.makeText(this, "Lỗi kết nối API", Toast.LENGTH_SHORT).show();
            });

    Volley.newRequestQueue(this).add(request);
}


    private byte[] convertBitmapToByteArray(int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}