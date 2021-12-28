package com.aampower.aampoweradmin.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aampower.aampoweradmin.R;

public class LedgerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    LayoutInflater inflater;
    Activity context;

    public LedgerAdapter (Activity context){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case 0: return new ViewHolder0(inflater.inflate(R.layout.ledger_top_view_item, viewGroup, false));
            case 1: return new ViewHolder2(inflater.inflate(R.layout.ledger_head_item, viewGroup, false));
        }


        return new ViewHolder2(inflater.inflate(R.layout.ledger_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int i) {

        switch (holder.getItemViewType()) {
            case 0:
                ViewHolder0 viewHolder0 = (ViewHolder0)holder;

                break;

            case 1:
                ViewHolder2 viewHolder2 = (ViewHolder2)holder;

                viewHolder2.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, String.valueOf(i), Toast.LENGTH_SHORT).show();
                    }
                });

                break;
        }

    }

    @Override
    public int getItemCount() {
        return 20;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolder0 extends RecyclerView.ViewHolder {

        public ViewHolder0(@NonNull View itemView) {
            super(itemView);
        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {

        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
        }
    }


    class ViewHolder3 extends RecyclerView.ViewHolder {

        public ViewHolder3(@NonNull View itemView) {
            super(itemView);

        }
    }

}
