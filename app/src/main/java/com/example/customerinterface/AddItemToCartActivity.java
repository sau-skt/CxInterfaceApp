package com.example.customerinterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.customerinterface.adapters.AddItemToCartActivityAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddItemToCartActivity extends AppCompatActivity {

    Button tableIdBtn, viewcartBtn;
    String tableId, username, uniqueid;
    DatabaseReference Cxcategorydatabasereference, Cxitemdatabasereference, cxCartData;
    ArrayList<String> Categorylist = new ArrayList<>();
    ArrayList<String> Itemslist = new ArrayList<>();
    ArrayList<String> ItemsCategoryList = new ArrayList<>();
    ArrayList<String> ItemsPriceList = new ArrayList<>();
    ArrayList<String> ItemsDescList = new ArrayList<>();
    ArrayList<String> ItemsTypeList = new ArrayList<>();
    ArrayList<String> ItemIdList = new ArrayList<>();
    ArrayList<String> ItemImageUrlList = new ArrayList<>();
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
        viewcartBtn = findViewById(R.id.AddItemToCartActivityViewCart);
        tableId = getIntent().getStringExtra("table");
        username = getIntent().getStringExtra("username");
        cxCartData = FirebaseDatabase.getInstance().getReference("CxCart").child(username);
        uniqueid = cxCartData.push().getKey();
        tableIdBtn.setText("Table - " + tableId);
        layoutManager = new LinearLayoutManager(AddItemToCartActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new AddItemToCartActivityAdapter(Item_Or_Category, Categorylist, Itemslist,ItemsPriceList, ItemsDescList, ItemsTypeList, ItemIdList, username, uniqueid, ItemsCategoryList, ItemImageUrlList);
        recyclerView.setAdapter(adapter);
        Cxcategorydatabasereference = FirebaseDatabase.getInstance().getReference("SIDCxMenu").child(username);
        Cxitemdatabasereference = FirebaseDatabase.getInstance().getReference("SIDCxMenu").child(username);

        Cxcategorydatabasereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Categorylist.clear();
                Itemslist.clear();
                ItemsCategoryList.clear();
                Item_Or_Category.clear();
                ItemImageUrlList.clear();
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String category = categorySnapshot.getKey();
                    Cxitemdatabasereference.child(category).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ItemsCategoryList.add(category);
                            Item_Or_Category.add("Category");
                            Categorylist.add(category);
                            Itemslist.add(category);
                            ItemsPriceList.add(category);
                            ItemsDescList.add(category);
                            ItemsTypeList.add(category);
                            ItemIdList.add(category);
                            ItemImageUrlList.add(category);
                            for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                                String itemId = itemSnapshot.getKey();
                                String itemName = itemSnapshot.child("itemname").getValue(String.class);
                                String itemcategoryname = itemSnapshot.child("itemcategory").getValue(String.class);
                                String itemPrice = itemSnapshot.child("itemprice").getValue(String.class);
                                String itemDesc = itemSnapshot.child("itemdescription").getValue(String.class);
                                String itemType = itemSnapshot.child("itemtype").getValue(String.class);
                                String itemStock = itemSnapshot.child("instock").getValue(String.class);
                                String itemimageu = itemSnapshot.child("itemimage").getValue(String.class);
                                if (itemStock.equals("true")){
                                    Itemslist.add(itemName);
                                    ItemsCategoryList.add(itemcategoryname);
                                    ItemsPriceList.add(itemPrice);
                                    ItemsDescList.add(itemDesc);
                                    ItemsTypeList.add(itemType);
                                    ItemIdList.add(itemId);
                                    Item_Or_Category.add("Item");
                                    Categorylist.add(itemName);
                                    ItemImageUrlList.add(itemimageu);
                                    Log.e("ASD", String.valueOf(itemimageu));
                                }
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

        viewcartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddItemToCartActivity.this,CxCartActivity.class);
                intent.putExtra("uniqueId", uniqueid);
                intent.putExtra("username", username);
                intent.putExtra("tableId", tableId);
                startActivity(intent);
            }
        });
    }
}