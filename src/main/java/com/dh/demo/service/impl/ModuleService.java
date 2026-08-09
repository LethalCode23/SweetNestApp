package com.dh.demo.service.impl;

import com.dh.demo.dto.ModuleDto;
import com.dh.demo.entity.Module;
import com.dh.demo.repository.IModuleRepository;
import com.dh.demo.service.IModuleService;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ModuleService implements IModuleService {

    private final IModuleRepository moduleRepository;

    public ModuleService(IModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    @Override
    public ModuleDto save(ModuleDto moduleDto) {

        Module module = mapToEntity(moduleDto);
        Module saved = moduleRepository.save(module);

        return mapToDto(saved);
    }

    @Override
    public ModuleDto update(Long id, ModuleDto ModuleDto) {
        return null;
    }

    @Override
    public Optional<ModuleDto> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public void delete(Long id) {

    }

    private Module mapToEntity(ModuleDto dto) {

        Module module = new Module();

        module.setId(dto.getModuleSec());
        module.setName(dto.getModuleName());
        module.setState(dto.getModuleState());

        return module;
    }

    private ModuleDto mapToDto(Module module) {

        ModuleDto dto = new ModuleDto();

        dto.setModuleSec(module.getId());
        dto.setModuleName(module.getName());
        dto.setModuleState(module.getState());

        return dto;
    }
}