package com.dh.demo.service.impl;

import com.dh.demo.repository.IPermissionProfilesModulePerRepository;
import com.dh.demo.service.IPermissionProfilesModulePerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionProfilesModulePerService implements IPermissionProfilesModulePerService {

    private final IPermissionProfilesModulePerRepository permissionProfilesModulePerRepository;

    @Override
    @Transactional
    public void updateActionAllowed(Long profileId, Long moduleId, Long subModuleId,
                                    Character code, boolean allowed) {

        Character value = allowed ? 'S' : 'N';

        int updatedRows = permissionProfilesModulePerRepository.updateCheckAllowed(
                profileId, moduleId, subModuleId, code, value);

        if (updatedRows == 0) {

            throw new EntityNotFoundException(
                    "No existe la acción '" + code + "' para el perfil " + profileId +
                            " en el submódulo " + subModuleId
            );
        }
    }
}