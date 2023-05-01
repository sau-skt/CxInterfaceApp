package com.example.customerinterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddItemToCartActivity extends AppCompatActivity {

    Button tableIdBtn;
    String tableId, username;
    DatabaseReference Cxcategorydatabasereference, Cxitemdatabasereference;
    ArrayList<String> Categorylist = new ArrayList<>();
    ArrayList<String> Itemslist = new ArrayList<>();
    ArrayList<String> ItemsCategoryList = new ArrayList<>();
    ArrayList<String> ItemsPriceList = new ArrayList<>();
    ArrayList<String> ItemsDescList = new ArrayList<>();
    ArrayList<String> ItemsTypeList = new ArrayList<>();
    ArrayList<String> ItemIdList = new ArrayList<>();
    ArrayList<String> Item_Or_Category = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_to_cart);
        tableIdBtn = findViewById(R.id.AddItemToCartActivityTableId);
        recyclerView = findViewById(R.id.AddItemToCartActivityRv);
        tableId = getIntent().getStringExtra("table");
        username = getIntent().getStringExtra("username");
        tableIdBtn.setText("Table - " + tableId);
        layoutManager = new LinearLayoutManager(AddItemToCartActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new AddItemToCartActivityAdapter(Item_Or_Category, Categorylist, Itemslist,ItemsPriceList, ItemsDescList, ItemsTypeList, ItemIdList, username);
        recyclerView.setAdapter(adapter);
        Cxcategorydatabasereference = FirebaseDatabase.getInstance().getReference("SIDCxMenu").child(username);
        Cxitemdatabasereference = FirebaseDatabase.getInstance().getReference("SIDCxMenu").child(username);

        Cxcategorydatabasereference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Categorylist.clear();
                Itemslist.clear();
                ItemsCategoryList.clear();
                Item_Or_Category.clear();
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String category = categorySnapshot.getKey();
                    ItemsCategoryList.add(category);
                    Cxitemdatabasereference.child(category).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Item_Or_Category.add("Category");
                            Categorylist.add(category);
                            Itemslist.add(category);
                            ItemsPriceList.add(category);
                            ItemsDescList.add(category);
                            ItemsTypeList.add(category);
                            ItemIdList.add(category);
                            for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                                String itemId = itemSnapshot.getKey();
                                String itemName = itemSnapshot.child("itemname").getValue(String.class);
                                String itemcategoryname = itemSnapshot.child("itemcategory").getValue(String.class);
                                String itemPrice = itemSnapshot.child("itemprice").getValue(String.class);
                                String itemDesc = itemSnapshot.child("itemdescription").getValue(String.class);
                                String itemType = itemSnapshot.child("itemtype").getValue(String.class);
                                Itemslist.add(itemName);
                                ItemsCategoryList.add(itemcategoryname);
                                ItemsPriceList.add(itemPrice);
                                ItemsDescList.add(itemDesc);
                                ItemsTypeList.add(itemType);
                                ItemIdList.add(itemId);
                                Item_Or_Category.add("Item");
                                Categorylist.add(itemName);
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseDemo", "onCancelled", databaseError.toException());
            }
        });
    }
}