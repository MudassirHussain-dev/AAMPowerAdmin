package com.aampower.aampoweradmin.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.aampower.aampoweradmin.R;
import com.aampower.aampoweradmin.ui.fragments.CashReceivedFragment;
import com.aampower.aampoweradmin.ui.fragments.ChequeReceivedFragment;
import com.aampower.aampoweradmin.ui.fragments.DiscountAllowFragment;
import com.aampower.aampoweradmin.ui.fragments.LedgerFragment;
import com.aampower.aampoweradmin.ui.fragments.OrdersFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LedgerActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    TextView tvLedName, tvLedCity, tvAccNo, tvLedPhone, tvLedAdd, tvFrom, tvLedTo;
    Toolbar ledgerToolBar;
    BottomNavigationView bottomNaviView;
    RelativeLayout ledgerHideLayout;
    TextView tvtitle;
    LinearLayout parentLayout;

    Activity context;

    String accName, accNo, city, mobile, address, balance;

    LedgerFragment ledgerFragment;
    OrdersFragment ordersFragment;
    ChequeReceivedFragment chequeReceivedFragment;
    CashReceivedFragment cashReceivedFragment;
    DiscountAllowFragment discountAllowFragment;
    FragmentManager fm = getSupportFragmentManager();

    Fragment active;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.context = LedgerActivity.this;
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
        setContentView(R.layout.activity_ledger);

        tvLedName = findViewById(R.id.tvLedName);
        tvLedCity = findViewById(R.id.tvLedCity);
        tvAccNo = findViewById(R.id.tvAccNo);
        tvLedPhone = findViewById(R.id.tvLedPhone);
        tvLedAdd = findViewById(R.id.tvLedAdd);
        tvFrom = findViewById(R.id.tvFrom);
        tvLedTo = findViewById(R.id.tvLedTo);
        ledgerToolBar = findViewById(R.id.ledgerToolBar);
        bottomNaviView = findViewById(R.id.bottomNaviView);
        ledgerHideLayout = findViewById(R.id.ledgerHideLayout);
        tvtitle = findViewById(R.id.tvtitle);
        parentLayout = findViewById(R.id.parentLayout);


        if (getIntent().getExtras() != null) {

            final Bundle bundle = getIntent().getExtras();

            accName = bundle.getString("accName", "");
            accNo = bundle.getString("accNo", "");
            city = bundle.getString("city", "");
            mobile = bundle.getString("mobile", "");
            address = bundle.getString("address", "");
            balance = bundle.getString("balance", "");

            tvLedName.setText(accName);
            tvLedCity.setText(city);
            tvAccNo.setText(accNo);
            tvLedPhone.setText(mobile);
            tvLedAdd.setText(address);


            Bundle bundle1 = new Bundle();
            bundle1.putString("accNo", accNo);
            bundle1.putString("accName", accName);
            bundle1.putString("city", city);
            bundle1.putString("mobile", mobile);
            bundle1.putString("address", address);
            bundle1.putString("balance", balance);

            ledgerFragment = new LedgerFragment();
            ordersFragment = new OrdersFragment();
            chequeReceivedFragment = new ChequeReceivedFragment();
            cashReceivedFragment = new CashReceivedFragment();
            discountAllowFragment = new DiscountAllowFragment();

            ledgerFragment.setArguments(bundle1);
            ordersFragment.setArguments(bundle1);
            chequeReceivedFragment.setArguments(bundle1);
            cashReceivedFragment.setArguments(bundle1);
            discountAllowFragment.setArguments(bundle1);

            active = ledgerFragment;

            fm.beginTransaction().add(R.id.framLay, ordersFragment, "order").hide(ordersFragment).commit();
            fm.beginTransaction().add(R.id.framLay, chequeReceivedFragment, "cheque").hide(chequeReceivedFragment).commit();
            fm.beginTransaction().add(R.id.framLay, cashReceivedFragment, "cash").hide(cashReceivedFragment).commit();
            fm.beginTransaction().add(R.id.framLay, discountAllowFragment, "discount").hide(discountAllowFragment).commit();
            fm.beginTransaction().add(R.id.framLay, ledgerFragment, "ledger").commit();

//            LedgerFragment ledgerFragment = new LedgerFragment();
//            ledgerFragment.setArguments(bundle1);
//
//
//            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//            fragmentTransaction.replace(R.id.framLay, ledgerFragment);
//            fragmentTransaction.commit();


        }

        if (ledgerToolBar != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ledgerToolBar.setElevation(30);
            }
        }


        bottomNaviView.setItemIconTintList(null);
        bottomNaviView.setSelectedItemId(R.id.action_ledger);
        bottomNaviView.setOnNavigationItemSelectedListener(this);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();


        if (id == R.id.action_ledger) {

            ledgerHideLayout.setVisibility(View.VISIBLE);
            tvtitle.setText("Ledger");

            fm.beginTransaction().hide(active).show(ledgerFragment).commit();
            active = ledgerFragment;
            return true;

        } else if (id == R.id.action_order) {

            ledgerHideLayout.setVisibility(View.GONE);

            tvtitle.setText("Order");

            fm.beginTransaction().hide(active).show(ordersFragment).commit();
            active = ordersFragment;
            return true;

        } else if (id == R.id.action_cheque) {

            ledgerHideLayout.setVisibility(View.GONE);

            tvtitle.setText("Cheque Receiving");

            fm.beginTransaction().hide(active).show(chequeReceivedFragment).commit();
            active = chequeReceivedFragment;
            return true;

        } else if (id == R.id.action_cash) {

            ledgerHideLayout.setVisibility(View.GONE);
            tvtitle.setText("Cash Receiving");

            fm.beginTransaction().hide(active).show(cashReceivedFragment).commit();
            active = cashReceivedFragment;
            return true;

        } else if (id == R.id.action_discount) {

            ledgerHideLayout.setVisibility(View.GONE);
            tvtitle.setText("Discount Allowed");

            fm.beginTransaction().hide(active).show(discountAllowFragment).commit();
            active = discountAllowFragment;
            return true;

        }


        return false;
    }

    public void settingFromAndTo(String fromDate, String endDate) {

        String[] fromDatee = fromDate.split(" ");
        String[] toDatee = endDate.split(" ");

        tvFrom.setText(fromDatee[0]);
        tvLedTo.setText(toDatee[0]);

    }

    public void selectedLedgerFragment() {

        ledgerHideLayout.setVisibility(View.VISIBLE);
        tvtitle.setText("Ledger");

        fm.beginTransaction().hide(active).show(ledgerFragment).commit();
        active = ledgerFragment;

        bottomNaviView.setSelectedItemId(R.id.action_ledger);


        Fragment fragment = getSupportFragmentManager().findFragmentByTag("ledger");

        LedgerFragment screen = (LedgerFragment) fragment;

        screen.refreshDate();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", 2);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

    }
}
