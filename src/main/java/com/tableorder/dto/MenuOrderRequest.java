package com.tableorder.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MenuOrderRequest {
    private Long menuId;
    private Integer displayOrder;
}
