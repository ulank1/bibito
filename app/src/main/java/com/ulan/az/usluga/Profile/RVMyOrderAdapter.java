package com.ulan.az.usluga.Profile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.User;
import com.ulan.az.usluga.helpers.DataHelper;
import com.ulan.az.usluga.order.OrderMoreInfoActivity;
import com.ulan.az.usluga.service.Service;

import java.util.ArrayList;


public class RVMyOrderAdapter extends RecyclerView.Adapter<RVMyOrderAdapter.PersonViewHolder> {
    Context context;
    Service vse;


    public class PersonViewHolder extends RecyclerView.ViewHolder {

       TextView category;
       ImageView imageView,redactor;
       DataHelper dataHelper;
       RecyclerView mRecyclerView;


        PersonViewHolder(final View itemView) {
            super(itemView);


            imageView = (ImageView) itemView.findViewById(R.id.delete);
            category = (TextView) itemView.findViewById(R.id.category);

            dataHelper = new DataHelper(context);

            redactor = (ImageView) itemView.findViewById(R.id.redactor);

            redactor.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,RedactorOrderActivity.class);
                    intent.putExtra("service",listVse.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });


            category.setVisibility(View.VISIBLE);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete(getAdapterPosition(),listVse.get(getAdapterPosition()).getId(),context,RVMyOrderAdapter.this);
                }
            });
            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.rv);

            LinearLayoutManager llm = new LinearLayoutManager(context);
            mRecyclerView.setLayoutManager(llm);
            mRecyclerView.setHasFixedSize(true);

            itemView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    context.startActivity(new Intent(context,OrderMoreInfoActivity.class).putExtra("service",listVse.get(getAdapterPosition())));

                }


            });


        }


    }

    public static ArrayList<Service> listVse;
    ArrayList<ArrayList<User>> users;

    public RVMyOrderAdapter(Context context, ArrayList<Service> listVse,ArrayList<ArrayList<User>> users) {
        this.listVse = listVse;
        this.context = context;
        this.users = users;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = null;
        try {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_my_order, viewGroup, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        vse = listVse.get(i);
        personViewHolder.category.setText(vse.getCategory());
        RVMyOrderSecondAdapter adapter = new RVMyOrderSecondAdapter(context,users.get(i),vse.getId(),i,this);
        personViewHolder.mRecyclerView.setAdapter(adapter);


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

    public static void delete(int position, int id, Context context, RVMyOrderAdapter rvMyOrderAdapter){
        ClientApi.requestDelete(id,context);
        DataHelper dataHelper = new DataHelper(context);
        dataHelper.delete(id);
        listVse.remove(position);
        rvMyOrderAdapter.notifyDataSetChanged();
    }

}