package com.aampower.aampoweradmin.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class UpdateLedgerActivity extends AppCompatActivity {
    EditText et_updateVoucherNumberVoucherLedgerActivity, et_updateAmountVoucherLedgerActivity;
    String str_VoucherNumber, str_Amount;
    Button btn_UpdateVoucherLedger;
    //  Toolbar updateLedgerToolbar;
    String accName, str_getVoucherNumber, str_getCredit, city, mobile, address;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_ledger);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        View view = LayoutInflater.from(this).inflate(R.layout.loading_alert, null);
        builder.setView(view);

        dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        //  updateLedgerToolbar = findViewById(R.id.updateLedgerToolbar);
        et_updateVoucherNumberVoucherLedgerActivity = (EditText) findViewById(R.id.et_updateVoucherNumberVoucherLedgerActivity);
        et_updateAmountVoucherLedgerActivity = (EditText) findViewById(R.id.et_updateAmountVoucherLedgerActivity);
        btn_UpdateVoucherLedger = (Button) findViewById(R.id.btn_UpdateVoucherLedger);

    /*    setSupportActionBar(updateLedgerToolbar);

        if (getSupportActionBar() != null) {


            setTitle("Update Records");

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }*/

        if (getIntent().getExtras() != null) {

            final Bundle bundle = getIntent().getExtras();

            // accName = bundle.getString("accName", "");
            str_getVoucherNumber = bundle.getString("voucherNo", "");
            city = bundle.getString("city", "");
            str_getCredit = bundle.getString("Credit", "");
            // mobile = bundle.getString("mobile", "");
            // address = bundle.getString("address", "");

            et_updateVoucherNumberVoucherLedgerActivity.setText(str_getVoucherNumber);
            et_updateAmountVoucherLedgerActivity.setText(str_getCredit);
         /*   tvBillName.setText(accName);
            tvBillAccNo.setText(accNo);
            tvBillCity.setText(city);
            tvBillPhone.setText(mobile);
            tvBillAdd.setText(address);

            billTask(getString(R.string.billLink));*/


        }

        btn_UpdateVoucherLedger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                str_VoucherNumber = et_updateVoucherNumberVoucherLedgerActivity.getText().toString();
                str_Amount = et_updateAmountVoucherLedgerActivity.getText().toString();

                UpdateVoucherLedgerRecordsTask();
            }
        });


    }

    private void UpdateVoucherLedgerRecordsTask() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.Update_Voucher), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.hide();
                if (response.equals("T")) {

                    // Toast.makeText(UpdateLedgerActivity.this, "Submitted Successfully", Toast.LENGTH_SHORT).show();
                    final AlertDialog.Builder builder1 = new AlertDialog.Builder(UpdateLedgerActivity.this);
                    builder1.setCancelable(false);

                    builder1.setMessage("Submitted Successfully!");

                    builder1.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });

                    builder1.create().show();

                } else if (response.equals("F")) {

                    Toast.makeText(UpdateLedgerActivity.this, "Not submitted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(UpdateLedgerActivity.this, "Something went wrongs..." + response, Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
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
                HashMap<String, String> params = new HashMap<>();
                params.put("VOUCHER_NO", str_VoucherNumber);
                params.put("AMOUNT", str_Amount);
                return params;
            }
        };
        MySingleTon.getInstance(this).

                addRequestQue(stringRequest);
    }
}