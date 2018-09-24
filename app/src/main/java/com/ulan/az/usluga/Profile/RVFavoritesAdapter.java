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
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.helpers.DataHelper;
import com.ulan.az.usluga.service.Service;
import com.ulan.az.usluga.service.ServiceMoreInfoActivity;

import java.util.ArrayList;


public class RVFavoritesAdapter extends RecyclerView.Adapter<RVFavoritesAdapter.PersonViewHolder> {
    Context context;
    Service vse;


    public class PersonViewHolder extends RecyclerView.ViewHolder {

       TextView name,phone,experience,category;
       ImageView imageView;
       DataHelper dataHelper;


        PersonViewHolder(final View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            phone = (TextView) itemView.findViewById(R.id.phone);
            experience = (TextView) itemView.findViewById(R.id.experience);
            imageView = (ImageView) itemView.findViewById(R.id.favorite);
            category = (TextView) itemView.findViewById(R.id.category);
            imageView.setVisibility(View.GONE);
            dataHelper = new DataHelper(context);
            category.setVisibility(View.VISIBLE);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Service vse = listVse.get(getAdapterPosition());
                    if (dataHelper.isFavorite(listVse.get(getAdapterPosition()).getId())){
                        dataHelper.delete(listVse.get(getAdapterPosition()).getId());
                        Glide.with(context).load(R.drawable.ic_action_star_none_active).into(imageView);
                    }else {
                        dataHelper.addService(listVse.get(getAdapterPosition()));
                        Glide.with(context).load(R.drawable.ic_action_star_active).into(imageView);
                    }
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

    public RVFavoritesAdapter(Context context, ArrayList<Service> listVse) {
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
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_service, viewGroup, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        vse = listVse.get(i);
        personViewHolder.name.setText(vse.getUser().getName());
        personViewHolder.category.setText(vse.getCategory());
        personViewHolder.experience.setText(vse.getExperience()+"");
        personViewHolder.phone.setText(vse.getUser().getPhone());

       /* if (vse.is_favorite){
            Glide.with(context).load(R.drawable.ic_action_star_active).into(personViewHolder.imageView);
        }else {
            Glide.with(context).load(R.drawable.ic_action_star_none_active).into(personViewHolder.imageView);
        }*/

    }


    @Override
    public int getItemCount() {
        return listVse.size();
    }
}