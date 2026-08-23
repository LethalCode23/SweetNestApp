package com.dh.demo.service;

public interface IPermissionProfilesModulePerService {
    void updateActionAllowed(Long profileId, Long moduleId, Long subModuleId, Character code, boolean allowed);
}