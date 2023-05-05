package com.example.customerinterface.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customerinterface.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class CxCartActivityAdapter extends RecyclerView.Adapter<CxCartActivityAdapter.MyViewHolder> {

    ArrayList<String> itemName, itemPrice, itemIds, itemQty;
    DatabaseReference cxCartData;
    String username, uniqueId;

    public CxCartActivityAdapter(ArrayList<String> itemName, ArrayList<String> itemPrice, ArrayList<String> itemIds, String username, String uniqueId, ArrayList<String> itemQty) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemQty = itemQty;
        this.username = username;
        this.uniqueId = uniqueId;
        this.cxCartData = FirebaseDatabase.getInstance().getReference("CxCart").child(username).child(uniqueId);
        this.itemIds = itemIds;
    }

    @NonNull
    @Override
    public CxCartActivityAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item_display_cart,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CxCartActivityAdapter.MyViewHolder holder, int position) {
        holder.itemname.setText(itemName.get(position));
        holder.itemprice.setText(itemPrice.get(position));
        holder.itemqty.setText("1");
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.itemqty.setText(String.valueOf(Integer.parseInt(holder.itemqty.getText().toString()) + 1));
                itemQty.set(position,String.valueOf(holder.itemqty.getText()));
                cxCartData.child(itemIds.get(position)).child("itemqty").setValue(String.valueOf(holder.itemqty.getText()));
                cxCartData.child(itemIds.get(position)).child("itemtotal").setValue(String.valueOf(Integer.parseInt(holder.itemqty.getText().toString()) * Integer.parseInt(itemPrice.get(position))));
            }
        });
        holder.substract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(holder.itemqty.getText().toString()) > 1) {
                    holder.itemqty.setText(String.valueOf(Integer.parseInt(holder.itemqty.getText().toString()) - 1));
                    itemQty.set(position,String.valueOf(holder.itemqty.getText()));
                    cxCartData.child(itemIds.get(position)).child("itemqty").setValue(String.valueOf(holder.itemqty.getText()));
                    cxCartData.child(itemIds.get(position)).child("itemtotal").setValue(String.valueOf(Integer.parseInt(holder.itemqty.getText().toString()) * Integer.parseInt(itemPrice.get(position))));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemName.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView itemname, itemprice, itemqty;
        Button add, substract;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemname = itemView.findViewById(R.id.item_name);
            itemprice = itemView.findViewById(R.id.item_price);
            itemqty = itemView.findViewById(R.id.item_qty_display);
            add = itemView.findViewById(R.id.item_qty_add);
            substract = itemView.findViewById(R.id.item_qty_minus);
        }
    }
}
