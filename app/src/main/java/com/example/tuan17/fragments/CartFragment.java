package com.example.tuan17.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.Login_Activity;
import com.example.tuan17.R;
import com.example.tuan17.adapter.GioHangAdapter;
import com.example.tuan17.helper.SharedPrefHelper;
import com.example.tuan17.models.GioHang;
import com.example.tuan17.GioHangManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment {
    private ListView cartItemsView;
    private GioHangAdapter cartAdapter;
    private GioHangManager gioHangManager;
    private Button paymentButton;
    private TextView totalPriceTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gio_hang, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        paymentButton = view.findViewById(R.id.btnthanhtoan);
        cartItemsView = view.findViewById(R.id.productGroupList);
        totalPriceTextView = view.findViewById(R.id.tongtien);

        gioHangManager = GioHangManager.getInstance();
        List<GioHang> gioHangList = gioHangManager.getGioHangList();
        cartAdapter = new GioHangAdapter(getActivity(), gioHangList, totalPriceTextView);
        cartAdapter.setOnSelectionChangedListener((selectedTotal, selectedCount) -> {
            // Hiển thị tổng tiền theo sản phẩm được chọn (nếu có chọn)
            if (selectedCount > 0) {
                totalPriceTextView.setText(String.valueOf(selectedTotal));
            } else {
                totalPriceTextView.setText(String.valueOf(gioHangManager.getTongTien()));
            }
            paymentButton.setEnabled(selectedCount > 0);
        });
        cartItemsView.setAdapter(cartAdapter);

        // Cập nhật tổng tiền ngay từ giỏ hàng
        totalPriceTextView.setText(String.valueOf(gioHangManager.getTongTien()));
        paymentButton.setEnabled(false);

        // Xử lý sự kiện click thanh toán
        paymentButton.setOnClickListener(v -> {
            if (!cartAdapter.hasSelection()) {
                Toast.makeText(getActivity(), "Vui lòng chọn sản phẩm để thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }
            // Chuyển sang màn hình thanh toán với danh sách sản phẩm đã chọn
            Intent intent = new Intent(getActivity(), com.example.tuan17.Checkout_Activity.class);
            ArrayList<com.example.tuan17.models.GioHang> selected = new ArrayList<>(cartAdapter.getSelectedItems());
            intent.putParcelableArrayListExtra("selectedItems", selected);
            intent.putExtra("selectedTotal", cartAdapter.getSelectedTotal());
            startActivity(intent);
        });
    }

    private void showPaymentDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.activity_thong_tin_thanh_toan);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);

        EditText edtTenKh = dialog.findViewById(R.id.tenkh);
        EditText edtDiaChi = dialog.findViewById(R.id.diachi);
        EditText edtSdt = dialog.findViewById(R.id.sdt);
        Button btnLuu = dialog.findViewById(R.id.btnxacnhandathang);
        TextView tvTongTien = dialog.findViewById(R.id.tienthanhtoan);

        String tongTien = totalPriceTextView.getText().toString();
        tvTongTien.setText(tongTien);

        btnLuu.setOnClickListener(v -> {
            String tenKh = edtTenKh.getText().toString().trim();
            String diaChi = edtDiaChi.getText().toString().trim();
            String sdt = edtSdt.getText().toString().trim();

            if (tenKh.isEmpty() || diaChi.isEmpty() || sdt.isEmpty()) {
                Toast.makeText(getActivity(), "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            } else {
                float tongThanhToan;
                try {
                    tongThanhToan = Float.parseFloat(tongTien.replace(",", ""));
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "Có lỗi xảy ra với tổng tiền!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int userId = SharedPrefHelper.getUserId(getActivity());

                String urlOrder = "http://10.0.2.2:3000/dathang";
                StringRequest request = new StringRequest(Request.Method.POST, urlOrder,
                        response -> {
                            try {
                                JSONObject obj = new JSONObject(response);
                                int orderId = obj.getInt("id");
                                List<GioHang> gioHangList = gioHangManager.getGioHangList();
                                for (GioHang item : gioHangList) {
                                    String masp = item.getSanPham().getMasp();
                                    int soluong = item.getSoLuong();
                                    float dongia = item.getSanPham().getDongia();
//                                    byte[] anhByte = item.getSanPham().getAnh();
//                                    String base64Image = Base64.encodeToString(anhByte, Base64.DEFAULT);
                                    String anh = item.getSanPham().getAnh();
                                    themChiTietDonHang(orderId, masp, soluong, dongia, anh);
                                }

                                Toast.makeText(getActivity(), "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                                gioHangManager.clearGioHang();
                                totalPriceTextView.setText("0");
                                cartAdapter.notifyDataSetChanged();
                                // Quay về HomeFragment
                                if (getActivity() != null) {
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .setCustomAnimations(
                                                    R.anim.slide_in_left,
                                                    R.anim.slide_out_right,
                                                    R.anim.slide_in_right,
                                                    R.anim.slide_out_left
                                            )
                                            .replace(R.id.fragment_container, new HomeFragment())
                                            .commit();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "Lỗi phân tích dữ liệu!", Toast.LENGTH_SHORT).show();
                            }
                        },
                        error -> {
                            Toast.makeText(getActivity(), "Lỗi đặt hàng!", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> data = new HashMap<>();
                        data.put("user_id", String.valueOf(userId));
                        data.put("tenkh", tenKh);
                        data.put("diachi", diaChi);
                        data.put("sdt", sdt);
                        data.put("tongthanhtoan", String.valueOf(tongThanhToan));
                        return data;
                    }
                };

                Volley.newRequestQueue(getActivity()).add(request);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void themChiTietDonHang(int idDonHang, String masp, int soluong, float dongia, String base64Anh) {
        String url = "http://10.0.2.2:3000/chitietdathang";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                },
                error -> {
                    error.printStackTrace();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_dathang", String.valueOf(idDonHang));
                params.put("masp", masp);
                params.put("soluong", String.valueOf(soluong));
                params.put("dongia", String.valueOf(dongia));
                params.put("anh", base64Anh);
                return params;
            }
        };

        Volley.newRequestQueue(getActivity()).add(request);
    }
}

