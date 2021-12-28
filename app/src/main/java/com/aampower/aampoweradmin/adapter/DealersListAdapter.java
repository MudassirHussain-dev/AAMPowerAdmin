package com.aampower.aampoweradmin.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aampower.aampoweradmin.R;
import com.aampower.aampoweradmin.model.DealersModel;
import com.aampower.aampoweradmin.ui.FullImageActivity;
import com.aampower.aampoweradmin.ui.LedgerActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by waqar on 19/04/2018.
 */

public class DealersListAdapter extends RecyclerView.Adapter<DealersListAdapter.ViewHolder> {

    private final static int FADE_DURATION = 1000;

    private Activity context;
    private ArrayList<DealersModel> arrayList;
    LayoutInflater inflater;

    String city;

    private ArrayList<DealersModel> duplicateList;

    public DealersListAdapter(Activity context, ArrayList<DealersModel> arrayList, String city) {
        this.context = context;
        this.arrayList = arrayList;

        this.duplicateList = new ArrayList<>();
        this.duplicateList.addAll(arrayList);

        inflater = LayoutInflater.from(context);

        this.city = city;

    }

    public void refreshData(ArrayList<DealersModel> arrayList){

        this.arrayList.clear();
        this.arrayList = arrayList;

        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.customers_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final String accountNo, accountName, address, mobileNo, balance, profileLink;
        double balancee;


        accountNo = arrayList.get(position).getAccountNo();
        accountName = arrayList.get(position).getAccountName();
        address = arrayList.get(position).getAddress();
        mobileNo = arrayList.get(position).getMobileNo();
        balance = arrayList.get(position).getBalance();
//        profileLink = arrayList.get(position).


        balancee = Double.valueOf(balance);

        NumberFormat formatter = new DecimalFormat("#,###");
        String formattedNumber = formatter.format(balancee);

        holder.tvCusName.setText(accountName);
        holder.tvCusAddress.setText(address);
        holder.tvTotalAmount.setText("Rs. " + formattedNumber);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();

                bundle.putString("accNo", accountNo);
                bundle.putString("accName", accountName);
                bundle.putString("city", city);
                bundle.putString("address", address);
                bundle.putString("mobile", mobileNo);
                bundle.putString("balance", balance);

                Intent intent = new Intent(context, LedgerActivity.class);
                intent.putExtras(bundle);

                context.startActivityForResult(intent, 1);

            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        private TextView tvCusName, tvCusAddress, tvCallNow, tvTotalAmount;
        private ImageView profile_image;


        public ViewHolder(View itemView) {
            super(itemView);

            this.tvCusName = (TextView) itemView.findViewById(R.id.tvCusName);
            this.tvCusAddress = (TextView) itemView.findViewById(R.id.tvCusAddress);
            this.tvCallNow = (TextView) itemView.findViewById(R.id.tvCallNow);
            this.tvTotalAmount = (TextView) itemView.findViewById(R.id.tvTotalAmount);
            this.profile_image = (ImageView) itemView.findViewById(R.id.profile_image);

            this.profile_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();

                    bundle.putString("accNo", arrayList.get(getAdapterPosition()).getAccountNo());
                    bundle.putString("accName", arrayList.get(getAdapterPosition()).getAccountName());

                    Intent intent = new Intent(context, FullImageActivity.class);
                    intent.putExtras(bundle);

                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(context, profile_image, Objects.requireNonNull(ViewCompat.getTransitionName(profile_image)));

                    context.startActivity(intent, optionsCompat.toBundle());

                }
            });

            this.tvCallNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //region

                    String[] mob = {};

                    if (arrayList.get(getAdapterPosition()).getMobileNo().length() > 0) {
                        mob = arrayList.get(getAdapterPosition()).getMobileNo().split(",");
                    }

                    int b = mob.length;

                    AlertDialog.Builder build = new AlertDialog.Builder(context);
                    build.setTitle("Select To Call");

                    final String[] finalMob = mob;
                    build.setItems(mob, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialContactPhone(finalMob[which]);


                        }
                    }).create().show();

                    //endregion

                }
            });


        }
    }

    private void dialContactPhone(final String phoneNumber) {
        context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }

    private void sendingSMS(String address) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At least KitKat
        {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context); // Need to change the build to API 19

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra("address", address);

            if (defaultSmsPackageName != null)// Can be null in case that there is no default, then the user would be able to choose
            // any app that support this intent.
            {
                sendIntent.setPackage(defaultSmsPackageName);
            }
            context.startActivity(sendIntent);

        } else // For early versions, do what worked for you before.
        {
            Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", address);
//            smsIntent.putExtra("sms_body","message");
            context.startActivity(smsIntent);
        }
    }

    String[] f(String[] first, String[] second, String[] third) {
        List<String> both = new ArrayList<String>(first.length + second.length + third.length);
        if (first.length > 0) {
            Collections.addAll(both, first);
        }
        if (second.length > 0) {
            Collections.addAll(both, second);
        }
        if (third.length > 0) {
            Collections.addAll(both, third);
        }
        return both.toArray(new String[both.size()]);
    }

    protected void sendEmail(String email) {

        String[] TO = {email};
//        String[] CC = {"xyz@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
//        emailIntent.putExtra(Intent.EXTRA_CC, CC);
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
//        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrayList.clear();
        if (charText.length() == 0) {
            arrayList.addAll(duplicateList);
        } else {
            for (DealersModel wp : duplicateList) {

                if (wp.getAccountName().toLowerCase(Locale.getDefault()).contains(charText) ||
                        wp.getAddress().toLowerCase(Locale.getDefault()).contains(charText) ||
                        wp.getMobileNo().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }


}
