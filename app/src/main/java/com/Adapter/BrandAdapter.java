package com.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.Vo.brandVo;

import com.momskitchen.Product_Details;
import com.momskitchen.R;

import java.util.List;

/**
 * Created by saloni.bhansali on 4/21/2016.
 */
public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.ViewHolder> {
    Context context;
    List<brandVo> brandList;
    String title;
    int id;

    public BrandAdapter(List<brandVo> listingList, Context context, String title) {
        this.context = context;
        this.brandList = listingList;
        this.title = title;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_brand_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final brandVo productlist = brandList.get(position);

        holder.name.setText(productlist.name);
        holder.price.setText(productlist.price);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,Product_Details.class).putExtra("ID",productlist.id);
                Log.e("productlist",""+productlist.id);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return brandList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        ImageView img;


        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.txt_name);
            price = (TextView) itemView.findViewById(R.id.textView_price);



        }
    }

}
