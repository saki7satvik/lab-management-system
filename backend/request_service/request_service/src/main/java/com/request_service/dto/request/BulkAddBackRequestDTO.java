package com.request_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkAddBackRequestDTO {

 @NotEmpty(message = "Add back items cannot be empty")
 @Valid
 private List<AddBackRequestDTO> items;
}
