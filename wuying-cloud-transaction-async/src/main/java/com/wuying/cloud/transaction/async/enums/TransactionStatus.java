package com.wuying.cloud.transaction.async.enums;


/**
 * 事务状态
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-10
 */
public enum TransactionStatus {

    init("创建",0),
    retry("待重试",1),
    success("成功",100),
    fail("失败",-1);

    private String name;

    private int value;

    private TransactionStatus(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static TransactionStatus toEnum(int value) {
        switch (value) {
            case 0:
                return init;
            case 1:
                return retry;
            case 100:
                return success;
            case -1:
                return fail;
            default:
                throw new IllegalArgumentException("无此类型");
        }
    }
}
