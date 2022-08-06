package com.androidheroes.iqexpensemanager.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidheroes.iqexpensemanager.Constants;
import com.androidheroes.iqexpensemanager.R;
import com.androidheroes.iqexpensemanager.databinding.ActivityLoginBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    private String email, password;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private boolean loggedIn = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        binding.emailEt.setText(preferences.getString("email", ""));
        binding.passwordEt.setText(preferences.getString("password", ""));

        loggedIn = preferences.getBoolean("loggedIn", loggedIn);

        if (loggedIn) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("email", preferences.getString("email", ""));
            startActivity(intent);
            finish();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging In!");

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = binding.emailEt.getText().toString();
                password = binding.passwordEt.getText().toString();

                if (email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    login();
                }
            }
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        binding.forgotBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.reset_password_layout, viewGroup, false);
            TextInputEditText resetEmailEt = dialogView.findViewById(R.id.resetEmailEt);
            Button resetPasswordBtn = dialogView.findViewById(R.id.resetPasswordBtn);

            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();

            resetPasswordBtn.setOnClickListener(view1 -> {
                String resetEmail = resetEmailEt.getText().toString().trim();
                resetPassword(resetEmail);
                alertDialog.dismiss();
            });

            alertDialog.show();
        });
    }

    private void resetPassword(String resetEmail) {
        progressDialog.setMessage("Resetting Password!");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Constants.mainUrl + "forgot_password.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equalsIgnoreCase("success")) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Password sent to your Email!", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", resetEmail);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(request);
    }

    private void login() {
        StringRequest request = new StringRequest(Request.Method.POST, Constants.mainUrl + "login.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equalsIgnoreCase("success")) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    if (binding.rememberChkBx.isChecked()) {
                        preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                        editor = preferences.edit();
                        editor.putString("email", email);
                        editor.putString("password", password);
                        editor.putBoolean("loggedIn", true);
                        editor.apply();
                    }
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(request);
    }
}