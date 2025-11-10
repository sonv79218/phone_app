package com.example.tuan17.fragments;

import android.content.Intent;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.R;
import com.example.tuan17.ThemSanPham_Activity;
import com.example.tuan17.adapter.SanPhamAdapter;
import com.example.tuan17.models.SanPham;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminProductManagementFragment extends Fragment {
    private ListView lv;
    private FloatingActionButton addButton;
    private ArrayList<SanPham> mangSP;
    private SanPhamAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_sanpham_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lv = view.findViewById(R.id.listtk);
        addButton = view.findViewById(R.id.btnthem);

        mangSP = new ArrayList<>();
        adapter = new SanPhamAdapter(getActivity(), mangSP, true);
        lv.setAdapter(adapter);
        loadData();

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ThemSanPham_Activity.class);
            startActivity(intent);
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
                        mangSP.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            String masp = obj.getString("masp");
                            String tensp = obj.getString("tensp");
                            String dongiaStr = obj.getString("dongia");
                            String mota = obj.getString("mota");
                            String ghichu = obj.getString("ghichu");
                            String soluongkhoStr = obj.getString("soluongkho");
                            String maso = obj.getString("maso");
                            String anhBase64 = obj.getString("anh");

                            float dongia = Float.parseFloat(dongiaStr);
                            int soluongkho = Integer.parseInt(soluongkhoStr);
                            byte[] imageBytes = Base64.decode(anhBase64, Base64.DEFAULT);

                            SanPham sp = new SanPham(
                                    masp, tensp, dongia, mota, ghichu,
                                    soluongkho, maso, imageBytes
                            );

                            mangSP.add(sp);
                        }

                        adapter.notifyDataSetChanged();

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

