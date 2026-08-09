package com.dh.demo.service;

import com.dh.demo.dto.ModuleDto;
import java.util.Optional;

public interface IModuleService {

    ModuleDto save(ModuleDto moduleDto);

    ModuleDto update(Long id, ModuleDto ModuleDto);

    Optional<ModuleDto> findById(Long id);

    void delete(Long id);
}