package com.example.customerinterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderInvoiceActivity extends AppCompatActivity {

    TextView invoicenumber, item_name_list, item_qty_list, item_price_list, total, calculation;
    ArrayList<String> itemnamelist, itemqtylist, itempricelist;
    ArrayList<String> taxnamelist = new ArrayList<>();
    ArrayList<String> taxpercentlist = new ArrayList<>();
    int itemtotal, invoice_number;
    int qtylist = 0;
    float ordertotal = 0;
    String username;
    DatabaseReference taxdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_invoice);
        invoicenumber = findViewById(R.id.invoice_number_textview);
        item_name_list = findViewById(R.id.item_name_list_textview);
        item_qty_list = findViewById(R.id.item_qty_list_textview);
        item_price_list = findViewById(R.id.item_price_list_textview);
        total = findViewById(R.id.item_total_textview);
        calculation = findViewById(R.id.cal_textview);
        username = getIntent().getStringExtra("username");
        taxdata = FirebaseDatabase.getInstance().getReference("TaxData").child(username);
        itemnamelist = getIntent().getStringArrayListExtra("itemnamelist");
        itempricelist = getIntent().getStringArrayListExtra("itempricelist");
        itemqtylist = getIntent().getStringArrayListExtra("itemqtylist");
        itemtotal = getIntent().getIntExtra("itemtotal",0);
        invoice_number = getIntent().getIntExtra("invoicenumber",0);

        taxdata.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taxnamelist.clear();
                taxpercentlist.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()){
                    String taxname = itemSnapshot.child("taxname").getValue(String.class);
                    String taxpercent = itemSnapshot.child("taxpercent").getValue(String.class);
                    taxnamelist.add(taxname);
                    taxpercentlist.add(taxpercent);
                }
                total.append("\n\nSub-Total " + "\n");
                calculation.append("\n\n" + String.valueOf(itemtotal) + "\n");
                ordertotal = itemtotal;
                for (int i = 0; i < taxnamelist.size(); i++) {
                    total.append(taxnamelist.get(i) + "  " + taxpercentlist.get(i) + "% " + "\n");
                    float roundednum = Math.round((itemtotal * (Float.parseFloat(taxpercentlist.get(i))) / 100) * 100.0) / 100.0f;
                    String formattednum = String.format("%.2f", roundednum);
                    calculation.append(formattednum + "\n");
                    ordertotal = ordertotal + (Float.parseFloat(formattednum));
                }
                total.append("Total");
                float roundedNum = Math.round(ordertotal * 100.0) / 100.0f;
                String formattedNum = String.format("%.2f", roundedNum);
                calculation.append(formattedNum);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        for (int i = 0; i < itemnamelist.size(); i++) {
            item_name_list.append("\n\n" + itemnamelist.get(i));
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

    }
}