package com.aampower.aampoweradmin.adapter;

/*public class LedgerAdapterr extends RecyclerView.Adapter<LedgerAdapterr.ViewHolder> {

    byte FontStyleVal;
    private static BluetoothSocket mbtSocket;
    private static OutputStream mbtOutputStream;
    private boolean PrintImage = false;
    int mPrintType = 0;

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

        final String voucher, date, description, debit, credit, balance;
        double balancee = 0, debi = 0, credi = 0;

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


                            Toast.makeText(context, "PRINT", Toast.LENGTH_SHORT).show();

                            dialog.dismiss();
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



            *//*ll_UpdatePrint.setOnClickListener(new View.OnClickListener() {
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
            });*//*

        }
    }


}*/
