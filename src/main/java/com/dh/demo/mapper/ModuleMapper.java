package com.dh.demo.mapper;

import com.dh.demo.dto.ModuleDto;
import com.dh.demo.entity.Module;
import org.springframework.stereotype.Component;

@Component
public class ModuleMapper {

    public Module toEntity(ModuleDto dto) {

        Module module = new Module();

        module.setId(dto.getModuleSec());
        module.setName(dto.getModuleName());
        module.setState(dto.getModuleState());

        return module;
    }

    public ModuleDto toDto(Module module) {

        ModuleDto dto = new ModuleDto();

        dto.setModuleSec(module.getId());
        dto.setModuleName(module.getName());
        dto.setModuleState(module.getState());

        return dto;
    }
}