package com.androidheroes.iqexpensemanager.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidheroes.iqexpensemanager.Adapter.AdapterTrans;
import com.androidheroes.iqexpensemanager.Constants;
import com.androidheroes.iqexpensemanager.Models.ModelTransaction;
import com.androidheroes.iqexpensemanager.Prefs;
import com.androidheroes.iqexpensemanager.R;
import com.androidheroes.iqexpensemanager.databinding.ActivityHomeBinding;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActivityHomeBinding binding;
    public String id, email, income, expense, symbol;
    public ProgressDialog progressDialog;
    private Integer balance;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private List<ModelTransaction> modelTransactions;
    private AdapterTrans adapterTrans;
    private AdLoader adLoader;
    private Prefs prefs;
    private Boolean premium = false;
    private final int REQUEST_CODE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        preferences = getSharedPreferences("currency", MODE_PRIVATE);
        if (!preferences.getString("symbol", "").isEmpty()) {
            symbol = preferences.getString("symbol", "");
        } else {
            symbol = "₹";
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please Wait!");

        email = getIntent().getStringExtra("email");

        prefs = new Prefs(this);

        checkForUpdate();

        //MobileAds.setRequestConfiguration(new RequestConfiguration().toBuilder().setTestDeviceIds(Arrays.asList("1c79c9af-6a01-400e-a45e-4919f606e1ae")).build());

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, binding.drawerLy, binding.toolbar, R.string.nav_open, R.string.nav_close);
        binding.drawerLy.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        binding.navigationView.setNavigationItemSelectedListener(this);
        View headerView = binding.navigationView.getHeaderView(0);
        TextView textViewNavEmail = headerView.findViewById(R.id.textViewNavEmail);
        textViewNavEmail.setText(email);

        loadUserId();

        binding.transactionRv.setLayoutManager(new LinearLayoutManager(this));
        modelTransactions = new ArrayList<>();

        binding.addTransactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, AddTransactionActivity.class));
            }
        });

        binding.swiperefreshLy.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadIncome();
                progressDialog.show();
            }
        });
    }

    private void loadIncome() {
        StringRequest request = new StringRequest(Request.Method.POST, Constants.mainUrl + "getincome.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                income = response;
                if (income.isEmpty()) {
                    binding.incomeTv.setText(symbol + "0");
                } else {
                    binding.incomeTv.setText(symbol + income);
                }
                loadExpense();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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

        Volley.newRequestQueue(HomeActivity.this).add(request);
    }

    private void loadExpense() {
        StringRequest request = new StringRequest(Request.Method.POST, Constants.mainUrl + "getexpense.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                expense = response;
                if (expense.isEmpty()) {
                    binding.expenseTv.setText(symbol + 0);
                    if (income.isEmpty()){
                        binding.balanceTv.setText(symbol + 0);
                    }else {
                        binding.balanceTv.setText(symbol + income);
                    }
                } else {
                    binding.expenseTv.setText(symbol + expense);
                    balance = Integer.parseInt(income) - Integer.parseInt(expense);
                    binding.balanceTv.setText(symbol + balance);
                }

                loadTransaction();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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

        Volley.newRequestQueue(HomeActivity.this).add(request);
    }

    private void loadUserId() {
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constants.mainUrl + "get.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        int userid = object.getInt("id");
                        int subscribed = object.getInt("subscribed");

                        id = String.valueOf(userid);
                        preferences = getSharedPreferences("userid", Context.MODE_PRIVATE);
                        editor = preferences.edit();
                        editor.putString("id", id);
                        editor.putInt("subscribed", subscribed);
                        editor.apply();

                        if (subscribed == 1) {
                            premium = true;
                            prefs.setPremium(1);
                        } else {
                            prefs.setPremium(0);
                            premium = false;

                            MobileAds.initialize(HomeActivity.this, new OnInitializationCompleteListener() {
                                @Override
                                public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                                }
                            });

                            binding.adView.setVisibility(View.VISIBLE);
                            AdRequest adRequest = new AdRequest.Builder().build();
                            binding.adView.loadAd(adRequest);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                loadIncome();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        Volley.newRequestQueue(HomeActivity.this).add(request);
    }

    private void loadTransaction() {
        StringRequest request = new StringRequest(Request.Method.POST, Constants.mainUrl + "gettransaction.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                modelTransactions.clear();
                try {
                    JSONArray array = new JSONArray(response);
                    if (array.isNull(0)){
                        binding.emptyTransaction.setVisibility(View.VISIBLE);
                        binding.transactionRv.setVisibility(View.GONE);
                    }else {
                        binding.emptyTransaction.setVisibility(View.GONE);
                        binding.transactionRv.setVisibility(View.VISIBLE);
                    }
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        int trans_id = object.getInt("trans_id");
                        String type = object.getString("type");
                        String category = object.getString("category");
                        int amount = object.getInt("amount");
                        String note = object.getString("note");
                        int userid = object.getInt("userid");
                        String timestamp = object.getString("timestamp");

                        ModelTransaction transaction = new ModelTransaction(trans_id, type, category, amount, note, userid, timestamp);
                        modelTransactions.add(transaction);
                    }
                } catch (Exception e) {
                    progressDialog.dismiss();
                    binding.swiperefreshLy.setRefreshing(false);
                    Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                adapterTrans = new AdapterTrans(modelTransactions, HomeActivity.this);
                binding.transactionRv.setAdapter(adapterTrans);
                progressDialog.dismiss();
                binding.swiperefreshLy.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                binding.swiperefreshLy.setRefreshing(false);
                Toast.makeText(HomeActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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

        Volley.newRequestQueue(HomeActivity.this).add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                break;
            case R.id.logout:
                preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                editor = preferences.edit();
                editor.remove("loggedIn");
                editor.clear();
                editor.apply();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.exit_dialog, viewGroup, false);
        Button exitBtn = dialogView.findViewById(R.id.exitBtn);
        Button cancelBtn = dialogView.findViewById(R.id.cancelBtn);
        TemplateView template = dialogView.findViewById(R.id.my_template);

        if (premium) {
            //not loading ads
        } else {
            template.setVisibility(View.VISIBLE);
            adLoader = new AdLoader.Builder(HomeActivity.this, Constants.nativeAdId)
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd NativeAd) {
                            // Show the ad.
                            if (adLoader.isLoading()) {
                                // The AdLoader is still loading ads.
                                // Expect more adLoaded or onAdFailedToLoad callbacks.
                            } else {
                                // The AdLoader has finished loading ads.
                            }

                            if (isDestroyed()) {
                                NativeAd.destroy();
                                return;
                            }

                            NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(new ColorDrawable(Color.WHITE)).build();
                            template.setStyles(styles);
                            template.setNativeAd(NativeAd);

                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(LoadAdError adError) {
                            // Handle the failure by logging, altering the UI, and so on.
                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder()
                            // Methods in the NativeAdOptions.Builder class can be
                            // used here to specify individual options settings.
                            .build())
                    .build();
            adLoader.loadAd(new AdRequest.Builder().build());
        }

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.transactions:
                break;
            case R.id.budget:
                startActivity(new Intent(HomeActivity.this, BudgetActivity.class));
                break;
            case R.id.bank:
                startActivity(new Intent(HomeActivity.this, BankActivity.class));
                break;
            case R.id.categories:
                startActivity(new Intent(HomeActivity.this, CategoriesActivity.class));
                break;
            case R.id.policy:
                startActivity(new Intent(HomeActivity.this, PrivacyPolicyActivity.class));
                break;
            case R.id.contact:
                startActivity(new Intent(HomeActivity.this, ContactUsActivity.class));
                break;
            case R.id.aboutus:
                startActivity(new Intent(HomeActivity.this, AboutUsActivity.class));
                break;
            case R.id.buy_premium:
                startActivity(new Intent(HomeActivity.this, PremiumActivity.class));
                break;
        }
        binding.drawerLy.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkForUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(HomeActivity.this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, HomeActivity.this, REQUEST_CODE);
                }catch (IntentSender.SendIntentException e){
                    e.printStackTrace();
                }
            }
        });
    }
}