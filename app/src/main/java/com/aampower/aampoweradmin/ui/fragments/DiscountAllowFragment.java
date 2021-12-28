package com.aampower.aampoweradmin.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aampower.aampoweradmin.printer.BTWrapperActivity;
import com.aampower.aampoweradmin.printer.PrintUtils;
import com.aampower.aampoweradmin.R;
import com.aampower.aampoweradmin.ui.LedgerActivity;
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

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DiscountAllowFragment extends Fragment implements View.OnClickListener {

    private EditText etDate, etDescription, etAmount;
    private Button btnCashSubmit;

    private Activity context;

    private String accNo, userName, accName, city, mobile, address, selectedDate, currentDate, balance, session_key, admin_level;
    private int curDay, curMonth, curYear;
    private Calendar calendar;
    private AlertDialog dialog;

    String date;
    String description;
    String amount;

    byte FontStyleVal;
    private static BluetoothSocket mbtSocket;
    private static OutputStream mbtOutputStream;
    private boolean PrintImage = false;
    int mPrintType = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.cash_fragment, container, false);
        etDate = view.findViewById(R.id.etDate);
        etDescription = view.findViewById(R.id.etDescription);
        etAmount = view.findViewById(R.id.etAmount);
        btnCashSubmit = view.findViewById(R.id.btnCashSubmit);

        if (getArguments() != null) {

            accNo = getArguments().getString("accNo", "");
            accName = getArguments().getString("accName", "");
            city = getArguments().getString("city", "");
            mobile = getArguments().getString("mobile", "");
            address = getArguments().getString("address", "");
            balance = getArguments().getString("balance", "");


            etAmount.setText(balance);

            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.PREF_FILE), Context.MODE_PRIVATE);

            userName = sharedPreferences.getString("username", "");
            session_key = sharedPreferences.getString("session_key", "");
            admin_level = sharedPreferences.getString("admin_level", "");


        }

        etDescription.setText("DISCOUNT ALLOWED");

        calendar = Calendar.getInstance();

        curDay = calendar.get(Calendar.DAY_OF_MONTH);
        curMonth = calendar.get(Calendar.MONTH) + 1;
        curYear = calendar.get(Calendar.YEAR) % 100;


        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(calendar.getTime());


        etDate.setText(String.valueOf(curDay) + "-" + month_name + "-" + String.valueOf(curYear));

        btnCashSubmit.setOnClickListener(this);
        etDate.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        mPrintType = 0;
        mbtSocket=null;
        StartBluetoothConnection();

        int id = v.getId();

        if (id == R.id.btnCashSubmit) {

            date = etDate.getText().toString().trim();
            description = etDescription.getText().toString().trim();
            amount = etAmount.getText().toString().trim();

            if (date.length() > 0 && description.length() > 0 && amount.length() > 0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Alert");
                builder.setMessage("Are you sure you want to submit?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        cashReceivingTask(getString(R.string.cashReceiveLink), accNo, date, description, amount, userName);

                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();

            } else {

                if (date.length() == 0) {
                    etDate.setError("Required");
                }

                if (description.length() == 0) {
                    etDescription.setError("Required");
                }

                if (amount.length() == 0) {
                    etAmount.setError("Required");
                }

            }

        } else if (id == R.id.etDate) {

            DatePickerDialog pickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth);

                    SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                    String month_name = month_date.format(calendar.getTime());

                    selectedDate = String.valueOf(dayOfMonth) + "-" + month_name + "-" + String.valueOf(year % 100);

                    etDate.setText(selectedDate);
                }

            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            pickerDialog.show();

        }

    }


    protected void StartBluetoothConnection() {
        if (mbtSocket == null) {
            Intent BTIntent = new Intent(getContext(),
                    BTWrapperActivity.class);
            this.startActivityForResult(BTIntent,
                    BTWrapperActivity.REQUEST_CONNECT_BT);
        } else {
            // mbtSocket.connect();
            OutputStream tmpOut = null;
            try {
                tmpOut = mbtSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mbtOutputStream = tmpOut;
            senddatatodevice();
            // cashReceivingTask(getString(R.string.cashReceiveLink), accNo, date, description, amount, userName);
        }
    }

    private void senddatatodevice() {

        /*  byte[] cc = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
            byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
            byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
            byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text

            outputStream.write(cc);
            outputStream.write("Your String");*/

        try {
            mbtOutputStream = mbtSocket.getOutputStream();
            switch (mPrintType) {
                case 0:
                    byte[] Command = {0x1B, 0x21, 0x08};
                    mbtOutputStream.write(Command);

                    Bitmap bitmapaampowerlogo = BitmapFactory.decodeResource(getResources(), R.drawable.aampowerlastlogo);
                    byte[] b_aampowerlogo = PrintUtils.decodeBitmap(bitmapaampowerlogo);
                    mbtOutputStream.write(b_aampowerlogo);
                    mbtOutputStream.flush();

                    //String str_printTitle = "           AAM Power   \n";
                    // String str_printTitle = "   AAM Power Motorcycle Spare \n              Parts\n\n";
                    // String str_printTitle = "-------------------------------\n   AAM Power Motorcycle Spare \n              Parts\n-------------------------------\n\n\n";
                    //String str_printTitle = "-------------------------------\n   AAM Power Motorcycle Spare \n              Parts\n-------------------------------\n        Cheque Receiving\n\n\n";
                    SimpleDateFormat format = new SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss");
                    String str_printTitle = "-------------------------------\n   AAM Power Motorcycle Spare \n              Parts\n-------------------------------\n\n" + "   " + format.format(new Date()) + "\n\n             *****             \n\n";
                    byte[] b_printTitle = str_printTitle.getBytes();
                    mbtOutputStream.write(b_printTitle);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();


                    String str_printName = "  NAME     :  " + accName + "\n\n";
                    byte[] b_printName = str_printName.getBytes();
                    mbtOutputStream.write(b_printName);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();


                    String str_printCity = "  CITY     :  " + city + "\n";
                    byte[] b_printCity = str_printCity.getBytes();
                    mbtOutputStream.write(b_printCity);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();


                    String str_printAccountNumber = "  ACCT#    :  " + accNo + "\n\n";
                    byte[] b_printAccountNumber = str_printAccountNumber.getBytes();
                    mbtOutputStream.write(b_printAccountNumber);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();

                    String str_printDate = "  DATE     :  " + etDate.getText().toString() + "\n\n";
                    byte[] b_printDate = str_printDate.getBytes();
                    mbtOutputStream.write(b_printDate);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();


                    String str_printAmount = "  AMOUNT   :  " + etAmount.getText().toString() + "\n\n\n             *****             \n\n  DISCOUNTED BY >>  " + userName + "\n\n\n";
                    byte[] b_printAmount = str_printAmount.getBytes();
                    mbtOutputStream.write(b_printAmount);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();

                    Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.qrcodeaampower);
                    byte[] imageCommadn = PrintUtils.decodeBitmap(image);
                    mbtOutputStream.write(imageCommadn);
                    mbtOutputStream.flush();

                    String str_PrintDotted = "*******************************\n*          THANK YOU          *\n*******************************\n\n\n\n";
                    byte[] b_PrintDotted = str_PrintDotted.getBytes();
                    mbtOutputStream.write(b_PrintDotted);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();
                    break;
                default:
                    break;
            }
            // outstream.write(0);
            // mbtOutputStream.close();
            // mbtOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mbtSocket != null) {
                mbtOutputStream.close();
                mbtSocket.close();
                mbtSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BTWrapperActivity.REQUEST_CONNECT_BT:
                try {
                    mbtSocket = BTWrapperActivity.getSocket();
                    if (mbtSocket != null) {
                        if (PrintImage == false) {
                            Thread.sleep(100);
                            senddatatodevice();
                            //cashReceivingTask(getString(R.string.cashReceiveLink), accNo, date, description, amount, userName);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void cashReceivingTask(String url, final String accNo, final String datedd, final String desc,
                                   final String amountt, final String username) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);

        View view = LayoutInflater.from(context).inflate(R.layout.loading_alert, null);
        builder.setView(view);

        dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

      //  dialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                     //   dialog.dismiss();

                        if (response != null) {

                            if (response.equals("true")) {

                               /* int bal = Integer.valueOf(balance);

                                int remaining = bal - Integer.valueOf(etAmount.getText().toString().trim());

                                etAmount.setText(String.valueOf(remaining));*/


                                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                builder1.setCancelable(false);

                                builder1.setMessage("Submitted Successfully!");

                                builder1.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        ((LedgerActivity) context).selectedLedgerFragment();

                                    }
                                });

                                builder1.create().show();

                            } else {

                                Toast.makeText(context, "Not submitted", Toast.LENGTH_SHORT).show();

                            }

                        } else {
                            Toast.makeText(context, "Something goes wrong", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_SHORT).show();

                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(context, "Authentication Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(context, "Server Side Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(context, "Network Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(context, "Parse Error!", Toast.LENGTH_SHORT).show();
                }

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                params.put("account_no", accNo);
                params.put("dated", datedd);
                params.put("description", desc);
                params.put("amount", amountt);
                params.put("user", username);
                params.put("session_key", session_key);
                params.put("admin_level", admin_level);

                return params;

            }
        };

        MySingleTon.getInstance(context).addRequestQue(stringRequest);

    }

}
