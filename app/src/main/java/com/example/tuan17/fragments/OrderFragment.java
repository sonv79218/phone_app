package com.example.tuan17.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.R;
import com.example.tuan17.adapter.DonHang_Adapter;
import com.example.tuan17.helper.SharedPrefHelper;
import com.example.tuan17.models.Order;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {
    private ListView listView;
    private DonHang_Adapter donHangAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_don_hang_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.listViewChiTiet);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order order = donHangAdapter.getItem(position);

                if (order != null) {
                    OrderDetailFragment fragment = new OrderDetailFragment();
                    Bundle args = new Bundle();
                    args.putString("donHangId", String.valueOf(order.getId()));
                    fragment.setArguments(args);

                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(
                                        R.anim.slide_in_right,
                                        R.anim.slide_out_left,
                                        R.anim.slide_in_left,
                                        R.anim.slide_out_right
                                )
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            }
        });

        String tenDN = SharedPrefHelper.getUsername(getActivity());
        if (tenDN == null) {
            tenDN = getArguments() != null ? getArguments().getString("tendn") : null;
        }

        int user_id = SharedPrefHelper.getUserId(getActivity());
        if (user_id == -1) {
            Toast.makeText(getActivity(), "không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }
        loadDonHang(user_id);
    }

    private void loadDonHang(int userId) {
        String url = "http://10.0.2.2:3000/dathang/user/" + userId;
        Log.d("abc", "userId" + userId);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("API_Response", response.toString());
                    List<Order> orders = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int idDatHang = obj.getInt("id");
                            String tenkh = obj.getString("tenkh");
                            String diachi = obj.getString("diachi");
                            String sdt = obj.getString("sdt");
                            float tong = (float) obj.getDouble("tongthanhtoan");
                            String ngaydat = obj.getString("ngaydathang");
                            orders.add(new Order(idDatHang, tenkh, diachi, sdt, tong, ngaydat));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (orders.isEmpty()) {
                        Toast.makeText(getActivity(), "Không tìm thấy đơn hàng!", Toast.LENGTH_SHORT).show();
                    } else {
                        donHangAdapter = new DonHang_Adapter(getActivity(), orders);
                        listView.setAdapter(donHangAdapter);
                    }
                },
                error -> {
                    Toast.makeText(getActivity(), "Lỗi kết nối hoặc không có đơn hàng", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        );

        Volley.newRequestQueue(getActivity()).add(request);
    }
}

