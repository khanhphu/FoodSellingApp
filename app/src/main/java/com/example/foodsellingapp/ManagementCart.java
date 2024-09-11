package com.example.foodsellingapp;

import android.content.Context;
import android.widget.Toast;

import com.example.foodsellingapp.Model.MonAn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ManagementCart {
    private Context context;
    private TinyDB tinyDB;
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    String uid=  firebaseAuth.getCurrentUser().getUid();
    //constructor


    public ManagementCart(Context context) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
    }

    public void insertFood(MonAn item) {
        ArrayList<MonAn> listpop = getListCart();
        boolean existAlready = false;
        int n = 0;
        for (int i = 0; i < listpop.size(); i++) {
            if (listpop.get(i).getTenMon().equals(item.getTenMon())) {
                existAlready = true;
                n = i;
                break;
            }
        }
        if(existAlready){
            listpop.get(n).setNumberinCart(item.getNumberinCart());
        }
        else{
            listpop.add(item);
        }

//        tinyDB.putListObject("CartList",listpop);
       tinyDB.putListObject(uid,listpop);
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show();
    }

    public ArrayList<MonAn> getListCart() {


       return tinyDB.getListObject(uid);
    }


        public  void removeAll(){


        }

    public Long getTotalFee(){
        ArrayList<MonAn> listItem=getListCart();
        long fee=0;
        for (int i = 0; i < listItem.size(); i++) {
            fee=fee+((listItem.get(i).getGia()+listItem.get(i).getPhuThu())*listItem.get(i).getNumberinCart());
        }
        return fee;
    }
    public int totalMon(){
        ArrayList<MonAn> listItem=getListCart();
        int sl=0;
        for (int i = 0; i < listItem.size(); i++) {
            sl=sl+(listItem.get(i).getNumberinCart());
        }
        return sl;
    }
    public String tenMon(){
        ArrayList<MonAn> listItem=getListCart();
        String tenMon="";
        for (int i = 0; i < listItem.size(); i++) {
           return listItem.get(i).getTenMon().toString();
        }
        return tenMon;
    }
    public void clearAll(){
        ArrayList<MonAn> listItem=getListCart();

        for (int i = 0; i < listItem.size(); i++) {
           listItem.clear();
           tinyDB.clear();
        }

    }
    public void minusNumberItem(ArrayList<MonAn> listItem,int position,ChangeNumberItemsListerner changeNumberItemsListener){
        if(listItem.get(position).getNumberinCart()==1){
            listItem.remove(position);
        }else{
            listItem.get(position).setNumberinCart(listItem.get(position).getNumberinCart()-1);
        }
        tinyDB.putListObject(uid,listItem);
        changeNumberItemsListener.change();
    }
    public  void plusNumberItem(ArrayList<MonAn> listItem,int position,ChangeNumberItemsListerner changeNumberItemsListener){
        listItem.get(position).setNumberinCart(listItem.get(position).getNumberinCart()+1);
        tinyDB.putListObject(uid,listItem);
        changeNumberItemsListener.change();
    }
    public  void removeItem(ArrayList<MonAn> listItem,int position,ChangeNumberItemsListerner changeNumberItemsListener){
        listItem.remove(position);
        tinyDB.putListObject(uid,listItem);
        changeNumberItemsListener.change();
    }
}
