package com.example.myapplication66;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "expense_channel";
    private static final int NOTIFICATION_ID = 1001;
    private static final int PERMISSION_REQUEST_CODE = 2001;

    private TextView textTotalAmount;
    private Button btnAddExpense;
    private View layoutEmptyState;
    private RecyclerView recyclerViewExpenses;

    private Button btnSaveData;
    private Button btnReadData;
    private TextView tvStorageStatus;

    private Button btnStartService;
    private Button btnStopService;
    private TextView tvServiceStatus;

    private ExpenseViewModel expenseViewModel;
    private TransactionAdapter adapter;

    private RecordDbHelper dbHelper;

    private boolean isServiceRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new RecordDbHelper(this);

        createNotificationChannel();
        requestNotificationPermission();

        initViews();
        initViewModel();
        setupClickListeners();

        loadDataFromDatabase();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "记账提醒";
            String description = "记账添加成功通知";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "通知权限已开启", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "通知权限未开启，将无法收到提醒", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initViews() {
        textTotalAmount = findViewById(R.id.text_total_amount);
        btnAddExpense = findViewById(R.id.btn_add_expense);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        recyclerViewExpenses = findViewById(R.id.recycler_view_expenses);

        btnSaveData = findViewById(R.id.btn_save_data);
        btnReadData = findViewById(R.id.btn_read_data);
        tvStorageStatus = findViewById(R.id.tv_storage_status);

        btnStartService = findViewById(R.id.btn_start_service);
        btnStopService = findViewById(R.id.btn_stop_service);
        tvServiceStatus = findViewById(R.id.tv_service_status);

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        adapter = new TransactionAdapter();
        recyclerViewExpenses.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewExpenses.setAdapter(adapter);

        adapter.setOnTransactionClickListener(new TransactionAdapter.OnTransactionClickListener() {
            @Override
            public void onDeleteClick(int position) {
                deleteTransaction(position);
            }

            @Override
            public void onItemClick(int position) {
                toggleNoteVisibility(position);
            }
        });
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
                updateEmptyState(transactions);
                adapter.setTransactions(transactions);
            }
        });
    }

    private void setupClickListeners() {
        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddExpenseDialog();
            }
        });

        btnSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToDatabase();
            }
        });

        btnReadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDataFromDatabase();
            }
        });

        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBackgroundService();
            }
        });

        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopBackgroundService();
            }
        });
    }

    private void deleteTransaction(int position) {
        boolean deleted = expenseViewModel.deleteTransactionAt(position);
        if (deleted) {
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleNoteVisibility(int position) {
        adapter.toggleNoteVisibility(position);
    }

    private void showAddExpenseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_expense, null);

        TextInputEditText editAmount = dialogView.findViewById(R.id.edit_amount);
        TextInputEditText editCategory = dialogView.findViewById(R.id.edit_category);
        TextInputEditText editNote = dialogView.findViewById(R.id.edit_note);
        TextInputLayout layoutAmount = dialogView.findViewById(R.id.layout_amount);
        TextInputLayout layoutCategory = dialogView.findViewById(R.id.layout_category);

        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);

        builder.setView(dialogView);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = editAmount.getText() != null ? editAmount.getText().toString().trim() : "";
                String category = editCategory.getText() != null ? editCategory.getText().toString().trim() : "";
                String note = editNote.getText() != null ? editNote.getText().toString().trim() : "";

                if (validateInput(amountStr, category, layoutAmount, layoutCategory)) {
                    double amount = Double.parseDouble(amountStr);
                    String currentDate = getCurrentDate();

                    Transaction transaction = new Transaction(amount, category, note, currentDate);

                    expenseViewModel.addTransaction(transaction);

                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "添加成功", Toast.LENGTH_SHORT).show();

                    sendNotification(transaction);
                }
            }
        });

        dialog.show();
    }

    private boolean validateInput(String amountStr, String category, TextInputLayout layoutAmount,
            TextInputLayout layoutCategory) {
        boolean isValid = true;

        if (amountStr.isEmpty()) {
            layoutAmount.setError("请输入金额");
            isValid = false;
        } else {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    layoutAmount.setError("金额必须大于0");
                    isValid = false;
                } else {
                    layoutAmount.setError(null);
                }
            } catch (NumberFormatException e) {
                layoutAmount.setError("请输入有效的金额");
                isValid = false;
            }
        }

        if (category.isEmpty()) {
            layoutCategory.setError("请输入类别");
            isValid = false;
        } else {
            layoutCategory.setError(null);
        }

        return isValid;
    }

    private String getCurrentDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }

    private void updateTotalAmountDisplay(double amount) {
        String amountText = String.format("¥%.2f", amount);
        textTotalAmount.setText(amountText);
    }

    private void updateEmptyState(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerViewExpenses.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerViewExpenses.setVisibility(View.VISIBLE);
        }
    }

    private void sendNotification(Transaction transaction) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_save)
                .setContentTitle("记账成功")
                .setContentText(transaction.getCategory() + " ¥" + String.format("%.2f", transaction.getAmount()))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("类别：" + transaction.getCategory() + "\n" +
                                "金额：¥" + String.format("%.2f", transaction.getAmount()) + "\n" +
                                "备注：" + (transaction.getNote().isEmpty() ? "无" : transaction.getNote()) + "\n" +
                                "日期：" + transaction.getDate()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void saveDataToDatabase() {
        List<Transaction> transactions = expenseViewModel.getTransactions();

        dbHelper.deleteAllTransactions();

        int savedCount = 0;
        for (Transaction transaction : transactions) {
            long result = dbHelper.insertTransaction(transaction);
            if (result != -1) {
                savedCount++;
            }
        }

        tvStorageStatus.setText("✅ 保存成功！共保存 " + savedCount + " 条记录");
        Toast.makeText(this, "📥 数据已保存到数据库", Toast.LENGTH_SHORT).show();
    }

    private void loadDataFromDatabase() {
        List<Transaction> savedTransactions = dbHelper.getAllTransactions();

        expenseViewModel.clearAllTransactions();

        for (Transaction transaction : savedTransactions) {
            expenseViewModel.addTransaction(transaction);
        }

        if (!savedTransactions.isEmpty()) {
            tvStorageStatus.setText("已加载 " + savedTransactions.size() + " 条记录");
        }
    }

    private void startBackgroundService() {
        if (isServiceRunning) {
            Toast.makeText(this, "后台任务已在运行中", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent serviceIntent = new Intent(this, BackgroundService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        isServiceRunning = true;
        tvServiceStatus.setText("后台服务运行中...");
        tvServiceStatus.setBackgroundColor(0xFFE8F5E9);
        Toast.makeText(this, "后台任务已启动", Toast.LENGTH_SHORT).show();
    }

    private void stopBackgroundService() {
        if (!isServiceRunning) {
            Toast.makeText(this, "后台任务未运行", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent serviceIntent = new Intent(this, BackgroundService.class);
        stopService(serviceIntent);

        isServiceRunning = false;
        tvServiceStatus.setText("后台服务已停止");
        tvServiceStatus.setBackgroundColor(0xFFF3E5F5);
        Toast.makeText(this, "后台任务已停止", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
