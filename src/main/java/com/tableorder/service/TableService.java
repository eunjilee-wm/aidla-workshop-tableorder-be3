package com.tableorder.service;

import com.tableorder.entity.StoreTable;
import com.tableorder.exception.TableNotFoundException;
import com.tableorder.repository.StoreTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TableService {
    private final StoreTableRepository storeTableRepository;

    public StoreTable validateTable(Long storeId, Integer tableNumber) {
        return storeTableRepository.findByStoreIdAndTableNumber(storeId, tableNumber)
                .orElseThrow(() -> new TableNotFoundException("Table not found: storeId=" + storeId + ", tableNumber=" + tableNumber));
    }
}
