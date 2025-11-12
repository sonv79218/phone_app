package com.example.tuan17.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.R;
import com.example.tuan17.adapter.SanPham_DanhMuc_Admin_Adapter;
import com.example.tuan17.models.SanPham;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminCategoryFragment extends Fragment {
    private GridView grv;
    private ArrayList<SanPham> productList;
    private SanPham_DanhMuc_Admin_Adapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_danh_muc_san_pham_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        grv = view.findViewById(R.id.grv);
        productList = new ArrayList<>();

        Bundle args = getArguments();
        String nhomSpId = args != null ? args.getString("nhomSpId") : null;

        if (nhomSpId != null) {
            loadSanPhamTheoNhom(nhomSpId);
        } else {
            Toast.makeText(getActivity(), "ID nhóm sản phẩm không hợp lệ!", Toast.LENGTH_SHORT).show();
        }

        grv.setOnItemClickListener((parent, view1, position, id) -> {
            SanPham sanPham = productList.get(position);
            navigateToProductDetail(sanPham);
        });
    }

    private void loadSanPhamTheoNhom(String maso) {
        String url = com.example.tuan17.util.ApiConfig.productsByGroup(maso);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray dataArray = response.getJSONArray("data");
                        productList = new ArrayList<>();

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject obj = dataArray.getJSONObject(i);
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
                            int soluongkho = obj.optInt("soluongkho", 0);
                            // Xử lý maso có thể null
                            Object masoObj = obj.opt("maso");
                            String masoSp = (masoObj != null && !masoObj.toString().equals("null")) ? String.valueOf(masoObj) : null;
                            // Xử lý picurl có thể null
                            String anh = obj.optString("picurl", null);
                            if (anh != null && (anh.equals("null") || anh.isEmpty())) {
                                anh = null;
                            }

                            SanPham sp = new SanPham(masp, tensp, dongia, mota, ghichu, soluongkho, masoSp, anh);
                            productList.add(sp);
                        }

                        productAdapter = new SanPham_DanhMuc_Admin_Adapter(getActivity(), productList, false);
                        grv.setAdapter(productAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Lỗi định dạng JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    String message = "Lỗi kết nối đến API";
                    if (error.networkResponse != null) {
                        message += " (" + error.networkResponse.statusCode + ")";
                    }
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(getActivity()).add(request);
    }

    private void navigateToProductDetail(SanPham sanPham) {
        AdminProductDetailFragment fragment = new AdminProductDetailFragment();
        Bundle args = new Bundle();
        args.putString("masp", sanPham.getMasp());
        args.putString("tensp", sanPham.getTensp());
        args.putFloat("dongia", sanPham.getDongia());
        args.putString("mota", sanPham.getMota());
        args.putString("ghichu", sanPham.getGhichu());
        args.putInt("soluongkho", sanPham.getSoluongkho());
        args.putString("maso", sanPham.getMansp());
        args.putString("anh", sanPham.getAnh());
        fragment.setArguments(args);

        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}

