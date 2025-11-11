package com.example.tuan17.fragments;

import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.R;
import com.example.tuan17.adapter.SanPham_TimKiem_Adapter;
import com.example.tuan17.models.SanPham;
import android.view.inputmethod.EditorInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private GridView grv;
    private ArrayList<SanPham> productList;
    private SanPham_TimKiem_Adapter productAdapter;
    private LinearLayout suggestionsContainer;
    private EditText timkiem;
    private TextView emptyView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tim_kiem_san_pham, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 🔹 Ánh xạ View
        timkiem = view.findViewById(R.id.timkiem);
        grv = view.findViewById(R.id.grv);
        suggestionsContainer = view.findViewById(R.id.suggestionsContainer); // thêm vào XML
        emptyView = view.findViewById(R.id.emptyView);

        // 🔹 Khởi tạo adapter
        productList = new ArrayList<>();
        productAdapter = new SanPham_TimKiem_Adapter(getActivity(), productList, false);
        grv.setAdapter(productAdapter);

        // 🔹 Khi nhấn Enter hoặc click vào EditText
        timkiem.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        timkiem.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId != EditorInfo.IME_ACTION_SEARCH) return false;
            String query = timkiem.getText().toString().trim();
            if (!query.isEmpty()) {
                timKiemSanPham(query);
                showSuggestions(false);
            } else {
                Toast.makeText(getActivity(), "Vui lòng nhập tên sản phẩm", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        timkiem.setOnClickListener(v -> showSuggestions(true));

        // 🔹 Hiển thị gợi ý tìm kiếm ban đầu
        populateSuggestions(new String[]{"iPhone", "Samsung", "Laptop", "Tai nghe", "Phụ kiện"});

        // 🔹 Bắt sự kiện click sản phẩm
        grv.setOnItemClickListener((parent, view1, position, id) -> {
            SanPham sanPham = productList.get(position);
            navigateToProductDetail(sanPham);
        });

        toggleEmptyState(false);
    }

    // 🔍 Gọi API tìm kiếm sản phẩm
    private void timKiemSanPham(String keyword) {
        String url = "http://10.0.2.2:3000/sanpham/search?name=" + keyword;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    productList.clear();
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray data = response.getJSONArray("data");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
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
                                String picurl = obj.optString("picurl", null);
                                if (picurl != null && (picurl.equals("null") || picurl.isEmpty())) {
                                    picurl = null;
                                }

                                productList.add(new SanPham(
                                        masp,
                                        tensp,
                                        dongia,
                                        mota,
                                        ghichu,
                                        soluongkho,
                                        maso,
                                        picurl
                                ));
                            }

                            productAdapter.notifyDataSetChanged();
                            toggleEmptyState(productList.isEmpty());
                        } else {
                            productList.clear();
                            productAdapter.notifyDataSetChanged();
                            toggleEmptyState(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        toggleEmptyState(true);
                        Toast.makeText(getActivity(), "Lỗi xử lý phản hồi từ máy chủ!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    toggleEmptyState(true);
                    Toast.makeText(getActivity(), "Lỗi kết nối đến máy chủ", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }

    // 🔹 Hiển thị chi tiết sản phẩm
    private void navigateToProductDetail(SanPham sanPham) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString("masp", sanPham.getMasp());
        args.putString("tensp", sanPham.getTensp());
        args.putFloat("dongia", sanPham.getDongia());
        args.putString("mota", sanPham.getMota());
        args.putString("ghichu", sanPham.getGhichu());
        args.putInt("soluongkho", sanPham.getSoluongkho());
        args.putString("maso", sanPham.getMansp());
        args.putString("picurl", sanPham.getAnh());
        fragment.setArguments(args);

        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    // 💡 Hiển thị gợi ý tìm kiếm (như chip tags)
    private void populateSuggestions(String[] keywords) {
        if (suggestionsContainer == null) return;
        suggestionsContainer.removeAllViews();

        for (String kw : keywords) {
            TextView chip = new TextView(requireContext());
            chip.setText(kw);
            chip.setTextColor(0xFF072349);
            chip.setPadding(32, 16, 32, 16);
            chip.setBackgroundResource(R.drawable.bg_chip_suggestion);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 0, 16, 16);
            chip.setLayoutParams(lp);

            chip.setOnClickListener(v -> {
                timkiem.setText(kw);
                timkiem.setSelection(kw.length());
                showSuggestions(false);
                timKiemSanPham(kw);
            });

            suggestionsContainer.addView(chip);
        }
        showSuggestions(true);
    }

    private void showSuggestions(boolean show) {
        if (suggestionsContainer != null) {
            suggestionsContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void toggleEmptyState(boolean showEmpty) {
        if (emptyView != null) {
            emptyView.setVisibility(showEmpty ? View.VISIBLE : View.GONE);
        }
    }
}
