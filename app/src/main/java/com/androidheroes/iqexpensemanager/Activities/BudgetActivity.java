package com.androidheroes.iqexpensemanager.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidheroes.iqexpensemanager.Adapter.AdapterBudgets;
import com.androidheroes.iqexpensemanager.Constants;
import com.androidheroes.iqexpensemanager.Models.ModelBudgets;
import com.androidheroes.iqexpensemanager.R;
import com.androidheroes.iqexpensemanager.databinding.ActivityBudgetBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BudgetActivity extends AppCompatActivity {

    private ActivityBudgetBinding binding;
    private String id;
    public String symbol;
    private ProgressDialog progressDialog;
    private List<ModelBudgets> modelBudgetsList;
    private SharedPreferences preferences;
    private RewardedAd mrewardedAd;
    private int subscribed;
    private int budgetSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBudgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        preferences = getSharedPreferences("userid", Context.MODE_PRIVATE);
        id = preferences.getString("id", "");
        subscribed = preferences.getInt("subscribed", 0);

        preferences = getSharedPreferences("currency", MODE_PRIVATE);
        if (!preferences.getString("symbol", "").isEmpty()) {
            symbol = preferences.getString("symbol", "");
        } else {
            symbol = "₹";
        }

        progressDialog = new ProgressDialog(BudgetActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);

        binding.budgetRv.setLayoutManager(new LinearLayoutManager(BudgetActivity.this));
        modelBudgetsList = new ArrayList<>();

        getBudget();

        if (subscribed == 0){
            binding.adView.setVisibility(View.VISIBLE);
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                }
            });

            AdRequest adRequestBanner = new AdRequest.Builder().build();
            binding.adView.loadAd(adRequestBanner);

            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(BudgetActivity.this, Constants.rewardedVideoAdId, adRequest, new RewardedAdLoadCallback() {
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

        binding.addBudgetFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (budgetSize>=2 && subscribed == 0){
                    Toast.makeText(BudgetActivity.this, "Buy Premium to add More Budgets!", Toast.LENGTH_SHORT).show();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BudgetActivity.this);
                    ViewGroup viewGroup = findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(BudgetActivity.this).inflate(R.layout.add_budget_dialog, viewGroup, false);
                    EditText budgetTitleEt = dialogView.findViewById(R.id.budgetTitleEt);
                    EditText budgetAmountEt = dialogView.findViewById(R.id.budgetAmountEt);
                    EditText budgetDescriptionEt = dialogView.findViewById(R.id.budgetDescriptionEt);
                    Button add_budget_Btn = dialogView.findViewById(R.id.addBudgetBtn);
                    AdView adView = dialogView.findViewById(R.id.adView);
                    builder.setView(dialogView);
                    AlertDialog alertDialog = builder.create();

                    if (subscribed == 0){
                        adView.setVisibility(View.VISIBLE);
                        MobileAds.initialize(BudgetActivity.this, new OnInitializationCompleteListener() {
                            @Override
                            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                            }
                        });

                        //MobileAds.setRequestConfiguration(new RequestConfiguration().toBuilder().setTestDeviceIds(Arrays.asList("1c79c9af-6a01-400e-a45e-4919f606e1ae")).build());
                        AdRequest adRequest = new AdRequest.Builder().build();
                        adView.loadAd(adRequest);
                    }

                    add_budget_Btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String title = budgetTitleEt.getText().toString().trim();
                            String amount = budgetAmountEt.getText().toString().trim();
                            String description = budgetDescriptionEt.getText().toString().trim();
                            String timestamp = "" + System.currentTimeMillis();

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(Long.parseLong(timestamp));
                            String timestampFormat = DateFormat.format("dd/MM/yyyy | hh:mm aa", calendar).toString();

                            if (title.isEmpty()) {
                                Toast.makeText(BudgetActivity.this, "Enter Budget Title", Toast.LENGTH_SHORT).show();
                            } else if (amount.isEmpty()) {
                                Toast.makeText(BudgetActivity.this, "Enter Budget Amount", Toast.LENGTH_SHORT).show();
                            } else {
                                if (description.isEmpty()) {
                                    description = "No Description";
                                }
                                addBudget(id, title, amount, timestampFormat, description);
                                alertDialog.dismiss();
                            }
                        }
                    });
                    alertDialog.show();
                }
            }
        });

        binding.swiperefreshLy.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBudget();
            }
        });
    }

    private void getBudget() {
        progressDialog.setMessage("Getting Budgets...");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constants.mainUrl + "get_budget.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                modelBudgetsList.clear();
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        int budget_id = object.getInt("budget_id");
                        int spend = object.getInt("spend");
                        String title = object.getString("title");
                        String description = object.getString("description");
                        int amount = object.getInt("amount");
                        int userid = object.getInt("userid");
                        String timestamp = object.getString("timestamp");

                        ModelBudgets budgets = new ModelBudgets(title, description, timestamp, amount, budget_id, userid, spend);
                        modelBudgetsList.add(budgets);
                    }
                } catch (Exception e) {
                    progressDialog.dismiss();
                    binding.swiperefreshLy.setRefreshing(false);
                    Toast.makeText(BudgetActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                AdapterBudgets adapterBudgets = new AdapterBudgets(modelBudgetsList, BudgetActivity.this);
                binding.budgetRv.setAdapter(adapterBudgets);
                budgetSize = modelBudgetsList.size();
                progressDialog.dismiss();
                binding.swiperefreshLy.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                binding.swiperefreshLy.setRefreshing(false);
                Toast.makeText(BudgetActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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

        Volley.newRequestQueue(BudgetActivity.this).add(request);
    }

    private void addBudget(String id, String title, String amount, String timestampFormat, String description) {
        progressDialog.setMessage("Adding Budget");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constants.mainUrl + "add_budget.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equalsIgnoreCase("success")) {
                    progressDialog.dismiss();
                    Toast.makeText(BudgetActivity.this, "Budget Added!", Toast.LENGTH_SHORT).show();
                    if (subscribed == 0){
                        if (mrewardedAd != null){
                            mrewardedAd.show(BudgetActivity.this, new OnUserEarnedRewardListener() {
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
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(BudgetActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("title", title);
                params.put("amount", amount);
                params.put("description", description);
                params.put("spend", "0");
                params.put("timestamp", timestampFormat);
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