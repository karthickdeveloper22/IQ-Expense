package com.androidheroes.iqexpensemanager.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidheroes.iqexpensemanager.Constants;
import com.androidheroes.iqexpensemanager.Prefs;
import com.androidheroes.iqexpensemanager.databinding.ActivityPremiumBinding;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PremiumActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    private ActivityPremiumBinding binding;
    private Prefs prefs;
    private BillingClient billingClient;
    private SharedPreferences preferences;
    private String id;
    private int subscribed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPremiumBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences("userid", Context.MODE_PRIVATE);
        id = preferences.getString("id", "");
        subscribed = preferences.getInt("subscribed", 0);

        prefs = new Prefs(PremiumActivity.this);

        billingClient = BillingClient.newBuilder(PremiumActivity.this)
                .setListener(this)
                .enablePendingPurchases()
                .build();

        connectGooglePlay();

        if (subscribed == 1){
            binding.buyPremiumBtn.setVisibility(View.GONE);
            binding.premiumAmount.setText("You are a Premium Member!");
            prefs.setPremium(1);
        }else {
            binding.buyPremiumBtn.setVisibility(View.VISIBLE);
        }

        binding.buyPremiumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (billingClient.isReady()){
                    showProducts();
                }else {
                    Toast.makeText(PremiumActivity.this, "Not Ready!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void connectGooglePlay() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Toast.makeText(PremiumActivity.this, "Started", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                connectGooglePlay();
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }

    private void showProducts() {
        ImmutableList<QueryProductDetailsParams.Product> productList  = ImmutableList.of(
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("premium_sub")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build());

        QueryProductDetailsParams queryProductDetailsParams = QueryProductDetailsParams.newBuilder().setProductList(productList).build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(BillingResult billingResult,
                                                         List<ProductDetails> productDetailsList) {
                        for (ProductDetails productDetails : productDetailsList){
                            List<ProductDetails.OneTimePurchaseOfferDetails> oneTimePurchasedetails = Collections.singletonList(productDetails.getOneTimePurchaseOfferDetails());
                            assert oneTimePurchasedetails != null;
                            binding.buyPremiumBtn.setText("Processing...");
                            lauchFlow(productDetails);
                        }
                        // check billingResult
                        // process returned productDetailsList
                    }
                }
        );
    }

    private void lauchFlow(ProductDetails productDetails) {
        assert productDetails.getOneTimePurchaseOfferDetails() != null;

        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParams = ImmutableList.of(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                        // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                        .setProductDetails(productDetails)
                        // to get an offer token, call ProductDetails.getSubscriptionOfferDetails()
                        // for a list of offers that are available to the user
                        .build()
        );

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParams)
                .build();

        // Launch the billing flow
        BillingResult billingResult = billingClient.launchBillingFlow(PremiumActivity.this, billingFlowParams);
        Toast.makeText(this, "" + billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
    }

    private void verifySubPurchase(Purchase purchase) {
        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();

        billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                Toast.makeText(this, "You are Premium User!", Toast.LENGTH_SHORT).show();
                prefs.setPremium(1);
            }else {
                Toast.makeText(this, "Not Subscribed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void onResume() {
        super.onResume();
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(),
                (billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (Purchase purchase : list) {
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                                verifySubPurchase(purchase);
                            }
                        }
                    }
                }
        );
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null){
            for (Purchase purchase : list){
                Toast.makeText(this, "Purchase Successful!", Toast.LENGTH_SHORT).show();
                StringRequest request = new StringRequest(Request.Method.POST, Constants.mainUrl + "update_subscription.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equalsIgnoreCase("success")) {
                            Toast.makeText(PremiumActivity.this, "Restart App for Changes!", Toast.LENGTH_SHORT).show();
                            binding.buyPremiumBtn.setVisibility(View.GONE);
                            binding.premiumAmount.setText("You are a Premium Member!");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PremiumActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("id", id);
                        params.put("subscribed", String.valueOf(1));
                        return params;
                    }
                };

                Volley.newRequestQueue(this).add(request);
                verifySubPurchase(purchase);
            }
        }else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED){
            Toast.makeText(this, "Cancelled!", Toast.LENGTH_SHORT).show();
            binding.buyPremiumBtn.setText("Buy Premium");
        }else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
            Toast.makeText(this, "You are Already a Premium User!", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Error: " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}