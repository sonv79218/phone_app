package com.example.tuan17.adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuan17.GioHangManager;
import com.example.tuan17.R;
import com.example.tuan17.models.GioHang;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GioHangAdapter extends ArrayAdapter<GioHang> {
    private Context context;
    private List<GioHang> items;
    private TextView txtTongTien; // Tham chiếu tới TextView tổng tiền
    private GioHangManager gioHangManager;
    private final Set<String> selectedProductIds = new HashSet<>();
    private OnSelectionChangedListener selectionChangedListener;

    public interface OnSelectionChangedListener {
        void onSelectionChanged(float selectedTotal, int selectedCount);
    }

    public GioHangAdapter(Context context, List<GioHang> items, TextView txtTongTien) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        this.txtTongTien = txtTongTien;
        this.gioHangManager = GioHangManager.getInstance(); // Khởi tạo GioHangManager
        updateTongTien(); // Cập nhật tổng tiền ban đầu
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.selectionChangedListener = listener;
    }

    public List<GioHang> getSelectedItems() {
        List<GioHang> selected = new ArrayList<>();
        for (GioHang i : items) {
            if (selectedProductIds.contains(i.getSanPham().getMasp())) {
                selected.add(i);
            }
        }
        return selected;
    }

    public float getSelectedTotal() {
        float total = 0f;
        for (GioHang i : items) {
            if (selectedProductIds.contains(i.getSanPham().getMasp())) {
                total += i.getTongGia();
            }
        }
        return total;
    }

    public boolean hasSelection() {
        return !selectedProductIds.isEmpty();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.ds_giohang, parent, false);
        }

        TextView tensp = convertView.findViewById(R.id.tensp);
        ImageView imgSanPham = convertView.findViewById(R.id.imgsp);
        TextView masp = convertView.findViewById(R.id.masp);
        TextView dongia = convertView.findViewById(R.id.dongia);
        EditText soLuong = convertView.findViewById(R.id.soluongdat);
        ImageButton btnGiam = convertView.findViewById(R.id.btnTru);
        ImageButton btnTang = convertView.findViewById(R.id.btnCong);
        TextView xoasp = convertView.findViewById(R.id.xoasp);
        CheckBox cbChon = convertView.findViewById(R.id.cbChon);

        GioHang item = items.get(position);
        tensp.setText(item.getSanPham().getTensp());
        dongia.setText(String.valueOf(item.getSanPham().getDongia()));
        soLuong.setText(String.valueOf(item.getSoLuong()));
        masp.setText(item.getSanPham().getMasp());

        byte[] anhByteArray = item.getSanPham().getAnh();
        if (anhByteArray != null && anhByteArray.length > 0) {
            Bitmap imganh = BitmapFactory.decodeByteArray(anhByteArray, 0, anhByteArray.length);
            imgSanPham.setImageBitmap(imganh);
        } else {
            imgSanPham.setImageResource(R.drawable.vest);
        }

        // Checkbox state
        cbChon.setOnCheckedChangeListener(null);
        cbChon.setChecked(selectedProductIds.contains(item.getSanPham().getMasp()));
        cbChon.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String id = item.getSanPham().getMasp();
            if (isChecked) {
                selectedProductIds.add(id);
            } else {
                selectedProductIds.remove(id);
            }
            notifySelectionChanged();
        });

        // Thiết lập sự kiện cho nút tăng số lượng
        btnTang.setOnClickListener(v -> {
            int soluongKho = item.getSanPham().getSoluongkho();
            int soluongtronggio = item.getSoLuong();
            if (soluongtronggio < soluongKho) {
                gioHangManager.addItem(item.getSanPham());
                notifyDataSetChanged();
                updateTotals();
            } else {
                Toast.makeText(context, "Không thể tăng. Đã vượt quá số lượng trong kho!", Toast.LENGTH_SHORT).show();
            }
        });

        btnGiam.setOnClickListener(v -> {
            if (item.getSoLuong() > 1) {
                item.setSoLuong(item.getSoLuong() - 1);
            } else {
                if (position >= 0 && position < items.size()) {
                    selectedProductIds.remove(item.getSanPham().getMasp());
                    gioHangManager.removeItem(position); // chỉ cần xóa từ manager
                }
            }
            notifyDataSetChanged();
            updateTotals();
        });

        // Sửa số lượng bằng bàn phím
        soLuong.setSelectAllOnFocus(true);
        soLuong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String txt = s.toString().trim();
                if (txt.isEmpty()) return;
                try {
                    int val = Integer.parseInt(txt);
                    if (val < 1) val = 1;
                    int max = item.getSanPham().getSoluongkho();
                    if (val > max) {
                        val = max;
                        Toast.makeText(context, "Vượt quá số lượng trong kho!", Toast.LENGTH_SHORT).show();
                    }
                    if (val != item.getSoLuong()) {
                        item.setSoLuong(val);
                        updateTotals();
                    }
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        });

        xoasp.setOnClickListener(v -> {
            if (position >= 0 && position < items.size()) {
                selectedProductIds.remove(item.getSanPham().getMasp());
                gioHangManager.removeItem(position); // chỉ cần gọi cái này thôi
                notifyDataSetChanged(); // cập nhật lại giao diện
                updateTotals(); // cập nhật tổng
                Toast.makeText(context, "Sản phẩm đã được xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    private void updateTongTien() {
        float tongTien = gioHangManager.getTongTien(); // Lấy tổng tiền từ GioHangManager
        txtTongTien.setText(String.valueOf(tongTien)); // Cập nhật TextView
    }

    private void updateTotals() {
        updateTongTien();
        notifySelectionChanged();
    }

    private void notifySelectionChanged() {
        if (selectionChangedListener != null) {
            selectionChangedListener.onSelectionChanged(getSelectedTotal(), selectedProductIds.size());
        }
    }
}
