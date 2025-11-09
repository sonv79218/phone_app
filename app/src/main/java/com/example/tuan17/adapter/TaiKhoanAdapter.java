package com.example.tuan17.adapter;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.database.Database;
import com.example.tuan17.R;
import com.example.tuan17.database.TaiKhoanDB;
import com.example.tuan17.models.TaiKhoan;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaiKhoanAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<TaiKhoan> taiKhoanList;

//    private TaiKhoanDB taiKhoanDB;

    public TaiKhoanAdapter(Context context, int layout, List<TaiKhoan> taiKhoanList) {
        this.context = context;
        this.layout = layout;
        this.taiKhoanList = taiKhoanList;
    }

    @Override
    public int getCount() {
        return taiKhoanList.size();
    }

    @Override
    public Object getItem(int position) {
        return taiKhoanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View viewtemp;
        if (view == null) {
            viewtemp = View.inflate(viewGroup.getContext(), R.layout.ds_taikhoan, null);
        } else {
            viewtemp = view;
        }
        TaiKhoan tt = taiKhoanList.get(i);
        TextView tendn = viewtemp.findViewById(R.id.tdn1);
        TextView matkhau = viewtemp.findViewById(R.id.mk1);
        TextView quyenhang = viewtemp.findViewById(R.id.quyen1);
        TextView trangthai = viewtemp.findViewById(R.id.trangthai1);
        TextView sua = viewtemp.findViewById(R.id.imgsua);
        TextView xoa = viewtemp.findViewById(R.id.imgxoa);
        tendn.setText(tt.getTdn());
       // matkhau.setText(tt.getMk());
        quyenhang.setText(tt.getQuyen());
        // Hiển thị trạng thái người dùng
        if (tt.getTrangthai() == 1) {
            trangthai.setText("Hoạt động");
            xoa.setText("Chặn");
        } else {
            trangthai.setText("Bị chặn");
            xoa.setText("Mở khóa");
        }

        sua.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(viewGroup.getContext());

            View dialogView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.activity_sua_tai_khoan, null);

       //     EditText editTdn = dialogView.findViewById(R.id.tdn);
         //   EditText editMk = dialogView.findViewById(R.id.mk);
            RadioButton user = dialogView.findViewById(R.id.user);
            RadioButton admin = dialogView.findViewById(R.id.admin);

          //  editTdn.setText(tt.getTdn());
          //  editMk.setText(tt.getMk());

            if ("admin".equals(tt.getQuyen())) {
                admin.setChecked(true);
            } else {
                user.setChecked(true);
            }


            builder.setView(dialogView)
                    .setPositiveButton("Lưu", (dialog, which) -> {
                       // String newTdn = editTdn.getText().toString().trim();
                      //  String newMk = editMk.getText().toString().trim();
                        String quyen = user.isChecked() ? "user" : "admin";
                        String thangthai;
                        // Gọi API cập nhật tài khoản
                        String url = "http://10.0.2.2:3000/taikhoan/" + tt.getTdn(); // dùng tdn cũ để update

                        StringRequest request = new StringRequest(Request.Method.PUT, url,
                                response -> {
                                    // Cập nhật trong danh sách
                                   // tt.setTdn(newTdn); // nếu muốn cho phép đổi tên đăng nhập
                                  //  tt.setMk(newMk);
                                    tt.setQuyen(quyen);
                                    notifyDataSetChanged();
                                    Toast.makeText(viewGroup.getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                },
                                error -> {
                                    error.printStackTrace();
                                    Toast.makeText(viewGroup.getContext(), "Lỗi cập nhật tài khoản", Toast.LENGTH_SHORT).show();
                                }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                               // params.put("tdn", newTdn); // nếu đổi tên đăng nhập
                             //   params.put("matkhau", newMk);
                                params.put("quyen", quyen);
                                return params;
                            }
                        };

                        Volley.newRequestQueue(viewGroup.getContext()).add(request);
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

            builder.show();
        });

        xoa.setOnClickListener(v -> {
            String action = tt.getTrangthai() == 1 ? "chặn" : "mở khóa";
            int newStatus = tt.getTrangthai() == 1 ? 0 : 1;

            new AlertDialog.Builder(viewGroup.getContext())
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc chắn muốn " + action + " tài khoản này?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        String url = "http://10.0.2.2:3000/taikhoan/khoataikhoan";

                        StringRequest request = new StringRequest(Request.Method.POST, url,
                                response -> {
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (obj.getBoolean("success")) {
                                            tt.setTrangthai(newStatus); // cập nhật trạng thái mới
                                            notifyDataSetChanged();
                                            Toast.makeText(viewGroup.getContext(), "Đã " + action + " tài khoản", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(viewGroup.getContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(viewGroup.getContext(), "Lỗi phản hồi JSON", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                error -> {
                                    error.printStackTrace();
                                    Toast.makeText(viewGroup.getContext(), "Lỗi server", Toast.LENGTH_SHORT).show();
                                }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put("id", String.valueOf(tt.getId()));
                                params.put("newStatus", String.valueOf(newStatus));
                                return params;
                            }
                        };

                        Volley.newRequestQueue(viewGroup.getContext()).add(request);
                    })
                    .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                    .show();
        });


        return viewtemp;
    }
}
