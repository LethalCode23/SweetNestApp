package com.dh.demo.repository;

import com.dh.demo.entity.PermissionProfilesModulePer;
import com.dh.demo.entity.PermissionProfilesModulePerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IPermissionProfilesModulePerRepository extends JpaRepository<PermissionProfilesModulePer, PermissionProfilesModulePerId> {

    @Modifying
    @Query("UPDATE PermissionProfilesModulePer p SET p.check = :checkAllowed " +
            "WHERE p.id.profileId = :profileId " +
            "AND p.id.moduleId = :moduleId " +
            "AND p.id.submoduleId = :submoduleId " +
            "AND p.id.code = :code")
    int updateCheckAllowed(
            @Param("profileId") Long profileId,
            @Param("moduleId") Long moduleId,
            @Param("submoduleId") Long submoduleId,
            @Param("code") Character code,
            @Param("checkAllowed") Character checkAllowed
    );
}