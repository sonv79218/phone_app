package com.example.tuan17.fragments;

import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.R;
import com.example.tuan17.adapter.ChiTietDonHangAdapter;
import com.example.tuan17.models.ChiTietDonHang;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailFragment extends Fragment {
    ListView listViewChiTiet;
    ChiTietDonHangAdapter chiTietAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_chi_tiet_don_hang, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listViewChiTiet = view.findViewById(R.id.productGroupList);

        Bundle args = getArguments();
        String donHangIdStr = args != null ? args.getString("donHangId") : null;

        if (donHangIdStr != null) {
            try {
                int donHangId = Integer.parseInt(donHangIdStr);
                loadChiTietDonHangFromAPI(donHangId);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "ID đơn hàng không hợp lệ!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadChiTietDonHangFromAPI(int orderId) {
        String url = "http://10.0.2.2:3000/chitietdathang/" + orderId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<ChiTietDonHang> chiTietList = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int id_chitiet = obj.getInt("id_chitiet");
                            int masp = obj.getInt("masp");
                            int soluong = obj.getInt("soluong");
                            float dongia = (float) obj.getDouble("dongia");
                            String base64Image = obj.getString("anh");
                            byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                            ChiTietDonHang ct = new ChiTietDonHang(id_chitiet, masp, soluong, dongia, imageBytes);
                            chiTietList.add(ct);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (chiTietList.isEmpty()) {
                        Toast.makeText(getActivity(), "Không có chi tiết đơn hàng!", Toast.LENGTH_SHORT).show();
                    } else {
                        chiTietAdapter = new ChiTietDonHangAdapter(getActivity(), chiTietList);
                        listViewChiTiet.setAdapter(chiTietAdapter);
                    }
                },
                error -> {
                    Toast.makeText(getActivity(), "Lỗi khi tải chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        );

        Volley.newRequestQueue(getActivity()).add(request);
    }
}

