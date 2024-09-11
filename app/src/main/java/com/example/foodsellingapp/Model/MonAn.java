package com.example.foodsellingapp.Model;

import java.io.Serializable;

public class MonAn implements Serializable {
   private int gia;
   private String maMon;
   private String tenMon;
   private String url;
   private int phuThu;
   private String gioiThieu;
   //shopping cart
    private int numberinCart;

    public MonAn() {
    }

    public MonAn(int gia, String maMon, String tenMon, String url, int phuThu, String gioiThieu, int numberinCart) {
        this.gia = gia;
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.url = url;
        this.phuThu = phuThu;
        this.gioiThieu = gioiThieu;
        this.numberinCart = numberinCart;
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

    public int getPhuThu() {
        return phuThu;
    }

    public void setPhuThu(int phuThu) {
        this.phuThu = phuThu;
    }

    public String getGioiThieu() {
        return gioiThieu;
    }

    public void setGioiThieu(String gioiThieu) {
        this.gioiThieu = gioiThieu;
    }

    public int getNumberinCart() {
        return numberinCart;
    }

    public void setNumberinCart(int numberinCart) {
        this.numberinCart = numberinCart;
    }
}
