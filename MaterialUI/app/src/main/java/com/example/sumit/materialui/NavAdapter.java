package com.example.sumit.materialui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by sumit on 07/05/2015.
 */
public class NavAdapter extends RecyclerView.Adapter<NavAdapter.MyViewHolder> {

    private LayoutInflater layoutInflater;
    Context context;
    List<information> data = Collections.emptyList();
    public NavAdapter(Context context, List<information> dataSet){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.data = dataSet;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.custom_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final information currentItem = data.get(position);
        holder.title.setText(currentItem.title);
        holder.icon.setImageResource(currentItem.iconId);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        ImageView icon;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView)itemView.findViewById(R.id.listText);
            icon = (ImageView)itemView.findViewById(R.id.listIcon);
        }

        @Override
        public void onClick(View view) {
            context.startActivity(new Intent(context, ActivityUsingTabLibrary.class));
        }
    }
}
