package com.example.tuan17.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.ChiTietSanPham_Activity;
import com.example.tuan17.DanhMucSanPham_Admin_Activity;
import com.example.tuan17.R;
import com.example.tuan17.SanPham_TrangChuAdmin_Adapter;
import com.example.tuan17.adapter.BannerAdapter;
import com.example.tuan17.adapter.NhomSanPham_trangChuadmin_Adapter;
import com.example.tuan17.models.NhomSanPham;
import com.example.tuan17.models.SanPham;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Handler;

public class AdminHomeFragment extends Fragment {
    GridView grv2;
    GridView grv1;
    ViewPager2 bannerPager;
    BannerAdapter bannerAdapter;
    Handler bannerHandler = new Handler();
    Runnable bannerRunnable;
    ArrayList<SanPham> mangSPgrv1;
    ArrayList<NhomSanPham> mangNSPgrv2;
    NhomSanPham_trangChuadmin_Adapter adapterGrv2;
    SanPham_TrangChuAdmin_Adapter adapterGrv1;
    String serverUrl = "http://10.0.2.2:3000";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_trangchu_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        grv2 = view.findViewById(R.id.grv2);
        grv1 = view.findViewById(R.id.grv1);
        bannerPager = view.findViewById(R.id.bannerPager);

        // Banner setup: 3 images, swipeable, auto-slide
        List<Integer> bannerImages = Arrays.asList(R.drawable.img_2, R.drawable.img_2, R.drawable.img_2);
        bannerAdapter = new BannerAdapter(bannerImages);
        bannerPager.setAdapter(bannerAdapter);
        bannerPager.setOffscreenPageLimit(1);
        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                if (bannerAdapter.getItemCount() == 0) return;
                int next = (bannerPager.getCurrentItem() + 1) % bannerAdapter.getItemCount();
                bannerPager.setCurrentItem(next, true);
                bannerHandler.postDelayed(this, 5000);
            }
        };
        bannerHandler.postDelayed(bannerRunnable, 5000);
        bannerPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bannerHandler.removeCallbacks(bannerRunnable);
                bannerHandler.postDelayed(bannerRunnable, 5000);
            }
        });

        grv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NhomSanPham nhomSanPham = mangNSPgrv2.get(position);
                Intent intent = new Intent(getContext(), DanhMucSanPham_Admin_Activity.class);
                intent.putExtra("nhomSpId", nhomSanPham.getMa());
                startActivity(intent);

//                if (nhomSanPham != null) {
//                    AdminCategoryFragment fragment = new AdminCategoryFragment();
//                    Bundle args = new Bundle();
//                    args.putString("nhomSpId", nhomSanPham.getMa());
//                    fragment.setArguments(args);
//
//                    if (getActivity() != null) {
//                        getActivity().getSupportFragmentManager().beginTransaction()
//                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
//                                .replace(R.id.fragment_container, fragment)
//                                .addToBackStack(null)
//                                .commit();
//                    }
//                }
            }
        });

        grv2 = view.findViewById(R.id.grv2);
        grv1 = view.findViewById(R.id.grv1);
        mangNSPgrv2 = new ArrayList<>();
        mangSPgrv1 = new ArrayList<>();
        adapterGrv2 = new NhomSanPham_trangChuadmin_Adapter(getActivity(), mangNSPgrv2, false);
        grv2.setAdapter(adapterGrv2);

        adapterGrv1 = new SanPham_TrangChuAdmin_Adapter(getActivity(), mangSPgrv1, false);
        grv1.setAdapter(adapterGrv1);

        grv1.setOnItemClickListener((parent, view1, position, id) -> {
            SanPham sanPham = mangSPgrv1.get(position);
            navigateToProductDetail(sanPham);
        });

        loadNhomSanPhamAPI();
        loadSanPhamAPI();
    }

    private void loadNhomSanPhamAPI() {
        String url = serverUrl + "/nhomsanpham/random?limit=8";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    mangNSPgrv2.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String ma = obj.optString("maso", "");
                            String ten = obj.optString("tennsp", "");
                            // Xử lý picurl có thể null
                            String picurl = obj.optString("picurl", null);
                            if (picurl != null && (picurl.equals("null") || picurl.isEmpty())) {
                                picurl = null;
                            }
                            mangNSPgrv2.add(new NhomSanPham(ma, ten, picurl));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapterGrv2.notifyDataSetChanged();
                },
                error -> Toast.makeText(getActivity(), "Không thể tải nhóm sản phẩm", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(getActivity()).add(request);
    }

    private void loadSanPhamAPI() {
        String url = serverUrl + "/sanpham/random?limit=8";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    mangSPgrv1.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            // Xử lý masp: nếu không có thì dùng maso, nếu maso cũng null thì dùng index
                            String masp = obj.optString("masp", null);
                            if (masp == null || masp.equals("null")) {
                                Object masoObj = obj.opt("maso");
                                if (masoObj != null && !masoObj.toString().equals("null")) {
                                    masp = String.valueOf(masoObj);
                                } else {
                                    masp = "SP" + i;
                                }
                            }
                            
                            String tensp = obj.optString("tensp", "");
                            // Xử lý dongia: có thể là string hoặc number
                            float dongia = 0;
                            if (obj.has("dongia") && !obj.isNull("dongia")) {
                                if (obj.get("dongia") instanceof String) {
                                    dongia = Float.parseFloat(obj.getString("dongia"));
                                } else {
                                    dongia = (float) obj.getDouble("dongia");
                                }
                            }
                            String mota = obj.optString("mota", "");
                            String ghichu = obj.optString("ghichu", "");
                            int soluongkho = obj.optInt("soluongkho", 0);
                            // Xử lý maso có thể null
                            Object masoObj = obj.opt("maso");
                            String maso = (masoObj != null && !masoObj.toString().equals("null")) ? String.valueOf(masoObj) : null;
                            // Xử lý picurl có thể null
                            String anh = obj.optString("picurl", null);
                            if (anh != null && (anh.equals("null") || anh.isEmpty())) {
                                anh = null;
                            }

                            mangSPgrv1.add(new SanPham(masp, tensp, dongia, mota, ghichu, soluongkho, maso, anh));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                    adapterGrv1.notifyDataSetChanged();
                },
                error -> Toast.makeText(getActivity(), "Không thể tải sản phẩm", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(getActivity()).add(request);
    }

    private void navigateToProductDetail(SanPham sanPham) {
        AdminProductDetailFragment fragment = new AdminProductDetailFragment();
        Bundle args = new Bundle();
        args.putString("masp", sanPham.getMasp());
        args.putString("tensp", sanPham.getTensp());
        args.putFloat("dongia", sanPham.getDongia());
        args.putString("mota", sanPham.getMota());
        args.putString("ghichu", sanPham.getGhichu());
        args.putInt("soluongkho", sanPham.getSoluongkho());
        args.putString("maso", sanPham.getMansp());
        args.putString("anh", sanPham.getAnh());
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

    @Override
    public void onPause() {
        super.onPause();
        bannerHandler.removeCallbacks(bannerRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bannerRunnable != null) {
            bannerHandler.postDelayed(bannerRunnable, 3000);
        }
    }
}

