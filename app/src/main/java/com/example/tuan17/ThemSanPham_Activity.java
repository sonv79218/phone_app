package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.models.NhomSanPham;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ThemSanPham_Activity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    EditText productNameEditText, productPriceEditText, productDescriptionEditText, productNoteEditText, productStockQuantityEditText;
    Spinner productGroupSpinner;
    ImageView productImageView;
    ArrayList<NhomSanPham> productGroupList;
    private Uri imageUri; // Biến để lưu trữ URI của ảnh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_san_pham);
    // Khởi tạo các view
        productNameEditText = findViewById(R.id.tensp);
        productImageView = findViewById(R.id.imgsp);
        productDescriptionEditText = findViewById(R.id.mota);
        productNoteEditText = findViewById(R.id.ghichu);
        productPriceEditText = findViewById(R.id.dongia);
        productStockQuantityEditText = findViewById(R.id.soluongkho);
        productGroupSpinner = findViewById(R.id.spn);
        productImageView = findViewById(R.id.imgsp);

        Button selectImageButton = findViewById(R.id.btnAddImg);
        Button addProductButton = findViewById(R.id.btnadd);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker(); // Gọi hàm mở gallery để chọn ảnh từ điện thoại
            }
        });
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createProduct();
            }
        });

        loadTenNhomSanPham();

    }

    private void loadTenNhomSanPham() {
        productGroupList = new ArrayList<>();
        String url = "http://10.0.2.2:3000/nhomsanpham";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String maso = obj.getString("maso");
                            String tennsp = obj.getString("tennsp");
                            productGroupList.add(new NhomSanPham(maso, tennsp, null));
                        }

                        // Gắn dữ liệu vào Spinner
                        ArrayAdapter<NhomSanPham> adapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_item, productGroupList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        productGroupSpinner.setAdapter(adapter);
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
    }

    private void createProduct() {
        String tenspStr = productNameEditText.getText().toString().trim();
        String motaStr = productDescriptionEditText.getText().toString().trim();
        String ghichuStr = productNoteEditText.getText().toString().trim();
        String dongiaStr = productPriceEditText.getText().toString().trim();
        String soluongStr = productStockQuantityEditText.getText().toString().trim();
        String maso = productGroupList.get(productGroupSpinner.getSelectedItemPosition()).getMa();
        if (tenspStr.isEmpty() || motaStr.isEmpty() || ghichuStr.isEmpty() || dongiaStr.isEmpty() || soluongStr.isEmpty()) {
            Toast.makeText(ThemSanPham_Activity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh sản phẩm!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔹 Copy ảnh vào bộ nhớ app và lấy đường dẫn thực
        String localPath = copyImageToAppStorage(imageUri);
        if (localPath == null) {
            Toast.makeText(this, "Lỗi khi xử lý ảnh!", Toast.LENGTH_SHORT).show();
            return;
        }
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
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("tensp", tenspStr);
                data.put("mota", motaStr);
                data.put("ghichu", ghichuStr);
                data.put("dongia", dongiaStr);
                data.put("soluong", soluongStr);
                data.put("maso", maso);
                data.put("anh", localPath);
                return data;
            }
        };
        Volley.newRequestQueue(this).add(request);
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
                productImageView.setImageURI(imageUri);
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