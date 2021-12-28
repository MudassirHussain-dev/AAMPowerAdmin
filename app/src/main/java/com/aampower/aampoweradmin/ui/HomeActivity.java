package com.aampower.aampoweradmin.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.aampower.aampoweradmin.R;
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

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvHName;
    LinearLayout btnRecovery;
    ImageView btnLogout;

    Dialog dialog;

    Activity context;

    @Override
    protected void onStart() {
        super.onStart();

        //region
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {

            Window window = getWindow();

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(ContextCompat.getColor(HomeActivity.this, R.color.textColorDark));

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
        //endregion

//        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
//
//        if (sharedPreferences.getString("username", "").equals("")){
//
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//
//        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = HomeActivity.this;
        setContentView(R.layout.activity_home);
        btnRecovery = findViewById(R.id.btnRecovery);
        tvHName = findViewById(R.id.tvHName);
        btnLogout = findViewById(R.id.btnLogout);


        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);

        tvHName.setText("Welcome, " + sharedPreferences.getString("username", ""));

        btnRecovery.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.btnRecovery){

            startActivity(new Intent(getApplicationContext(), CitiesListActivity.class));

        }else if (id == R.id.btnLogout){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirmation!");
            builder.setMessage("Are you sure you want to logout?");
            builder.setCancelable(false);
            builder.setIcon(R.mipmap.ic_launcher);

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    logoutTask(getString(R.string.logoutTaskLink));

                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });


            builder.create().show();

        }

    }

    private void logoutTask (String url){


        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);

        View view = LayoutInflater.from(context).inflate(R.layout.loading_alert, null);
        builder.setView(view);

        dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.show();



        final SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);

        final String session = preferences.getString("session_key", "");
        final String username = preferences.getString("username", "");
        final String admin_level = preferences.getString("admin_level", "");


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                dialog.dismiss();

                if (response != null){


                    if (response.equals("true")){

                        SharedPreferences.Editor sharedPreferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE).edit();
                        sharedPreferences.clear();
                        sharedPreferences.apply();

                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                    }else {
                        showErroeMessage("Oops", "Something goes wrong.");
                    }

                }else {
                    Toast.makeText(context, "Something goes wrong", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();

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
                params.put("session_key", session);
                params.put("username", username);
                params.put("admin_level", admin_level);

                return params;
            }
        };

        MySingleTon.getInstance(context).addRequestQue(stringRequest);

    }

    private void showErroeMessage(String noRecord, String noRecordMsg){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(noRecord);
        builder.setMessage(noRecordMsg);

        builder.setCancelable(true);

        builder.create().show();

    }

}
