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
import com.example.tuan17.ThemNhomSanPham_Activity;
import com.example.tuan17.adapter.NhomSanPhamAdapter;
import com.example.tuan17.models.NhomSanPham;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminCategoryManagementFragment extends Fragment {

    private ListView productGroupList;
    private FloatingActionButton addProductGroupButton;
    private ArrayList<NhomSanPham> productGroupArray;
    private NhomSanPhamAdapter productGroupAdapter;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Uri selectedImageUri;
    private NhomSanPham currentEditingProductGroup;
    private ImageView currentPreviewImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_nhomsanpham_admin_actvity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productGroupList = view.findViewById(R.id.productGroupList);
        addProductGroupButton = view.findViewById(R.id.addProductGroupButton);
        productGroupArray = new ArrayList<>();
        productGroupAdapter = new NhomSanPhamAdapter(getActivity(), productGroupArray, true);
        productGroupList.setAdapter(productGroupAdapter);

        // Nút thêm nhóm sản phẩm
        addProductGroupButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ThemNhomSanPham_Activity.class);
            startActivity(intent);
        });

        // ✅ Gắn callback chọn ảnh
        productGroupAdapter.setOnImageSelectListener((nhomSanPham, previewImage) -> {
            currentEditingProductGroup = nhomSanPham;
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

                        if (selectedImageUri != null && currentPreviewImage != null && currentEditingProductGroup != null) {
                            // Cập nhật ảnh hiển thị ngay trong dialog
                            currentPreviewImage.setImageURI(selectedImageUri);

                            // Lưu lại URI ảnh vào model và adapter
                            currentEditingProductGroup.setAnh(selectedImageUri.toString());
                            productGroupAdapter.setSelectedImageUri(selectedImageUri);
                        }
                    }
                }
        );

        productGroupAdapter.setOnProductGroupUpdatedListener(() -> {
            loadData(); // gọi lại API GET để load danh sách mới
        });
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(); // Reload data when returning to fragment
    }

    private void loadData() {
        String url = "http://10.0.2.2:3000/nhomsanpham/all";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        productGroupArray.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String maso = obj.optString("maso", "");
                            String tennsp = obj.optString("tennsp", "");
                            String picurl = obj.optString("picurl", "");
                            NhomSanPham nsp = new NhomSanPham(maso, tennsp, picurl);
                            productGroupArray.add(nsp);
                        }
                        productGroupAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(getActivity(), "Lỗi kết nối API", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                });
        Volley.newRequestQueue(getActivity()).add(request);
    }
}
