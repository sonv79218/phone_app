package com.example.tuan17.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tuan17.R;
import com.example.tuan17.models.Order;

import java.util.List;
public class DonHang_Adapter extends ArrayAdapter<Order> {
    public DonHang_Adapter(Context context, List<Order> orders) {
        super(context, 0, orders);
    }

    @Override

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.ds_donhang, parent, false);
            }

            Order order = getItem(position);
        TextView txtMadh = convertView.findViewById(R.id.txtMahd);
            TextView txtTenKh = convertView.findViewById(R.id.txtTenKh);
            TextView txtDiaChi = convertView.findViewById(R.id.txtDiaChi);
            TextView txtSdt = convertView.findViewById(R.id.txtSdt);
            TextView txtTongThanhToan = convertView.findViewById(R.id.txtTongThanhToan);
            TextView txtNgayDatHang = convertView.findViewById(R.id.txtNgayDatHang);

        txtTenKh.setText(order.getTenKh());
            txtDiaChi.setText(order.getDiaChi());
            txtSdt.setText(order.getSdt());
            txtTongThanhToan.setText(String.valueOf(order.getTongTien()));
            txtNgayDatHang.setText(order.getNgayDatHang());
        txtMadh.setText(String.valueOf(order.getId()));
            return convertView;
        }
}
