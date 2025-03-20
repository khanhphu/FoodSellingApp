package com.example.foodsellingapp.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class DonHang implements Serializable {
private String maDH;
private String maKH;
private String ngayTao;
private int tongCong;
private int tongSL;
private String trangThai;
//ngay, gio xac nhan don (admin)
private  String ngayXacNhan;
//reason for huy
    private String lyDo;
    private String ptNhanHang;
    private String ptThanhToan;
    private String congThanhToan;
    //updated for detail order
    private ArrayList<CTDonHang> items; // Danh sách sản phẩm trong đơn hàng

    public DonHang() {
        items = new ArrayList<>();
    }

    public DonHang(String maDH, String maKH, String ngayTao, int tongCong, int tongSL, String trangThai, String ngayXacNhan, String lyDo, String ptNhanHang, String ptThanhToan, String congThanhToan) {
        this.maDH = maDH;
        this.maKH = maKH;
        this.ngayTao = ngayTao;
        this.tongCong = tongCong;
        this.tongSL = tongSL;
        this.trangThai = trangThai;
        this.ngayXacNhan=ngayXacNhan;
        this.lyDo=lyDo;
        this.ptNhanHang=ptNhanHang;
        this.ptThanhToan=ptThanhToan;
        this.congThanhToan=congThanhToan;
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

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public String getPtNhanHang() {
        return ptNhanHang;
    }

    public void setPtNhanHang(String ptNhanHang) {
        this.ptNhanHang = ptNhanHang;
    }

    public String getPtThanhToan() {
        return ptThanhToan;
    }

    public void setPtThanhToan(String ptThanhToan) {
        this.ptThanhToan = ptThanhToan;
    }

    public String getCongThanhToan() {
        return congThanhToan;
    }

    public void setCongThanhToan(String congThanhToan) {
        this.congThanhToan = congThanhToan;
    }


    public ArrayList<CTDonHang> getItems() {
        return items;
    }

    public void setItems(ArrayList<CTDonHang> items) {
        this.items = items;
    }
}
