package com.fct.csd.contractor.impl;

import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.request.SmartTransferRequestBody;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
class ContractorController {

    ContractorController() {
    }

    @PostMapping("/contract")
    private List<Transaction> runSmartContract(@RequestBody SmartTransferRequestBody request) {
        return null;
    }
}