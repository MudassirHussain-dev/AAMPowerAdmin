package com.aampower.aampoweradmin.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.aampower.aampoweradmin.R;
import com.aampower.aampoweradmin.utils.MySingleTon;
import com.an.biometric.BiometricCallback;
import com.an.biometric.BiometricManager;
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
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout tiUserName, tiPassword;
    EditText etUserName, etPassword;
    Button btnLogin;
    LinearLayout btnTouchID;
    TextView tvTouchID;

    Activity context;

    Dialog dialog;

    RequestQueue mRequestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = MainActivity.this;
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

        setContentView(R.layout.activity_main);
        etUserName = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tiUserName = findViewById(R.id.tiUserName);
        tiPassword = findViewById(R.id.tiPassword);
        btnTouchID = findViewById(R.id.btnTouchID);
        tvTouchID = findViewById(R.id.tvTouchID);

        btnTouchID.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        etUserName.addTextChangedListener(new MyTextWatcher(etUserName));
        etPassword.addTextChangedListener(new MyTextWatcher(etPassword));


        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);

        String userName = sharedPreferences.getString("username", "");

        etUserName.setText(userName);
//        etPassword.setText(sharedPreferences.getString("password", ""));


        String pass = sharedPreferences.getString("password", "");
        String session = sharedPreferences.getString("session_key", "");
        String level = sharedPreferences.getString("admin_level", "");


        if (userName != null && pass != null && !userName.isEmpty() && !pass.isEmpty()) {

            showBioMatric();

        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (sharedPreferences.getString("username", "").length() > 0) {

                btnTouchID.setVisibility(View.VISIBLE);

            }
        } else {
            btnTouchID.setVisibility(View.GONE);
        }

//        startActivity(new Intent(context, HomeActivity.class));

        mRequestQueue = Volley.newRequestQueue(context);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.btnLogin) {

            if (etUserName.getText().toString().trim().length() > 0 && etPassword.getText().toString().trim().length() > 0) {


                gettingIpAddress(getString(R.string.ipLink), 0);


//                loginTask(getString(R.string.loginLLink), "192.168.1.10", 0);


            } else {

                if (etUserName.getText().toString().trim().length() == 0) {
                    tiUserName.setError("Required");
                }
                if (etPassword.getText().toString().trim().length() == 0) {
                    tiPassword.setError("Required");
                }

            }

        } else if (id == R.id.btnTouchID) {

            showBioMatric();

        }

    }


    private void showBioMatric() {

        //region

        new BiometricManager.BiometricBuilder(MainActivity.this)
                .setTitle("Login")
                .setSubtitle("Login to your account")
                .setDescription("Place your finger on the fingerprint sensor to verify your identity")
                .setNegativeButtonText("Cancel")
                .build()
                .authenticate(new BiometricCallback() {
                    @Override
                    public void onSdkVersionNotSupported() {
                        /*
                         *  Will be called if the device sdk versi1on does not support Biometric authentication
                         */
                    }

                    @Override
                    public void onBiometricAuthenticationNotSupported() {
                        /*
                         *  Will be called if the device does not contain any fingerprint sensors
                         */

                        showToastt("Your device device does not contain any fingerprint sensors");

                    }

                    @Override
                    public void onBiometricAuthenticationNotAvailable() {
                        /*
                         *  The device does not have any biometrics registered in the device.
                         */
                        showToastt("Your device does not have any biometrics registered, Please register at least one fingerprint");
                    }

                    @Override
                    public void onBiometricAuthenticationPermissionNotGranted() {
                        /*
                         *  android.permission.USE_BIOMETRIC permission is not granted to the app
                         */

                        showToastt("Permission not granted");

                    }

                    @Override
                    public void onBiometricAuthenticationInternalError(String error) {
                        /*
                         *  This method is called if one of the fields such as the title, subtitle,
                         * description or the negative button text is empty
                         */
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        /*
                         * When the fingerprint doesn’t match with any of the fingerprints registered on the device,
                         * then this callback will be triggered.
                         */
                    }

                    @Override
                    public void onAuthenticationCancelled() {
                        /*
                         * The authentication is cancelled by the user.
                         */

                        showToastt("The authentication is cancelled by the user");
                    }

                    @Override
                    public void onAuthenticationSuccessful() {
                        /*
                         * When the fingerprint is has been successfully matched with one of the fingerprints
                         * registered on the device, then this callback will be triggered.
                         */

                        gettingIpAddress(getString(R.string.ipLink), 1);

//                                startActivity(new Intent(context, HomeActivity.class));

//                                finish();

                    }

                    @Override
                    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                        /*
                         * This method is called when a non-fatal error has occurred during the authentication
                         * process. The callback will be provided with an help code to identify the cause of the
                         * error, along with a help message.
                         */
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        /*
                         * When an unrecoverable error has been encountered and the authentication process has
                         * completed without success, then this callback will be triggered. The callback is provided
                         * with an error code to identify the cause of the error, along with the error message.
                         */
                    }
                });

        //endregion

    }


    public static final String md5(final String toEncrypt) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return ""; // Impossibru!
        }
    }

    private void showToastt(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    private class MyTextWatcher implements TextWatcher {

        View view;

        public MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (s.length() > 0) {

                if (view.getId() == R.id.etUserName) {
                    tiUserName.setError(null);
                } else if (view.getId() == R.id.etPassword) {
                    tiPassword.setError(null);
                }

            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }


    private void loginTask(String url, final String ipv4, final int id) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                dialog.dismiss();

                if (response != null) {

//                    Toast.makeText(context, response, Toast.LENGTH_SHORT).show();

                    if (!response.equals("false") && !response.equals("")) {


                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            if (jsonObject.getString("admin_level").equals("0")) {

                                SharedPreferences.Editor sharedPreferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE).edit();

                                if (id == 0) {
                                    sharedPreferences.putString("username", etUserName.getText().toString().trim());
                                    sharedPreferences.putString("password", etPassword.getText().toString().trim());
                                }
                                sharedPreferences.putString("session_key", jsonObject.getString("session_key"));
                                sharedPreferences.putString("admin_level", jsonObject.getString("admin_level"));

                                sharedPreferences.apply();
                                sharedPreferences.commit();

                                startActivity(new Intent(context, HomeActivity.class));

                                finish();

                            }else {
                                Toast.makeText(context, "Not Authorized", Toast.LENGTH_SHORT).show();
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {

                        Toast.makeText(context, "Invalid Username OR Password", Toast.LENGTH_SHORT).show();

                    }


                } else {
                    Toast.makeText(context, "No response, Something goes wrong", Toast.LENGTH_SHORT).show();
                }
                mRequestQueue.stop();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();

                if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "Connection Timeout!!!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "No Internet Connection!!!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getApplicationContext(), "Authentication Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "Server Side Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getApplicationContext(), "Network Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(), "Parse Error!", Toast.LENGTH_SHORT).show();
                }


            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String user = etUserName.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();


                SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
                String passs = md5(preferences.getString("password", ""));

                Map<String, String> params = new HashMap<>();
                params.put("username", user);


                String p = md5(pass);

//                Toast.makeText(context, p, Toast.LENGTH_SHORT).show();

                if (id == 0) {
                    params.put("password", p);
                } else if (id == 1) {
                    params.put("password", passs);
                }
                params.put("ipv4", ipv4);

                return params;
            }
        };

        MySingleTon.getInstance(context).addRequestQue(stringRequest);


    }

    private void gettingIpAddress(String url, final int id) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);

        View view = LayoutInflater.from(context).inflate(R.layout.loading_alert, null);
        builder.setView(view);

        dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if (response != null) {

                    if (response.length() > 0) {

//                        Toast.makeText(context, response, Toast.LENGTH_SHORT).show();

                        loginTask(getString(R.string.loginLLink), response, id);
                      //  Toast.makeText(context, ""+response, Toast.LENGTH_SHORT).show();

                    }

                } else {
                    Toast.makeText(context, "No response, Something goes wrong", Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();

                if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "Connection Timeout!!!", Toast.LENGTH_SHORT).show();

                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();

                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getApplicationContext(), "Authentication Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "Server Side Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getApplicationContext(), "Network Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(), "Parse Error!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        MySingleTon.getInstance(context).addRequestQue(stringRequest);

    }

}
