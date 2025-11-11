package com.example.tuan17.adapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tuan17.R;
import com.example.tuan17.models.NhomSanPham;
import com.example.tuan17.util.ImageLoader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NhomSanPhamAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<NhomSanPham> productGroupArrayList;
    private boolean showViewAdminManagement;
    private Uri selectedImageUri;

    public interface OnProductGroupUpdatedListener {
    void onProductGroupUpdated();
}

    private OnProductGroupUpdatedListener updateListener;

    public void setOnProductGroupUpdatedListener(OnProductGroupUpdatedListener listener) {
        this.updateListener = listener;
    }

    //
    public interface OnImageSelectListener {
        void onSelectImageRequested(NhomSanPham nhomSanPham, ImageView previewImage);
    }

    private OnImageSelectListener onImageSelectListener;

    public void setOnImageSelectListener(OnImageSelectListener listener) {
        this.onImageSelectListener = listener;
    }



    public NhomSanPhamAdapter(Activity context, ArrayList<NhomSanPham> nhomSanPhamList, boolean showFullDetails) {
        this.context = context;
        this.productGroupArrayList = nhomSanPhamList;
        this.showViewAdminManagement = showFullDetails;
    }

    @Override
    public int getCount() {
        return productGroupArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return productGroupArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return showViewAdminManagement
                ? getViewAdminManagement(position, convertView, parent)
                : getViewHome(position, convertView, parent);
    }

    private View getViewAdminManagement(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.ds_nhomsanpham, parent, false);
        }
// khai báo
        NhomSanPham productGroup = productGroupArrayList.get(position);
        TextView productGroupName = view.findViewById(R.id.product_group_name);
        TextView productGroupId = view.findViewById(R.id.product_group_id);
        ImageView productGroupImg = view.findViewById(R.id.product_group_img);
        ImageButton deleteProductGroupButton = view.findViewById(R.id.delete_product_group_button);
        ImageButton editProductGroupButton = view.findViewById(R.id.edit_product_group_button);

        productGroupId.setText(productGroup.getMa());
        productGroupName.setText(productGroup.getTennhom());
        // Load ảnh từ file path
        String imagePath = productGroup.getAnh();
        ImageLoader.loadFromFile(productGroupImg, imagePath, R.drawable.vest);
        editProductGroupButton.setOnClickListener(v -> showEditProductGroupDialog(productGroup));
        deleteProductGroupButton.setOnClickListener(v ->deleteProductGroup(productGroup.getMa(), position));

        return view;
    }

    private View getViewHome(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.ds_hienthi_gridview2_nguoidung, parent, false);
        }

        NhomSanPham nhomSanPham = productGroupArrayList.get(position);
        TextView ten = view.findViewById(R.id.product_group_name);
        TextView id = view.findViewById(R.id.product_group_id);
        ImageView anh = view.findViewById(R.id.product_group_img);

        id.setText(nhomSanPham.getMa());
        ten.setText(nhomSanPham.getTennhom());

        // Load ảnh từ file path
        String imagePath = nhomSanPham.getAnh();
        ImageLoader.loadFromFile(anh, imagePath, R.drawable.vest);

        return view;
    }

    private void deleteProductGroup(String maso, int position) {
        // Hiển thị hộp thoại xác nhận
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận xoá")
                .setMessage("Bạn có chắc chắn muốn xoá nhóm sản phẩm này không?")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    String url = "http://10.0.2.2:3000/nhomsanpham/" + maso;

                    StringRequest request = new StringRequest(Request.Method.DELETE, url,
                            response -> {
                                productGroupArrayList.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Đã xoá nhóm sản phẩm", Toast.LENGTH_SHORT).show();
                            },
                            error -> {
                                error.printStackTrace();
                                Toast.makeText(context, "Lỗi xoá nhóm sản phẩm", Toast.LENGTH_SHORT).show();
                            }
                    );

                    Volley.newRequestQueue(context).add(request);
                })
                .setNegativeButton("Huỷ", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showEditProductGroupDialog(NhomSanPham nhomSanPham) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.activity_sua_nhomsanpham, null);
        builder.setView(dialogView);

        EditText productGroupNameEditText = dialogView.findViewById(R.id.product_group_name);
        ImageView productGroupImgPreview = dialogView.findViewById(R.id.product_group_img);
        Button selectImageButton = dialogView.findViewById(R.id.btnAddImg);

        productGroupNameEditText.setText(nhomSanPham.getTennhom());
        ImageLoader.loadFromFile(productGroupImgPreview, nhomSanPham.getAnh(), R.drawable.vest);

        // Khi bấm "Chọn ảnh" → gọi callback cho Activity
        selectImageButton.setOnClickListener(v -> {
            if (onImageSelectListener != null) {
                onImageSelectListener.onSelectImageRequested(nhomSanPham, productGroupImgPreview);
            }
        });

        builder.setPositiveButton("Lưu", (dialog, which) -> updateGroupProduct(nhomSanPham, productGroupNameEditText));
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    private void updateGroupProduct(NhomSanPham nhomSanPham, EditText nameEditText) {
        String newGroupProductName = nameEditText.getText().toString().trim();
//        nếu mà có tên thì đổi k có thì thôi
        if (newGroupProductName.isEmpty()) {
            newGroupProductName = nhomSanPham.getTennhom();
        }
        String newImagePath = nhomSanPham.getAnh(); // giữ ảnh cũ mặc định

        // Nếu người dùng chọn ảnh mới, sao chép vào thư mục app
        if (selectedImageUri != null) {
            String copiedPath = copyImageToAppStorage(selectedImageUri);
            if (copiedPath != null) {
                newImagePath = copiedPath;
            } else {
                Toast.makeText(context, "Lỗi khi sao chép ảnh mới!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        String url = "http://10.0.2.2:3000/nhomsanpham/" + nhomSanPham.getMa();

        String finalImagePath = newImagePath;
        String finalNewTen = newGroupProductName;
        StringRequest request = new StringRequest(Request.Method.PUT, url,
                response -> {
                    nhomSanPham.setTennhom(finalNewTen);
                    nhomSanPham.setAnh(finalImagePath);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                          // ✅ Báo về Fragment
        if (updateListener != null) {
            updateListener.onProductGroupUpdated();
        }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(context, "Lỗi cập nhật nhóm sản phẩm", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tennsp", finalNewTen);
                params.put("picurl", finalImagePath); // gửi URL ảnh
                return params;
            }
        };

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
    public void setSelectedImageUri(Uri uri) {
        this.selectedImageUri = uri;
    }


}
