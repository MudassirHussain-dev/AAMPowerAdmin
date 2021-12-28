package com.aampower.aampoweradmin.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aampower.aampoweradmin.printer.BTWrapperActivity;
import com.aampower.aampoweradmin.printer.PrintUtils;
import com.aampower.aampoweradmin.R;
import com.aampower.aampoweradmin.model.LedgerModel;
import com.aampower.aampoweradmin.ui.BillActivity;
import com.aampower.aampoweradmin.ui.LedgerActivity;
import com.aampower.aampoweradmin.ui.MainActivity;
import com.aampower.aampoweradmin.ui.UpdateLedgerActivity;
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

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LedgerFragment extends Fragment {

    private ShimmerFrameLayout mShimmerLayout;
    private RecyclerView ledgerRecView;
    private RelativeLayout noRecordLayout;
    private TextView tvNoRecord, tvNoRecordMsg;

    private SwipeRefreshLayout pullToRefresh;

    private ArrayList<LedgerModel> arrayList;
    private LedgerAdapterr adapter;

    private Activity context;

    private String accName, accNo, city, mobile, address;
    private String session_key, username, admin_level;

    private RequestQueue mRequestQueue;


    byte FontStyleVal;
    private static BluetoothSocket mbtSocket;
    private static OutputStream mbtOutputStream;
    private boolean PrintImage = false;
    static int mPrintType = 0;
    String voucher, date, description, debit, credit, balance;
    double balancee = 0, debi = 0, credi = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.context = getActivity();
        View view = inflater.inflate(R.layout.ledger_fragment, container, false);

        mShimmerLayout = view.findViewById(R.id.ledgerList_shimmer_view_container);
        ledgerRecView = view.findViewById(R.id.ledgerRecView);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        noRecordLayout = view.findViewById(R.id.noRecordLayout);
        tvNoRecord = view.findViewById(R.id.tvNoRecord);
        tvNoRecordMsg = view.findViewById(R.id.tvNoRecordMsg);

        ledgerRecView.setLayoutManager(new LinearLayoutManager(context));

        if (getArguments() != null) {

            accNo = getArguments().getString("accNo", "");
            accName = getArguments().getString("accName", "");
            city = getArguments().getString("city", "");
            mobile = getArguments().getString("mobile", "");
            address = getArguments().getString("address", "");

            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.PREF_FILE), Context.MODE_PRIVATE);

            session_key = sharedPreferences.getString("session_key", "");
            username = sharedPreferences.getString("username", "");
            admin_level = sharedPreferences.getString("admin_level", "");


            mRequestQueue = Volley.newRequestQueue(context);

            LedgerTask(getString(R.string.ledgerLink), "");

        }


        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mShimmerLayout.setVisibility(View.VISIBLE);
                ledgerRecView.setVisibility(View.VISIBLE);
                noRecordLayout.setVisibility(View.GONE);

                LedgerTask(getString(R.string.ledgerLink), "");

            }
        });

        // get initial position
//        final int initialTopPosition = ledgerRecView.getTop();
//
//        // Set a listener to scroll view
//        ledgerRecView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && adapter != null) {
//
//                    if(ledgerToolBar == null) {
//                        return;
//                    }
//
//                    if(!recyclerView.canScrollVertically(-1)) {
//                        // we have reached the top of the list
//                        ledgerToolBar.setElevation(0f);
//                    } else {
//                        // we are not at the top yet
//                        ledgerToolBar.setElevation(30f);
//                    }
//
//                }
//            }
//        });


        return view;
    }

    public void refreshDate() {

        LedgerTask(getString(R.string.ledgerLink), "refresh");

    }

    private void LedgerTask(String url, final String refresh) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                pullToRefresh.setRefreshing(false);

                if (response != null) {

                    try {

                        if (isValidJSON(response)) {


                            JSONArray jsonArray = new JSONArray(response);

                            arrayList = new ArrayList<>();

                            float openingBal = 0.0f;

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                if (i == 0) {
                                    if (!jsonObject.getString("open_bal").equals("")) {
                                        openingBal = Float.valueOf(jsonObject.getString("open_bal"));
                                    }
                                }
                                LedgerModel model = new LedgerModel();

                                model.setVoucher(jsonObject.getString("Voucher_no"));
                                model.setCredit(jsonObject.getString("credit"));
                                model.setDebit(jsonObject.getString("debit"));
                                model.setDate(jsonObject.getString("dated"));
                                model.setDescription(jsonObject.getString("description"));

                                String deb, cre;

                                deb = jsonObject.getString("debit");
                                cre = jsonObject.getString("credit");


                                float debit, credit, balance;

                                if (!deb.equals("")) {
                                    debit = Float.valueOf(deb);
                                } else {
                                    debit = 0;
                                }

                                if (!cre.equals("")) {
                                    credit = Float.valueOf(cre);
                                } else {
                                    credit = 0;
                                }

                                balance = (openingBal + debit) - credit;
                                openingBal = balance;

                                model.setBalance(String.valueOf(balance));

                                arrayList.add(model);

                            }

                            if (arrayList.size() > 0) {

                                ((LedgerActivity) context).settingFromAndTo(arrayList.get(0).getDate(), arrayList.get(arrayList.size() - 1).getDate());

                                mShimmerLayout.stopShimmerAnimation();
                                mShimmerLayout.setVisibility(View.GONE);

                                Context recContext = ledgerRecView.getContext();
                                LayoutAnimationController controller = null;
                                controller = AnimationUtils.loadLayoutAnimation(recContext, R.anim.layout_slid_from_bottom);

                                if (refresh.equals("refresh")) {

                                    adapter.updateData(arrayList);


                                } else {
                                    adapter = new LedgerAdapterr(context, arrayList, accNo, accName, city, mobile, address);
                                    ledgerRecView.setAdapter(adapter);

                                    ledgerRecView.setLayoutAnimation(controller);
                                    ledgerRecView.getAdapter().notifyDataSetChanged();
                                    ledgerRecView.scheduleLayoutAnimation();
                                }


                            } else {
//                                    Toast.makeText(context, "Ledger not found", Toast.LENGTH_SHORT).show();
                                noRecordLayout.setVisibility(View.VISIBLE);
                                ledgerRecView.setVisibility(View.GONE);
//                                    finish();
                            }

                        } else {

                            if (response.equals("false")) {

                                SharedPreferences.Editor preferences = context.getSharedPreferences(context.getResources().getString(R.string.PREF_FILE), Context.MODE_PRIVATE).edit();
                                preferences.clear();
                                preferences.apply();

                                Intent i = new Intent(context, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);

                            } else if (refresh.equals("")) {
                                noRecordLayout.setVisibility(View.VISIBLE);
                                ledgerRecView.setVisibility(View.GONE);
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(context, "Something goes wrong", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                pullToRefresh.setRefreshing(false);

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
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("account_no", accNo);
                params.put("session_key", session_key);
                params.put("username", username);
                params.put("admin_level", admin_level);

                return params;
            }
        };

        MySingleTon.getInstance(context).addRequestQue(stringRequest);

    }


    public boolean isValidJSON(String toTestStr) {
        try {
            new JSONObject(toTestStr);
        } catch (JSONException jsExcp) {
            try {
                new JSONArray(toTestStr);
            } catch (JSONException jsExcp1) {
                return false;
            }
        }
        return true;
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
        mShimmerLayout.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        mShimmerLayout.stopShimmerAnimation();
        super.onPause();
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
                            // cashReceivingTask(getString(R.string.cashReceiveLink), accNo, date, description, amount, userName);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    public class LedgerAdapterr extends RecyclerView.Adapter<LedgerAdapterr.ViewHolder> {

        private Activity context;
        private LayoutInflater inflater;

        private ArrayList<LedgerModel> arrayList;

        private String accNo, accName, city, mobile, address;

        public LedgerAdapterr(Activity context, ArrayList<LedgerModel> arrayList,
                              String accNo, String accName, String city, String mobile, String address) {

            this.context = context;
            this.inflater = LayoutInflater.from(context);

            this.arrayList = arrayList;

            this.accNo = accNo;
            this.accName = accName;
            this.city = city;
            this.mobile = mobile;
            this.address = address;

        }

        public void updateData(ArrayList<LedgerModel> arrayList) {

            this.arrayList.clear();
            this.arrayList = arrayList;

            notifyDataSetChanged();

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view = inflater.inflate(R.layout.ledger_list_item, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

            //   final String voucher, date, description, debit, credit, balance;


            voucher = arrayList.get(position).getVoucher();
            date = arrayList.get(position).getDate().split("T")[0];
            description = arrayList.get(position).getDescription();
            debit = arrayList.get(position).getDebit();
            credit = arrayList.get(position).getCredit();
            balance = arrayList.get(position).getBalance();


            if (!balance.equals("")) {
                balancee = Double.valueOf(balance);
            }
            if (!debit.equals("")) {
                debi = Double.valueOf(debit);
            }
            if (!credit.equals("")) {
                credi = Double.valueOf(credit);
            }

            NumberFormat formatter = new DecimalFormat("#,###");
            String formattedNumber = formatter.format(balancee);
            String formattedDebit = formatter.format(debi);
            String formattedCredit = formatter.format(credi);

            viewHolder.tvVoucher.setText(voucher);
            viewHolder.tvDate.setText(date);
            viewHolder.tvDescription.setText(description);
            viewHolder.tvDebit.setText(debi == 0 ? "" : formattedDebit);
            viewHolder.tvCredit.setText(credi == 0 ? "" : formattedCredit);
            viewHolder.tvBalance.setText(formattedNumber);


            if (position % 2 == 0) {
                viewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.ledgerFirstItemColor));
            } else {
                viewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.ledgerSecondItemColor));
            }

            viewHolder.ll_UpdatePrint.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (arrayList.get(position).getVoucher().trim().charAt(0) == 'C') {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                        builder.setTitle("Confirm");
                        builder.setIcon(R.mipmap.ic_launcher);
                        builder.setMessage("Do you want to print or update?");

                        builder.setPositiveButton("PRINT", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                Toast.makeText(context, "PRINT", Toast.LENGTH_SHORT).show();
                                mPrintType = 0;
                                mbtSocket=null;
                                StartBluetoothConnection();


                            }
                        });

                        builder.setNegativeButton("UPDATE", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Bundle bundle = new Bundle();
                                bundle.putString("voucherNo", arrayList.get(position).getVoucher());
                                bundle.putString("accName", accName);
                                bundle.putString("city", city);
                                bundle.putString("address", address);
                                bundle.putString("mobile", mobile);
                                bundle.putString("Credit", credit);
                                Intent intent = new Intent(context, UpdateLedgerActivity.class);
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();

                    } else {
                        Toast.makeText(context, "Please Select the Voucher Entry..", Toast.LENGTH_SHORT).show();
                    }


                    return false;
                }
            });

            viewHolder.ll_UpdatePrint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();

                    bundle.putString("voucherNo", arrayList.get(position).getVoucher());
                    bundle.putString("accName", accName);
                    bundle.putString("city", city);
                    bundle.putString("address", address);
                    bundle.putString("mobile", mobile);

                    Intent intent = new Intent(context, BillActivity.class);
                    intent.putExtras(bundle);

                    context.startActivity(intent);

                }
            });

        }


        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvVoucher, tvDate, tvDescription, tvDebit, tvCredit, tvBalance;
            LinearLayout ll_UpdatePrint;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvVoucher = itemView.findViewById(R.id.tvVoucher);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvDescription = itemView.findViewById(R.id.tvDescription);
                tvDebit = itemView.findViewById(R.id.tvDebit);
                tvCredit = itemView.findViewById(R.id.tvCredit);
                tvBalance = itemView.findViewById(R.id.tvBalance);
                ll_UpdatePrint = itemView.findViewById(R.id.ll_UpdatePrint);



            /*ll_UpdatePrint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();

                    bundle.putString("voucherNo", arrayList.get(getAdapterPosition()).getVoucher());
                    bundle.putString("accName", accName);
                    bundle.putString("city", city);
                    bundle.putString("address", address);
                    bundle.putString("mobile", mobile);

                    Intent intent = new Intent(context, BillActivity.class);
                    intent.putExtras(bundle);

                    context.startActivity(intent);

                }
            });*/

            }
        }


    }

    protected void StartBluetoothConnection() {
        if (mbtSocket == null) {
            Intent BTIntent = new Intent(context,
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
        try {
            SimpleDateFormat format = new SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss");
            mbtOutputStream = mbtSocket.getOutputStream();
            switch (mPrintType) {
                case 0:
                    byte[] Command = {0x1B, 0x21, FontStyleVal};
                    mbtOutputStream.write(Command);

                    Bitmap bitmapaampowerlogo = BitmapFactory.decodeResource(context.getResources(), R.drawable.aampowerlastlogo);
                    byte[] b_aampowerlogo = PrintUtils.decodeBitmap(bitmapaampowerlogo);
                    mbtOutputStream.write(b_aampowerlogo);
                    mbtOutputStream.flush();

                    //String str_printTitle = "           AAM Power   \n";
                    // String str_printTitle = "   AAM Power Motorcycle Spare \n              Parts\n\n";
                    //String str_printTitle = "-------------------------------\n   AAM Power Motorcycle Spare \n              Parts\n-------------------------------\n\n\n";
                    //String str_printTitle = "-------------------------------\n   AAM Power Motorcycle Spare \n              Parts\n-------------------------------\n        Cheque Receiving\n\n\n";
                    String str_printTitle = "-------------------------------\n   AAM Power Motorcycle Spare \n              Parts\n-------------------------------\n\n" + "   " + format.format(new Date()) + "\n\n             *****             \n\n";
                    byte[] b_printTitle = str_printTitle.getBytes();
                    mbtOutputStream.write(b_printTitle);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();

                    String str_printVoucher = "VOUCHER    :  " + voucher + " \n\n";
                    byte[] b_printVoucher = str_printVoucher.getBytes();
                    mbtOutputStream.write(b_printVoucher);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();

                    String str_printCity = "CITY       :  " + city + "\n\n";
                    byte[] b_printCity = str_printCity.getBytes();
                    mbtOutputStream.write(b_printCity);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();

                    String str_printName = "NAME       :  " + accName + "\n\n";
                    byte[] b_printName = str_printName.getBytes();
                    mbtOutputStream.write(b_printName);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();

                    String str_printAccountNumber = "ACCT#      :  " + accNo + "\n\n";
                    byte[] b_printAccountNumber = str_printAccountNumber.getBytes();
                    mbtOutputStream.write(b_printAccountNumber);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();

                    String str_printDate = "DATE       :  " + date + "\n\n";
                    byte[] b_printDate = str_printDate.getBytes();
                    mbtOutputStream.write(b_printDate);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();


                    String str_prinChequeDescription = "DESC       :  " + "CASH RECEIVED" + "\n\n";
                    byte[] b_printChequeDescription = str_prinChequeDescription.getBytes();
                    mbtOutputStream.write(b_printChequeDescription);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();

                    String str_printCashBeforeAmount = "TOTAL BAL ` :  " + balancee + "\n\n";
                    byte[] b_printCashBeforeAmount = str_printCashBeforeAmount.getBytes();
                    mbtOutputStream.write(b_printCashBeforeAmount);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();

                    String str_printCashReceivable = "RECEIVED   :  " + credit + "\n\n";
                    byte[] b_printCashReceivable = str_printCashReceivable.getBytes();
                    mbtOutputStream.write(b_printCashReceivable);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();



                   /* String str_printAmount = "REMAINING  :  " + str_Remaing + "\n\n\n             *****             \n\n  RECEIVED BY >>  " + username + "\n\n\n";
                    byte[] b_printAmount = str_printAmount.getBytes();
                    mbtOutputStream.write(b_printAmount);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();*/

                    Bitmap image_duplicate = BitmapFactory.decodeResource(getResources(), R.drawable.duplicate1);
                    byte[] image_duplicateCommadn = PrintUtils.decodeBitmap(image_duplicate);
                    mbtOutputStream.write(image_duplicateCommadn);
                    mbtOutputStream.flush();

                    Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.qrcodeaampower);
                    byte[] imageCommadn = PrintUtils.decodeBitmap(image);
                    mbtOutputStream.write(imageCommadn);
                    mbtOutputStream.flush();

                    String str_space = "\n\n\n";
                    byte[] b_space = str_space.getBytes();
                    mbtOutputStream.write(b_space);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.write(0x0D);
                    mbtOutputStream.flush();

                    String str_PrintDotted = "\n\n\n*******************************\n*          THANK YOU          *\n*******************************\n\n\n\n";
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


}
