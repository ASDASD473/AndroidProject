package com.example.myapplication66;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

/**
 * MainActivity 使用示例
 * <p>
 * 展示如何在 Activity 中使用 ExpenseViewModel
 * </p>
 */
public class MainActivity_Example extends AppCompatActivity {

    private TextView textTotalAmount;
    private Button btnAddExpense;
    private Button btnClearAll;

    private ExpenseViewModel expenseViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initViewModel();
        setupClickListeners();
    }

    private void initViews() {
        textTotalAmount = findViewById(R.id.text_total_amount);
        btnAddExpense = findViewById(R.id.btn_add_expense);
    }

    private void initViewModel() {
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        expenseViewModel.getTotalAmountLiveData().observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double totalAmount) {
                updateTotalAmountDisplay(totalAmount);
            }
        });

        expenseViewModel.getTransactionsLiveData().observe(this, new Observer<List<Transaction>>() {
            @Override
            public void onChanged(List<Transaction> transactions) {
                updateTransactionListDisplay(transactions);
            }
        });
    }

    private void setupClickListeners() {
        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddExpenseClicked();
            }
        });
    }

    private void onAddExpenseClicked() {
        Transaction transaction = new Transaction(
                50.0,
                "餐饮",
                "午餐",
                "2026-03-26");
        expenseViewModel.addTransaction(transaction);
        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
    }

    private void updateTotalAmountDisplay(double amount) {
        String amountText = String.format("¥%.2f", amount);
        textTotalAmount.setText(amountText);
    }

    private void updateTransactionListDisplay(List<Transaction> transactions) {
    }
}
