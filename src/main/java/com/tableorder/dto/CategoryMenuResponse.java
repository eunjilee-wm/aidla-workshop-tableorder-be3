package com.tableorder.dto;

import com.tableorder.entity.Menu;
import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoryMenuResponse {
    private Long categoryId;
    private String categoryName;
    private Integer displayOrder;
    private List<Menu> menus;
}
