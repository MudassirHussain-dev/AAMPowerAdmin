package com.aampower.aampoweradmin.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aampower.aampoweradmin.printer.BTWrapperActivity;
import com.aampower.aampoweradmin.printer.PrintUtils;
import com.aampower.aampoweradmin.R;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChequeReceivedFragment extends Fragment implements View.OnClickListener {


    private EditText etCDate, etCNumber, etCDescription, etCAmount;
    private Button btnChequeSubmit;
    private String accNo, accName, city, mobile, address, userName, selectedDate, currentDate, balance, session_key, admin_level;
    private int curDay, curMonth, curYear;
    private Calendar calendar;
    private AlertDialog dialog;
    private Activity context;

    byte FontStyleVal;
    private static BluetoothSocket mbtSocket;
    private static OutputStream mbtOutputStream;
    private boolean PrintImage = false;
    int mPrintType = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.cheque_fragment, container, false);
        etCDate = view.findViewById(R.id.etCDate);
        etCNumber = view.findViewById(R.id.etCNumber);
        etCDescription = view.findViewById(R.id.etCDescription);
        etCAmount = view.findViewById(R.id.etCAmount);
        btnChequeSubmit = view.findViewById(R.id.btnChequeSubmit);

        if (getArguments() != null) {

            accNo = getArguments().getString("accNo", "");
            accName = getArguments().getString("accName", "");
            city = getArguments().getString("city", "");
            mobile = getArguments().getString("mobile", "");
            address = getArguments().getString("address", "");
            balance = getArguments().getString("balance", "");


            etCAmount.setText(balance);

            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.PREF_FILE), Context.MODE_PRIVATE);

            userName = sharedPreferences.getString("username", "");
            session_key = sharedPreferences.getString("session_key", "");
            admin_level = sharedPreferences.getString("admin_level", "");

        }


        calendar = Calendar.getInstance();

        curDay = calendar.get(Calendar.DAY_OF_MONTH);
        curMonth = calendar.get(Calendar.MONTH) + 1;
        curYear = calendar.get(Calendar.YEAR) % 100;


        @SuppressLint("SimpleDateFormat") SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(calendar.getTime());


        etCDate.setText(curDay + "-" + month_name + "-" + curYear);

        btnChequeSubmit.setOnClickListener(this);
        etCDate.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.etDate) {
            DatePickerDialog pickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth);

                    SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                    String month_name = month_date.format(calendar.getTime());

                    selectedDate = dayOfMonth + "-" + month_name + "-" + year % 100;

                    etCDate.setText(selectedDate);
                }

            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            pickerDialog.show();

        }
        if (id == R.id.btnChequeSubmit) {
            mPrintType = 0;
            mbtSocket=null;
            StartBluetoothConnection();
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

        }
    }

    private void senddatatodevice() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss");
            mbtOutputStream = mbtSocket.getOutputStream();
            switch (mPrintType) {
                case 0:
                    byte[] Command = {0x1B, 0x21, FontStyleVal};
                    mbtOutputStream.write(Command);


                    Bitmap bitmapaampowerlogo = BitmapFactory.decodeResource(getResources(), R.drawable.aampowerlastlogo);
                    byte[] b_aampowerlogo = PrintUtils.decodeBitmap(bitmapaampowerlogo);
                    mbtOutputStream.write(b_aampowerlogo);
                    mbtOutputStream.flush();

                    //String str_printTitle = "           AAM Power   \n";
                    // String str_printTitle = "   AAM Power Motorcycle Spare \n              Parts\n\n";
                    String str_printTitle = "-------------------------------\n   AAM Power Motorcycle Spare \n              Parts\n-------------------------------\n\n"+"   "+     format.format(new Date())   +"\n\n             *****             \n\n";
                    //String str_printTitle = "-------------------------------\n   AAM Power Motorcycle Spare \n              Parts\n-------------------------------\n        Cheque Receiving\n\n\n";
                    byte[] b_printTitle = str_printTitle.getBytes();
                    mbtOutputStream.write(b_printTitle);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();


                    String str_printName = "  NAME     :  " + accName + "\n\n";
                    byte[] b_printName = str_printName.getBytes();
                    mbtOutputStream.write(b_printName);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();


                    String str_printCity = "  CITY     :  " + city + "\n\n";
                    byte[] b_printCity = str_printCity.getBytes();
                    mbtOutputStream.write(b_printCity);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();


                    String str_printAccountNumber = "  ACCT#    :  " + accNo + "\n\n";
                    byte[] b_printAccountNumber = str_printAccountNumber.getBytes();
                    mbtOutputStream.write(b_printAccountNumber);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();


                    String str_printDate = "  DATE     :  " + etCDate.getText().toString() + "\n\n";
                    byte[] b_printDate = str_printDate.getBytes();
                    mbtOutputStream.write(b_printDate);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();

                    String str_prinChequeNumber = "  CK#      :  " + etCNumber.getText().toString() + "\n\n";
                    byte[] b_printChequeNumber = str_prinChequeNumber.getBytes();
                    mbtOutputStream.write(b_printChequeNumber);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();

                    String str_prinChequeDescription = "  DESC     :  " + etCDescription.getText().toString() + "\n\n";
                    byte[] b_printChequeDescription = str_prinChequeDescription.getBytes();
                    mbtOutputStream.write(b_printChequeDescription);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();

                    String str_printAmount = "  AMOUNT   :  " + etCAmount.getText().toString() + "\n\n\n             *****             \n\n  RECEIVED BY >>  " + userName + "\n\n\n";
                    byte[] b_printAmount = str_printAmount.getBytes();
                    mbtOutputStream.write(b_printAmount);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();

                    /*String str_spacePrint="   ";
                    byte[] b_printspace = str_spacePrint.getBytes();
                    mbtOutputStream.write(b_printspace);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();*/

                    Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.qrcodeaampower);
                    byte[] imageCommadn = PrintUtils.decodeBitmap(image);
                    mbtOutputStream.write(imageCommadn);
                    mbtOutputStream.flush();

                   /* String str_printedby="\n  RECEIVED BY >>  " + userName + "\n\n";
                    byte[] b_printby = str_printedby.getBytes();
                    mbtOutputStream.write(b_printby);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();*/

                    String str_PrintDotted = "\n\n*******************************\n*          THANK YOU          *\n*******************************\n\n\n\n";
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

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
