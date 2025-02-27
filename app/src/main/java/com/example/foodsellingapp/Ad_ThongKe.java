package com.example.foodsellingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
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

import com.example.foodsellingapp.databinding.ActivityAdThongKeBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.type.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Ad_ThongKe extends AppCompatActivity {
    private ActivityAdThongKeBinding binding;
    private ImageView img_chooseDate;
    private EditText ed_date;
    //spinner value
    private String selectedOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdThongKeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });


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
                    RadioButton selectedButton = bottomSheetDialog.findViewById(checkedId);
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
                    RadioButton selectedButton = bottomSheetDialog.findViewById(checkedId);
                    binding.txtResultFilter.setText(selectedButton.getText().toString());
                }

            }
        });
        //Mon an- stock
        radioGroupStock.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId!=-1){
                    RadioButton selectedButton=bottomSheetDialog.findViewById(checkedId);
                    binding.txtResultFilter.setText(selectedButton.getText().toString());
                }
            }
        });
        //Mon an- selling
        radioGroupSelling.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId!=-1){
                    RadioButton selectedButton=bottomSheetDialog.findViewById(checkedId);
                    binding.txtResultFilter.setText(selectedButton.getText().toString());
                }
            }
        });
        //KH- Cus
        radioGroupCus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId!=-1){
                    RadioButton selectedButton=bottomSheetDialog.findViewById(checkedId);
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
        ImageView img_clear=sheetView.findViewById(R.id.img_clear);
        img_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroupCus.clearCheck();
                radioGroupPayment.clearCheck();
                radioGroupSelling.clearCheck();
                radioGroupStatus.clearCheck();
                radioGroupStock.clearCheck();
                //spinner
                Spinner spinner=bottomSheetDialog.findViewById(R.id.statisticFilter);
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
                if(ed_date.getText().toString().isEmpty()){
                    SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
                    ed_date.setText(sdf.format(Calendar.getInstance().getTime()).toString());
                }
                getValueFilter(selectedOptions, ed_date.getText().toString());

            }
        });

        bottomSheetDialog.show();
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
    public void clearCheckedRadioButton(RadioGroup radioGroup){
        radioGroup.clearCheck();
    }

}