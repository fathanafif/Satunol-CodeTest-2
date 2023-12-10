package com.satunol.satunolcodetest2.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.satunol.satunolcodetest2.EventListActivity;
import com.satunol.satunolcodetest2.Model.Events;
import com.satunol.satunolcodetest2.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    ArrayList<Events> list;
    Context context;


    public EventAdapter(Context context, ArrayList<Events> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Events events = list.get(position);
        holder.textTitle.setText(events.getTitle());
        holder.textDescription.setText(events.getDescription());
        holder.textDate.setText(events.getDate());
        holder.textStartEnd.setText(events.getStart() + " - " + events.getEnd());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDescription, textDate, textStartEnd;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.title_tv);
            textDescription = itemView.findViewById(R.id.description_tv);
            textDate = itemView.findViewById(R.id.date_tv);
            textStartEnd = itemView.findViewById(R.id.start_end_tv);
        }

    }
}

