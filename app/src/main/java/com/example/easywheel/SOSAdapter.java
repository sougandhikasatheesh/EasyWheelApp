package com.example.easywheel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SOSAdapter extends RecyclerView.Adapter<SOSAdapter.ViewHolder> {

    private List<SOSModel> list;

    public SOSAdapter(List<SOSModel> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;

        public ViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SOSModel model = list.get(position);

        holder.tvMessage.setText(model.message);
        holder.tvTime.setText(String.valueOf(model.timestamp));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}