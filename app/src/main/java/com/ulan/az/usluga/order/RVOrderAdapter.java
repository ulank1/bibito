package com.ulan.az.usluga.order;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ulan.az.usluga.R;
import com.ulan.az.usluga.service.Service;

import java.util.ArrayList;


public class RVOrderAdapter extends RecyclerView.Adapter<RVOrderAdapter.PersonViewHolder> {
    Context context;
    Service vse;


    public class PersonViewHolder extends RecyclerView.ViewHolder {

       TextView name,phone;


        PersonViewHolder(final View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            phone = (TextView) itemView.findViewById(R.id.phone);

            itemView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                context.startActivity(new Intent(context,OrderMoreInfoActivity.class).putExtra("service",listVse.get(getAdapterPosition())));

                }


            });

        }

    }

    ArrayList<Service> listVse;

    public RVOrderAdapter(Context context, ArrayList<Service> listVse) {
        this.listVse = listVse;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = null;
        try {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_order, viewGroup, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        vse = listVse.get(i);
        personViewHolder.name.setText(vse.getDescription());
        //personViewHolder.phone.setText(vse.getUser().getPhone());
    }

    @Override
    public int getItemCount() {
        return listVse.size();
    }
}