package com.example.tuan17.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.ChinhSuaThongTin_Activity;
import com.example.tuan17.DoiMatKhau_Activity;
import com.example.tuan17.Login_Activity;
import com.example.tuan17.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AdminProfileFragment extends Fragment {
    String tendn;
    Integer id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_trang_ca_nhan_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvHoten = view.findViewById(R.id.tvHoTen);
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        TextView tvSdt = view.findViewById(R.id.tvSdt);
        TextView tvDiachi = view.findViewById(R.id.tvDiaChi);
        TextView tvQuyen = view.findViewById(R.id.tvQuyen);
        TextView tvNgaytao = view.findViewById(R.id.tvNgayTao);
        Button dangxuat = view.findViewById(R.id.btndangxuat);
        TextView textTendn = view.findViewById(R.id.tendn);
        Button doimk = view.findViewById(R.id.btnDoiMatKhau);
        Button suatt = view.findViewById(R.id.btnChinhSua);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", getActivity().MODE_PRIVATE);
        tendn = sharedPreferences.getString("tendn", null);
        id = sharedPreferences.getInt("user_id", -1);

        if (tendn == null) {
            tendn = getArguments() != null ? getArguments().getString("tendn") : null;
        }

        if (tendn != null) {
            textTendn.setText(tendn);
        } else {
            Intent intent = new Intent(getActivity(), Login_Activity.class);
            startActivity(intent);
            getActivity().finish();
            return;
        }

        String url = "http://10.0.2.2:3000/taikhoan/thongtin?id=" + id;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONObject user = json.getJSONObject("user");
                            tvHoten.setText(getSafe(user, "hoten"));
                            tvEmail.setText(getSafe(user, "email"));
                            tvSdt.setText(getSafe(user, "sdt"));
                            tvDiachi.setText(getSafe(user, "diachi"));
                            tvQuyen.setText(getSafe(user, "quyen"));
                            tvNgaytao.setText(formatDate(user.optString("ngaytao")));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getActivity(), "Lỗi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
        );
        Volley.newRequestQueue(getActivity()).add(stringRequest);

        dangxuat.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Đăng Xuất")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        Intent intent = new Intent(getActivity(), Login_Activity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        getActivity().finishAffinity();
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });

        doimk.setOnClickListener(viewdmk -> {
            Intent intent = new Intent(getActivity(), DoiMatKhau_Activity.class);
            startActivity(intent);
        });

        suatt.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChinhSuaThongTin_Activity.class);
            startActivity(intent);
        });
    }

    private String getSafe(JSONObject obj, String key) {
        try {
            String value = obj.optString(key, "").trim();
            if (value.equals("null") || value.isEmpty()) {
                return "Không có thông tin";
            }
            return value;
        } catch (Exception e) {
            return "Không có thông tin";
        }
    }

    private String formatDate(String isoDate) {
        try {
            if (isoDate == null || isoDate.equals("null") || isoDate.isEmpty()) return "Không có thông tin";

            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = isoFormat.parse(isoDate);
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return displayFormat.format(date);
        } catch (Exception e) {
            return "Không có thông tin";
        }
    }
}

