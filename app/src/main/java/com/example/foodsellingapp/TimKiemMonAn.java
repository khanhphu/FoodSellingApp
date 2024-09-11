package com.example.foodsellingapp;

import android.widget.Filter;

import com.example.foodsellingapp.Adapter.User_AdpaterMonAn;
import com.example.foodsellingapp.Model.MonAn;

import java.util.ArrayList;

public class TimKiemMonAn extends Filter {
    ArrayList<MonAn> arr_mon;
    User_AdpaterMonAn adpaterMonAn;

    public TimKiemMonAn(ArrayList<MonAn> arr_mon, User_AdpaterMonAn adpaterMonAn) {
        this.arr_mon = arr_mon;
        this.adpaterMonAn = adpaterMonAn;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults=new FilterResults();
        if(constraint!=null && constraint.length()>0) {
            constraint = constraint.toString().toUpperCase();
            ArrayList<MonAn> filterSearch = new ArrayList<>();
            for (int i = 0; i < arr_mon.size(); i++) {
                if (arr_mon.get(i).getTenMon().toUpperCase().contains(constraint)) {
                    filterSearch.add(arr_mon.get(i));
                }
            }

            filterResults.count = filterSearch.size();
            filterResults.values=filterSearch;

        }
        else{
            filterResults.count=arr_mon.size();
            filterResults.values=arr_mon;
        }
        return filterResults;
    }
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
          adpaterMonAn.arrMon  = (ArrayList<MonAn>) results.values;
        adpaterMonAn.notifyDataSetChanged();
    }
}
