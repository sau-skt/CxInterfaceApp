package com.example.customerinterface;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;

public class OrderStatusActivity extends AppCompatActivity {

    TextView invoicenumber, item_name_list, item_qty_list, item_price_list, total;
    ArrayList<String> itemnamelist, itemqtylist, itempricelist;
    int itemtotal, invoice_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        invoicenumber = findViewById(R.id.invoice_number_textview);
        item_name_list = findViewById(R.id.item_name_list_textview);
        item_qty_list = findViewById(R.id.item_qty_list_textview);
        item_price_list = findViewById(R.id.item_price_list_textview);
        total = findViewById(R.id.item_total_textview);
        itemnamelist = getIntent().getStringArrayListExtra("itemnamelist");
        itempricelist = getIntent().getStringArrayListExtra("itempricelist");
        itemqtylist = getIntent().getStringArrayListExtra("itemqtylist");
        itemtotal = getIntent().getIntExtra("itemtotal",0);
        invoice_number = getIntent().getIntExtra("invoicenumber",0);
        for (int i = 0; i < itemnamelist.size(); i++) {
            item_name_list.append((CharSequence) itemnamelist.get(i) + "\n\n");
        }
        for (int i = 0; i < itemqtylist.size(); i++) {
            item_qty_list.append((CharSequence) itemqtylist.get(i) + "\n\n");
        }
        for (int i = 0; i < itempricelist.size(); i++) {
            item_price_list.append((CharSequence) itempricelist.get(i) + "\n\n");
        }
        invoicenumber.setText(String.valueOf(invoice_number));
        total.setText("Total\t\t" + String.valueOf(itemtotal));

    }
}