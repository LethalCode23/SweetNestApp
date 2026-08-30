package com.dh.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubModuleDto {

    private Long id;
    private String name;
    private String url;

    @Builder.Default
    private List<ActionDto> actions = new ArrayList<>();
}