package com.dh.demo.repository;

import com.dh.demo.entity.SubModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ISubModuleRepository extends JpaRepository<SubModule, Long> {

    Optional<SubModule> findByModuleIdAndName(Long moduleId, String name);
}