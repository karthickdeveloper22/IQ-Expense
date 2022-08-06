package com.androidheroes.iqexpensemanager.Activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.androidheroes.iqexpensemanager.databinding.ActivityAddTransactionBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddTransactionActivity extends AppCompatActivity {

    ActivityAddTransactionBinding binding;
    private String id, type, category, amount, note, timestamp;
    private ProgressDialog progressDialog;
    private ArrayList<String> incomeLists;
    private ArrayList<String> expenseLists;
    private ArrayAdapter<String> incomeAdapter;
    private ArrayAdapter<String> expenseAdapter;
    private SharedPreferences preferences;
    private RewardedAd mrewardedAd;
    private int subscribed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait!");
        progressDialog.setCanceledOnTouchOutside(false);

        preferences = getSharedPreferences("userid", Context.MODE_PRIVATE);
        id = preferences.getString("id", "");
        subscribed = preferences.getInt("subscribed", 0);

        if (subscribed == 0){
            binding.adView.setVisibility(View.VISIBLE);
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                }
            });

            //MobileAds.setRequestConfiguration(new RequestConfiguration().toBuilder().setTestDeviceIds(Arrays.asList("1c79c9af-6a01-400e-a45e-4919f606e1ae")).build());
            AdRequest adRequestbanner = new AdRequest.Builder().build();
            binding.adView.loadAd(adRequestbanner);

            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(AddTransactionActivity.this, Constants.rewardedVideoAdId, adRequest, new RewardedAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Log.d("AdError", loadAdError.toString());
                    mrewardedAd = null;
                    super.onAdFailedToLoad(loadAdError);
                }

                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                    mrewardedAd = rewardedAd;
                    super.onAdLoaded(rewardedAd);
                }
            });
        }

        ArrayList<String> transactionTypeList = new ArrayList<String>();
        transactionTypeList.add("Income");
        transactionTypeList.add("Expense");
        ArrayAdapter adapter = new ArrayAdapter(AddTransactionActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, transactionTypeList);
        binding.transactionTypeTv.setAdapter(adapter);

        incomeLists = new ArrayList<>();
        expenseLists = new ArrayList<>();

        binding.transactionCategoryEt.setEnabled(false);
        binding.amountTv.setEnabled(false);
        binding.noteTv.setEnabled(false);
        binding.pickDateBtn.setEnabled(false);

        binding.transactionTypeTv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    getIncomeCategories();
                } else {
                    getExpenseCategories();
                }
                binding.transactionCategoryEt.setEnabled(true);
                binding.amountTv.setEnabled(true);
                binding.noteTv.setEnabled(true);
                binding.pickDateBtn.setEnabled(true);
            }
        });

        binding.addTransactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = binding.transactionCategoryEt.getText().toString();
                type = binding.transactionTypeTv.getText().toString();
                note = binding.noteTv.getText().toString();
                amount = binding.amountTv.getText().toString();

                if (category.isEmpty()) {
                    Toast.makeText(AddTransactionActivity.this, "Choose Category", Toast.LENGTH_SHORT).show();
                } else if (type.isEmpty()) {
                    Toast.makeText(AddTransactionActivity.this, "Choose Transaction Type", Toast.LENGTH_SHORT).show();
                } else if (amount.isEmpty()) {
                    Toast.makeText(AddTransactionActivity.this, "Enter Amount", Toast.LENGTH_SHORT).show();
                } else if (timestamp == null) {
                    Toast.makeText(AddTransactionActivity.this, "Choose Date & Time", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    addTransaction();
                }
            }
        });

        binding.pickDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimeDialog();
            }
        });
    }

    private void getIncomeCategories() {
        progressDialog.setMessage("Getting Categories");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Constants.mainUrl + "get_income_categories.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                incomeLists.clear();
                expenseLists.clear();
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String name = object.getString("name");
                        incomeLists.add(name);
                        incomeAdapter = new ArrayAdapter(AddTransactionActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, incomeLists);
                        binding.transactionCategoryEt.setAdapter(incomeAdapter);
                        progressDialog.dismiss();
                    }
                } catch (Exception e) {
                    Toast.makeText(AddTransactionActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddTransactionActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                return params;
            }
        };

        Volley.newRequestQueue(AddTransactionActivity.this).add(request);
    }

    private void getExpenseCategories() {
        progressDialog.setMessage("Getting Categories");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Constants.mainUrl + "get_expense_categories.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                incomeLists.clear();
                expenseLists.clear();
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String name = object.getString("name");
                        expenseLists.add(name);
                        expenseAdapter = new ArrayAdapter(AddTransactionActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, expenseLists);
                        binding.transactionCategoryEt.setAdapter(expenseAdapter);
                        progressDialog.dismiss();
                    }
                } catch (Exception e) {
                    Toast.makeText(AddTransactionActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddTransactionActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                return params;
            }
        };

        Volley.newRequestQueue(AddTransactionActivity.this).add(request);
    }

    private void showDateTimeDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy | HH:mm aa");

                        timestamp = simpleDateFormat.format(calendar.getTime());
                        binding.pickDateBtn.setText(timestamp);
                    }
                };

                new TimePickerDialog(AddTransactionActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        };

        new DatePickerDialog(AddTransactionActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void addTransaction() {
        StringRequest request = new StringRequest(Request.Method.POST, Constants.mainUrl + "add_transaction.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equalsIgnoreCase("success")) {
                    Toast.makeText(AddTransactionActivity.this, "Transaction Added!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    if (subscribed == 0){
                        if (mrewardedAd != null){
                            mrewardedAd.show(AddTransactionActivity.this, new OnUserEarnedRewardListener() {
                                @Override
                                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                    onBackPressed();
                                }
                            });
                        }else {
                            onBackPressed();
                        }
                    }else {
                        onBackPressed();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(AddTransactionActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(AddTransactionActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("type", type);
                params.put("category", category);
                params.put("note", note);
                params.put("amount", amount);
                params.put("userid", id);
                params.put("timestamp", timestamp);
                return params;
            }
        };

        Volley.newRequestQueue(AddTransactionActivity.this).add(request);
    }
}