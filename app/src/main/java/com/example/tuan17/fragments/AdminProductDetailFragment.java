package com.example.tuan17.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tuan17.Login_Activity;
import com.example.tuan17.R;
import com.example.tuan17.models.ChiTietSanPham;

public class AdminProductDetailFragment extends Fragment {
    private ChiTietSanPham chiTietSanPham;
    String masp, tendn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_chi_tiet_san_pham_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tensp = view.findViewById(R.id.tensp);
        ImageView imgsp = view.findViewById(R.id.imgsp);
        TextView dongia = view.findViewById(R.id.dongia);
        TextView mota = view.findViewById(R.id.mota);
        TextView soluongkho = view.findViewById(R.id.soluongkho);
        TextView textTendn = view.findViewById(R.id.tendn);

        SharedPreferences sharedPre = getActivity().getSharedPreferences("MyPrefs", getActivity().MODE_PRIVATE);
        tendn = sharedPre.getString("tendn", null);

        if (tendn != null) {
            textTendn.setText(tendn);
        } else {
            Intent intent = new Intent(getActivity(), Login_Activity.class);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
            return;
        }

        Bundle args = getArguments();
        if (args != null) {
            masp = args.getString("masp");
            String tenspStr = args.getString("tensp");
            float dongiaFloat = args.getFloat("dongia");
            String motaStr = args.getString("mota");
            String ghichuStr = args.getString("ghichu");
            int soluongkhoInt = args.getInt("soluongkho");
            String maso = args.getString("maso");
            byte[] anh = args.getByteArray("anh");

            chiTietSanPham = new ChiTietSanPham(masp, tenspStr, dongiaFloat, motaStr, ghichuStr, soluongkhoInt, maso, anh);

            tensp.setText(tenspStr);
            dongia.setText(String.valueOf(dongiaFloat));
            mota.setText(motaStr != null ? motaStr : "Không có dữ liệu");
            soluongkho.setText(String.valueOf(soluongkhoInt));

            if (anh != null && anh.length > 0) {
                Bitmap imganhbs = BitmapFactory.decodeByteArray(anh, 0, anh.length);
                imgsp.setImageBitmap(imganhbs);
            } else {
                imgsp.setImageResource(R.drawable.vest);
            }
        } else {
            tensp.setText("Không có dữ liệu");
        }
    }
}

