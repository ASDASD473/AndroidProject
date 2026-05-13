package com.example.myapplication66;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactions;
    private OnTransactionClickListener listener;
    private Map<Integer, Boolean> noteVisibilityMap;

    public interface OnTransactionClickListener {
        void onDeleteClick(int position);

        void onItemClick(int position);
    }

    public TransactionAdapter() {
        this.transactions = new ArrayList<>();
        this.noteVisibilityMap = new HashMap<>();
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions != null ? transactions : new ArrayList<Transaction>();
        notifyDataSetChanged();
    }

    public void setOnTransactionClickListener(OnTransactionClickListener listener) {
        this.listener = listener;
    }

    public void clear() {
        transactions.clear();
        noteVisibilityMap.clear();
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return transactions == null || transactions.isEmpty();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        boolean isNoteVisible = noteVisibilityMap.containsKey(position) ? noteVisibilityMap.get(position) : false;
        holder.bind(transaction, isNoteVisible);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void toggleNoteVisibility(int position) {
        boolean currentVisibility = noteVisibilityMap.containsKey(position) ? noteVisibilityMap.get(position) : false;
        noteVisibilityMap.put(position, !currentVisibility);
        notifyItemChanged(position);
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private TextView textAmount;
        private TextView textCategory;
        private TextView textDate;
        private TextView textNote;
        private ImageButton btnDelete;
        private View cardView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            textAmount = itemView.findViewById(R.id.text_amount);
            textCategory = itemView.findViewById(R.id.text_category);
            textDate = itemView.findViewById(R.id.text_date);
            textNote = itemView.findViewById(R.id.text_note);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            cardView = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(getAdapterPosition());
                    }
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onDeleteClick(getAdapterPosition());
                    }
                }
            });
        }

        public void bind(Transaction transaction, boolean isNoteVisible) {
            textAmount.setText(String.format("¥%.2f", transaction.getAmount()));
            textCategory.setText(transaction.getCategory());
            textDate.setText(transaction.getDate());

            String note = transaction.getNote();
            if (note != null && !note.isEmpty()) {
                textNote.setText("备注：" + note);
                textNote.setVisibility(isNoteVisible ? View.VISIBLE : View.GONE);
            } else {
                textNote.setVisibility(View.GONE);
            }
        }
    }
}
