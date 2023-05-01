package com.example.customerinterface;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddItemToCartActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int CATEGORY_VIEW_TYPE = 0;
    private static final int ITEM_VIEW_TYPE = 1;

    private ArrayList<String> item_or_category, CategoryList, ItemsList, ItemsPrice, ItemsDesc, ItemsType, ItemsIdList;
    String username;
    ArrayList<String> ItemsIdAddedToCart = new ArrayList<>();

    public AddItemToCartActivityAdapter(ArrayList<String> item_or_category, ArrayList<String> CategoryList, ArrayList<String> ItemsList, ArrayList<String> ItemsPrice, ArrayList<String> ItemsDesc, ArrayList<String> ItemsType, ArrayList<String> ItemIdList, String username) {
        this.item_or_category = item_or_category;
        this.CategoryList = CategoryList;
        this.ItemsList = ItemsList;
        this.ItemsPrice = ItemsPrice;
        this.ItemsDesc = ItemsDesc;
        this.ItemsType = ItemsType;
        this.ItemsIdList = ItemIdList;
        this.username = username;
    }

    @Override
    public int getItemViewType(int position) {
        if (item_or_category.get(position).equals("Category")) {
            return CATEGORY_VIEW_TYPE;
        } else {
            return ITEM_VIEW_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case CATEGORY_VIEW_TYPE:
                View categoryView = inflater.inflate(R.layout.cardview_category_name, parent, false);
                return new CategoryViewHolder(categoryView);
            case ITEM_VIEW_TYPE:
                View itemView = inflater.inflate(R.layout.cardview_item_display, parent, false);
                return new ItemViewHolder(itemView);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            // bind data for item view holder
            ((ItemViewHolder) holder).ItemName.setText(ItemsList.get(position));
            ((ItemViewHolder) holder).ItemPrice.setText("\u20B9 " + ItemsPrice.get(position));
            ((ItemViewHolder) holder).ItemDesc.setText(ItemsDesc.get(position));
            ((ItemViewHolder) holder).cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            Log.e("QWERTY", String.valueOf(ItemsIdList));
            if (ItemsType.get(position).equals("Veg")) {
                ((ItemViewHolder) holder).ItemType.setImageResource(R.drawable.vegetarian_food);
            } else {
                ((ItemViewHolder) holder).ItemType.setImageResource(R.drawable.nonvegetarian_food);
            }
            ((ItemViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((ItemViewHolder) holder).cardView.getCardBackgroundColor().getDefaultColor() == Color.parseColor("#FFFFFF")) {
                        ((ItemViewHolder) holder).cardView.setCardBackgroundColor(Color.parseColor("#FFFF00"));
                        ItemsIdAddedToCart.add(ItemsIdList.get(position));
                    } else {
                        ((ItemViewHolder) holder).cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                        ItemsIdAddedToCart.remove(ItemsIdList.get(position));
                    }
                    Log.e("QWERTY", String.valueOf(ItemsIdAddedToCart));
                }
            });
        } else if (holder instanceof CategoryViewHolder) {
            // bind data for category view holder
            ((CategoryViewHolder) holder).CategoryName.setText(CategoryList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return item_or_category.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView ItemName, ItemDesc, ItemPrice;
        ImageView ItemType;
        CardView cardView;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            // initialize views for item view holder
            ItemName = itemView.findViewById(R.id.itemname);
            ItemDesc = itemView.findViewById(R.id.itemdesc);
            ItemPrice = itemView.findViewById(R.id.itemprice);
            ItemType = itemView.findViewById(R.id.itemtype);
            cardView = itemView.findViewById(R.id.cardviewtouch);
        }
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView CategoryName;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // initialize views for category view holder
            CategoryName = itemView.findViewById(R.id.category_name);
        }
    }
}
