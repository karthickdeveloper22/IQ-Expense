package com.androidheroes.iqexpensemanager.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.androidheroes.iqexpensemanager.Adapter.AdapterBankTransactions;
import com.androidheroes.iqexpensemanager.Constants;
import com.androidheroes.iqexpensemanager.Models.ModelBankTransactions;
import com.androidheroes.iqexpensemanager.R;
import com.androidheroes.iqexpensemanager.databinding.ActivityBankBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankActivity extends AppCompatActivity {

    private ActivityBankBinding binding;
    private ArrayList<String> bankLists;
    private ArrayList<String> bankIdLists;
    private String id, bankid;
    private int subscribed;
    private SharedPreferences preferences;
    private ProgressDialog progressDialog;
    private ArrayAdapter<String> adapterBanks;
    private List<ModelBankTransactions> bankTransactionsList;
    private AdapterBankTransactions adapterBankTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBankBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        preferences = getSharedPreferences("userid", Context.MODE_PRIVATE);
        id = preferences.getString("id", "");
        subscribed = preferences.getInt("subscribed", 0);

        progressDialog = new ProgressDialog(BankActivity.this);

        getBankAccounts();

        binding.addTransactionBtn.setOnClickListener(view -> {
            Intent intent = new Intent(BankActivity.this, AddBankTransactionsActivity.class);
            intent.putExtra("bank_id", bankid);
            startActivity(intent);
        });
    }

    private void getBankAccounts() {
        progressDialog.setMessage("Getting Bank Accounts");
        progressDialog.show();

        bankLists = new ArrayList<>();
        bankIdLists = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.POST, Constants.mainUrl + "get_user_bank_accounts.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                bankLists.clear();
                bankIdLists.clear();
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String name = object.getString("bank_name");
                        int bank_id = object.getInt("bank_id");
                        bankLists.add(name);
                        bankIdLists.add(String.valueOf(bank_id));
                        adapterBanks = new ArrayAdapter(BankActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, bankLists);
                        binding.bankAccountEt.setAdapter(adapterBanks);

                        binding.bankAccountEt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                bankid = bankIdLists.get(i);
                                loadBankTransactionDetails(bankIdLists.get(i));
                            }
                        });

                        progressDialog.dismiss();
                    }
                } catch (Exception e) {
                    Toast.makeText(BankActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BankActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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

        Volley.newRequestQueue(BankActivity.this).add(request);
    }

    private void loadBankTransactionDetails(String s) {
        progressDialog.setMessage("Getting Bank Details...");
        progressDialog.show();
        binding.transactionDetailsRl.setVisibility(View.VISIBLE);

        bankTransactionsList = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.POST, Constants.mainUrl + "get_bank_transactions.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                bankTransactionsList.clear();
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        int bank_trans_id = object.getInt("bank_trans_id");
                        String category = object.getString("category");
                        String type = object.getString("type");
                        int amount = object.getInt("amount");
                        String note = object.getString("note");
                        int userid = object.getInt("userid");
                        String timestamp = object.getString("timestamp");
                        int bank_id = object.getInt("bank_id");

                        ModelBankTransactions modelBankTransactions = new ModelBankTransactions(category, type, note, timestamp, amount, userid, bank_id, bank_trans_id);
                        bankTransactionsList.add(modelBankTransactions);
                    }
                } catch (Exception e) {
                    progressDialog.dismiss();
                    binding.swiperefreshLy.setRefreshing(false);
                    Toast.makeText(BankActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                adapterBankTransactions = new AdapterBankTransactions(bankTransactionsList, BankActivity.this);
                binding.transactionRv.setAdapter(adapterBankTransactions);
                binding.addTransactionBtn.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
                binding.swiperefreshLy.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                binding.swiperefreshLy.setRefreshing(false);
                Toast.makeText(BankActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("bank_id", s);
                params.put("userid", id);
                return params;
            }
        };

        Volley.newRequestQueue(BankActivity.this).add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bank_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_bank:
                AlertDialog.Builder builder = new AlertDialog.Builder(BankActivity.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(BankActivity.this).inflate(R.layout.add_bank_layout, viewGroup, false);
                TextInputEditText bankNameEt = dialogView.findViewById(R.id.bankNameEt);
                Button addBankBtn = dialogView.findViewById(R.id.addBankBtn);
                AdView adView = dialogView.findViewById(R.id.adView);

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

                addBankBtn.setOnClickListener(view -> {
                    String bank_name = bankNameEt.getText().toString().trim();

                    addBankAccount(id,bank_name);
                    alertDialog.dismiss();
                });

                alertDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addBankAccount(String id, String bank_name) {
        progressDialog.setMessage("Adding Bank Account");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constants.mainUrl + "add_bank_account.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equalsIgnoreCase("success")) {
                    progressDialog.dismiss();
                    Toast.makeText(BankActivity.this, "Bank Account Added!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(BankActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("bank", bank_name);
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