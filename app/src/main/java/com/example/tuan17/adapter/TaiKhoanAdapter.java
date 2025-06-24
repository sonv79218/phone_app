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
        ImageButton sua = viewtemp.findViewById(R.id.imgsua);
        ImageButton xoa = viewtemp.findViewById(R.id.imgxoa);
        tendn.setText(tt.getTdn());
        matkhau.setText(tt.getMk());
        quyenhang.setText(tt.getQuyen());
        // Xử lý sự kiện cho ImageButton "Sửa"
//        sua.setOnClickListener(v -> {
//            AlertDialog.Builder builder = new AlertDialog.Builder(viewGroup.getContext());
//
//            View dialogView = LayoutInflater.from(viewGroup.getContext())
//                    .inflate(R.layout.activity_sua_tai_khoan, null);
//
//            EditText editTdn = dialogView.findViewById(R.id.tdn);
//            EditText editMk = dialogView.findViewById(R.id.mk);
//            RadioButton user = dialogView.findViewById(R.id.user);
//            RadioButton admin = dialogView.findViewById(R.id.admin);
//
//            editTdn.setText(tt.getTdn());
//            editMk.setText(tt.getMk());
//
//            // Đặt quyền hiện tại cho RadioButton
//            if ("admin".equals(tt.getQuyen())) {
//                admin.setChecked(true);
//            } else {
//                user.setChecked(true);
//            }
//
//            builder.setView(dialogView)
//                    .setPositiveButton("Lưu", (dialog, which) -> {
//                        String newTdn = editTdn.getText().toString().trim();
//                        String newMk = editMk.getText().toString().trim();
//                        String quyen = user.isChecked() ? "user" : "admin";
//
//                        // Sử dụng class DB riêng
////                        taiKhoanDB = new TaiKhoanDB(viewGroup.getContext());
//                        boolean success = taiKhoanDB.suaTaiKhoan(tt.getTdn(), newMk, quyen); // sửa theo tên cũ
//
//                        if (success) {
//                            tt.setTdn(newTdn); // Nếu cho phép đổi tên đăng nhập, cần xử lý thêm
//                            tt.setMk(newMk);
//                            tt.setQuyen(quyen);
//                            notifyDataSetChanged();
//                            Toast.makeText(viewGroup.getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(viewGroup.getContext(), "Cập nhật không thành công", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
//
//            builder.show();
//        });
        sua.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(viewGroup.getContext());

            View dialogView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.activity_sua_tai_khoan, null);

            EditText editTdn = dialogView.findViewById(R.id.tdn);
            EditText editMk = dialogView.findViewById(R.id.mk);
            RadioButton user = dialogView.findViewById(R.id.user);
            RadioButton admin = dialogView.findViewById(R.id.admin);

            editTdn.setText(tt.getTdn());
            editMk.setText(tt.getMk());

            if ("admin".equals(tt.getQuyen())) {
                admin.setChecked(true);
            } else {
                user.setChecked(true);
            }

            builder.setView(dialogView)
                    .setPositiveButton("Lưu", (dialog, which) -> {
                        String newTdn = editTdn.getText().toString().trim();
                        String newMk = editMk.getText().toString().trim();
                        String quyen = user.isChecked() ? "user" : "admin";

                        // Gọi API cập nhật tài khoản
                        String url = "http://10.0.2.2:3000/taikhoan/" + tt.getTdn(); // dùng tdn cũ để update

                        StringRequest request = new StringRequest(Request.Method.PUT, url,
                                response -> {
                                    // Cập nhật trong danh sách
                                    tt.setTdn(newTdn); // nếu muốn cho phép đổi tên đăng nhập
                                    tt.setMk(newMk);
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
                                params.put("tdn", newTdn); // nếu đổi tên đăng nhập
                                params.put("matkhau", newMk);
                                params.put("quyen", quyen);
                                return params;
                            }
                        };

                        Volley.newRequestQueue(viewGroup.getContext()).add(request);
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

            builder.show();
        });

        // Xử lý sự kiện cho ImageButton "Xóa"
//        xoa.setOnClickListener(v -> {
//            new AlertDialog.Builder(viewGroup.getContext())
//                    .setTitle("Xác nhận")
//                    .setMessage("Bạn có chắc chắn muốn xóa tài khoản này?")
//                    .setPositiveButton("Có", (dialog, which) -> {
//                        taiKhoanDB = new TaiKhoanDB(viewGroup.getContext());
//                        boolean success = taiKhoanDB.xoaTaiKhoan(tt.getTdn());
//
//                        if (success) {
//                            taiKhoanList.remove(i);
//                            notifyDataSetChanged();
//                            Toast.makeText(viewGroup.getContext(), "Xóa tài khoản thành công", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(viewGroup.getContext(), "Không tìm thấy tài khoản để xóa", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
//                    .show();
//        });
        xoa.setOnClickListener(v -> {
            new AlertDialog.Builder(viewGroup.getContext())
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc chắn muốn xóa tài khoản này?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        String url = "http://10.0.2.2:3000/taikhoan/" + tt.getTdn(); // tt.getTdn() là tên đăng nhập

                        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                                response -> {
                                    taiKhoanList.remove(i);
                                    notifyDataSetChanged();
                                    Toast.makeText(viewGroup.getContext(), "Xóa tài khoản thành công", Toast.LENGTH_SHORT).show();
                                },
                                error -> {
                                    error.printStackTrace();
                                    Toast.makeText(viewGroup.getContext(), "Lỗi khi xóa tài khoản", Toast.LENGTH_SHORT).show();
                                });

                        Volley.newRequestQueue(viewGroup.getContext()).add(request);
                    })
                    .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        return viewtemp;
    }
}
