package com.example.customerinterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OccupiedTableInvoiceActivity extends AppCompatActivity {

    DatabaseReference Tablereference, CxOrderReference, taxdata;
    String username, tableId;
    TextView invoicenumbertv, invoicedatetv, calculation;
    ArrayList<String> ItemNameList = new ArrayList<>();
    ArrayList<String> ItemPriceList = new ArrayList<>();
    ArrayList<String> ItemQtyList = new ArrayList<>();
    ArrayList<String> ItemTotalList = new ArrayList<>();
    ArrayList<String> taxnamelist = new ArrayList<>();
    ArrayList<String> taxpercentlist = new ArrayList<>();
    TextView item_name_list, item_qty_list, item_price_list, total;
    int qtylist = 0;
    int itemtotal = 0;
    float ordertotal;
    Button make_payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occupied_table_invoice);
        username = getIntent().getStringExtra("username");
        tableId = getIntent().getStringExtra("table");
        invoicenumbertv = findViewById(R.id.invoice_number_textview);
        invoicedatetv = findViewById(R.id.invoice_date_textview);
        make_payment = findViewById(R.id.make_payment);
        total = findViewById(R.id.item_total_textview);
        item_name_list = findViewById(R.id.item_name_list_textview);
        item_qty_list = findViewById(R.id.item_qty_list_textview);
        item_price_list = findViewById(R.id.item_price_list_textview);
        calculation = findViewById(R.id.cal_textview);
        taxdata = FirebaseDatabase.getInstance().getReference("TaxData").child(username);
        Tablereference = FirebaseDatabase.getInstance().getReference("TableInfo").child(username).child(tableId);
        CxOrderReference = FirebaseDatabase.getInstance().getReference("CxOrder").child(username);

        Tablereference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ItemNameList.clear();
                ItemQtyList.clear();
                ItemPriceList.clear();
                ItemTotalList.clear();
                String invoicenumer = snapshot.child("invoicenumber").getValue(String.class);
                invoicenumbertv.setText(invoicenumer);
                CxOrderReference.child(invoicenumer).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String date = snapshot.child("invoicedate").getValue(String.class);
                        invoicedatetv.setText("Invoice Date - " + date);
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String name = dataSnapshot.child("itemname").getValue(String.class);
                            String qty = dataSnapshot.child("itemqty").getValue(String.class);
                            String total = dataSnapshot.child("itemtotal").getValue(String.class);
                            ItemNameList.add(name);
                            ItemQtyList.add(qty);
                            ItemPriceList.add(total);
                            ItemTotalList.add(total);
                        }

                        for (int i = 0; i < ItemNameList.size(); i++) {
                            if (ItemNameList.get(i) != null) {
                                item_name_list.append("\n\n" + ItemNameList.get(i));
                            }
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
                                    if (blankLineCounts[i] == 0 && blankLineCounts[i + 1] == 1) {
                                        item_qty_list.append("\n\n");
                                        item_price_list.append("\n\n");
                                    }
                                    if (blankLineCounts[i] == 0 && blankLineCounts[i + 1] == 0) {
                                        item_qty_list.append("\n");
                                        item_price_list.append("\n");
                                    }
                                    if (blankLineCounts[i] == 1 && blankLineCounts[i + 1] == 0) {
                                        if (ItemQtyList.get(qtylist) != null) {
                                            item_qty_list.append(ItemQtyList.get(qtylist));
                                            item_price_list.append(ItemPriceList.get(qtylist));
                                            qtylist++;
                                        }

                                    }
                                }


                                // Remove the listener so it doesn't keep getting called
                                item_name_list.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                        });

                        for (int i = 0; i < ItemTotalList.size()-1; i++) {
                            itemtotal = itemtotal + Integer.parseInt(ItemTotalList.get(i));
                        }
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        make_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tablereference.child("availibility").setValue("collect payment");
                Toast.makeText(OccupiedTableInvoiceActivity.this, "Assigned person is coming to receive payment", Toast.LENGTH_SHORT).show();
            }
        });
    }
}