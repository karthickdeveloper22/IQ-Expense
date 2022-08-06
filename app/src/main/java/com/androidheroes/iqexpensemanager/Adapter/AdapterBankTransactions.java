package com.androidheroes.iqexpensemanager.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidheroes.iqexpensemanager.Activities.EditTransactionActivity;
import com.androidheroes.iqexpensemanager.Models.ModelBankTransactions;
import com.androidheroes.iqexpensemanager.R;

import java.util.List;

public class AdapterBankTransactions extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<ModelBankTransactions> modelBankTransactionsList;
    private String symbol;
    private Context context;
    private SharedPreferences preferences;

    public AdapterBankTransactions(List<ModelBankTransactions> modelBankTransactionsList, Context context) {
        this.modelBankTransactionsList = modelBankTransactionsList;
        this.context = context;

        preferences = context.getSharedPreferences("currency", MODE_PRIVATE);
        if (!preferences.getString("symbol", "").isEmpty()) {
            symbol = preferences.getString("symbol", "");
        } else {
            symbol = "₹";
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (modelBankTransactionsList.get(position).getType().equals("Income")) {
            return 0;
        }
        if (modelBankTransactionsList.get(position).getType().equals("Expense")) {
            return 1;
        }
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0: {
                View incomeView = inflater.inflate(R.layout.income, parent, false);
                return new incomeHolder(incomeView);
            }
            case 1: {
                View expenseView = inflater.inflate(R.layout.expense, parent, false);
                return new expenseHolder(expenseView);
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case 0: {
                incomeHolder incomeHolder = (incomeHolder) holder;

                String bank_id = String.valueOf(modelBankTransactionsList.get(position).getBank_id());
                String bank_trans_id = String.valueOf(modelBankTransactionsList.get(position).getBank_trans_id());
                String type = modelBankTransactionsList.get(position).getType();
                String category = modelBankTransactionsList.get(position).getCategory();
                String amount = String.valueOf(modelBankTransactionsList.get(position).getAmount());
                String note = modelBankTransactionsList.get(position).getNote();
                String timestamp = modelBankTransactionsList.get(position).getTimestamp();

                incomeHolder.dataTimeTv.setText(timestamp);
                incomeHolder.categoryTv.setText(category);
                incomeHolder.amountTv.setText(symbol + amount);
                incomeHolder.noteTv.setText(note);

                incomeHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, EditTransactionActivity.class);
                        intent.putExtra("bank_trans_id", bank_trans_id);
                        intent.putExtra("type", type);
                        intent.putExtra("category", category);
                        intent.putExtra("amount", amount);
                        intent.putExtra("note", note);
                        intent.putExtra("timestamp", timestamp);
                        context.startActivity(intent);
                    }
                });
                break;
            }
            case 1: {
                expenseHolder expenseHolder = (expenseHolder) holder;

                String bank_id = String.valueOf(modelBankTransactionsList.get(position).getBank_id());
                String bank_trans_id = String.valueOf(modelBankTransactionsList.get(position).getBank_trans_id());
                String type = modelBankTransactionsList.get(position).getType();
                String category = modelBankTransactionsList.get(position).getCategory();
                String amount = String.valueOf(modelBankTransactionsList.get(position).getAmount());
                String note = modelBankTransactionsList.get(position).getNote();
                String timestamp = modelBankTransactionsList.get(position).getTimestamp();

                expenseHolder.dataTimeTv.setText(timestamp);
                expenseHolder.categoryTv.setText(category);
                expenseHolder.amountTv.setText(symbol + amount);
                expenseHolder.noteTv.setText(note);

                expenseHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, EditTransactionActivity.class);
                        intent.putExtra("bank_trans_id", bank_trans_id);
                        intent.putExtra("type", type);
                        intent.putExtra("category", category);
                        intent.putExtra("amount", amount);
                        intent.putExtra("note", note);
                        intent.putExtra("timestamp", timestamp);
                        context.startActivity(intent);
                    }
                });
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return modelBankTransactionsList.size();
    }

    static class incomeHolder extends RecyclerView.ViewHolder {

        private TextView dataTimeTv, categoryTv, amountTv, noteTv;

        public incomeHolder(@NonNull View itemView) {
            super(itemView);

            dataTimeTv = itemView.findViewById(R.id.dataTimeTv);
            categoryTv = itemView.findViewById(R.id.categoryTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            noteTv = itemView.findViewById(R.id.noteTv);
        }
    }

    static class expenseHolder extends RecyclerView.ViewHolder {

        private TextView dataTimeTv, categoryTv, amountTv, noteTv;

        public expenseHolder(@NonNull View itemView) {
            super(itemView);

            dataTimeTv = itemView.findViewById(R.id.dataTimeTv);
            categoryTv = itemView.findViewById(R.id.categoryTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            noteTv = itemView.findViewById(R.id.noteTv);
        }
    }
}
