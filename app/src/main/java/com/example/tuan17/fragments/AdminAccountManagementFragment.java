package com.example.tuan17.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.R;
import com.example.tuan17.ThemTaiKhoan_Activity;
import com.example.tuan17.adapter.TaiKhoanAdapter;
import com.example.tuan17.models.TaiKhoan;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminAccountManagementFragment extends Fragment {
    ListView lv;
    List<TaiKhoan> mangTK;
    TaiKhoanAdapter adapter;
    FloatingActionButton dauconggocphai;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_taikhoan_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lv = view.findViewById(R.id.listtk);
        dauconggocphai = view.findViewById(R.id.btnthem);

        mangTK = new ArrayList<>();
        adapter = new TaiKhoanAdapter(getActivity(), R.layout.ds_taikhoan, mangTK);
        lv.setAdapter(adapter);

        dauconggocphai.setOnClickListener(v -> {
            Intent a = new Intent(getActivity(), ThemTaiKhoan_Activity.class);
            startActivity(a);
        });

        loadTaiKhoanFromAPI();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTaiKhoanFromAPI(); // Reload data when returning to fragment
    }

    private void loadTaiKhoanFromAPI() {
        String url = "http://10.0.2.2:3000/taikhoan/all";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray dataArray = response.getJSONArray("data");
                        mangTK.clear();

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject obj = dataArray.getJSONObject(i);

                            int id = obj.getInt("id");
                            String tendn = obj.getString("tendn");
                            String matkhau = obj.getString("matkhau");
                            String email = obj.optString("email", "");
                            String sdt = obj.optString("sdt", "");
                            String hoten = obj.optString("hoten", "");
                            String diachi = obj.optString("diachi", "");
                            String quyen = obj.getString("quyen");
                            String ngaytao = obj.optString("ngaytao", "");
                            int trangthai = obj.getInt("trangthai");

                            TaiKhoan tk = new TaiKhoan(id, tendn, matkhau, email, sdt, hoten, diachi, quyen, ngaytao, trangthai);
                            mangTK.add(tk);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Lỗi định dạng dữ liệu!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getActivity(), "Lỗi kết nối API!", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(getActivity()).add(request);
    }
}

