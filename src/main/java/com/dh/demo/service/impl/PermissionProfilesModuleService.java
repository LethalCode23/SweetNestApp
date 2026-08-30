package com.dh.demo.service.impl;

import com.dh.demo.entity.*;
import com.dh.demo.entity.Module;
import com.dh.demo.repository.*;
import com.dh.demo.service.IPermissionProfilesModuleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionProfilesModuleService implements IPermissionProfilesModuleService {

    // temp
    private static final List<Character> DEFAULT_ACTIONS = List.of('R', 'C', 'U', 'D');

    private final IPermissionProfilesModulePerRepository permissionProfilesModulePerRepository;
    private final IPermissionProfilesModuleRepository permissionProfilesModuleRepository;
    private final IPermissionProfilesRepository permissionProfilesRepository;
    private final IProfileRepository profileRepository;
    private final IModuleRepository moduleRepository;
    private final ISubModuleRepository subModuleRepository;

    @Override
    @Transactional
    public void grantSubModuleAccess(Long profileId, Long moduleId, Long subModuleId) {

        if (!permissionProfilesRepository.existsById(new PermissionProfilesId(profileId, moduleId))) {

            throw new IllegalStateException(
                    "El perfil no tiene acceso al módulo padre. Actívalo primero con entry-allowed.");
        }

        boolean alreadyExists = permissionProfilesModuleRepository
                .existsById_ProfileIdAndId_ModuleIdAndId_SubmoduleId(profileId, moduleId, subModuleId);

        if (alreadyExists) {
            return;
        }

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado"));

        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new EntityNotFoundException("Módulo no encontrado"));

        SubModule subModule = subModuleRepository.findById(subModuleId)
                .orElseThrow(() -> new EntityNotFoundException("Submódulo no encontrado"));

        PermissionProfiles permissionProfiles = permissionProfilesRepository
                .findById(new PermissionProfilesId(profileId, moduleId))
                .orElseThrow(() -> new EntityNotFoundException("Permiso de módulo no encontrado"));

        PermissionProfilesModuleId id = new PermissionProfilesModuleId(profileId, moduleId, subModuleId);

        PermissionProfilesModule ppm = PermissionProfilesModule.builder()
                .id(id)
                .profile(profile)
                .module(module)
                .subModule(subModule)
                .permissionProfiles(permissionProfiles)
                .build();

        permissionProfilesModuleRepository.save(ppm);

        // temp: create actions for modules
        for (Character code : DEFAULT_ACTIONS) {

            PermissionProfilesModulePerId perId =
                    new PermissionProfilesModulePerId(profileId, moduleId, subModuleId, code);

            PermissionProfilesModulePer per = PermissionProfilesModulePer.builder()
                    .id(perId)
                    .permissionProfilesModule(ppm)
                    .actionName(actionLabel(code) + " en " + subModule.getName())
                    .check('N')
                    .build();

            permissionProfilesModulePerRepository.save(per);
        }
    }

    @Override
    @Transactional
    public void revokeSubModuleAccess(Long profileId, Long moduleId, Long subModuleId) {
        permissionProfilesModuleRepository
                .deleteById_ProfileIdAndId_ModuleIdAndId_SubmoduleId(profileId, moduleId, subModuleId);
    }

    private String actionLabel(Character code) {

        return switch (code) {
            case 'R' -> "Permiso de Listar";
            case 'C' -> "Permiso de Agregar";
            case 'U' -> "Permiso de Actualizar";
            case 'D' -> "Permiso de Eliminar";
            default -> "Permiso desconocido";
        };
    }
}