package com.androidheroes.iqexpensemanager.Activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidheroes.iqexpensemanager.Constants;
import com.androidheroes.iqexpensemanager.databinding.ActivityEditTransactionBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditTransactionActivity extends AppCompatActivity {

    private ActivityEditTransactionBinding binding;
    private ProgressDialog progressDialog;
    private String trans_id, type, category, note, timestamp, amount, id;
    private SharedPreferences preferences;
    private int subscribed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        trans_id = getIntent().getStringExtra("trans_id");
        type = getIntent().getStringExtra("type");
        category = getIntent().getStringExtra("category");
        amount = getIntent().getStringExtra("amount");
        note = getIntent().getStringExtra("note");
        timestamp = getIntent().getStringExtra("timestamp");

        progressDialog = new ProgressDialog(this);

        preferences = getSharedPreferences("userid", Context.MODE_PRIVATE);
        id = preferences.getString("id", "");
        subscribed = preferences.getInt("subscribed", 0);

        if (subscribed == 0){
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                }
            });

            //MobileAds.setRequestConfiguration(new RequestConfiguration().toBuilder().setTestDeviceIds(Arrays.asList("1c79c9af-6a01-400e-a45e-4919f606e1ae")).build());
            AdRequest adRequest = new AdRequest.Builder().build();
            binding.adView.loadAd(adRequest);
        }

        setValues();

        binding.pickDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimeDialog();
            }
        });

        binding.updateTransactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = binding.transactionTypeTv.getText().toString().trim();
                category = binding.transactionCategoryEt.getText().toString().trim();
                amount = binding.amountTv.getText().toString().trim();
                note = binding.noteTv.getText().toString().trim();

                if (type.isEmpty()){
                    Toast.makeText(EditTransactionActivity.this, "Choose Type", Toast.LENGTH_SHORT).show();
                }else if (category.isEmpty()){
                    Toast.makeText(EditTransactionActivity.this, "Choose Category", Toast.LENGTH_SHORT).show();
                }else if (amount.toString().isEmpty()){
                    Toast.makeText(EditTransactionActivity.this, "Enter Amount", Toast.LENGTH_SHORT).show();
                }else if (timestamp.isEmpty()){
                    Toast.makeText(EditTransactionActivity.this, "Choose Date & Time", Toast.LENGTH_SHORT).show();
                }else {
                    updateTransactionDetails();
                }
            }
        });
    }

    private void setValues() {
        if (!type.isEmpty()){
            binding.transactionTypeTv.setText(type);
        }
        if (!category.isEmpty()) {
            binding.transactionCategoryEt.setText(category);
        }
        if (!amount.isEmpty()) {
            binding.amountTv.setText(amount);
        }
        if (!note.isEmpty()) {
            binding.noteTv.setText(note);
        }
        if (!timestamp.isEmpty()) {
            binding.pickDateBtn.setText(timestamp);
        }
    }

    private void updateTransactionDetails() {
        progressDialog.setTitle("Updating");
        progressDialog.setMessage("Please Wait!");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constants.mainUrl + "update_transaction.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equalsIgnoreCase("success")){
                    progressDialog.dismiss();
                    Toast.makeText(EditTransactionActivity.this, "Transaction Updated!", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(EditTransactionActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(EditTransactionActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("trans_id", trans_id);
                params.put("type", type);
                params.put("category", category);
                params.put("note", note);
                params.put("amount", amount);
                params.put("timestamp", timestamp);
                return params;
            }
        };

        Volley.newRequestQueue(EditTransactionActivity.this).add(request);
    }

    private void showDateTimeDialog() {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy | HH:mm aa");

                        timestamp = simpleDateFormat.format(calendar.getTime());
                        binding.pickDateBtn.setText(timestamp);
                    }
                };

                new TimePickerDialog(EditTransactionActivity.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };

        new DatePickerDialog(EditTransactionActivity.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}