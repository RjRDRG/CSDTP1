package com.fct.csd.contractor.impl;

import com.fct.csd.common.contract.BlockChainView;
import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.request.SmartTransferRequestBody;
import com.fct.csd.contractor.repository.ClosedTransactionRepository;
import com.fct.csd.contractor.repository.SmartContractEntity;
import com.fct.csd.contractor.repository.SmartContractRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
class ContractorController {

    SmartContractRepository smartContractRepository;
    BlockChainView blockChainView;

    ContractorController(ClosedTransactionRepository closedTransactionRepository, SmartContractRepository smartContractRepository) {
        this.smartContractRepository = smartContractRepository;
        this.blockChainView = new BlockChainViewImpl(closedTransactionRepository);
    }

    @PostMapping("/contract")
    private List<Transaction> runSmartContract(@RequestBody SmartTransferRequestBody request) {
        Optional<SmartContractEntity> entity = smartContractRepository.findById(request.getContractId());

        if (entity.isEmpty()) return new ArrayList<>(0);

        try {
            return entity.get().getContract().run(request.getParameters(), blockChainView);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(0);
        }
    }
}