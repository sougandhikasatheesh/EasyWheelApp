package com.example.easywheel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicineResultAdapter extends RecyclerView.Adapter<MedicineResultAdapter.ViewHolder> {

    List<MedicineResult> medicineList;

    public MedicineResultAdapter(List<MedicineResult> medicineList) {
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicine_shop, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MedicineResult medicine = medicineList.get(position);

        holder.name.setText(medicine.getName());
        holder.shop.setText("Shop: " + medicine.getShop());
        holder.price.setText("Price: ₹" + medicine.getPrice());

        // ✅ Out of stock logic
        if (medicine.getQuantity() == 0) {
            holder.quantity.setText("Out of Stock");
        } else {
            holder.quantity.setText("Available: " + medicine.getQuantity());
        }
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, shop, price, quantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.medName);
            shop = itemView.findViewById(R.id.shopName);
            price = itemView.findViewById(R.id.medPrice);
            quantity = itemView.findViewById(R.id.medQuantity);
        }
    }
}