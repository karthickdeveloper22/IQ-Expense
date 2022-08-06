package com.androidheroes.iqexpensemanager.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidheroes.iqexpensemanager.Constants;
import com.androidheroes.iqexpensemanager.R;
import com.androidheroes.iqexpensemanager.databinding.FragmentExpenseBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExpenseFragment extends Fragment {

    private FragmentExpenseBinding binding;
    private ProgressDialog progressDialog;
    public ArrayList<String> expenseLists;
    private ArrayAdapter<String> expenseAdapter;
    private String id;
    private SharedPreferences preferences;
    private int subscribed;

    public ExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentExpenseBinding.inflate(inflater, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading Expenses!");
        progressDialog.show();

        preferences = getActivity().getSharedPreferences("userid", Context.MODE_PRIVATE);
        id = preferences.getString("id", id);
        subscribed = preferences.getInt("subscribed", 0);

        if (subscribed == 0){
            MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                }
            });

            //MobileAds.setRequestConfiguration(new RequestConfiguration().toBuilder().setTestDeviceIds(Arrays.asList("1c79c9af-6a01-400e-a45e-4919f606e1ae")).build());
            AdRequest adRequest = new AdRequest.Builder().build();
            binding.adView.loadAd(adRequest);
        }

        loadExpense();

        return binding.getRoot();
    }

    private void loadExpense() {
        expenseLists = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.POST, Constants.mainUrl + "get_expense_categories.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                expenseLists.clear();
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String name = object.getString("name");
                        expenseLists.add(name);
                        expenseAdapter = new ArrayAdapter(getContext(), R.layout.categories_layout, expenseLists);
                        binding.listView.setAdapter(expenseAdapter);
                        progressDialog.dismiss();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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

        Volley.newRequestQueue(getContext()).add(request);
    }
}