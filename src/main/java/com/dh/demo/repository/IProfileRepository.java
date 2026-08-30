package com.dh.demo.repository;

import com.dh.demo.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByIsDefault(Character isDefault);
}