package com.example.myapplication66;

import java.util.ArrayList;
import java.util.List;

/**
 * 消费记录数据仓库
 * <p>
 * 负责统一管理消费记录数据的存储和访问。
 * 当前使用内存列表存储数据，未来可扩展为数据库存储。
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
public class TransactionRepository {

    /**
     * 消费记录列表（内存存储）
     */
    private List<Transaction> transactions;

    /**
     * 单例实例
     */
    private static TransactionRepository instance;

    /**
     * 私有构造方法，实现单例模式
     */
    private TransactionRepository() {
        transactions = new ArrayList<>();
    }

    /**
     * 获取 TransactionRepository 单例实例
     *
     * @return TransactionRepository 实例
     */
    public static synchronized TransactionRepository getInstance() {
        if (instance == null) {
            instance = new TransactionRepository();
        }
        return instance;
    }

    /**
     * 添加消费记录
     * <p>
     * 将消费记录添加到存储中。
     * </p>
     *
     * @param transaction 要添加的消费记录
     * @return 如果添加成功返回 true，否则返回 false
     * @throws IllegalArgumentException 当消费记录为 null 时抛出
     */
    public boolean addTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("消费记录不能为 null");
        }
        return transactions.add(transaction);
    }

    /**
     * 删除消费记录
     * <p>
     * 从存储中删除指定的消费记录。
     * </p>
     *
     * @param transaction 要删除的消费记录
     * @return 如果删除成功返回 true，否则返回 false
     */
    public boolean deleteTransaction(Transaction transaction) {
        if (transaction == null) {
            return false;
        }
        return transactions.remove(transaction);
    }

    /**
     * 根据索引删除消费记录
     * <p>
     * 从存储中删除指定索引的消费记录。
     * </p>
     *
     * @param position 要删除的消费记录的索引
     * @return 如果删除成功返回 true，否则返回 false
     */
    public boolean deleteTransactionAt(int position) {
        if (position < 0 || position >= transactions.size()) {
            return false;
        }
        transactions.remove(position);
        return true;
    }

    /**
     * 获取所有消费记录
     * <p>
     * 返回存储中所有消费记录的副本，防止外部直接修改内部数据。
     * </p>
     *
     * @return 所有消费记录的列表
     */
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    /**
     * 获取总消费金额
     * <p>
     * 计算并返回所有消费记录的金额总和。
     * </p>
     *
     * @return 总消费金额
     */
    public double getTotalExpense() {
        double total = 0.0;
        for (Transaction transaction : transactions) {
            total += transaction.getAmount();
        }
        return total;
    }

    /**
     * 获取消费记录数量
     *
     * @return 消费记录数量
     */
    public int getTransactionCount() {
        return transactions.size();
    }

    /**
     * 根据索引获取消费记录
     *
     * @param position 消费记录的索引
     * @return 指定索引的消费记录，如果索引无效返回 null
     */
    public Transaction getTransactionAt(int position) {
        if (position < 0 || position >= transactions.size()) {
            return null;
        }
        return transactions.get(position);
    }

    /**
     * 清空所有消费记录
     * <p>
     * 删除存储中的所有消费记录。
     * </p>
     */
    public void clearAllTransactions() {
        transactions.clear();
    }

    /**
     * 获取指定类别的消费记录列表
     *
     * @param category 消费类别
     * @return 指定类别的消费记录列表
     */
    public List<Transaction> getTransactionsByCategory(String category) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getCategory().equals(category)) {
                result.add(transaction);
            }
        }
        return result;
    }

    /**
     * 获取指定日期的消费记录列表
     *
     * @param date 消费日期
     * @return 指定日期的消费记录列表
     */
    public List<Transaction> getTransactionsByDate(String date) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getDate().equals(date)) {
                result.add(transaction);
            }
        }
        return result;
    }

    /**
     * 获取指定类别的消费总额
     *
     * @param category 消费类别
     * @return 指定类别的消费总额
     */
    public double getTotalExpenseByCategory(String category) {
        double total = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.getCategory().equals(category)) {
                total += transaction.getAmount();
            }
        }
        return total;
    }

    /**
     * 获取指定日期的消费总额
     *
     * @param date 消费日期
     * @return 指定日期的消费总额
     */
    public double getTotalExpenseByDate(String date) {
        double total = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.getDate().equals(date)) {
                total += transaction.getAmount();
            }
        }
        return total;
    }
}
