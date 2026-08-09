package com.dh.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDto {

    private long moduleSec;
    private String moduleName;
    private String moduleUrl;
    private String moduleDescription;
    private Character moduleState;
}