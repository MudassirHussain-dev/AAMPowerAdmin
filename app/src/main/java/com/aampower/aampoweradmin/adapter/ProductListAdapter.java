package com.aampower.aampoweradmin.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aampower.aampoweradmin.R;
import com.aampower.aampoweradmin.model.CitiesModel;
import com.aampower.aampoweradmin.ui.CustomersListActivity;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by waqar on 18/04/2018.
 */

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private final static int FADE_DURATION = 1000;

    private ArrayList<CitiesModel> arrayList;
    private Context context;
    private LayoutInflater inflater;
    private int identifier;

    private ArrayList<CitiesModel> duplicateList;

    public ProductListAdapter(Context context, ArrayList<CitiesModel> arrayList, int identifier) {
        this.arrayList = arrayList;
        this.context = context;
        this.identifier = identifier;

        this.duplicateList = new ArrayList<>();
        this.duplicateList.addAll(arrayList);

        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.product_name_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (identifier == 0) {
            holder.tvProductName.setText(arrayList.get(position).getProductName());
        }else if (identifier == 1){
            holder.tvProductName.setText(arrayList.get(position).getCityName());
        }

//        setFadeAnimation(holder.tvProductName);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvProductName;
        //CardView proListRootView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvProductName = (TextView) itemView.findViewById(R.id.tvProductName);
//            this.proListRootView = (CardView) itemView.findViewById(R.id.proListRootView);

            tvProductName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (identifier == 1){

                        Bundle bundle = new Bundle();
                        bundle.putString("cityCode", arrayList.get(getAdapterPosition()).getCityNo());
                        bundle.putString("cityName", arrayList.get(getAdapterPosition()).getCityName());

                        Intent intent = new Intent(context, CustomersListActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtras(bundle);

                        context.startActivity(intent);


                    }

                }
            });

        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrayList.clear();
        if (charText.length() == 0) {
            arrayList.addAll(duplicateList);
        } else {
            for (CitiesModel wp : duplicateList) {

                if (identifier == 0) {
                    if (wp.getProductName().toLowerCase(Locale.getDefault()).contains(charText)) {
                        arrayList.add(wp);
                    }
                }else if (identifier == 1){
                    if (wp.getCityName().toLowerCase(Locale.getDefault()).contains(charText)) {
                        arrayList.add(wp);
                    }
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
