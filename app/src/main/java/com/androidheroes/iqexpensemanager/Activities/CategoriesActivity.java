package com.androidheroes.iqexpensemanager.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidheroes.iqexpensemanager.Adapter.ViewPagerAdapter;
import com.androidheroes.iqexpensemanager.Constants;
import com.androidheroes.iqexpensemanager.R;
import com.androidheroes.iqexpensemanager.databinding.ActivityCategoriesBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoriesActivity extends AppCompatActivity {

    ActivityCategoriesBinding binding;
    public String id;
    private ProgressDialog progressDialog;
    private String file_name;
    private SharedPreferences preferences;
    private InterstitialAd minterstitialAd;
    private int subscribed;
    private int incomeSize,expenseSize;

    public ArrayList<String> expenseLists, incomeLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = getSharedPreferences("userid", Context.MODE_PRIVATE);
        id = preferences.getString("id", "");
        subscribed = preferences.getInt("subscribed", 0);

        if (subscribed == 0){
            AdRequest adRequest1 = new AdRequest.Builder().build();

            InterstitialAd.load(CategoriesActivity.this, Constants.interstitialAdId, adRequest1, new InterstitialAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    minterstitialAd = null;
                    Toast.makeText(CategoriesActivity.this, "" + loadAdError, Toast.LENGTH_SHORT).show();
                    super.onAdFailedToLoad(loadAdError);
                }

                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    minterstitialAd = interstitialAd;
                    super.onAdLoaded(interstitialAd);
                }
            });
        }

        progressDialog = new ProgressDialog(CategoriesActivity.this);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        binding.viewPager.setAdapter(adapter);
        binding.tabsLayout.setupWithViewPager(binding.viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.categories_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.addCategories:

                if (subscribed == 0) {
                    if (minterstitialAd != null){
                        minterstitialAd.show(CategoriesActivity.this);
                    }else {
                        Log.d("AdError", "Ad is not Ready");
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(CategoriesActivity.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(CategoriesActivity.this).inflate(R.layout.new_categories_layout, viewGroup, false);
                EditText newCategoryNameEt = dialogView.findViewById(R.id.newCategoryNameEt);
                AutoCompleteTextView categoryTypeEt = dialogView.findViewById(R.id.categoryTypeEt);
                Button addCategoryBtn = dialogView.findViewById(R.id.addCategoryBtn);
                AdView adView = dialogView.findViewById(R.id.adView);

                ArrayList<String> transactionTypeList = new ArrayList<String>();
                transactionTypeList.add("Income");
                transactionTypeList.add("Expense");
                ArrayAdapter adapter = new ArrayAdapter(CategoriesActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, transactionTypeList);
                categoryTypeEt.setAdapter(adapter);

                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();

                if (subscribed == 0){
                    MobileAds.initialize(this, new OnInitializationCompleteListener() {
                        @Override
                        public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                        }
                    });

                    //MobileAds.setRequestConfiguration(new RequestConfiguration().toBuilder().setTestDeviceIds(Arrays.asList("1c79c9af-6a01-400e-a45e-4919f606e1ae")).build());
                    adView.setVisibility(View.VISIBLE);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    adView.loadAd(adRequest);
                }else {
                    adView.removeAllViews();
                }

                categoryTypeEt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i == 0){
                            file_name = "add_income_categories.php";
                            if (subscribed == 0){
                                loadIncome();
                            }
                        }else {
                            file_name = "add_expense_categories.php";
                            if (subscribed == 0){
                                loadExpense();
                            }
                        }
                    }
                });

                addCategoryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = newCategoryNameEt.getText().toString().trim();
                        if (subscribed == 0){
                            if (incomeSize >= 2){
                                Toast.makeText(CategoriesActivity.this, "Limits Reached! Upgrade to Premium to add Categories!", Toast.LENGTH_SHORT).show();
                            }else  if (expenseSize >= 2){
                                Toast.makeText(CategoriesActivity.this, "Limits Reached! Upgrade to Premium to add Categories!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                if (file_name == null){
                                    Toast.makeText(CategoriesActivity.this, "Select Category Type", Toast.LENGTH_SHORT).show();
                                }else if (name.isEmpty()){
                                    Toast.makeText(CategoriesActivity.this, "Enter Category Name", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    addCategory(name,id);
                                    alertDialog.dismiss();
                                }
                            }
                        }else {
                            if (file_name == null){
                                Toast.makeText(CategoriesActivity.this, "Select Category Type", Toast.LENGTH_SHORT).show();
                            }else if (name.isEmpty()){
                                Toast.makeText(CategoriesActivity.this, "Enter Category Name", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                addCategory(name,id);
                                alertDialog.dismiss();
                            }
                        }
                    }
                });
                alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadIncome() {
        progressDialog.setMessage("getting Info!");
        progressDialog.show();
        incomeLists = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.POST, Constants.mainUrl + "get_user_income_categories.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                incomeLists.clear();
                try {
                    JSONArray array = new JSONArray(response);
                    if (response.equalsIgnoreCase("[]")){
                        progressDialog.dismiss();
                    }else {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            String name = object.getString("name");

                            incomeLists.add(name);
                            incomeSize = incomeLists.size();

                            if (incomeSize >= 2){
                                Toast.makeText(CategoriesActivity.this, "Upgrade Premium to Add Categories!", Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(CategoriesActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CategoriesActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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

        Volley.newRequestQueue(CategoriesActivity.this).add(request);
    }

    private void loadExpense() {
        progressDialog.setMessage("getting Info!");
        progressDialog.show();
        expenseLists = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.POST, Constants.mainUrl + "get_user_expense_categories.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                expenseLists.clear();
                try {
                    JSONArray array = new JSONArray(response);
                    if (response.equalsIgnoreCase("[]")){
                        progressDialog.dismiss();
                    }else {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            String name = object.getString("name");

                            expenseLists.add(name);
                            expenseSize = expenseLists.size();
                            if (expenseSize >= 2){
                                Toast.makeText(CategoriesActivity.this, "Upgrade Premium to Add Categories!", Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(CategoriesActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CategoriesActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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

        Volley.newRequestQueue(CategoriesActivity.this).add(request);
    }

    private void addCategory(String name, String id) {
        progressDialog.setMessage("Adding Category");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constants.mainUrl + file_name, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equalsIgnoreCase("success")) {
                    progressDialog.dismiss();
                    Toast.makeText(CategoriesActivity.this, "Category Added!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(CategoriesActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("name", name);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}