package com.example.foodsellingapp.Model;

import java.io.Serializable;

public class CTDonHang implements Serializable {
private String maDH;
private int gia;
private String khuyenMai;
private int sl;
private String tenMon;
private String maMon;

//update for CTDH show image each item
    private  String itemImage;

    public CTDonHang() {
    }

//    public DonHang(String maDH, int gia, String khuyenMai, int sl, String tenMon, String maMon) {
//        this.maDH = maDH;
//        this.gia = gia;
//        this.khuyenMai = khuyenMai;
//        this.sl = sl;
//        this.tenMon = tenMon;
//        this.maMon = maMon;
//    }

    public CTDonHang(String maDH, int gia, String khuyenMai, int sl, String tenMon, String maMon,String itemImage) {
        this.maDH = maDH;
        this.gia = gia;
        this.khuyenMai = khuyenMai;
        this.sl = sl;
        this.tenMon = tenMon;
        this.maMon = maMon;
        this.itemImage=itemImage;

    }
    // Calculate effective price (assuming khuyenMai is a discount percentage or amount)
    public double getEffectivePrice() {
        if (khuyenMai != null && !khuyenMai.isEmpty()) {
            try {
                double discount = Double.parseDouble(khuyenMai); // Assume percentage for now
                return gia * (1 - discount / 100);
            } catch (NumberFormatException e) {
                return gia; // No discount if parsing fails
            }
        }
        return gia;
    }


    public String getMaDH() {
        return maDH;
    }

    public void setMaDH(String maDH) {
        this.maDH = maDH;
    }
    public int getGia() {
        return gia;
    }

    public void setGia(int gia) {
        this.gia = gia;
    }

    public String getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(String khuyenMai) {
        this.khuyenMai = khuyenMai;
    }

    public int getSl() {
        return sl;
    }

    public void setSl(int sl) {
        this.sl = sl;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }
}

