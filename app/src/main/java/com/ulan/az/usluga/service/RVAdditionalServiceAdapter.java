package com.ulan.az.usluga.service;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.helpers.DataHelper;

import java.util.ArrayList;


public class RVAdditionalServiceAdapter extends RecyclerView.Adapter<RVAdditionalServiceAdapter.PersonViewHolder> {
    Context context;
    Service vse;


    public class PersonViewHolder extends RecyclerView.ViewHolder {

       TextView name,phone,experience;
       ImageView imageView;
       DataHelper dataHelper;


        PersonViewHolder(final View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            phone = (TextView) itemView.findViewById(R.id.phone);
            experience = (TextView) itemView.findViewById(R.id.experience);
            imageView = (ImageView) itemView.findViewById(R.id.delete);
            dataHelper = new DataHelper(context);



            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context,ServiceMoreInfoActivity.class).putExtra("service",listVse.get(getAdapterPosition())));

                }
            });


            itemView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                context.startActivity(new Intent(context,ServiceMoreInfoActivity.class).putExtra("service",listVse.get(getAdapterPosition())));

                }


            });


        }


    }

    ArrayList<Service> listVse;

    public RVAdditionalServiceAdapter(Context context, ArrayList<Service> listVse) {
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
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_additional_service, viewGroup, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        vse = listVse.get(i);
        personViewHolder.name.setText(vse.getCategory()+" -> "+vse.getDescription());
        personViewHolder.experience.setText(vse.getExperience()+"");
        personViewHolder.phone.setText(vse.getUser().getPhone());


    }


    @Override
    public int getItemCount() {
        return listVse.size();
    }
}