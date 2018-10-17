package com.ulan.az.usluga.Profile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.helpers.DataHelper;
import com.ulan.az.usluga.service.Service;
import com.ulan.az.usluga.service.ServiceMoreInfoActivity;

import java.util.ArrayList;


public class RVMyServiceAdapter extends RecyclerView.Adapter<RVMyServiceAdapter.PersonViewHolder> {
    Context context;
    Service vse;


    public class PersonViewHolder extends RecyclerView.ViewHolder {

       TextView name,phone,experience;
       ImageView imageView,redactor;
       DataHelper dataHelper;



        PersonViewHolder(final View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            phone = (TextView) itemView.findViewById(R.id.phone);
            experience = (TextView) itemView.findViewById(R.id.experience);
            imageView = (ImageView) itemView.findViewById(R.id.delete);
            redactor = (ImageView) itemView.findViewById(R.id.redactor);
            dataHelper = new DataHelper(context);

            redactor.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,RedactorServiceActivity.class);
                    intent.putExtra("service",listVse.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });

            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    ClientApi.requestDeleteService(listVse.get(getAdapterPosition()).getId(),context);
                    listVse.remove(getAdapterPosition());
                    notifyDataSetChanged();
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

    public RVMyServiceAdapter(Context context, ArrayList<Service> listVse) {
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
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_my_service, viewGroup, false);
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
        personViewHolder.experience.setText(vse.getExperience()+"");
        personViewHolder.phone.setText(vse.getUser().getPhone());


    }


    @Override
    public int getItemCount() {
        return listVse.size();
    }
}