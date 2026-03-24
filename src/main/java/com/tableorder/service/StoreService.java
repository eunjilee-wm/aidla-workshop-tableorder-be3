package com.tableorder.service;

import com.tableorder.entity.Store;
import com.tableorder.exception.StoreNotFoundException;
import com.tableorder.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;

    public Store getStore(String storeId) {
        return storeRepository.findByStoreId(storeId)
                .orElseThrow(() -> new StoreNotFoundException("Store not found: " + storeId));
    }

    public Store validateStore(String storeId) {
        return getStore(storeId);
    }
}
