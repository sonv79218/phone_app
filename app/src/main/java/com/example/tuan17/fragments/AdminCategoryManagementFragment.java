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
import com.example.tuan17.ThemNhomSanPham_Activity;
import com.example.tuan17.adapter.NhomSanPhamAdapter;
import com.example.tuan17.models.NhomSanPham;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminCategoryManagementFragment extends Fragment {
    private ListView lv;
    private FloatingActionButton addButton;
    private ArrayList<NhomSanPham> mangNSP;
    private NhomSanPhamAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_nhomsanpham_admin_actvity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lv = view.findViewById(R.id.listtk);
        addButton = view.findViewById(R.id.btnthem);

        mangNSP = new ArrayList<>();
        adapter = new NhomSanPhamAdapter(getActivity(), mangNSP, true);
        lv.setAdapter(adapter);
        loadData();

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ThemNhomSanPham_Activity.class);
            startActivity(intent);
        });
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
                        mangNSP.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String maso = obj.getString("maso");
                            String tennsp = obj.getString("tennsp");
                            String base64Image = obj.getString("anh");
                            byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                            NhomSanPham nsp = new NhomSanPham(maso, tennsp, imageBytes);
                            mangNSP.add(nsp);
                        }
                        adapter.notifyDataSetChanged();
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

