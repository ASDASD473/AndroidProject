package com.example.myapplication66;

/**
 * 消费记录实体类
 * <p>
 * 用于存储和管理单笔消费记录的详细信息，包括消费金额、类别、备注和日期。
 * 该类提供了完整的字段访问控制和数据验证机制，确保数据的完整性和安全性。
 * </p>
 *
 * @author Your Name
 * @version 1.1
 * @since 1.0
 */
public class Transaction {

    /**
     * 记录ID（数据库主键）
     */
    private long id;

    /**
     * 消费金额（单位：元）
     */
    private double amount;

    /**
     * 消费类别（如：餐饮、交通、购物等）
     */
    private String category;

    /**
     * 备注信息
     */
    private String note;

    /**
     * 消费日期（格式：yyyy-MM-dd）
     */
    private String date;

    /**
     * 获取记录ID
     *
     * @return 记录ID
     */
    public long getId() {
        return id;
    }

    /**
     * 设置记录ID
     *
     * @param id 记录ID
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * 默认构造方法
     * <p>
     * 创建一个空的消费记录对象，所有字段将被初始化为默认值。
     * 金额初始化为0.0，字符串字段初始化为空字符串。
     * </p>
     */
    public Transaction() {
        this.id = 0;
        this.amount = 0.0;
        this.category = "";
        this.note = "";
        this.date = "";
    }

    /**
     * 完整参数构造方法
     * <p>
     * 使用指定的参数创建消费记录对象。构造方法会对参数进行验证，
     * 确保数据的合法性。
     * </p>
     *
     * @param amount   消费金额（必须大于等于0）
     * @param category 消费类别（不能为null）
     * @param note     备注信息（不能为null）
     * @param date     消费日期（不能为null）
     * @throws IllegalArgumentException 当金额小于0时抛出
     */
    public Transaction(double amount, String category, String note, String date) {
        setAmount(amount);
        setCategory(category);
        setNote(note);
        setDate(date);
    }

    /**
     * 获取消费金额
     *
     * @return 消费金额（单位：元）
     */
    public double getAmount() {
        return amount;
    }

    /**
     * 设置消费金额
     * <p>
     * 对金额进行范围检查，确保金额不为负数。
     * </p>
     *
     * @param amount 消费金额（必须大于等于0）
     * @throws IllegalArgumentException 当金额小于0时抛出
     */
    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("消费金额不能为负数");
        }
        this.amount = amount;
    }

    /**
     * 获取消费类别
     *
     * @return 消费类别
     */
    public String getCategory() {
        return category;
    }

    /**
     * 设置消费类别
     * <p>
     * 对类别进行非空检查，如果传入null则设置为空字符串。
     * </p>
     *
     * @param category 消费类别
     */
    public void setCategory(String category) {
        this.category = (category != null) ? category : "";
    }

    /**
     * 获取备注信息
     *
     * @return 备注信息
     */
    public String getNote() {
        return note;
    }

    /**
     * 设置备注信息
     * <p>
     * 对备注进行非空检查，如果传入null则设置为空字符串。
     * </p>
     *
     * @param note 备注信息
     */
    public void setNote(String note) {
        this.note = (note != null) ? note : "";
    }

    /**
     * 获取消费日期
     *
     * @return 消费日期（格式：yyyy-MM-dd）
     */
    public String getDate() {
        return date;
    }

    /**
     * 设置消费日期
     * <p>
     * 对日期进行非空检查，如果传入null则设置为空字符串。
     * </p>
     * 
     * @param date 消费日期
     */
    public void setDate(String date) {
        this.date = (date != null) ? date : "";
    }

    /**
     * 返回消费记录的字符串表示
     * <p>
     * 包含所有字段的详细信息，便于调试和日志输出。
     * </p>
     *
     * @return 包含所有字段信息的字符串
     */
    @Override
    public String toString() {
        return "Transaction{" +
                "amount=" + amount +
                ", category='" + category + '\'' +
                ", note='" + note + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
