package com.dh.demo.repository;

import com.dh.demo.entity.PermissionProfiles;
import com.dh.demo.entity.PermissionProfilesId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPermissionProfilesRepository extends JpaRepository<PermissionProfiles, PermissionProfilesId> { }