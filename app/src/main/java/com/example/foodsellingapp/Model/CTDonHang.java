package com.example.foodsellingapp.Model;

public class CTDonHang {
private String maDH;
private int gia;
private String khuyenMai;
private int sl;
private String tenMon;
private String maMon;



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

    public CTDonHang(String maDH, int gia, String khuyenMai, int sl, String tenMon, String maMon) {
        this.maDH = maDH;
        this.gia = gia;
        this.khuyenMai = khuyenMai;
        this.sl = sl;
        this.tenMon = tenMon;
        this.maMon = maMon;

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


}

