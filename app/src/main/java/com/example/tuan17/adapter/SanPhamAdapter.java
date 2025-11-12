package com.example.tuan17.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.ChiTietSanPham_Activity;
import com.example.tuan17.R;
import com.example.tuan17.models.ChiTietSanPham;
import com.example.tuan17.models.NhomSanPham;
import com.example.tuan17.models.SanPham;
import com.example.tuan17.util.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SanPhamAdapter extends BaseAdapter {

    private Context context;
    private Uri selectedImageUri;
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private ArrayList<SanPham> productList;
    private boolean getViewAdminManagement;
    private ArrayList<NhomSanPham> mangNSPList = new ArrayList<>();
    //

    public interface OnProductGroupUpdatedListener {
        void onProductGroupUpdated();
    }

    private OnProductGroupUpdatedListener updateListener;

    public void setOnProductGroupUpdatedListener(OnProductGroupUpdatedListener listener) {
        this.updateListener = listener;
    }

    //
    public interface OnImageSelectListener {
        void onSelectImageRequested(SanPham product, ImageView previewImage);
    }

    private SanPhamAdapter.OnImageSelectListener onImageSelectListener;

    public void setOnImageSelectListener(SanPhamAdapter.OnImageSelectListener listener) {
        this.onImageSelectListener = listener;
    }
    //


    public SanPhamAdapter(Context context, ArrayList<SanPham> productList, boolean getViewAdminManagement) {
        this.context = context;
        this.productList = productList;
        this.getViewAdminManagement = getViewAdminManagement;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSelectedImageUri(Uri uri) {
        this.selectedImageUri = uri;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getViewAdminManagement ?
                getViewAdminManagement(position, convertView, parent) :
                getViewHome(position, convertView, parent);
    }

    private View getViewAdminManagement(int i, View view, ViewGroup parent) {
        View viewtemp = (view == null)
                ? LayoutInflater.from(parent.getContext()).inflate(R.layout.ds_sanpham, parent, false)
                : view;

        SanPham product = productList.get(i);
        TextView productIdTextView = viewtemp.findViewById(R.id.masp);
        TextView productNameTextView = viewtemp.findViewById(R.id.tensp);
        TextView productPriceTextView = viewtemp.findViewById(R.id.dongia);
        TextView productDescriptionTextView = viewtemp.findViewById(R.id.mota);
        TextView productNoteTextView = viewtemp.findViewById(R.id.ghichu);
        TextView productStockQuantityTextView = viewtemp.findViewById(R.id.soluongkho);
        TextView productGroupIdTextView = viewtemp.findViewById(R.id.manhomsanpham);
        ImageView productImageView = viewtemp.findViewById(R.id.imgsp);
        ImageButton editProductButton = viewtemp.findViewById(R.id.edit_product_group_button);
        ImageButton deleteProductButton = viewtemp.findViewById(R.id.delete_product_group_button);

        productIdTextView.setText(product.getMasp());
        productNameTextView.setText(product.getTensp());
        productPriceTextView.setText(String.valueOf(product.getDongia()));
        productDescriptionTextView.setText(product.getMota());
        productNoteTextView.setText(product.getGhichu());
        productStockQuantityTextView.setText(String.valueOf(product.getSoluongkho()));
        productGroupIdTextView.setText(product.getMansp());
        // Load ảnh từ file path
        String imagePath = product.getAnh();
        ImageLoader.loadFromFile(productImageView, imagePath, R.drawable.vest);

        editProductButton.setOnClickListener(view1 -> showEditDialog(product));
        deleteProductButton.setOnClickListener(v -> confirmDelete(parent, i, product));
        return viewtemp;
    }

    private void confirmDelete(ViewGroup parent, int i, SanPham tt) {
        new AlertDialog.Builder(parent.getContext())
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    // Prefer server id if available; fallback to masp
                    String resourceId = tt.getId() != null ? tt.getId() : tt.getMasp();
                    String url = "http://10.0.2.2:3000/sanpham/" + resourceId;

                    StringRequest request = new StringRequest(Request.Method.DELETE, url,
                            response -> {
                                productList.remove(i);
                                notifyDataSetChanged();
                                Toast.makeText(parent.getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                            },
                            error -> Toast.makeText(parent.getContext(), "Lỗi xóa sản phẩm", Toast.LENGTH_SHORT).show());
                    Volley.newRequestQueue(parent.getContext()).add(request);
                })
                .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                .show();
    }


    private View getViewHome(int i, View view, ViewGroup parent) {
        View viewtemp = (view == null)
                ? LayoutInflater.from(parent.getContext()).inflate(R.layout.ds_hienthi_gridview1_nguoidung, parent, false)
                : view;

        SanPham tt = productList.get(i);
        ((TextView) viewtemp.findViewById(R.id.masp)).setText(tt.getMasp());
        ((TextView) viewtemp.findViewById(R.id.tensp)).setText(tt.getTensp());
        ((TextView) viewtemp.findViewById(R.id.dongia)).setText(String.valueOf(tt.getDongia()));

        ImageView anh = viewtemp.findViewById(R.id.imgsp);
        // Load ảnh từ file path
        String imagePath = tt.getAnh();
        ImageLoader.loadFromFile(anh, imagePath, R.drawable.vest);

        viewtemp.setOnClickListener(v -> {
            Intent intent = new Intent(parent.getContext(), ChiTietSanPham_Activity.class);
            ChiTietSanPham chiTietSanPham = new ChiTietSanPham(
                    tt.getMasp(), tt.getTensp(), tt.getDongia(), tt.getMota(),
                    tt.getGhichu(), tt.getSoluongkho(), tt.getMansp(), tt.getAnh());
            intent.putExtra("chitietsanpham", chiTietSanPham);
            parent.getContext().startActivity(intent);
        });
        return viewtemp;
    }


    private void showEditDialog(SanPham product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.activity_sua_san_pham, null);
        builder.setView(dialogView);

        EditText editProductIdEditText = dialogView.findViewById(R.id.masp);
        EditText editProductNameEditText = dialogView.findViewById(R.id.tensp);
        EditText editProductPriceEditText = dialogView.findViewById(R.id.dongia);
        EditText editProductDescriptionEditText = dialogView.findViewById(R.id.mota);
        EditText editProductNoteEditText = dialogView.findViewById(R.id.ghichu);
        EditText editProductStockQuantityEditText = dialogView.findViewById(R.id.soluongkho);
        Spinner editProductGroupSpinner = dialogView.findViewById(R.id.manhomsanpham);
        ImageView editProductImagePreview = dialogView.findViewById(R.id.imgsp);
        Button editSelectImageButton = dialogView.findViewById(R.id.btnAddImg);

        loadTenNhomSanPham(editProductGroupSpinner);

        editProductIdEditText.setText(product.getMasp());
        editProductNameEditText.setText(product.getTensp());
        editProductPriceEditText.setText(String.valueOf(product.getDongia()));
        editProductDescriptionEditText.setText(product.getMota());
        editProductNoteEditText.setText(product.getGhichu());
        editProductStockQuantityEditText.setText(String.valueOf(product.getSoluongkho()));
//        hiển thị ảnh có sẵn
        ImageLoader.loadFromFile(editProductImagePreview, product.getAnh(), R.drawable.vest);

        // Khi bấm "Chọn ảnh" → gọi callback cho Activity
        editSelectImageButton.setOnClickListener(v -> {
            if (onImageSelectListener != null) {
                onImageSelectListener.onSelectImageRequested(product, editProductImagePreview);
            }
        });
        builder.setPositiveButton("Lưu", (dialog, which) ->
                updateSanPham(product, editProductIdEditText, editProductNameEditText, editProductPriceEditText, editProductDescriptionEditText, editProductNoteEditText, editProductStockQuantityEditText, editProductGroupSpinner, editProductImagePreview));

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void updateSanPham(
            SanPham product,
            @NonNull EditText editProductIdEditText,
            EditText editProductNameEditText,
            EditText editProductPriceEditText,
            EditText editProductDescriptionEditText,
            EditText editProductNoteEditText,
            EditText editProductStockQuantityEditText,
            Spinner editProductGroupSpinner,
            ImageView editProductImagePreview
    ) {
        // 🔹 Lấy dữ liệu từ các EditText
        String newProductId = editProductIdEditText.getText().toString().trim();
        String newProductName = editProductNameEditText.getText().toString().trim();
        String newDescription = editProductDescriptionEditText.getText().toString().trim();
        String newNote = editProductNoteEditText.getText().toString().trim();
        String newGroupId = ((NhomSanPham) editProductGroupSpinner.getSelectedItem()).getMa();

        // 🔹 Chuyển đổi giá & số lượng với kiểm tra lỗi
        float newPrice;
        int newStockQuantity;
        try {
            newPrice = Float.parseFloat(editProductPriceEditText.getText().toString().trim());
        } catch (NumberFormatException e) {
            newPrice = product.getDongia(); // giữ nguyên nếu lỗi
        }
        try {
            newStockQuantity = Integer.parseInt(editProductStockQuantityEditText.getText().toString().trim());
        } catch (NumberFormatException e) {
            newStockQuantity = product.getSoluongkho(); // giữ nguyên nếu lỗi
        }

        // 🔹 Nếu không nhập tên mới thì giữ nguyên
        if (newProductName.isEmpty()) {
            newProductName = product.getTensp();
        }
        if (newDescription.isEmpty()) {
            newDescription = product.getMota();
        }
        if (newNote.isEmpty()) {
            newNote = product.getGhichu();
        }

        // 🔹 Xử lý ảnh (chọn ảnh mới thì copy lại, không thì giữ ảnh cũ)
        String newImagePath = product.getAnh();
        if (selectedImageUri != null) {
            String copiedPath = copyImageToAppStorage(selectedImageUri);
            if (copiedPath != null) {
                newImagePath = copiedPath;
            } else {
                Toast.makeText(context, "Lỗi khi sao chép ảnh mới!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // 🔹 URL API PUT
        // Prefer server id if available; fallback to masp
        String resourceId = product.getId() != null ? product.getId() : product.getMasp();
        String url = "http://10.0.2.2:3000/sanpham/" + resourceId;

        // 🔹 Gửi yêu cầu cập nhật
        String finalNewProductName = newProductName;
        String finalNewDescription = newDescription;
        String finalNewNote = newNote;
        String finalNewImagePath = newImagePath;
        String finalNewGroupId = newGroupId;
        float finalNewPrice = newPrice;
        int finalNewStockQuantity = newStockQuantity;

        StringRequest request = new StringRequest(Request.Method.PUT, url,
                response -> {
                    product.setTensp(finalNewProductName);
                    product.setDongia(finalNewPrice);
                    product.setSoluongkho(finalNewStockQuantity);
                    product.setMansp(finalNewGroupId);
                    product.setAnh(finalNewImagePath);
                    product.setMota(finalNewDescription);
                    product.setGhichu(finalNewNote);

                    notifyDataSetChanged();
                    Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();

                    if (updateListener != null) updateListener.onProductGroupUpdated();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(context, "Lỗi cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public byte[] getBody() {
                try {
                    JSONObject json = new JSONObject();
                    // Include masp if backend uses it inside body
                    json.put("masp", product.getMasp());
                    json.put("tensp", finalNewProductName);
                    json.put("dongia", finalNewPrice);
                    json.put("mota", finalNewDescription);
                    json.put("ghichu", finalNewNote);
                    json.put("soluongkho", finalNewStockQuantity);
                    json.put("maso", finalNewGroupId);
                    json.put("picurl", finalNewImagePath != null ? finalNewImagePath : JSONObject.NULL);
                    return json.toString().getBytes();
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=UTF-8";
            }
        };


        Volley.newRequestQueue(context).add(request);
    }

    private void loadTenNhomSanPham(Spinner mansp) {
        String url = "http://10.0.2.2:3000/nhomsanpham";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        mangNSPList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            mangNSPList.add(new NhomSanPham(
                                    obj.getString("maso"),
                                    obj.getString("tennsp"), null));
                        }
                        ArrayAdapter<NhomSanPham> adapter = new ArrayAdapter<>(context,
                                android.R.layout.simple_spinner_item, mangNSPList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mansp.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(context, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show());
        Volley.newRequestQueue(context).add(request);
    }
    private String copyImageToAppStorage(Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            File directory = new File(context.getExternalFilesDir("images"), "");
            if (!directory.exists()) directory.mkdirs();

            String fileName = "img_" + System.currentTimeMillis() + ".png";
            File newFile = new File(directory, fileName);

            FileOutputStream outputStream = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();
            // In your API, you might host this file and return a URL; for now we keep local path
            return newFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Lỗi sao chép ảnh!", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
