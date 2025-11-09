package com.example.tuan17;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.adapter.SanPhamAdapter;
import com.example.tuan17.database.Database;
import com.example.tuan17.models.NhomSanPham;
import com.example.tuan17.models.SanPham;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ThemSanPham_Activity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1; // Mã yêu cầu cho việc chọn ảnh
    EditText tensp, dongia, mota, ghichu, soluongkho;
    Spinner mansp;
//    Database database;
    ImageView imgsp;
    ArrayList<SanPham> mangSP;
    ArrayList<NhomSanPham> mangNSPList;
    SanPhamAdapter adapter;
    ImageButton back;
    private Uri imageUri; // Biến để lưu trữ URI của ảnh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_san_pham);

        // Khởi tạo các view
        tensp = findViewById(R.id.tensp);
        imgsp = findViewById(R.id.imgsp);
        mota = findViewById(R.id.mota);
        ghichu = findViewById(R.id.ghichu);
        dongia = findViewById(R.id.dongia);
        soluongkho = findViewById(R.id.soluongkho);
        mansp = findViewById(R.id.spn);

        Button chonimgbs = findViewById(R.id.btnAddImg);
        Button btnthem = findViewById(R.id.btnadd);

        // Khởi tạo cơ sở dữ liệu
//        database = new Database(this, "banhang.db", null, 1);
//        database.QueryData("CREATE TABLE IF NOT EXISTS sanpham(masp INTEGER PRIMARY KEY AUTOINCREMENT, tensp NVARCHAR(200), dongia FLOAT, mota TEXT, ghichu TEXT, soluongkho INTEGER, maso INTEGER, anh BLOB)");

        // Tải danh sách nhóm sản phẩm
        loadTenNhomSanPham();

        // Thiết lập sự kiện cho nút chọn ảnh
        chonimgbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker(); // Gọi hàm mở gallery để chọn ảnh từ điện thoại
            }
        });

        // Thiết lập sự kiện cho nút thêm sản phẩm
        btnthem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSanPham();
            }
        });
    }

    private void loadTenNhomSanPham() {
        mangNSPList = new ArrayList<>();
        String url = "http://10.0.2.2:3000/nhomsanpham";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String maso = obj.getString("maso");
                            String tennsp = obj.getString("tennsp");
                            mangNSPList.add(new NhomSanPham(maso, tennsp, null));
                        }

                        // Gắn dữ liệu vào Spinner
                        ArrayAdapter<NhomSanPham> adapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_item, mangNSPList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mansp.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi dữ liệu JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Lỗi tải nhóm sản phẩm", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);

//        Cursor cursor = database.GetData("SELECT maso, tennsp FROM nhomsanpham"); // Lấy maso và tennhom
//
//        while (cursor.moveToNext()) {
//            String maso = cursor.getString(0); // Cột 0
//            String tennhom = cursor.getString(1); // Cột 1
//            mangNSPList.add(new NhomSanPham(maso, tennhom, null)); // null nếu không cần ảnh
//        }

        // Tạo adapter cho Spinner
//        ArrayAdapter<NhomSanPham> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mangNSPList);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mansp.setAdapter(adapter);
    }

    private void addSanPham() {
        // Lấy dữ liệu từ các trường
        // b1
        String tenspStr = tensp.getText().toString().trim();
        String motaStr = mota.getText().toString().trim();
        String ghichuStr = ghichu.getText().toString().trim();
        String dongiaStr = dongia.getText().toString().trim();
        String soluongStr = soluongkho.getText().toString().trim();
        String maso = mangNSPList.get(mansp.getSelectedItemPosition()).getMa(); // Lấy maso từ Spinner

        // Kiểm tra dữ liệu không rỗng
        if (tenspStr.isEmpty() || motaStr.isEmpty() || ghichuStr.isEmpty() || dongiaStr.isEmpty() || soluongStr.isEmpty()) {
            Toast.makeText(ThemSanPham_Activity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Khởi tạo giá trị cho imageBytes
        byte[] imageBytes = null;
        if (imageUri != null) {
            imageBytes = getBytesFromUri(imageUri);
            if (imageBytes == null) {
                Toast.makeText(ThemSanPham_Activity.this, "Lỗi khi lấy ảnh!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        String base64Image = (imageBytes != null) ? Base64.encodeToString(imageBytes, Base64.DEFAULT) : "";
        // Chuyển đổi giá trị số
        float dongiaFloat;
        int soluongInt;
        try {
            dongiaFloat = Float.parseFloat(dongiaStr);
            soluongInt = Integer.parseInt(soluongStr);
        } catch (NumberFormatException e) {
            Toast.makeText(ThemSanPham_Activity.this, "Giá trị không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }
        //b2 them vao cơ sở dữ liệu
        String url = "http://10.0.2.2:3000/sanpham";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, Sanpham_admin_Activity.class));
                    finish();
                },
                error -> {
                    Toast.makeText(this, "Lỗi kết nối API", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
                )
        {
            @Override
            protected Map<String,String>getParams(){
                Map<String,String> data = new HashMap<>();
                data.put("tensp", tenspStr);
                data.put("mota", motaStr);
                data.put("ghichu",ghichuStr);
                data.put("dongia", dongiaStr);
                data.put("soluong", soluongStr);
                data.put("maso", maso);
                data.put("anh", base64Image);
                return data;
            }
        };
        Volley.newRequestQueue(this).add(request);
        // Thêm sản phẩm vào cơ sở dữ liệu
//        database.QueryData("INSERT INTO sanpham(tensp, dongia, mota, ghichu, soluongkho, maso, anh) VALUES (?, ?, ?, ?, ?, ?, ?)",
//                new Object[]{tenNsp, dongiaFloat, motaStr, ghichuStr, soluongInt, maso, imageBytes});
//
//        Toast.makeText(ThemSanPham_Activity.this, "Thêm sản phẩm thành công!", Toast.LENGTH_LONG).show();
//
//        // Chuyển đến Activity thứ hai
//        Intent intent = new Intent(getApplicationContext(), Sanpham_admin_Activity.class);
//        startActivity(intent);
//        finish();
    }

    // Mở gallery để chọn ảnh từ điện thoại
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Xử lý kết quả khi người dùng chọn ảnh từ gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                // Hiển thị ảnh đã chọn lên ImageView
                imgsp.setImageURI(imageUri);
            }
        }
    }

    // Chuyển đổi URI thành mảng byte
    private byte[] getBytesFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray(); // Trả về mảng byte của ảnh
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}