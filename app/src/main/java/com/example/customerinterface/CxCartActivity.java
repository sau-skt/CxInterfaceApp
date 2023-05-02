package com.example.customerinterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;

import com.example.customerinterface.adapters.AddItemToCartActivityAdapter;
import com.example.customerinterface.adapters.CxCartActivityAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CxCartActivity extends AppCompatActivity {

    DatabaseReference cxCartData;
    String uniqueId, username;
    ArrayList<String> ItemName = new ArrayList<>();
    ArrayList<String> ItemPrice = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cx_cart);
        uniqueId = getIntent().getStringExtra("uniqueId");
        username = getIntent().getStringExtra("username");
        cxCartData = FirebaseDatabase.getInstance().getReference("CxCart").child(username).child(uniqueId);
        recyclerView = findViewById(R.id.activity_cx_cart_rv);
        layoutManager = new LinearLayoutManager(CxCartActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new CxCartActivityAdapter(ItemName, ItemPrice);
        recyclerView.setAdapter(adapter);

        cxCartData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ItemName.clear();
                ItemPrice.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()){
                    String name = itemSnapshot.child("itemname").getValue(String.class);
                    String price = itemSnapshot.child("itemprice").getValue(String.class);
                    ItemName.add(name);
                    ItemPrice.add(price);
                }
                Log.e("WER", String.valueOf(ItemName));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}