package com.androidheroes.iqexpensemanager.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.androidheroes.iqexpensemanager.databinding.ActivitySettingsBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences("currency", MODE_PRIVATE);
        if (!preferences.getString("symbol", "").isEmpty()) {
            binding.currencyEt.setText(preferences.getString("symbol", ""));
        } else {
            binding.currencyEt.setText("₹");
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });

        //MobileAds.setRequestConfiguration(new RequestConfiguration().toBuilder().setTestDeviceIds(Arrays.asList("1c79c9af-6a01-400e-a45e-4919f606e1ae")).build());
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.currencyEt.getText().toString().equals("")) {
                    preferences = getSharedPreferences("currency", MODE_PRIVATE);
                    editor = preferences.edit();
                    editor.putString("symbol", binding.currencyEt.getText().toString());
                    editor.apply();
                    Toast.makeText(SettingsActivity.this, "Currency Saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, "Enter Currency Symbol", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}