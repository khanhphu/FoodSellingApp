package com.example.foodsellingapp.Model;

public class DonHang {
private String maDH;
private String maKH;
private String ngayTao;
private int tongCong;
private int tongSL;
private String trangThai;
//ngay, gio xac nhan don (admin)
    private  String ngayXacNhan;

    public DonHang() {
    }

    public DonHang(String maDH, String maKH, String ngayTao, int tongCong, int tongSL, String trangThai, String ngayXacNhan) {
        this.maDH = maDH;
        this.maKH = maKH;
        this.ngayTao = ngayTao;
        this.tongCong = tongCong;
        this.tongSL = tongSL;
        this.trangThai = trangThai;
        this.ngayXacNhan=ngayXacNhan;
    }

    public String getMaDH() {
        return maDH;
    }

    public void setMaDH(String maDH) {
        this.maDH = maDH;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }

    public int getTongCong() {
        return tongCong;
    }

    public void setTongCong(int tongCong) {
        this.tongCong = tongCong;
    }

    public int getTongSL() {
        return tongSL;
    }

    public void setTongSL(int tongSL) {
        this.tongSL = tongSL;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getNgayXacNhan() {
        return ngayXacNhan;
    }

    public void setNgayXacNhan(String ngayXacNhan) {
        this.ngayXacNhan = ngayXacNhan;
    }
}
