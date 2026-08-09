package com.dh.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "permission_profiles")
public class PermissionProfiles {

    @EmbeddedId
    private PermissionProfilesId id;

    @ManyToOne
    @MapsId("profileId")
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToOne
    @MapsId("moduleId")
    @JoinColumn(name = "module_id")
    private Module module;

    @Column(name = "entry_allowed")
    private Character entryAllowed;
}