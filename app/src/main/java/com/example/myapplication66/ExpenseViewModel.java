package com.example.myapplication66;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

/**
 * 消费记录视图模型
 * <p>
 * 负责管理消费记录的业务逻辑，包括消费记录的增删改查和总金额计算。
 * 使用 ViewModel 架构组件，确保在配置更改时数据不丢失。
 * 通过 TransactionRepository 进行数据访问，实现数据层与业务层的分离。
 * </p>
 *
 * @author Your Name
 * @version 2.0
 * @since 1.0
 */
public class ExpenseViewModel extends ViewModel {

    /**
     * 消费记录数据仓库
     */
    private TransactionRepository repository;

    /**
     * 消费记录列表的 LiveData，用于观察数据变化
     */
    private MutableLiveData<List<Transaction>> transactionsLiveData;

    /**
     * 总消费金额的 LiveData，用于观察数据变化
     */
    private MutableLiveData<Double> totalAmountLiveData;

    /**
     * 构造方法
     * <p>
     * 初始化数据仓库和相关数据。
     * </p>
     */
    public ExpenseViewModel() {
        repository = TransactionRepository.getInstance();
        transactionsLiveData = new MutableLiveData<>();
        totalAmountLiveData = new MutableLiveData<>();
        updateLiveData();
    }

    /**
     * 获取消费记录列表的 LiveData
     *
     * @return 消费记录列表的 LiveData
     */
    public MutableLiveData<List<Transaction>> getTransactionsLiveData() {
        return transactionsLiveData;
    }

    /**
     * 获取总消费金额的 LiveData
     *
     * @return 总消费金额的 LiveData
     */
    public MutableLiveData<Double> getTotalAmountLiveData() {
        return totalAmountLiveData;
    }

    /**
     * 获取消费记录列表
     *
     * @return 消费记录列表
     */
    public List<Transaction> getTransactions() {
        return repository.getAllTransactions();
    }

    /**
     * 获取总消费金额
     *
     * @return 总消费金额
     */
    public double getTotalAmount() {
        return repository.getTotalExpense();
    }

    /**
     * 添加消费记录
     * <p>
     * 将消费记录添加到仓库中，并更新 LiveData。
     * </p>
     *
     * @param transaction 要添加的消费记录
     * @throws IllegalArgumentException 当消费记录为 null 时抛出
     */
    public void addTransaction(Transaction transaction) {
        boolean added = repository.addTransaction(transaction);
        if (added) {
            updateLiveData();
        }
    }

    /**
     * 删除消费记录
     * <p>
     * 从仓库中删除指定的消费记录，并更新 LiveData。
     * </p>
     *
     * @param transaction 要删除的消费记录
     * @return 如果删除成功返回 true，否则返回 false
     */
    public boolean deleteTransaction(Transaction transaction) {
        boolean deleted = repository.deleteTransaction(transaction);
        if (deleted) {
            updateLiveData();
        }
        return deleted;
    }

    /**
     * 根据索引删除消费记录
     * <p>
     * 从仓库中删除指定索引的消费记录，并更新 LiveData。
     * </p>
     *
     * @param position 要删除的消费记录的索引
     * @return 如果删除成功返回 true，否则返回 false
     */
    public boolean deleteTransactionAt(int position) {
        boolean deleted = repository.deleteTransactionAt(position);
        if (deleted) {
            updateLiveData();
        }
        return deleted;
    }

    /**
     * 清空所有消费记录
     * <p>
     * 删除仓库中的所有消费记录，并更新 LiveData。
     * </p>
     */
    public void clearAllTransactions() {
        repository.clearAllTransactions();
        updateLiveData();
    }

    /**
     * 获取消费记录数量
     *
     * @return 消费记录数量
     */
    public int getTransactionCount() {
        return repository.getTransactionCount();
    }

    /**
     * 根据索引获取消费记录
     *
     * @param position 消费记录的索引
     * @return 指定索引的消费记录，如果索引无效返回 null
     */
    public Transaction getTransactionAt(int position) {
        return repository.getTransactionAt(position);
    }

    /**
     * 更新 LiveData
     * <p>
     * 从仓库获取最新数据，通知观察者数据已发生变化。
     * </p>
     */
    private void updateLiveData() {
        transactionsLiveData.setValue(repository.getAllTransactions());
        totalAmountLiveData.setValue(repository.getTotalExpense());
    }

    /**
     * 获取指定类别的消费记录列表
     *
     * @param category 消费类别
     * @return 指定类别的消费记录列表
     */
    public List<Transaction> getTransactionsByCategory(String category) {
        return repository.getTransactionsByCategory(category);
    }

    /**
     * 获取指定日期的消费记录列表
     *
     * @param date 消费日期
     * @return 指定日期的消费记录列表
     */
    public List<Transaction> getTransactionsByDate(String date) {
        return repository.getTransactionsByDate(date);
    }

    /**
     * 获取指定类别的消费总额
     *
     * @param category 消费类别
     * @return 指定类别的消费总额
     */
    public double getTotalExpenseByCategory(String category) {
        return repository.getTotalExpenseByCategory(category);
    }

    /**
     * 获取指定日期的消费总额
     *
     * @param date 消费日期
     * @return 指定日期的消费总额
     */
    public double getTotalExpenseByDate(String date) {
        return repository.getTotalExpenseByDate(date);
    }
}
