package com.ulan.az.usluga.forum;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ulan.az.usluga.R;
import com.ulan.az.usluga.helpers.E;

import java.util.ArrayList;


public class RVCommentAdapter extends RecyclerView.Adapter<RVCommentAdapter.PersonViewHolder> {
    Context context;
    Comment vse;
    int tag;
    String cat;

    public class PersonViewHolder extends RecyclerView.ViewHolder {

        TextView comment,user,comment1,user1;
        LinearLayout each,own;


        PersonViewHolder(final View itemView) {
            super(itemView);

            user = (TextView) itemView.findViewById(R.id.user);
            comment = (TextView) itemView.findViewById(R.id.comment);

            user1 = (TextView) itemView.findViewById(R.id.user1);
            comment1 = (TextView) itemView.findViewById(R.id.comment1);
            each = itemView.findViewById(R.id.each);
            own = itemView.findViewById(R.id.own);




        }

    }

    ArrayList<Comment> listVse;

    public RVCommentAdapter(Context context, ArrayList<Comment> listVse) {
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
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comment, viewGroup, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        vse = listVse.get(i);
        if (vse.getUser().getId()== E.getAppPreferencesINT(E.APP_PREFERENCES_ID,context)){
            personViewHolder.each.setVisibility(View.GONE);
            personViewHolder.own.setVisibility(View.VISIBLE);
            personViewHolder.user1.setText(vse.getUser().getName());
            personViewHolder.comment1.setText(vse.getComment());
        }else {
            personViewHolder.each.setVisibility(View.VISIBLE);
            personViewHolder.own.setVisibility(View.GONE);
            personViewHolder.user.setText(vse.getUser().getName());
            personViewHolder.comment.setText(vse.getComment());
        }


    }


    @Override
    public int getItemCount() {
        return listVse.size();
    }
}