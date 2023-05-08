package com.example.customerinterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.customerinterface.adapters.CxCartActivityAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CxCartActivity extends AppCompatActivity {

    DatabaseReference cxCartData, cxinvoicenumber, cxorderreceived, tablereference;
    String uniqueId, username, tableId;
    ArrayList<String> ItemName = new ArrayList<>();
    ArrayList<String> ItemPrice = new ArrayList<>();
    ArrayList<String> ItemIds = new ArrayList<>();
    ArrayList<String> ItemTotal = new ArrayList<>();
    ArrayList<String> ItemQty = new ArrayList<>();
    TextView ordertotal, placeorder;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    int sum = 0, invoicenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cx_cart);
        uniqueId = getIntent().getStringExtra("uniqueId");
        username = getIntent().getStringExtra("username");
        tableId = getIntent().getStringExtra("tableId");
        ordertotal = findViewById(R.id.order_total);
        placeorder = findViewById(R.id.place_order);
        cxCartData = FirebaseDatabase.getInstance().getReference("CxCart").child(username).child(uniqueId);
        cxinvoicenumber = FirebaseDatabase.getInstance().getReference("SID").child(username).child("invoicenumber");
        cxorderreceived = FirebaseDatabase.getInstance().getReference("CxOrder").child(username);
        tablereference = FirebaseDatabase.getInstance().getReference("TableInfo").child(username).child(tableId);
        recyclerView = findViewById(R.id.activity_cx_cart_rv);
        layoutManager = new LinearLayoutManager(CxCartActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new CxCartActivityAdapter(ItemName, ItemPrice, ItemIds, username, uniqueId, ItemQty);
        recyclerView.setAdapter(adapter);

        cxCartData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ItemName.clear();
                ItemPrice.clear();
                ItemIds.clear();
                ItemQty.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()){
                    String itemid = itemSnapshot.getKey();
                    String name = itemSnapshot.child("itemname").getValue(String.class);
                    String price = itemSnapshot.child("itemprice").getValue(String.class);
                        ItemName.add(name);
                        ItemPrice.add(price);
                        ItemIds.add(itemid);
                        ItemQty.add("1");
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        cxCartData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ItemTotal.clear();
                sum = 0;
                for (DataSnapshot itemSnapshot : snapshot.getChildren()){
                    String total = itemSnapshot.child("itemtotal").getValue(String.class);
                    if (total != null) {
                        sum = sum + Integer.parseInt(total);
                        ItemTotal.add(total);
                    }
                }
                ordertotal.setText("Total " + String.valueOf(sum));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ItemName.isEmpty()) {
                    cxinvoicenumber.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            invoicenumber = Integer.parseInt(snapshot.getValue(String.class));
                            cxinvoicenumber.setValue(String.valueOf(invoicenumber + 1));
                            long timestamp = System.currentTimeMillis();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            String dateString = dateFormat.format(new Date(timestamp));
                            for (int i = 0; i < ItemName.size(); i++) {
                                cxorderreceived.child(String.valueOf(invoicenumber)).child(ItemIds.get(i)).child("itemname").setValue(ItemName.get(i));
                                cxorderreceived.child(String.valueOf(invoicenumber)).child(ItemIds.get(i)).child("itemprice").setValue(ItemPrice.get(i));
                                cxorderreceived.child(String.valueOf(invoicenumber)).child(ItemIds.get(i)).child("itemqty").setValue(ItemQty.get(i));
                                cxorderreceived.child(String.valueOf(invoicenumber)).child(ItemIds.get(i)).child("itemId").setValue(ItemIds.get(i));
                                cxorderreceived.child(String.valueOf(invoicenumber)).child(ItemIds.get(i)).child("itemtotal").setValue(ItemTotal.get(i));
                            }
                            cxorderreceived.child(String.valueOf(invoicenumber)).child("invoicedate").setValue(dateString);
                            tablereference.child("availibility").setValue("false");
                            tablereference.child("invoicenumber").setValue(String.valueOf(invoicenumber));
                            tablereference.child("tableid").setValue(tableId);
                            cxCartData.removeValue();
                            Intent intent = new Intent(CxCartActivity.this, OrderInvoiceActivity.class);
                            intent.putExtra("date",dateString);
                            intent.putExtra("invoicenumber", invoicenumber);
                            intent.putExtra("username", username);
                            intent.putExtra("itemnamelist", ItemName);
                            intent.putExtra("itempricelist", ItemTotal);
                            intent.putExtra("itemqtylist", ItemQty);
                            intent.putExtra("itemtotal", sum);
                            intent.putExtra("username", username);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    Toast.makeText(CxCartActivity.this, "The cart is empty, select items", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}