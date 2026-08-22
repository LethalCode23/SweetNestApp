package com.dh.demo.service.impl;

import com.dh.demo.dto.*;
import com.dh.demo.repository.IPermissionProfilesRepository;
import com.dh.demo.repository.projection.IModuleAccessProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RbacService {

    private final IPermissionProfilesRepository permissionProfilesRepository;

    public List<ModuleDto> getModulesWithActionsByProfile(Long profileId) {

        List<IModuleAccessProjection> rows =
                permissionProfilesRepository.findModulesWithActionsByProfileId(profileId);

        Map<Long, ModuleDto> modulesById = new LinkedHashMap<>();
        // key compuesta: moduleId + subModuleId, para no confundir submódulos repetidos entre módulos
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

    private Boolean toBoolean(Character c) {
        return c != null && (c == 'S' || c == 'Y');
    }
}