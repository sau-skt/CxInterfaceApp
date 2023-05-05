package com.example.customerinterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.example.customerinterface.adapters.AddItemToCartActivityAdapter;
import com.example.customerinterface.adapters.CxCartActivityAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CxCartActivity extends AppCompatActivity {

    DatabaseReference cxCartData, cxinvoicenumber, cxorderreceived;
    String uniqueId, username;
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
        ordertotal = findViewById(R.id.order_total);
        placeorder = findViewById(R.id.place_order);
        cxCartData = FirebaseDatabase.getInstance().getReference("CxCart").child(username).child(uniqueId);
        cxinvoicenumber = FirebaseDatabase.getInstance().getReference("SID").child(username).child("invoicenumber");
        cxorderreceived = FirebaseDatabase.getInstance().getReference("CxOrder").child(username);
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
                cxinvoicenumber.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        invoicenumber = Integer.parseInt(snapshot.getValue(String.class));
                        cxinvoicenumber.setValue(String.valueOf(invoicenumber + 1));
                        for (int i = 0; i < ItemName.size(); i++) {
                            cxorderreceived.child(String.valueOf(invoicenumber)).child(ItemIds.get(i)).child("itemname").setValue(ItemName.get(i));
                            cxorderreceived.child(String.valueOf(invoicenumber)).child(ItemIds.get(i)).child("itemprice").setValue(ItemPrice.get(i));
                            cxorderreceived.child(String.valueOf(invoicenumber)).child(ItemIds.get(i)).child("itemqty").setValue(ItemQty.get(i));
                            cxorderreceived.child(String.valueOf(invoicenumber)).child(ItemIds.get(i)).child("itemId").setValue(ItemIds.get(i));
                            cxorderreceived.child(String.valueOf(invoicenumber)).child(ItemIds.get(i)).child("itemtotal").setValue(ItemTotal.get(i));
                        }
                        cxCartData.removeValue();
                        Intent intent = new Intent(CxCartActivity.this,OrderStatusActivity.class);
                        intent.putExtra("invoicenumber", invoicenumber);
                        intent.putExtra("username", username);
                        intent.putExtra("itemnamelist",ItemName);
                        intent.putExtra("itempricelist",ItemTotal);
                        intent.putExtra("itemqtylist",ItemQty);
                        intent.putExtra("itemtotal",sum);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}