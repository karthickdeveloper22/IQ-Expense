package com.androidheroes.iqexpensemanager.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidheroes.iqexpensemanager.Activities.BudgetActivity;
import com.androidheroes.iqexpensemanager.Activities.EditTransactionActivity;
import com.androidheroes.iqexpensemanager.Models.ModelBudgets;
import com.androidheroes.iqexpensemanager.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterBudgets extends RecyclerView.Adapter<AdapterBudgets.budgetHolder> {

    private List<ModelBudgets> modelBudgetsList;
    private Context context;
    private String symbol;
    private SharedPreferences preferences;

    public AdapterBudgets(List<ModelBudgets> modelBudgetsList, Context context) {
        this.modelBudgetsList = modelBudgetsList;
        this.context = context;
    }

    @NonNull
    @Override
    public budgetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_layout, parent, false);
        return new budgetHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull budgetHolder holder, int position) {
        ModelBudgets modelBudgets = modelBudgetsList.get(position);

        preferences = context.getSharedPreferences("currency", MODE_PRIVATE);
        if (!preferences.getString("symbol", "").isEmpty()) {
            symbol = preferences.getString("symbol", "");
        } else {
            symbol = "₹";
        }

        int balance_spend = modelBudgets.getAmount() - modelBudgets.getSpend();

        int percentage = modelBudgets.getSpend() * 100 / modelBudgets.getAmount();

        holder.budgetTitleTv.setText(modelBudgets.getTitle());
        holder.budgetAmountTv.setText(symbol + modelBudgets.getAmount());
        holder.budgetSpendTv.setText(percentage + "%");
        holder.balanceAmountTv.setText(String.valueOf(modelBudgets.getSpend() + "/" + modelBudgets.getAmount()));
        holder.budgetDesTv.setText(modelBudgets.getDescription());
        holder.dataTimeTv.setText(modelBudgets.getTimestamp());

        holder.spendProgress.setProgress(percentage);

        if (percentage < 20) {
            holder.spendProgress.setIndicatorColor(context.getResources().getColor(R.color.primary_Light));
        }
        if (percentage >= 20) {
            holder.spendProgress.setIndicatorColor(context.getResources().getColor(R.color.progress20));
        }
        if (percentage >= 50) {
            holder.spendProgress.setIndicatorColor(context.getResources().getColor(R.color.progress50));
        }
        if (percentage >= 80) {
            holder.spendProgress.setIndicatorColor(context.getResources().getColor(R.color.progress80));
        }
        if (percentage == 100) {
            holder.spendProgress.setIndicatorColor(context.getResources().getColor(R.color.progress100));
        }

        if (modelBudgets.getAmount() == modelBudgets.getSpend()) {
            holder.spendBtn.setVisibility(View.GONE);
            holder.spendCompletedBtn.setVisibility(View.VISIBLE);
        }

        holder.spendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.add_spend_dialog, null);
                EditText budgetSpendEt = dialogView.findViewById(R.id.budgetSpendEt);
                Button addSpendBtn = dialogView.findViewById(R.id.addSpendBtn);
                AdView adView = dialogView.findViewById(R.id.adView);
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();

                MobileAds.initialize(context, new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                    }
                });

                //MobileAds.setRequestConfiguration(new RequestConfiguration().toBuilder().setTestDeviceIds(Arrays.asList("1c79c9af-6a01-400e-a45e-4919f606e1ae")).build());
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);

                addSpendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int spend = Integer.parseInt(budgetSpendEt.getText().toString().trim());
                        String budget_id = String.valueOf(modelBudgets.getBudget_id());
                        int add_budget = modelBudgets.getSpend() + spend;

                        if (spend > balance_spend) {
                            Toast.makeText(context, "Amount is greater than Budget Value!", Toast.LENGTH_SHORT).show();
                        } else {
                            StringRequest request = new StringRequest(Request.Method.POST, "http://hiqexpense.iqexpense.tk/update_spend.php", new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if (response.equalsIgnoreCase("success")) {
                                        alertDialog.dismiss();
                                        Toast.makeText(context, "Budget Spend Updated!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        alertDialog.dismiss();
                                        Toast.makeText(context, "" + response, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    alertDialog.dismiss();
                                    Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }) {
                                @Nullable
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("budget_id", budget_id);
                                    params.put("spend", String.valueOf(add_budget));
                                    return params;
                                }
                            };

                            Volley.newRequestQueue(context).add(request);
                        }
                    }
                });
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelBudgetsList.size();
    }

    static class budgetHolder extends RecyclerView.ViewHolder {

        public TextView budgetTitleTv, budgetDesTv, budgetAmountTv, budgetSpendTv, dataTimeTv, balanceAmountTv, spendBtn, spendCompletedBtn;
        private LinearProgressIndicator spendProgress;

        public budgetHolder(@NonNull View itemView) {
            super(itemView);

            budgetTitleTv = itemView.findViewById(R.id.budgetTitleTv);
            budgetDesTv = itemView.findViewById(R.id.budgetDesTv);
            budgetAmountTv = itemView.findViewById(R.id.budgetAmountTv);
            budgetSpendTv = itemView.findViewById(R.id.budgetSpendTv);
            dataTimeTv = itemView.findViewById(R.id.dataTimeTv);
            balanceAmountTv = itemView.findViewById(R.id.balanceAmountTv);
            spendBtn = itemView.findViewById(R.id.spendBtn);
            spendCompletedBtn = itemView.findViewById(R.id.spendCompletedBtn);
            spendProgress = itemView.findViewById(R.id.spendProgress);
        }
    }

}
