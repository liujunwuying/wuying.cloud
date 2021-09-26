package com.wuying.cloud.transaction.async.controller;

import com.wuying.cloud.transaction.async.service.TransactionAsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接口暴露
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-13
 */
@RestController
@RequestMapping("/transaction/async")
public class TransactionAsyncController {

    @Autowired
    private TransactionAsyncService transactionAsyncService;

    @PostMapping("/commit/txid/{txid}")
    public void commitByTxid(@PathVariable("txid")String txid) {
        transactionAsyncService.commitByTxid(txid);
    }

    @PostMapping("/commit/gxid/{gxid}")
    public void commitByGxid(@PathVariable("gxid")String gxid) {
        transactionAsyncService.commitByGxid(gxid);
    }

    @PostMapping("/status/{gxid}")
    public int getStatusByGxid(@PathVariable("gixd")String gxid) {
        return transactionAsyncService.getStatusByGxid(gxid);
    }
}
