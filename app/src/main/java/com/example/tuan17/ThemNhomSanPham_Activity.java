package com.example.tuan17;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.adapter.NhomSanPhamAdapter;
import com.example.tuan17.database.Database;
import com.example.tuan17.models.NhomSanPham;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ThemNhomSanPham_Activity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1; // Mã yêu cầu cho việc chọn ảnh
    EditText tennsp;
    ImageView imgnsp;
    ArrayList<NhomSanPham> mangNSP;
  NhomSanPhamAdapter adapter;
    ImageButton back;
    private Uri imageUri; // Biến để lưu trữ URI của ảnh
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_nhom_san_pham);
        tennsp= findViewById(R.id.ten);
        imgnsp = findViewById(R.id.imgnsp);
        Button chonimg = findViewById(R.id.btnAddImg);
        Button btnthem = findViewById(R.id.btnadd);
        mangNSP = new ArrayList<>();
        adapter = new NhomSanPhamAdapter(ThemNhomSanPham_Activity.this, mangNSP, true) {
        };
        // Thiết lập OnClickListener cho nút chọn ảnh
        chonimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker(); // Gọi hàm mở gallery để chọn ảnh từ điện thoại
            }
        });

        btnthem.setOnClickListener(view -> {
            String tenNsp = tennsp.getText().toString().trim();
            if (tenNsp.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }
            byte[] imageBytes = null;
            if (imageUri != null) {
                imageBytes = getBytesFromUri(imageUri);
                if (imageBytes == null) {
                    Toast.makeText(this, "Lỗi khi lấy ảnh!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            String base64Image = (imageBytes != null) ? Base64.encodeToString(imageBytes, Base64.DEFAULT) : "";

            // Gửi lên API
            String url = "http://10.0.2.2:3000/nhomsanpham"; // Thay <IP> bằng IP máy chủ Node
            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Toast.makeText(this, "Thêm nhóm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, Nhomsanpham_admin_Actvity.class));
                        finish();
                    },
                    error -> {
                        Toast.makeText(this, "Lỗi kết nối API", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> data = new HashMap<>();
                    data.put("tennsp", tenNsp);
                    data.put("anh", base64Image); // Gửi ảnh base64
                    return data;
                }
            };

            Volley.newRequestQueue(this).add(request);
        });
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
                imgnsp.setImageURI(imageUri);
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