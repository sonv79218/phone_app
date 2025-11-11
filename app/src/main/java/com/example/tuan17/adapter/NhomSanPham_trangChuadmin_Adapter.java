package com.example.tuan17.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tuan17.R;
import com.example.tuan17.models.NhomSanPham;
import com.example.tuan17.util.ImageLoader;

import java.util.ArrayList;

public class NhomSanPham_trangChuadmin_Adapter  extends BaseAdapter {
    private Context context;
    private ArrayList<NhomSanPham> nhomSanPhamList;
    private boolean showFullDetails;

    public NhomSanPham_trangChuadmin_Adapter(Activity context, ArrayList<NhomSanPham> nhomSanPhamList, boolean showFullDetails) {
        this.context = context;
        this.nhomSanPhamList = nhomSanPhamList;
        this.showFullDetails = showFullDetails;
    }

    @Override
    public int getCount() {
        return nhomSanPhamList.size();
    }

    @Override
    public Object getItem(int position) {
        return nhomSanPhamList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (showFullDetails) {
            return getViewTrue(position, convertView, parent);
        } else {
            return getViewFalse(position, convertView, parent);
        }
    }

    private View getViewTrue(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.ds_nhomsanpham, parent, false);
        }

        NhomSanPham nhomSanPham = nhomSanPhamList.get(position);
        TextView ten = view.findViewById(R.id.product_group_name);
        TextView id = view.findViewById(R.id.product_group_id);
        ImageView imgnsp = view.findViewById(R.id.product_group_img);
        ImageButton xoa = view.findViewById(R.id.delete_product_group_button);
        ImageButton sua = view.findViewById(R.id.edit_product_group_button);

        id.setText(nhomSanPham.getMa());
        ten.setText(nhomSanPham.getTennhom());

        // Load ảnh từ file path
        String imagePath = nhomSanPham.getAnh();
        ImageLoader.loadFromFile(imgnsp, imagePath, R.drawable.vest);
        return view;
    }

    private View getViewFalse(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.ds_hienthi_gridview2_nguoidung, parent, false);
        }

        NhomSanPham nhomSanPham = nhomSanPhamList.get(position);
        TextView ten = view.findViewById(R.id.product_group_name);
        TextView id = view.findViewById(R.id.product_group_id);
        ImageView imgnsp = view.findViewById(R.id.product_group_img);

        id.setText(nhomSanPham.getMa());
        ten.setText(nhomSanPham.getTennhom());

        // Load ảnh từ file path
        String imagePath = nhomSanPham.getAnh();
        ImageLoader.loadFromFile(imgnsp, imagePath, R.drawable.vest);

        return view;
    }


}
