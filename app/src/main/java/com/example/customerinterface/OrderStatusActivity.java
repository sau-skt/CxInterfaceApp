package com.example.customerinterface;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;

public class OrderStatusActivity extends AppCompatActivity {

    TextView invoicenumber, item_name_list, item_qty_list, item_price_list, total;
    ArrayList<String> itemnamelist, itemqtylist, itempricelist;
    int itemtotal, invoice_number;
    int qtylist = 0;

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
            item_name_list.append("\n\n" + (CharSequence) itemnamelist.get(i));
        }
        item_name_list.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Get the current line count
                int[] blankLineCounts = new int[item_name_list.getLineCount()];

                for (int i = 0; i < item_name_list.getLineCount(); i++) {
                    // Get the current line's text
                    int lineStart = item_name_list.getLayout().getLineStart(i);
                    int lineEnd = item_name_list.getLayout().getLineEnd(i);
                    String lineText = item_name_list.getText().subSequence(lineStart, lineEnd).toString();

                    // Check if the line is blank

                    if (lineText.trim().isEmpty()) {
                        // Increment the blank line counter for this line's index
                        blankLineCounts[i]++;
                    }
                }

// Print the blank line counts for each line
                for (int i = 0; i < blankLineCounts.length - 1; i++) {
                    Log.d("QWE", "Line " + i + ": " + blankLineCounts[i]);
                    if (blankLineCounts[i] == 0 && blankLineCounts[i+1] == 1) {
                        item_qty_list.append("\n\n");
                        item_price_list.append("\n\n");
                    }
                    if (blankLineCounts[i] == 0 && blankLineCounts[i+1] == 0) {
                        item_qty_list.append("\n");
                        item_price_list.append("\n");
                    }
                    if (blankLineCounts[i] == 1 && blankLineCounts[i+1] == 0) {
                        item_qty_list.append(itemqtylist.get(qtylist));
                        item_price_list.append(itempricelist.get(qtylist));
                        qtylist++;
                    }
                }


                // Remove the listener so it doesn't keep getting called
                item_name_list.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        invoicenumber.setText(String.valueOf(invoice_number));
        total.setText("\n\nTotal " + String.valueOf(itemtotal) + "\n\n");

    }
}