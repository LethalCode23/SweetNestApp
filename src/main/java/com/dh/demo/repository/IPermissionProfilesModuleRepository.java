package com.dh.demo.repository;

import com.dh.demo.entity.PermissionProfilesModule;
import com.dh.demo.entity.PermissionProfilesModuleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPermissionProfilesModuleRepository extends JpaRepository<PermissionProfilesModule, PermissionProfilesModuleId> { }