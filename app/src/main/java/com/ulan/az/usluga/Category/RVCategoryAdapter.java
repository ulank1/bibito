package com.ulan.az.usluga.Category;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ulan.az.usluga.R;
import com.ulan.az.usluga.SubCategory.SubCategoryActivity;
import com.ulan.az.usluga.helpers.Shared;
import com.ulan.az.usluga.order.OrderActivity;
import com.ulan.az.usluga.service.ServiceActivity;

import java.io.Serializable;
import java.util.ArrayList;


public class RVCategoryAdapter extends RecyclerView.Adapter<RVCategoryAdapter.PersonViewHolder> {
    Context context;
    Category vse;
    int tag;
    String cat;

    public class PersonViewHolder extends RecyclerView.ViewHolder {

        TextView category;


        PersonViewHolder(final View itemView) {
            super(itemView);

            category = (TextView) itemView.findViewById(R.id.category);



            itemView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (tag==1||tag==2) {
                        Intent intent = new Intent(context, SubCategoryActivity.class);
                        intent.putExtra("category", (Serializable) listVse.get(getAdapterPosition()));
                        intent.putExtra("tag",tag);
                        context.startActivity(intent);
                    }

                    if (tag==3) {
                        Intent intent = new Intent(context, ServiceActivity.class);
                        intent.putExtra("category", listVse.get(getAdapterPosition()).getCategory());
                        Shared.category_id = listVse.get(getAdapterPosition()).getId();
                        context.startActivity(intent);
                    }
                    if (tag==4) {
                        Intent intent = new Intent(context, OrderActivity.class);
                        intent.putExtra("category", listVse.get(getAdapterPosition()).getCategory());
                        Shared.category_id = listVse.get(getAdapterPosition()).getId();
                        context.startActivity(intent);
                    }

                }


            });


        }


    }

    ArrayList<Category> listVse;

    public RVCategoryAdapter(Context context, ArrayList<Category> listVse, int tag) {
        this.listVse = listVse;
        this.context = context;
        this.tag = tag;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = null;
        try {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_category, viewGroup, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        vse = listVse.get(i);
        //Log.e("vse",vse.getCategory());
        personViewHolder.category.setText(vse.getCategory());
    }


    @Override
    public int getItemCount() {
        return listVse.size();
    }
}