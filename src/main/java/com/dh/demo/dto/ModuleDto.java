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
public class ModuleDto {

    private long moduleSec;
    private String moduleName;
    private String moduleUrl;
    private String moduleDescription;
    private Character moduleState;
    private Boolean entryAllowed;

    @Builder.Default
    private List<SubModuleDto> subModules = new ArrayList<>(); // antes: List<String>
}