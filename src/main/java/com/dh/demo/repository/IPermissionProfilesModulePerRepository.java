package com.dh.demo.repository;

import com.dh.demo.entity.PermissionProfilesModulePer;
import com.dh.demo.entity.PermissionProfilesModulePerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPermissionProfilesModulePerRepository extends JpaRepository<PermissionProfilesModulePer, PermissionProfilesModulePerId> { }