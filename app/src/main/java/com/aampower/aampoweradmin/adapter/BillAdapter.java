package com.aampower.aampoweradmin.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aampower.aampoweradmin.R;
import com.aampower.aampoweradmin.model.LedgerModel;

import java.util.ArrayList;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder>{

    private Activity context;
    private ArrayList<LedgerModel> arrayList;

    LayoutInflater inflater;

    public BillAdapter (Activity context, ArrayList<LedgerModel> arrayList){

        this.context = context;
        this.arrayList = arrayList;

        this.inflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = inflater.inflate(R.layout.bill_list_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        String qty, name, price, amount;

        qty = arrayList.get(position).getQuantity();
        name = arrayList.get(position).getProductName();
        price = arrayList.get(position).getPrice();
        amount = arrayList.get(position).getAmount();


        viewHolder.tvQuantity.setText(qty);
        viewHolder.tvPName.setText(name);
        viewHolder.tvPrice.setText(price);
        viewHolder.tvAmount.setText(amount);


        if (position % 2 == 0){
            viewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.ledgerFirstItemColor));
        }else {
            viewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.ledgerSecondItemColor));
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvQuantity, tvPName, tvPrice, tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPName = itemView.findViewById(R.id.tvPName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvAmount = itemView.findViewById(R.id.tvAmount);

        }
    }

}
