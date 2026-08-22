package com.dh.demo.repository;

import com.dh.demo.entity.PermissionProfiles;
import com.dh.demo.entity.PermissionProfilesId;
import com.dh.demo.repository.projection.IModuleAccessProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface IPermissionProfilesRepository extends JpaRepository<PermissionProfiles, PermissionProfilesId> {

    @Query(value = """
        SELECT
            M.ID AS moduleId,
            M.NAME AS name,
            M.DESCRIPTION AS description,
            M.URL AS url,
            M.STATE AS state,
            PER.ENTRY_ALLOWED AS entryAllowed,
            SM.ID AS subModuleId,
            SM.NAME AS subModuleName,
            SM.URL AS subModuleUrl,
            PRO_MOD_PER.CODE AS actionCode,
            PRO_MOD_PER.ACTION_NAME AS actionName,
            PRO_MOD_PER.CHECK_ALLOWED AS checkAllowed
        FROM PERMISSION_PROFILES PER
        INNER JOIN PROFILE P ON P.ID = PER.PROFILE_ID
        INNER JOIN MODULES M ON M.ID = PER.MODULE_ID
        LEFT JOIN PERMISSION_PROFILES_MODULE PRO_MOD
            ON PRO_MOD.MODULE_ID = M.ID AND PRO_MOD.PROFILE_ID = P.ID
        LEFT JOIN SUBMODULES SM
            ON SM.ID = PRO_MOD.SUBMODULE_ID
        LEFT JOIN PERMISSION_PROFILES_MODULE_PER PRO_MOD_PER
            ON PRO_MOD_PER.MODULE_ID = PRO_MOD.MODULE_ID
            AND PRO_MOD_PER.PROFILE_ID = PRO_MOD.PROFILE_ID
            AND PRO_MOD_PER.SUBMODULE_ID = PRO_MOD.SUBMODULE_ID
        WHERE P.ID = :profileId
        """, nativeQuery = true)
    List<IModuleAccessProjection> findModulesWithActionsByProfileId(@Param("profileId") Long profileId);
}