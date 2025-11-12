package com.example.tuan17;

import com.example.tuan17.models.ChiTietSanPham;
import com.example.tuan17.models.GioHang;

import java.util.ArrayList;
import java.util.List;

public class GioHangManager {
    private static GioHangManager instance;
    private List<GioHang> items;

    private GioHangManager() {
        items = new ArrayList<>();
    }

    public List<GioHang> getGioHangList() {
        return items;
    }

    public static GioHangManager getInstance() {
        if (instance == null) {
            instance = new GioHangManager();
        }
        return instance;
    }

    public List<GioHang> getItems() {
        return items;
    }

    public void addItem(ChiTietSanPham sanPham) {
        for (GioHang item : items) {
            String masp1 = item.getSanPham().getMasp();
            String masp2 = sanPham.getMasp();

            // So sánh kỹ: nếu cả hai đều null thì dùng tên sản phẩm thay thế
            boolean sameProduct = false;

            if (masp1 != null && masp2 != null) {
                sameProduct = masp1.equals(masp2);
            } else if (masp1 == null && masp2 == null) {
                sameProduct = item.getSanPham().getTensp().equalsIgnoreCase(sanPham.getTensp());
            }

            if (sameProduct) {
                item.setSoLuong(item.getSoLuong() + 1);
                return;
            }
        }

        // Nếu sản phẩm chưa có trong giỏ → thêm mới
        items.add(new GioHang(sanPham, 1));
    }

    public void removeItem(int position) {
        if (position >= 0 && position < items.size()) {

            items.remove(position);
        }

}



    public float getTongTien() {
        float tong = 0;
        for (GioHang item : items) {
            tong += item.getTongGia();
        }
        return tong;
    }

    public void removeByProductIds(java.util.Set<String> productIds) {
        if (productIds == null || productIds.isEmpty()) return;
        java.util.Iterator<GioHang> it = items.iterator();
        while (it.hasNext()) {
            GioHang gh = it.next();
            if (productIds.contains(gh.getSanPham().getMasp())) {
                it.remove();
            }
        }
    }
    public void resetTongTien() {
        // Không cần làm gì ở đây, tổng tiền sẽ tự động trở về 0
        // khi giỏ hàng trống, nhưng có thể dùng để thông báo nếu cần.
        // Bạn có thể tạo một biến tổng tiền và quản lý nó tại đây nếu cần.
    }
    // Xóa toàn bộ giỏ hàng
    public void clearGioHang() {
        items.clear(); // Xóa danh sách giỏ hàng
        resetTongTien(); // Đặt tổng tiền về 0 nếu cần
    }



}