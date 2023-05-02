package com.example.customerinterface.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customerinterface.R;

import java.util.ArrayList;

public class CxCartActivityAdapter extends RecyclerView.Adapter<CxCartActivityAdapter.MyViewHolder> {

    ArrayList<String> ItemName, ItemPrice;

    public CxCartActivityAdapter(ArrayList<String> itemName, ArrayList<String> itemPrice) {
        ItemName = itemName;
        ItemPrice = itemPrice;
    }

    @NonNull
    @Override
    public CxCartActivityAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item_display_cart,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CxCartActivityAdapter.MyViewHolder holder, int position) {
        holder.itemname.setText(ItemName.get(position));
        holder.itemprice.setText(ItemPrice.get(position));
        holder.itemqty.setText("1");
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.itemqty.setText(String.valueOf(Integer.parseInt(holder.itemqty.getText().toString()) + 1));
            }
        });
        holder.substract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.itemqty.setText(String.valueOf(Integer.parseInt(holder.itemqty.getText().toString()) - 1));
            }
        });
    }

    @Override
    public int getItemCount() {
        return ItemName.size();
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
