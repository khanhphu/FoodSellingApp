package com.example.foodsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodsellingapp.Adapter.Ad_AdapterThongKe;
import com.example.foodsellingapp.Model.DonHang;
import com.example.foodsellingapp.Model.StatisticItems;
import com.example.foodsellingapp.databinding.ActivityAdThongKeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DateTime;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Ad_ThongKe extends AppCompatActivity {
    private ActivityAdThongKeBinding binding;
    private ImageView img_chooseDate;
    private EditText ed_date;

    private RecyclerView recyclerView;
    private Ad_AdapterThongKe ad_adapterThongKe;
    private FirebaseFirestore firestore;
    private ArrayList<StatisticItems> statisticItems;
    //khai bao cac thanh phan duoc chon cua bottom sheet filter
    //radio button
    private RadioButton selectedButton;
    private RadioButton sellingButton, stockButton;
    //spinner value
    private String selectedOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdThongKeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //firestore instance
        firestore = FirebaseFirestore.getInstance();
        //recycler view
        recyclerView = binding.listFilter;
        binding.btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });
        //btn refreshfp
        binding.btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
        //set adapter for recycle view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ad_adapterThongKe = new Ad_AdapterThongKe(statisticItems, this);
        recyclerView.setAdapter(ad_adapterThongKe);
        statisticItems = new ArrayList<>();
    }

    public void showCalendar() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Display selected date
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        ed_date.setText(selectedDate);
                    }
                }, year, month, day);

        datePickerDialog.show();
    }

    public void showBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_filter, null);
        bottomSheetDialog.setContentView(sheetView);
        //inital radio group
        RadioGroup radioGroupStatus = bottomSheetDialog.findViewById(R.id.radioGroupStatus);
        RadioGroup radioGroupPayment = bottomSheetDialog.findViewById(R.id.radioGroupPayment);
        RadioGroup radioGroupStock = bottomSheetDialog.findViewById(R.id.radioGroupStock);
        RadioGroup radioGroupSelling = bottomSheetDialog.findViewById(R.id.radioGroupSelling);
        RadioGroup radioGroupCus = bottomSheetDialog.findViewById(R.id.radioGroupCus);

        //handle radio group- button
        //Order- Status
        radioGroupStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    selectedButton = bottomSheetDialog.findViewById(checkedId);
                    binding.txtResultFilter.setText(selectedButton.getText().toString());
                }

            }
        });
        //PThuc thanh toan- Payment
        //add all radio button in radio group -> general radio button

        radioGroupPayment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    selectedButton = bottomSheetDialog.findViewById(checkedId);
                    binding.txtResultFilter.setText(selectedButton.getText().toString());
                }

            }
        });
        //Mon an- stock
        radioGroupStock.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    stockButton = bottomSheetDialog.findViewById(checkedId);
                    binding.txtResultFilter.setText(stockButton.getText().toString());
                }
            }
        });
        //Mon an- selling
        radioGroupSelling.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    sellingButton = bottomSheetDialog.findViewById(checkedId);
                    binding.txtResultFilter.setText(sellingButton.getText().toString());
                }
            }
        });
        //KH- Cus
        radioGroupCus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    selectedButton = bottomSheetDialog.findViewById(checkedId);
                    binding.txtResultFilter.setText(selectedButton.getText().toString());
                }
            }
        });
        // Find views inside the Bottom Sheet correctly
        img_chooseDate = sheetView.findViewById(R.id.iv_calendar); // Fix here
        ed_date = sheetView.findViewById(R.id.et_date); // Fix here

        img_chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar();
            }
        });
        ed_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar();
            }
        });
        //find spinner in bottom sheet
        Spinner statisticFilter = sheetView.findViewById(R.id.statisticFilter);
        //spinner data
        String[] statisticData = {"Đơn hàng", "Phương thức thanh toán", "Món ăn", "Khách hàng"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statisticData);
        statisticFilter.setAdapter(adapter);
        //handle item selection
        statisticFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position) != null) {
                    selectedOptions = parent.getItemAtPosition(position).toString();
                    binding.edtSearchFilter.setText(selectedOptions);
                    switch (selectedOptions) {
                        case "Đơn hàng":
                            setRadioGroupEnable(radioGroupStatus, true);
                            setRadioGroupEnable(radioGroupPayment, false);
                            setRadioGroupEnable(radioGroupStock, false);
                            setRadioGroupEnable(radioGroupSelling, false);
                            setRadioGroupEnable(radioGroupCus, false);
                            //clear check
                            clearCheckedRadioButton(radioGroupPayment);
                            clearCheckedRadioButton(radioGroupStock);
                            clearCheckedRadioButton(radioGroupSelling);
                            clearCheckedRadioButton(radioGroupCus);
                            break;


                        case "Phương thức thanh toán":
                            setRadioGroupEnable(radioGroupStatus, false);
                            setRadioGroupEnable(radioGroupPayment, true);
                            setRadioGroupEnable(radioGroupStock, false);
                            setRadioGroupEnable(radioGroupSelling, false);
                            setRadioGroupEnable(radioGroupCus, false);
                            clearCheckedRadioButton(radioGroupStatus);
                            clearCheckedRadioButton(radioGroupStock);
                            clearCheckedRadioButton(radioGroupSelling);
                            clearCheckedRadioButton(radioGroupCus);
                            break;

                        case "Món ăn":
                            setRadioGroupEnable(radioGroupStatus, false);
                            setRadioGroupEnable(radioGroupPayment, false);
                            setRadioGroupEnable(radioGroupStock, true);
                            setRadioGroupEnable(radioGroupSelling, true);
                            setRadioGroupEnable(radioGroupCus, false);
                            clearCheckedRadioButton(radioGroupStatus);
                            clearCheckedRadioButton(radioGroupPayment);
                            clearCheckedRadioButton(radioGroupCus);
                            break;
                        case "Khách hàng":
                            setRadioGroupEnable(radioGroupStatus, false);
                            setRadioGroupEnable(radioGroupPayment, false);
                            setRadioGroupEnable(radioGroupStock, false);
                            setRadioGroupEnable(radioGroupSelling, false);
                            setRadioGroupEnable(radioGroupCus, true);
                            clearCheckedRadioButton(radioGroupStatus);
                            clearCheckedRadioButton(radioGroupPayment);
                            clearCheckedRadioButton(radioGroupStock);
                            clearCheckedRadioButton(radioGroupSelling);
                            break;
                    }

                } else {
                    setRadioGroupEnable(radioGroupStatus, false);
                    setRadioGroupEnable(radioGroupPayment, false);
                    setRadioGroupEnable(radioGroupSelling, false);
                    setRadioGroupEnable(radioGroupStock, false);
                    setRadioGroupEnable(radioGroupCus, false);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setRadioGroupEnable(radioGroupStatus, false);
                setRadioGroupEnable(radioGroupPayment, false);
                setRadioGroupEnable(radioGroupSelling, false);
                setRadioGroupEnable(radioGroupStock, false);

            }
        });

        //button close
        ImageView img_close = sheetView.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        //button clear
        ImageView img_clear = sheetView.findViewById(R.id.img_clear);
        img_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroupCus.clearCheck();
                radioGroupPayment.clearCheck();
                radioGroupSelling.clearCheck();
                radioGroupStatus.clearCheck();
                radioGroupStock.clearCheck();
                //spinner
                Spinner spinner = bottomSheetDialog.findViewById(R.id.statisticFilter);
                spinner.setSelection(0); //tra ve gia tri default cho spinner
                ed_date.setText("");
                //in activity thong ke
                binding.edtSearchFilter.setText("");
                binding.txtResultDate.setText("");
                binding.txtResultFilter.setText("");


            }
        });

        //button apply
        Button btn_apply = sheetView.findViewById(R.id.btn_apply);
        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                //set current datetime if not selected date
                if (ed_date.getText().toString().isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    ed_date.setText(sdf.format(Calendar.getInstance().getTime()).toString());
                }
                getValueFilter(selectedOptions, ed_date.getText().toString());

                if ("Món ăn".equals(selectedOptions)) {
                    // Check if buttons are initialized
                    if (stockButton == null || sellingButton == null) {
                        Toast.makeText(Ad_ThongKe.this, "Error: Filter options not initialized", Toast.LENGTH_SHORT).show();
                        recreate();
                        return;
                    }

                    if (!stockButton.isChecked()) {
                        Toast.makeText(Ad_ThongKe.this, "Please select Stock", Toast.LENGTH_SHORT).show();
                        recreate();

                    } else if (!sellingButton.isChecked()) {
                        Toast.makeText(Ad_ThongKe.this, "Please select Selling", Toast.LENGTH_SHORT).show();
                        recreate();

                    } else {
                        fetchData();
                    }
                } else {
                    fetchData();

                }
            }


        });

        bottomSheetDialog.show();
    }

    public void fetchData() {
statisticItems.clear();
ad_adapterThongKe.notifyDataSetChanged();
        //Don Hang
        if (selectedOptions.equals("Đơn hàng")) {
            if (selectedButton != null) {
                String selectedButtonText = selectedButton.getText().toString();
                if (selectedButtonText.equals("Chờ xác nhận")) {
                    String status = "choXacNhan";
                    fetchStatusOrder(status);
                } else if (selectedButtonText.equals("Xác nhận")) {
                    String status = "XacNhan";
                    fetchStatusOrder(status);
                } else if (selectedButtonText.equals("Hủy")) {
                    String status = "Huy";
                    fetchStatusOrder(status);
                } else {
                    fetchAllOrder();
                }
                //
            } else {
                Toast.makeText(this, "Status for order is null now!", Toast.LENGTH_SHORT).show();
            }

        }
        //MonAn
        else if (selectedOptions.equals("Phương thức thanh toán")) {
            String selectedText = selectedButton.getText().toString();
            if (selectedText.equals("Thanh toán khi nhận hàng")) {
                String payment = "Tiền mặt";
                fetchPaymentMethod(payment);
            } else if (selectedText.equals("Thanh toán online")) {
                String payment = "Chuyển khoản";
                fetchPaymentMethod(payment);
            }
        } else if ("Món ăn".equals(selectedOptions)) {

            fetchMonAn(stockButton.getText().toString(), sellingButton.getText().toString());

        }
    }

    private void fetchMonAn(String stock, String selling) {
        // Clear the current list
        statisticItems.clear();
        Log.d("Firestore", "After clear - statisticItems size: " + statisticItems.size());

        // Map to store total quantity sold for each maMon
        Map<String, Integer> maMonSales = new HashMap<>();

        // Fetch all documents in the DonHang collection
        firestore.collection("DonHang")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("Firestore", "Fetched DonHang documents: " + queryDocumentSnapshots.size());
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(Ad_ThongKe.this, "No DonHang documents found", Toast.LENGTH_SHORT).show();
                        binding.listFilter.setVisibility(View.GONE);
                        return;
                    }

                    // Counter to keep track of processed DonHang documents
                    final int totalDocs = queryDocumentSnapshots.size();
                    final int[] processedDocs = {0};

                    // Loop through each DonHang document
                    for (QueryDocumentSnapshot donHangDoc : queryDocumentSnapshots) {
                        String dhID = donHangDoc.getId();
                        Log.d("Firestore", "Processing DonHang document: " + dhID);

                        // Access the CTDH subcollection (named "CT" + dhID)
                        String ctdhCollection = "CT" + dhID;
                        firestore.collection("DonHang")
                                .document(dhID)
                                .collection(ctdhCollection)
                                .get()
                                .addOnSuccessListener(ctdhSnapshots -> {
                                    Log.d("Firestore", "Fetched CTDH documents for DonHang " + dhID + ": " + ctdhSnapshots.size());

                                    // Loop through each CTDH document
                                    for (QueryDocumentSnapshot ctdhDoc : ctdhSnapshots) {
                                        String maMon = ctdhDoc.getString("maMon");
                                        Long soLuong = ctdhDoc.getLong("sl");
                                        if (maMon != null && soLuong != null) {
                                            // Update total quantity sold for this maMon
                                            maMonSales.put(maMon, maMonSales.getOrDefault(maMon, 0) + soLuong.intValue());
                                            Log.d("Firestore", "Found maMon: " + maMon + ", soLuong: " + soLuong + " in CTDH document: " + ctdhDoc.getId());
                                        } else {
                                            Log.w("Firestore", "maMon or soLuong is null for CTDH document: " + ctdhDoc.getId());
                                        }
                                    }

                                    // Increment processed documents counter
                                    processedDocs[0]++;

                                    // When all DonHang documents are processed, fetch MonAn data
                                    if (processedDocs[0] == totalDocs) {
                                        fetchAndFilterMonAn(maMonSales, stock, selling);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Error fetching CTDH for DonHang " + dhID, e);
                                    processedDocs[0]++;
                                    if (processedDocs[0] == totalDocs) {
                                        fetchAndFilterMonAn(maMonSales, stock, selling);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching DonHang documents", e);
                    Toast.makeText(Ad_ThongKe.this, "Error fetching DonHang: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.listFilter.setVisibility(View.GONE);
                });
    }

    private void fetchAndFilterMonAn(Map<String, Integer> maMonSales, String stock, String selling) {
        binding.txtCount.setVisibility(View.GONE);

        if (maMonSales.isEmpty()) {
            Toast.makeText(Ad_ThongKe.this, "No maMon values found in CTDH subcollections", Toast.LENGTH_SHORT).show();
            binding.listFilter.setVisibility(View.GONE);
            return;
        }

        // List to store filtered MonAn items
        List<StatisticItems> filteredMonAnItems = new ArrayList<>();

        // Counter to keep track of processed MonAn documents
        final int totalMaMon = maMonSales.size();
        final int[] processedMaMon = {0};

        // Fetch each MonAn document using maMon
        for (String maMon : maMonSales.keySet()) {
            firestore.collection("MonAn")
                    .document(maMon)
                    .get()
                    .addOnSuccessListener(monAnDoc -> {
                        if (monAnDoc.exists()) {
                            Long soLuongTon = monAnDoc.getLong("soLuong");
                            String tenMon = monAnDoc.getString("tenMon");
                            int orderCount = maMonSales.getOrDefault(maMon, 0);

                            if (soLuongTon != null && tenMon != null) {
                                // Create a StatisticItems object for this MonAn
                                StatisticItems monAnItem = new StatisticItems(
                                        maMon, StatisticItems.TYPE_MONAN, tenMon, "", soLuongTon.intValue(), orderCount);

                                // Apply stock filter
                                boolean passesStockFilter = false;
                                if ("In Stock".equals(stock)) {
                                    passesStockFilter = soLuongTon > 0;
                                } else if ("Out of Stock".equals(stock)) {
                                    passesStockFilter = soLuongTon <= 0;
                                } else {
                                    passesStockFilter = true; // No stock filter
                                }

                                // Apply selling filter (arbitrary thresholds for High/Low selling)
                                boolean passesSellingFilter = false;
                                if ("Bán chạy nhất".equals(selling)) {
                                    passesSellingFilter = orderCount > 10; // Example threshold for high selling
                                } else if ("Bán ít nhất".equals(selling)) {
                                    passesSellingFilter = orderCount <= 10; // Example threshold for low selling
                                } else {
                                    passesSellingFilter = true; // No selling filter
                                }

                                // Add MonAn item if it passes both filters
                                if (passesStockFilter && passesSellingFilter) {
                                    filteredMonAnItems.add(monAnItem);
                                    Log.d("Firestore", "Added MonAn: " + maMon + ", tenMon: " + tenMon + ", soLuongTon: " + soLuongTon + ", orderCount: " + orderCount);
                                }
                            } else {
                                Log.w("Firestore", "soLuongTon or tenMon is null for MonAn document: " + maMon);
                            }
                        } else {
                            Log.w("Firestore", "MonAn document does not exist: " + maMon);
                        }

                        // Increment processed maMon counter
                        processedMaMon[0]++;

                        // When all MonAn documents are processed, update the RecyclerView
                        if (processedMaMon[0] == totalMaMon) {
                            statisticItems.clear();
                            statisticItems.addAll(filteredMonAnItems);
                            ad_adapterThongKe.updateList(statisticItems);
                            if (statisticItems.isEmpty()) {
                                Toast.makeText(Ad_ThongKe.this, "No MonAn items match the filters", Toast.LENGTH_SHORT).show();
                                binding.listFilter.setVisibility(View.GONE);
                            } else {
                                binding.listFilter.setVisibility(View.VISIBLE);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error fetching MonAn document: " + maMon, e);
                        processedMaMon[0]++;
                        if (processedMaMon[0] == totalMaMon) {
                            statisticItems.clear();
                            statisticItems.addAll(filteredMonAnItems);
                            ad_adapterThongKe.updateList(statisticItems);
                            if (statisticItems.isEmpty()) {
                                Toast.makeText(Ad_ThongKe.this, "No MonAn items match the filters", Toast.LENGTH_SHORT).show();
                                binding.listFilter.setVisibility(View.GONE);
                            } else {
                                binding.listFilter.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }
    }

    private void fetchPaymentMethod(String payment) {

        firestore.collection("DonHang")
                .whereEqualTo("ptThanhToan", payment)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    long totalTongCong = 0;
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Long tongCong = doc.getLong("tongCong");
                        if (tongCong != null) {
                            totalTongCong += tongCong;
                        } else {
                            Log.w("Firestore", "tongCong is null for document: " + doc.getId());
                        }
                        statisticItems.add(new StatisticItems(doc.getId(), StatisticItems.TYPE_PTTHANHTOAN,
                                doc.getString("ptThanhToan"), doc.getString("trangThai"), 0, 0));
                    }
                    ad_adapterThongKe.updateList(statisticItems);
                    Log.d("Firestore", "Total tongCong: " + totalTongCong);
                    // Format the number with commas
                    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
                    binding.txtCount.setText(Format.formatVND(totalTongCong));
                    binding.txtCount.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error calculating total tongCong", e);
                    Toast.makeText(Ad_ThongKe.this, "Error calculating total: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    //hàm chuyển đổi ngày giờ sang ngày
    public String[] getDateRange(String selectedDate) {
        try {
            // Parse the selected date from "dd/MM/yyyy" format
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(selectedDate);

            // Create format for Firestore's ngayTao ("yyyy-MM-dd HH:mm:ss")
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());

            // Set start of day (00:00:00)
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            String startOfDay = outputFormat.format(calendar.getTime());

            // Set end of day (23:59:59)
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            String endOfDay = outputFormat.format(calendar.getTime());

            return new String[]{startOfDay, endOfDay};
        } catch (ParseException e) {
            Log.e("Ad_ThongKe", "Error parsing date: " + selectedDate, e);
            return new String[]{"", ""};
        }

    }


    private void fetchAllOrder() {
        binding.txtCount.setVisibility(View.GONE);
        statisticItems.clear();
        firestore.collection("DonHang").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot documentSnapshot : list) {
                    DonHang dh = documentSnapshot.toObject(DonHang.class);
                    statisticItems.add(new StatisticItems(dh.getMaDH(), dh.getTrangThai(), dh.getNgayTao(), dh.getTrangThai(), 0, 0));
                }
                ad_adapterThongKe.updateList(statisticItems);
                ad_adapterThongKe.notifyDataSetChanged();
            }
        });

    }

    private void fetchStatusOrder(String status) {
        binding.txtCount.setVisibility(View.GONE);
        //Handle datetime
        // Get the date range for the selected day
        String[] dateRange = getDateRange(ed_date.getText().toString());
        String startOfDay = dateRange[0];
        String endOfDay = dateRange[1];
        //data
        statisticItems.clear();
        firestore.collection("DonHang")
                .whereEqualTo("trangThai", status)
                .whereGreaterThanOrEqualTo("ngayTao", startOfDay) //MUON SD 2 CAU NAY  HI CAN PHAI LUU TREN CLOUD FIRESTORE- indexes
                .whereLessThanOrEqualTo("ngayTao", endOfDay)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            statisticItems.add(new StatisticItems(doc.getId(),
                                    doc.getString("ngayTao"),"DH", doc.getString("trangThai"), 0, 0));

                        }
                        Log.d("Firestore", "statisticItems size after fetch: " + statisticItems.size());
                        Log.d("Firestore", "Calling updateList with statisticItems: " + statisticItems.toString());
                        ad_adapterThongKe.updateList(statisticItems);
                        if (statisticItems.isEmpty()) {
                            binding.txtResultFilter.setText("Không có đơn hàng nào trong ngày " + binding.txtResultDate.getText().toString());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("DATE_CHANGE", "Error: " + e.getMessage());
                    }
                });
    }


    public void setRadioGroupEnable(RadioGroup group, boolean enable) {
        for (int i = 0; i < group.getChildCount(); i++) {
            group.getChildAt(i).setEnabled(enable);
        }
    }

    //get data from bottom sheet
    public void getValueFilter(String filterSelected, String dateSelected) {
        String value = filterSelected;
        binding.edtSearchFilter.setText(value);
        binding.txtResultDate.setText(dateSelected);
    }

    public void clearCheckedRadioButton(RadioGroup radioGroup) {
        radioGroup.clearCheck();
    }

}