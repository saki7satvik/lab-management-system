package com.return_service.dto.external;

import lombok.Data;
import java.util.List;

@Data
public class BulkAddBackRequestDTO {
    private List<AddBackRequestDTO> items;
}