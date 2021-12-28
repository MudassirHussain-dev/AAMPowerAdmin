package com.aampower.aampoweradmin.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aampower.aampoweradmin.R;
import com.aampower.aampoweradmin.adapter.BillAdapter;
import com.aampower.aampoweradmin.model.LedgerModel;
import com.aampower.aampoweradmin.utils.MySingleTon;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class BillActivity extends AppCompatActivity {

    TextView tvBillName, tvBillCity, tvBillAccNo, tvBillPhone, tvBillAdd, tvDated, tvTotalBill;
    RecyclerView billRecView;
    RelativeLayout noRecordLayoutBill;
    TextView tvBillNoRecord, tvBillNoRecordMsg;
    Toolbar billToolBar;
    ImageView btnBack;

    ShimmerFrameLayout mShimmerLayout;

    SwipeRefreshLayout pullToRefreshBill;

    String accName, accNo, city, mobile, address;

    Activity context;

    ArrayList<LedgerModel> arrayList;
    private BillAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.context = BillActivity.this;
        super.onCreate(savedInstanceState);
        //region
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {

            Window window = context.getWindow();

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(ContextCompat.getColor(context, R.color.textColorDark));

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
        //endregion
        setContentView(R.layout.bill_fragment);

        tvBillName = findViewById(R.id.tvBillName);
        tvBillCity = findViewById(R.id.tvBillCity);
        tvBillAccNo = findViewById(R.id.tvBillAccNo);
        tvBillPhone = findViewById(R.id.tvBillPhone);
        tvBillAdd = findViewById(R.id.tvBillAdd);
        tvDated = findViewById(R.id.tvDated);
        billRecView = findViewById(R.id.billRecView);
        tvTotalBill = findViewById(R.id.tvTotalBill);
        noRecordLayoutBill = findViewById(R.id.noRecordLayoutBill);
        tvBillNoRecord = findViewById(R.id.tvBillNoRecord);
        tvBillNoRecordMsg = findViewById(R.id.tvBillNoRecordMsg);
        pullToRefreshBill = findViewById(R.id.pullToRefreshBill);
        billToolBar = findViewById(R.id.billToolBar);
        mShimmerLayout = findViewById(R.id.billList_shimmer_view_container);
//        btnBack = findViewById(R.id.btnBack);

        setSupportActionBar(billToolBar);

        if (getSupportActionBar() != null){


            setTitle("Bill");

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        billRecView.setLayoutManager(new LinearLayoutManager(context));

        if (getIntent().getExtras() != null) {

            final Bundle bundle = getIntent().getExtras();

            accName = bundle.getString("accName", "");
            accNo = bundle.getString("voucherNo", "");
            city = bundle.getString("city", "");
            mobile = bundle.getString("mobile", "");
            address = bundle.getString("address", "");

            tvBillName.setText(accName);
            tvBillAccNo.setText(accNo);
            tvBillCity.setText(city);
            tvBillPhone.setText(mobile);
            tvBillAdd.setText(address);

            billTask(getString(R.string.billLink));

        }


        pullToRefreshBill.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mShimmerLayout.setVisibility(View.VISIBLE);
                billRecView.setVisibility(View.VISIBLE);
                noRecordLayoutBill.setVisibility(View.GONE);

                billTask(getString(R.string.billLink));

            }
        });


//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                finish();
//
//            }
//        });

    }


    private void billTask (String url){

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.PREF_FILE), Context.MODE_PRIVATE);

        final String session_key = sharedPreferences.getString("session_key", "");
        final String username = sharedPreferences.getString("username", "");
        final String admin_level = sharedPreferences.getString("admin_level", "");

        tvTotalBill.setText("0");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        pullToRefreshBill.setRefreshing(false);

                        if (response != null){

                            try {

                                JSONArray jsonArray = new JSONArray(response);

                                arrayList = new ArrayList<>();

                                float totalBill = 0f;

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    LedgerModel model = new LedgerModel();

                                    model.setQuantity(jsonObject.getString("quantity"));
                                    model.setPrice(jsonObject.getString("rate"));
                                    model.setAmount(jsonObject.getString("amount"));
                                    model.setProductName(jsonObject.getString("Product_Name"));
                                    model.setDated(jsonObject.getString("dated"));

                                    if (i == 0){
                                        tvDated.setText(jsonObject.getString("dated").split(" ")[0]);
                                    }

                                    totalBill += Float.valueOf(jsonObject.getString("amount"));

                                    arrayList.add(model);

                                }

                                double totbill = (double) totalBill;

                                NumberFormat formatter = new DecimalFormat("#,###");

                                tvTotalBill.setText(String.valueOf(formatter.format(totalBill)));

                                if (arrayList.size() > 0) {

//                                    ((LedgerActivity) context).settingFromAndTo(arrayList.get(0).getDate(), arrayList.get(arrayList.size() - 1).getDate());

                                    mShimmerLayout.stopShimmerAnimation();
                                    mShimmerLayout.setVisibility(View.GONE);

                                    Context recContext = billRecView.getContext();
                                    LayoutAnimationController controller = null;
                                    controller = AnimationUtils.loadLayoutAnimation(recContext, R.anim.layout_slid_from_bottom);

                                    adapter = new BillAdapter(context, arrayList);
                                    billRecView.setAdapter(adapter);

                                    billRecView.setLayoutAnimation(controller);
                                    billRecView.getAdapter().notifyDataSetChanged();
                                    billRecView.scheduleLayoutAnimation();




                                } else {
//                                    Toast.makeText(context, "Ledger not found", Toast.LENGTH_SHORT).show();
                                    noRecordLayoutBill.setVisibility(View.VISIBLE);
                                    billRecView.setVisibility(View.GONE);
//                                    finish();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }else {
                            Toast.makeText(context, "Something goes wrong", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                pullToRefreshBill.setRefreshing(false);

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    showErroeMessage(context.getResources().getString(R.string.internetConnectionError), context.getResources().getString(R.string.internetConnectionErrorMsg));

                } else if (error instanceof AuthFailureError) {

                    showErroeMessage(context.getResources().getString(R.string.oopsError), context.getResources().getString(R.string.oopsErrorMsg));

                } else if (error instanceof ServerError) {

                    showErroeMessage(context.getResources().getString(R.string.oopsError), context.getResources().getString(R.string.oopsErrorMsg));

                } else if (error instanceof NetworkError) {

                    showErroeMessage(context.getResources().getString(R.string.networkConnectionError), context.getResources().getString(R.string.networkConnectionErrorMsg));

                } else if (error instanceof ParseError) {

                    showErroeMessage(context.getResources().getString(R.string.oopsError), context.getResources().getString(R.string.oopsErrorMsg));

                }


            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("voucher_no", accNo);
                params.put("session_key", session_key);
                params.put("username", username);
                params.put("admin_level", admin_level);

                return params;
            }
        };

        MySingleTon.getInstance(context).addRequestQue(stringRequest);

    }

    private void showErroeMessage(String noRecord, String noRecordMsg){

        noRecordLayoutBill.setVisibility(View.VISIBLE);

        if (noRecordMsg.equals("")){
            tvBillNoRecordMsg.setVisibility(View.GONE);
        }

        tvBillNoRecord.setText(noRecord);
        tvBillNoRecordMsg.setText(noRecordMsg);

    }


    @Override
    public void onResume() {
        super.onResume();
        mShimmerLayout.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        mShimmerLayout.stopShimmerAnimation();
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.share_menu, menu);

        return true;
    }

    @Override
    public boolean onNavigateUp() {
        finish();
        return super.onNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_share){



        }else if (id == android.R.id.home){
            finish();
        }

        return true;
    }
}
