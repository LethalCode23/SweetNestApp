package com.dh.demo.service;

public interface IPermissionProfilesModuleService {

    void grantSubModuleAccess(Long profileId, Long moduleId, Long subModuleId);
    void revokeSubModuleAccess(Long profileId, Long moduleId, Long subModuleId);
}