package com.aampower.aampoweradmin.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aampower.aampoweradmin.R;
import com.aampower.aampoweradmin.adapter.ProductListAdapter;
import com.aampower.aampoweradmin.model.CitiesModel;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CitiesListActivity extends AppCompatActivity {

    EditText etCitySearch;
    RecyclerView citiesRecView;

    private ShimmerFrameLayout mShimmerViewContainer;

    SwipeRefreshLayout pullToRefresh;
    RelativeLayout noRecordLayout;
    TextView tvNoRecord, tvNoRecordMsg;

    ArrayList<CitiesModel> arrayList;
    RequestQueue requestQueue;
    ProductListAdapter adapter;

    Activity context;

    RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.context = CitiesListActivity.this;
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
        setContentView(R.layout.activity_cities_list);

        etCitySearch = findViewById(R.id.etCitySearch);
        citiesRecView = findViewById(R.id.citiesRecView);
        mShimmerViewContainer = findViewById(R.id.cityList_shimmer_view_container);

        pullToRefresh = findViewById(R.id.pullToRefresh);
        noRecordLayout = findViewById(R.id.noRecordLayout);
        tvNoRecord = findViewById(R.id.tvNoRecord);
        tvNoRecordMsg = findViewById(R.id.tvNoRecordMsg);

        citiesRecView.setLayoutManager(new LinearLayoutManager(context));

        mRequestQueue = Volley.newRequestQueue(context);
        arrayList = new ArrayList<>();

        etCitySearch.addTextChangedListener(new TextWatcher() {
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


        cityListTask(getString(R.string.citiesLink));


        // Initial Elevation
        final Toolbar toolbar = findViewById(R.id.toolBar);
        if (toolbar != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setElevation(0);
            }
        }

        // get initial position
        final int initialTopPosition = citiesRecView.getTop();

        // Set a listener to scroll view
        citiesRecView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && adapter != null) {

                    if (toolbar == null) {
                        return;
                    }

                    if (!recyclerView.canScrollVertically(-1)) {
                        // we have reached the top of the list
                        toolbar.setElevation(0f);
                    } else {
                        // we are not at the top yet
                        toolbar.setElevation(30f);
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
                citiesRecView.setVisibility(View.VISIBLE);
                noRecordLayout.setVisibility(View.GONE);

                cityListTask(getString(R.string.citiesLink));

            }
        });


    }

    private void cityListTask(String url) {

        final SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);

        final String session = preferences.getString("session_key", "");
        final String username = preferences.getString("username", "");
        final String admin_level = preferences.getString("admin_level", "");

//        Toast.makeText(context, "Session_key: " + session + "\nUserName: " + username, Toast.LENGTH_SHORT).show();

        if (arrayList.size() == 0) {
            mShimmerViewContainer.setVisibility(View.VISIBLE);
        }


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pullToRefresh.setRefreshing(false);

                if (response != null) {

                    try {

                        JSONArray jsonArray = new JSONArray(response);


                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            CitiesModel model = new CitiesModel();

                            model.setCityNo(jsonObject.getString("CityNo"));
                            model.setCityName(jsonObject.getString("CityName"));

                            arrayList.add(model);

                        }

                        if (arrayList.size() > 0) {

                            etCitySearch.setVisibility(View.VISIBLE);

                            mShimmerViewContainer.stopShimmerAnimation();
                            mShimmerViewContainer.setVisibility(View.GONE);

                            Context context = citiesRecView.getContext();
                            LayoutAnimationController controller = null;
                            controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_slid_from_bottom);

                            adapter = new ProductListAdapter(getApplicationContext(), arrayList, 1);
                            citiesRecView.setAdapter(adapter);

                            citiesRecView.setLayoutAnimation(controller);
                            citiesRecView.getAdapter().notifyDataSetChanged();
                            citiesRecView.scheduleLayoutAnimation();


                        } else {
                            noRecordLayout.setVisibility(View.VISIBLE);
                            citiesRecView.setVisibility(View.GONE);
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
                if (error instanceof TimeoutError) {
                    Toast.makeText(context, "Connection Timeout!", Toast.LENGTH_SHORT).show();

                }else if (error instanceof NoConnectionError){
                    Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(context, "Authentication Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(context, "Server Side Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(context, "Network Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(context, "Parse Error!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Something goes wrong", Toast.LENGTH_SHORT).show();
                }

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                params.put("username", username);
                params.put("session_key", session);
                params.put("admin_level", admin_level);

                return params;
            }
        };

        MySingleTon.getInstance(context).addRequestQue(stringRequest);

//        Response.Listener<String> stringResponse = new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                pullToRefresh.setRefreshing(false);
//
//                if (response != null) {
//
//                    try {
//
//                        JSONArray jsonArray = new JSONArray(response);
//
//
//                        for (int i = 0; i < jsonArray.length(); i++) {
//
//                            JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                            CitiesModel model = new CitiesModel();
//
//                            model.setCityNo(jsonObject.getString("CityNo"));
//                            model.setCityName(jsonObject.getString("CityName"));
//
//                            arrayList.add(model);
//
//                        }
//
//                        if (arrayList.size() > 0) {
//
//                            etCitySearch.setVisibility(View.VISIBLE);
//
//                            mShimmerViewContainer.stopShimmerAnimation();
//                            mShimmerViewContainer.setVisibility(View.GONE);
//
//                            Context context = citiesRecView.getContext();
//                            LayoutAnimationController controller = null;
//                            controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_slid_from_bottom);
//
//                            adapter = new ProductListAdapter(getApplicationContext(), arrayList, 1);
//                            citiesRecView.setAdapter(adapter);
//
//                            citiesRecView.setLayoutAnimation(controller);
//                            citiesRecView.getAdapter().notifyDataSetChanged();
//                            citiesRecView.scheduleLayoutAnimation();
//
//
//                        } else {
//                            noRecordLayout.setVisibility(View.VISIBLE);
//                            citiesRecView.setVisibility(View.GONE);
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//
//                } else {
//                    showErroeMessage(getString(R.string.oopsError), getString(R.string.oopsErrorMsg));
//                }
//
//                mRequestQueue.stop();
//
//            }
//        };
//
//
//        Response.ErrorListener errorListener = new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                pullToRefresh.setRefreshing(false);
//                if (error instanceof TimeoutError) {
//                    Toast.makeText(context, "Connection Timeout!", Toast.LENGTH_SHORT).show();
//
//                }else if (error instanceof NoConnectionError){
//                    Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_SHORT).show();
//                } else if (error instanceof AuthFailureError) {
//                    Toast.makeText(context, "Authentication Error!", Toast.LENGTH_SHORT).show();
//                } else if (error instanceof ServerError) {
//                    Toast.makeText(context, "Server Side Error!", Toast.LENGTH_SHORT).show();
//                } else if (error instanceof NetworkError) {
//                    Toast.makeText(context, "Network Error!", Toast.LENGTH_SHORT).show();
//                } else if (error instanceof ParseError) {
//                    Toast.makeText(context, "Parse Error!", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(context, "Something goes wrong", Toast.LENGTH_SHORT).show();
//                }
//
//                mRequestQueue.stop();
//
//            }
//        };
//
//        Map<String, String> params = new HashMap<>();
////        params.put("session_key", session);
//
//        new HTTPRequest(mRequestQueue).GET(url, params,
//                stringResponse, errorListener, "", null);

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



}
