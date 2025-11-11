package com.example.tuan17.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.R;
import com.example.tuan17.ThemSanPham_Activity;
import com.example.tuan17.adapter.SanPhamAdapter;
import com.example.tuan17.models.NhomSanPham;
import com.example.tuan17.models.SanPham;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminProductManagementFragment extends Fragment {
    private ListView productListView;
    private FloatingActionButton addNewProductButton;
    private ArrayList<SanPham> productArrayList;
    private SanPhamAdapter productAdapter;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ImageView currentPreviewImage;
    private Uri selectedImageUri;

    private SanPham currentEditingProduct;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_sanpham_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productListView = view.findViewById(R.id.productGroupList);
        addNewProductButton = view.findViewById(R.id.addProductGroupButton);

        productArrayList = new ArrayList<>();
        productAdapter = new SanPhamAdapter(getActivity(), productArrayList, true);
        productListView.setAdapter(productAdapter);
        loadData();

        addNewProductButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ThemSanPham_Activity.class);
            startActivity(intent);
        });
        // ✅ Gắn callback chọn ảnh
        productAdapter.setOnImageSelectListener((product, previewImage) -> {
            currentEditingProduct = product;
            currentPreviewImage = previewImage;
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        // ✅ Khởi tạo ActivityResultLauncher cho chọn ảnh
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();

                        if (selectedImageUri != null && currentPreviewImage != null && currentEditingProduct != null) {
                            // Cập nhật ảnh hiển thị ngay trong dialog
                            currentPreviewImage.setImageURI(selectedImageUri);

                            // Lưu lại URI ảnh vào model và adapter
                            currentEditingProduct.setAnh(selectedImageUri.toString());
                            productAdapter.setSelectedImageUri(selectedImageUri);
                        }
                    }
                }
        );

        productAdapter.setOnProductGroupUpdatedListener(() -> {
            loadData(); // gọi lại API GET để load danh sách mới
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(); // Reload data when returning to fragment
    }

    private void loadData() {
        String url = "http://10.0.2.2:3000/sanpham/all";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        productArrayList.clear();

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
                            String picurl = obj.optString("picurl", null);
                            if (picurl != null && (picurl.equals("null") || picurl.isEmpty())) {
                                picurl = null;
                            }
                            
                            SanPham sp = new SanPham(
                                    masp, tensp, dongia, mota, ghichu,
                                    soluongkho, maso, picurl
                            );

                            productArrayList.add(sp);
                        }

                        productAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Lỗi định dạng JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getActivity(), "Lỗi kết nối API", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(getActivity()).add(request);
    }
}

