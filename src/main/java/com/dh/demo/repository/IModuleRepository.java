package com.dh.demo.repository;

import com.dh.demo.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IModuleRepository extends JpaRepository<Module, Long> { }