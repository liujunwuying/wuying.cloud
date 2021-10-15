package com.wuying.cloud.transaction.async.dao;

import com.wuying.cloud.transaction.async.domain.Participant;
import com.wuying.cloud.transaction.async.domain.Transaction;
import com.wuying.cloud.transaction.async.enums.RetryIntervalLevel;
import com.wuying.cloud.transaction.async.enums.TransactionStatus;
import com.wuying.cloud.transaction.async.util.JsonUtil;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 数据库操作类
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-13
 */
@Repository
public class TransactionAsyncDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 通过txid获取事务sql
     */
    private final static String SQL_SELECT_BY_TXID = "select * from t_transaction_async where txid = ?";

    /**
     * 通过gxid获取事务sql
     */
    private final static String SQL_SELECT_BY_GXID = "select * from t_transaction_async where gxid = ?";

    /**
     * 插入事务对象
     */
    private final static String SQL_INSERT_TX = "insert int t_transaction_async(txid, gxid, application_name, coordinator, retry_interval, max_retry_times, transaction_level, bean, method, param, param_type, retried_times, status, status_text, create_time, last_update_time) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    /**
     * 更新事务状态sql
     */
    private final static String SQL_UPDATE_STATUS = "update t_transaction_async set status = ?, status_text = ?, retried_times = ?, retry_interval = ?, last_update_time = ? where txid = ?";

    /**
     * 查询需重试事务
     */
    private final static String SQL_SELECT_FOR_RETRY = "select * from t_transaction_async where status > -1 and status < ? and last_update_time + retry_interval * 60000 < ? and application_name = ? order by retried_times, create_time limit 1000";

    /**
     * 删除过期数据
     */
    private final static String SQL_DELETE_FOR_CLEAN = "delete from t_transaction_async where status = ? and last_update_time < ? and application_name = ? limit 1000";



    /**
     * 通过txid查询事务对象
     * @param txid 事务ID
     * @return 事务对象
     */
    public Transaction getByTxid(String txid) {
        Assert.hasText(txid, "txid入参为空");
        Object[] param = new Object[]{txid};
        return jdbcTemplate.query(SQL_SELECT_BY_TXID, param, resultSet -> {
            Transaction t = Transaction.builder().build();
            Participant p = Participant.builder().build();
            t.setParticipant(p);
            return resultSet.next() ? buildTransactionFromResultSet(resultSet) : t;

        });
    }

    /**
     * 通过gxid查询事务对象
     * @param gxid 全局事务ID
     * @return 事务列表
     */
    public List<Transaction> getByGxid(String gxid) {
        Assert.hasText(gxid, "gxid入参为空");
        Object[] param = new Object[]{gxid};
        return jdbcTemplate.query(SQL_SELECT_BY_GXID, param, (resultSet, i) -> buildTransactionFromResultSet(resultSet));
    }

    /**
     * 插入数据
     * @param transaction 事务对象
     */
    public void create(Transaction transaction) {
        Assert.notNull(transaction, "transaction入参为空");
        Object[] values = new Object[]{transaction.getTxid(), transaction.getGxid(), transaction.getApplicationName(),
          transaction.getCoordinator(), transaction.getRetryInterval(), transaction.getMaxRetryTimes(),
          transaction.getParticipant().getBeanName(),
          transaction.getParticipant().getMethod(), JsonUtil.writeValueAsString(transaction.getParticipant().getParam()),
          JsonUtil.writeValueAsString(transaction.getParticipant().getParamTypes()), transaction.getParticipant().getRetriedTimes(),
          transaction.getParticipant().getStatus().getValue(), transaction.getParticipant().getStatusText(),
          transaction.getParticipant().getCreateTime(), transaction.getParticipant().getLastUpdateTime()};
        int count = jdbcTemplate.update(SQL_INSERT_TX, values);
        logger.debug("insert {} row", count);
    }

    /**
     * 更新事务状态
     * @param txid 事务ID
     * @param status 事务状态
     * @param statusText 状态描述
     * @param retriedTimes 重试次数
     */
    public void updateStatus(String txid, int status, String statusText, int retriedTimes) {
        int retryInterval = retriedTimes >= RetryIntervalLevel.quick.getCount() ? RetryIntervalLevel.slow.getInterval()
                : RetryIntervalLevel.quick.getInterval();
        int count = jdbcTemplate.update(SQL_UPDATE_STATUS, status, statusText, retriedTimes, retryInterval, System.currentTimeMillis(), txid);
        logger.debug("update {} rows", count);
    }

    /**
     * 批量更新事务状态
     * @param transactionList 事务列表
     */
    public void updateStatus(List<Transaction> transactionList) {
        Assert.notEmpty(transactionList, "transactionList入参为空");
        jdbcTemplate.execute(SQL_UPDATE_STATUS, (PreparedStatementCallback<Object>) preparedStatement -> {
            preparedStatement.getConnection().setAutoCommit(false);
            int retryInterval = RetryIntervalLevel.quick.getInterval();
            for(Transaction transaction : transactionList) {
                preparedStatement.setInt(1, transaction.getParticipant().getStatus().getValue());
                preparedStatement.setString(2, transaction.getParticipant().getStatusText());
                preparedStatement.setInt(3, transaction.getParticipant().getRetriedTimes());
                if (transaction.getParticipant().getRetriedTimes() >= RetryIntervalLevel.quick.getCount()) {
                    retryInterval = RetryIntervalLevel.slow.getInterval();
                }
                preparedStatement.setInt(4, retryInterval);
                preparedStatement.setLong(5, System.currentTimeMillis());
                preparedStatement.setString(6, transaction.getTxid());
                preparedStatement.addBatch();
            }
            int[] result = preparedStatement.executeBatch();
            preparedStatement.getConnection().commit();
            return result;
        });
    }

    /**
     * 查询需重试事务
     * @return 需重试事务列表
     */
    public List<Transaction> findRetryTransaction() {
        Object[] param = new Object[]{TransactionStatus.success.getValue(), System.currentTimeMillis(), applicationName};
        return jdbcTemplate.query(SQL_SELECT_FOR_RETRY, param, (resultSet, i) -> buildTransactionFromResultSet(resultSet));
    }

    /**
     * 清理事务
     * @param days 最大保留天数
     * @return
     */
    public int cleanTransactionByMaxDays(int days) {
        int total = 0;
        int count;
        do {
            logger.debug("findRetryTransaction()");
            count = jdbcTemplate.update(SQL_DELETE_FOR_CLEAN, TransactionStatus.success.getValue(), System.currentTimeMillis() - (days * 24 * 3600 * 1000L), applicationName);
            total += count;
        } while (count >= 100);
        return total;
    }

    /**
     * ResultSet转为Transaction
     * @param resultSet 结果集
     * @return 事务对象
     * @throws SQLException sql异常
     */
    private Transaction buildTransactionFromResultSet(ResultSet resultSet) throws SQLException {
        Participant participant = Participant.builder()
                .beanName(resultSet.getString("bean"))
                //.method(resultSet.getString("method"))
                .createTime(resultSet.getLong("create_time"))
                .lastUpdateTime(resultSet.getLong("last_update_time"))
                .retriedTimes(resultSet.getInt("retried_times"))
                .status(TransactionStatus.toEnum(resultSet.getInt("status")))
                .statusText(resultSet.getString("status_text"))
                .build();
        Object[] param = JsonUtil.readArrayValue(resultSet.getString("param"), Object.class);
        String[] paramTypeNames = JsonUtil.readArrayValue(resultSet.getString("param_type"),String.class);
        if(param != null && param.length > 0 && paramTypeNames != null && paramTypeNames.length > 0) {
            Object[] realParams = new Object[param.length];
            Class[] paramTypes = new Class[paramTypeNames.length];
            for (int i = 0; i < paramTypeNames.length; ++i) {
                Class<?> clazz = getParamType(paramTypeNames[i]);
                paramTypes[i] = clazz;
                realParams[i] = JsonUtil.convertValue(param[i], clazz);
            }
            participant.setParam(realParams);
            participant.setParamTypes(paramTypes);
        }
        return Transaction.builder()
                .participant(participant)
                .gxid(resultSet.getString("gxid"))
                .maxRetryTimes(resultSet.getInt("max_retry_times"))
                .coordinator(resultSet.getString("coordinator"))
                .retryInterval(resultSet.getInt("retry_interval"))
                .txid(resultSet.getString("txid"))
                .applicationName(resultSet.getString("application_name"))
                .build();
    }

    private Class<?> getParamType(String paramTypeName) {
        Assert.hasText(paramTypeName,"paramTypeName入参为空");
        switch (paramTypeName) {
            case "int" : return Integer.TYPE;
            case "short" : return Short.TYPE;
            case "long" : return Long.TYPE;
            case "char" : return Character.TYPE;
            case "byte" : return Byte.TYPE;
            case "boolean" : return Boolean.TYPE;
            case "float" : return Float.TYPE;
            case "double" : return Double.TYPE;
            default : try {
                return Class.forName(paramTypeName);
            } catch (ClassNotFoundException classNotFoundException) {
                throw new IllegalStateException(classNotFoundException);
            }
        }
    }
}
