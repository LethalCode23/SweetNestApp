package com.dh.demo.service.impl;

import com.dh.demo.dto.*;
import com.dh.demo.repository.IPermissionProfilesRepository;
import com.dh.demo.repository.projection.IModuleAccessProjection;
import com.dh.demo.service.IRbacService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RbacService implements IRbacService {

    private final IPermissionProfilesRepository permissionProfilesRepository;

    @Override
    public List<ModuleDto> getModulesWithActionsByProfile(Long profileId) {

        List<IModuleAccessProjection> rows =
                permissionProfilesRepository.findModulesWithActionsByProfileId(profileId);

        Map<Long, ModuleDto> modulesById = new LinkedHashMap<>();

        Map<String, SubModuleDto> subModulesByKey = new LinkedHashMap<>();

        for (IModuleAccessProjection row : rows) {

            ModuleDto module = modulesById.computeIfAbsent(row.getModuleId(), id ->
                    ModuleDto.builder()
                            .moduleSec(row.getModuleId())
                            .moduleName(row.getName())
                            .moduleUrl(row.getUrl())
                            .moduleDescription(row.getDescription())
                            .moduleState(row.getState() != null ? row.getState().charAt(0) : null)
                            .entryAllowed(toBoolean(row.getEntryAllowed()))
                            .build()
            );

            if (row.getSubModuleId() == null) continue;

            String subKey = row.getModuleId() + "-" + row.getSubModuleId();
            SubModuleDto subModule = subModulesByKey.computeIfAbsent(subKey, k -> {
                SubModuleDto sm = SubModuleDto.builder()
                        .id(row.getSubModuleId())
                        .name(row.getSubModuleName())
                        .url(row.getSubModuleUrl())
                        .build();
                module.getSubModules().add(sm);
                return sm;
            });

            if (row.getActionCode() != null) {
                subModule.getActions().add(
                        ActionDto.builder()
                                .code(row.getActionCode())
                                .name(row.getActionName())
                                .allowed(toBoolean(row.getCheckAllowed()))
                                .build()
                );
            }
        }

        return new ArrayList<>(modulesById.values());
    }

    @Override
    @Transactional
    public int updateEntryAllowed(Long profileId, Long moduleId, Boolean entryAllowed) {

        Character value = entryAllowed ? 'S' : 'N';
        int updatedRows = permissionProfilesRepository.updateEntryAllowed(profileId, moduleId, value);

        if (updatedRows == 0) {

            throw new EntityNotFoundException(
                    "No existe permiso para el perfil " + profileId + " en el módulo " + moduleId
            );
        }

        return 0;
    }

    private Boolean toBoolean(Character c) {
        return c != null && (c == 'S' || c == 'Y');
    }
}