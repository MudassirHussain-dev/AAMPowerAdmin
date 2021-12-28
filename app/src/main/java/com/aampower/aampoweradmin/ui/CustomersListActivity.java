package com.aampower.aampoweradmin.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aampower.aampoweradmin.R;
import com.aampower.aampoweradmin.adapter.DealersListAdapter;
import com.aampower.aampoweradmin.model.DealersModel;
import com.aampower.aampoweradmin.utils.MySingleTon;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomersListActivity extends AppCompatActivity {

    Toolbar cusToolBar;
    EditText etCustomerSearch;
    ShimmerFrameLayout mShimmerViewContainer;
    RecyclerView customerRecView;
    SwipeRefreshLayout pullToRefresh;
    RelativeLayout noRecordLayout;
    TextView tvCity, tvNoRecord, tvNoRecordMsg;

    String cityCode, cityName;

    ArrayList<DealersModel> arrayList;

    DealersListAdapter adapter;

    RequestQueue requestQueue;


    Activity context;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.context = CustomersListActivity.this;
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
        setContentView(R.layout.activity_customers_list);

        cusToolBar = findViewById(R.id.customerToolBar);
        etCustomerSearch = findViewById(R.id.etCustomerSearch);
        mShimmerViewContainer = findViewById(R.id.custList_shimmer_view_container);
        customerRecView = findViewById(R.id.customerRecView);
        pullToRefresh = findViewById(R.id.pullToRefresh);
        noRecordLayout = findViewById(R.id.noRecordLayout);
        tvCity = findViewById(R.id.tvCity);
        tvNoRecord = findViewById(R.id.tvNoRecord);
        tvNoRecordMsg = findViewById(R.id.tvNoRecordMsg);

        mRequestQueue = Volley.newRequestQueue(context);

        if (getIntent().getExtras() != null) {

            Bundle bundle = getIntent().getExtras();

            cityCode = bundle.getString("cityCode");
            cityName = bundle.getString("cityName");

            tvCity.setText(cityName);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Fade fade = new Fade();
            View decor = getWindow().getDecorView();
            fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);

            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);

        }


        arrayList = new ArrayList<>();
        customerRecView.setLayoutManager(new LinearLayoutManager(context));


        etCustomerSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        dealersListTask(getString(R.string.citiesCodeLink), 1);


        // Initial Elevation
        if (cusToolBar != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cusToolBar.setElevation(0);
            }
        }


        // Set a listener to scroll view
        customerRecView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    if (cusToolBar == null) {
                        return;
                    }

                    if (!recyclerView.canScrollVertically(-1)) {
                        // we have reached the top of the list
                        cusToolBar.setElevation(0f);
                    } else {
                        // we are not at the top yet
                        cusToolBar.setElevation(30f);
                    }
                }
            }
        });


        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (arrayList.size() == 0) {
                    mShimmerViewContainer.setVisibility(View.VISIBLE);
                }
                customerRecView.setVisibility(View.VISIBLE);
                noRecordLayout.setVisibility(View.GONE);

                dealersListTask(getString(R.string.citiesCodeLink), 1);

            }
        });

    }


    private void dealersListTask(String url, final int refreshID) {


        final SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);

        final String session = preferences.getString("session_key", "");
        final String username = preferences.getString("username", "");
        final String admin_level = preferences.getString("admin_level", "");

        if (arrayList.size() == 0) {
            mShimmerViewContainer.setVisibility(View.VISIBLE);
        }


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pullToRefresh.setRefreshing(false);

                if (response != null) {

                    Log.d("response", response);

                    try {

                        JSONArray jsonArray = new JSONArray(response);



                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            DealersModel dealersModel = new DealersModel();

                            dealersModel.setAccountNo(jsonObject.getString("AccountNo"));
                            dealersModel.setAccountName(jsonObject.getString("Account_Name"));
                            dealersModel.setAddress(jsonObject.getString("Address"));
                            dealersModel.setMobileNo(jsonObject.getString("MOBILE"));
                            dealersModel.setBalance(jsonObject.getString("Balance"));

                            arrayList.add(dealersModel);

                        }

                        if (arrayList.size() > 0) {

                            if (refreshID == 1) {

                                mShimmerViewContainer.stopShimmerAnimation();
                                mShimmerViewContainer.setVisibility(View.GONE);

                                etCustomerSearch.setVisibility(View.VISIBLE);

                                Context recContext = customerRecView.getContext();
                                LayoutAnimationController controller = null;
                                controller = AnimationUtils.loadLayoutAnimation(recContext, R.anim.layout_slid_from_bottom);

                                adapter = new DealersListAdapter(context, arrayList, cityName);
                                customerRecView.setAdapter(adapter);

                                customerRecView.setLayoutAnimation(controller);
                                customerRecView.getAdapter().notifyDataSetChanged();
                                customerRecView.scheduleLayoutAnimation();

                            }else {

                                adapter.refreshData(arrayList);

                            }


                        } else {

                            noRecordLayout.setVisibility(View.VISIBLE);
                            customerRecView.setVisibility(View.GONE);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    showErroeMessage(getString(R.string.oopsError), getString(R.string.oopsErrorMsg));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pullToRefresh.setRefreshing(false);

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    showErroeMessage(getString(R.string.internetConnectionError), getString(R.string.internetConnectionErrorMsg));

                } else if (error instanceof AuthFailureError) {

                    showErroeMessage(getString(R.string.oopsError), getString(R.string.oopsErrorMsg));

                } else if (error instanceof ServerError) {

                    showErroeMessage(getString(R.string.oopsError), getString(R.string.oopsErrorMsg));

                } else if (error instanceof NetworkError) {

                    showErroeMessage(getString(R.string.networkConnectionError), getString(R.string.networkConnectionErrorMsg));

                } else if (error instanceof ParseError) {

                    showErroeMessage(getString(R.string.oopsError), getString(R.string.oopsErrorMsg));

                }
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("CityCode", cityCode);
                params.put("session_key", session);
                params.put("username", username);
                params.put("admin_level", admin_level);

                return params;

            }
        };

        MySingleTon.getInstance(context).addRequestQue(stringRequest);

    }

    private void showErroeMessage(String noRecord, String noRecordMsg) {

        noRecordLayout.setVisibility(View.VISIBLE);

        if (noRecordMsg.equals("")) {
            tvNoRecordMsg.setVisibility(View.GONE);
        }

        tvNoRecord.setText(noRecord);
        tvNoRecordMsg.setText(noRecordMsg);

    }


    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();

    }

    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        if (requestQueue != null) {
            requestQueue.stop();
        }

        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");

                dealersListTask(getString(R.string.citiesCodeLink), 2);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public void GET(String url, Map<String, String> params,
                    Response.Listener<String> response_listener,
                    Response.ErrorListener error_listener,
                    String API_KEY,
                    String stringRequestTag )
    {
        final Map<String, String> mParams = params;
        final String mAPI_KEY = API_KEY;
        final String mUrl = url;

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                mUrl,
                response_listener,
                error_listener
        ) {
            @Override
            protected Map<String, String> getParams() {
                return mParams;
            }

            @Override
            public String getUrl() {
                StringBuilder stringBuilder = new StringBuilder(mUrl);
                int i = 1;
                for (Map.Entry<String, String> entry : mParams.entrySet()) {
                    String key;
                    String value;
                    try {
                        key = URLEncoder.encode(entry.getKey(), "UTF-8");
                        value = URLEncoder.encode(entry.getValue(), "UTF-8");
                        if (i == 1) {
                            stringBuilder.append("?" + key + "=" + value);
                        } else {
                            stringBuilder.append("&" + key + "=" + value);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    i++;

                }
                String url = stringBuilder.toString();

                return url;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                if (!(mAPI_KEY.equals(""))) {
                    headers.put("X-API-KEY", mAPI_KEY);
                }
                return headers;
            }
        };

        if (stringRequestTag != null) {
            stringRequest.setTag(stringRequestTag);
        }

        mRequestQueue.add(stringRequest);
    }

}
