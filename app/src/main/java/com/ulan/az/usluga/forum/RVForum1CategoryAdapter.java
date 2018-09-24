package com.ulan.az.usluga.forum;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ulan.az.usluga.R;

import java.util.ArrayList;


public class RVForum1CategoryAdapter extends RecyclerView.Adapter<RVForum1CategoryAdapter.PersonViewHolder> {
    Context context;
    Forum vse;
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
                 /*   if (tag==1) {
                        Intent intent = new Intent(context, ForumSubForumActivity.class);
                        intent.putExtra("category", (Serializable) listVse.get(getAdapterPosition()));
                        intent.putExtra("tag",tag);
                        context.startActivity(intent);
                    }

                    if (tag==2) {
                        Intent intent = new Intent(context, ForumActivity.class);
                        intent.putExtra("category", listVse.get(getAdapterPosition()).getForum());
                        Shared.category_id = listVse.get(getAdapterPosition()).getId();
                        context.startActivity(intent);
                    }*/
                    Intent intent = new Intent(context, ForumInfoActivity.class);
                    intent.putExtra("forum", listVse.get(getAdapterPosition()));
                    context.startActivity(intent);
                }


            });

        }

    }

    ArrayList<Forum> listVse;

    public RVForum1CategoryAdapter(Context context, ArrayList<Forum> listVse) {
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
        personViewHolder.category.setText(vse.getTitle());
    }


    @Override
    public int getItemCount() {
        return listVse.size();
    }
}