package com.ulan.az.usluga.Profile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ulan.az.usluga.R;
import com.ulan.az.usluga.order.OrderMoreInfoActivity;
import com.ulan.az.usluga.service.Service;
import com.ulan.az.usluga.service.ServiceMoreInfoActivity;

import java.util.ArrayList;


public class RVTenderServiceAdapter extends RecyclerView.Adapter<RVTenderServiceAdapter.PersonViewHolder> {
    Context context;
    Service vse;


    public class PersonViewHolder extends RecyclerView.ViewHolder {

       TextView name,phone,status;


        PersonViewHolder(final View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            phone = (TextView) itemView.findViewById(R.id.phone);
            status = (TextView) itemView.findViewById(R.id.status);

            itemView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                context.startActivity(new Intent(context,ServiceMoreInfoActivity.class).putExtra("service",listVse.get(getAdapterPosition())));

                }


            });

        }

    }

    ArrayList<Service> listVse;

    public RVTenderServiceAdapter(Context context, ArrayList<Service> listVse) {
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
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_tender_service, viewGroup, false);
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

        if (vse.getStatus()==1){
            personViewHolder.status.setBackgroundResource(R.drawable.seryy);
            personViewHolder.status.setText("В расмотрении");
        }else    if (vse.getStatus()==0){
            personViewHolder.status.setBackgroundResource(R.drawable.zelenyy);
            personViewHolder.status.setText("Принято");
        }else    if (vse.getStatus()==2){
            personViewHolder.status.setBackgroundResource(R.drawable.krasnyy);
            personViewHolder.status.setText("Отказано");
        }



        //personViewHolder.phone.setText(vse.getUser().getPhone());
    }

    @Override
    public int getItemCount() {
        return listVse.size();
    }
}