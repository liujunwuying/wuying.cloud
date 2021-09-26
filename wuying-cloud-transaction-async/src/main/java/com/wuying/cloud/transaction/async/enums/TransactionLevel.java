package com.wuying.cloud.transaction.async.enums;


/**
 * 事务级别
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-10
 */
public enum TransactionLevel {

    assure("确保通知",0),
    tryTo("尽量通知",1),
    none("无事务",2);

    private String name;

    private int value;

    private TransactionLevel(String name,int value) {
        this.name = name;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static TransactionLevel toEnum(int value) {
        switch (value) {
            case 0:
                return assure;
            case 1:
                return tryTo;
            case 2:
                return none;
            default:
                throw new IllegalArgumentException("无此类型");
        }
    }
}
