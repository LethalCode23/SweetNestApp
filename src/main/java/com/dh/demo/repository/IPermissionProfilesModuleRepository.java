package com.dh.demo.repository;

import com.dh.demo.entity.PermissionProfilesModule;
import com.dh.demo.entity.PermissionProfilesModuleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPermissionProfilesModuleRepository extends JpaRepository<PermissionProfilesModule, PermissionProfilesModuleId> {

    boolean existsById_ProfileIdAndId_ModuleIdAndId_SubmoduleId(
            Long profileId, Long moduleId, Long submoduleId);

    void deleteById_ProfileIdAndId_ModuleIdAndId_SubmoduleId(
            Long profileId, Long moduleId, Long submoduleId);
}