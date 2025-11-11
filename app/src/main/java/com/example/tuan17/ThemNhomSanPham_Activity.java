package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.adapter.NhomSanPhamAdapter;
import com.example.tuan17.models.NhomSanPham;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ThemNhomSanPham_Activity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    EditText productGroupName;
    ImageView productGroupImage;
    ArrayList<NhomSanPham> productGroupList;
    NhomSanPhamAdapter adapter;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_nhom_san_pham);

        productGroupName = findViewById(R.id.product_group_name);
        productGroupImage = findViewById(R.id.product_group_img);
        Button selectImageButton = findViewById(R.id.btnAddImg);
        Button addProductGroupButton = findViewById(R.id.btnadd);
        productGroupList = new ArrayList<>();
        adapter = new NhomSanPhamAdapter(ThemNhomSanPham_Activity.this, productGroupList, true) {
        };

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker(); // Gọi hàm mở gallery để chọn ảnh từ điện thoại
            }
        });

        addProductGroupButton.setOnClickListener(view -> {
            String tenNsp = productGroupName.getText().toString().trim();
            String localPath = copyImageToAppStorage(imageUri);

            if (localPath == null) {
                Toast.makeText(this, "Vui lòng chọn ảnh!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (tenNsp.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

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
                    data.put("anh", localPath); // Gửi đường dẫn ảnh
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
                productGroupImage.setImageURI(imageUri);
            }
        }
    }

    private String copyImageToAppStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File directory = new File(getExternalFilesDir("images"), "");
            if (!directory.exists()) directory.mkdirs();

            String fileName = "img_" + System.currentTimeMillis() + ".png";
            File newFile = new File(directory, fileName);

            OutputStream outputStream = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return newFile.getAbsolutePath(); // Trả về đường dẫn thực
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}