package com.example.easywheel;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    private Context context;
    private List<Medicine> medicineList;

    public MedicineAdapter(Context context, List<Medicine> medicineList) {
        this.context = context;
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( context)
                .inflate(R.layout.medicine_item, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {

        Medicine medicine = medicineList.get(position);

        holder.tvName.setText(medicine.getName());
        holder.tvQuantity.setText("Quantity: " + medicine.getQuantity());

        holder.btnUpdateStock.setOnClickListener(v -> {
            showUpdateDialog(medicine, position);
        });
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    // ================= VIEW HOLDER =================

    public class MedicineViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvQuantity;
        Button btnUpdateStock;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvMedicineName);
            tvQuantity = itemView.findViewById(R.id.tvMedicineQuantity);
            btnUpdateStock = itemView.findViewById(R.id.btnUpdateStock);
        }
    }

    // ================= UPDATE DIALOG =================

    private void showUpdateDialog(Medicine medicine, int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context)
                .inflate(R.layout.update_medicine_dialog, null);

        builder.setView(view);

        EditText editQuantity = view.findViewById(R.id.editQuantity);
        EditText editPrice = view.findViewById(R.id.editPrice);
        Button btnSave = view.findViewById(R.id.btnSave);

        editQuantity.setText(String.valueOf(medicine.getQuantity()));
        editPrice.setText(medicine.getPrice());

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {

            String quantityText = editQuantity.getText().toString().trim();
            String priceText = editPrice.getText().toString().trim();

            if (!quantityText.isEmpty() && !priceText.isEmpty()) {

                int newQuantity = Integer.parseInt(quantityText);

                medicine.setQuantity(newQuantity);
                medicine.setPrice(priceText);

                notifyItemChanged(position);

                dialog.dismiss();
            }
        });
    }
}