package com.example.foodsellingapp.Model;

public class GioHang {
    private int gia;
    private String maMon;
    private String tenMon;
    private String url;
    private int soLuong;

    public GioHang(int gia, String maMon, String tenMon, String url, int soLuong) {
        this.gia = gia;
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.url = url;
        this.soLuong = soLuong;
    }

    public int getGia() {
        return gia;
    }

    public void setGia(int gia) {
        this.gia = gia;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }
}
