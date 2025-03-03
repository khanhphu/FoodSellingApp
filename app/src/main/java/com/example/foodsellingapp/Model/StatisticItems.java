package com.example.foodsellingapp.Model;

public class StatisticItems {
    public static final String TYPE_DONHANG = "DonHang";
    public static final String TYPE_PTTHANHTOAN = "ThanhToan";
   public static final String TYPE_MONAN = "MonAn";
    public static final String TYPE_KH = "KhachHang";

    private String id;
    private String type;
    private String name;
    private String status;
    private int quantity;
    private int orderCount; //Dem so luong mon an cho filter: Best-selling or Least-Selling;

    public StatisticItems(String id,String type, String name, String status, int quantity, int orderCount) {
        this.id = id;
        this.type=type;
        this.name = name;
        this.status = status;
        this.quantity = quantity;
        this.orderCount = orderCount;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getOrderCount() {
        return orderCount;
    }
}
