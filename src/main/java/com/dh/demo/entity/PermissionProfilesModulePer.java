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
@Table(name = "permission_profiles_module_per")
public class PermissionProfilesModulePer {

    @EmbeddedId
    private PermissionProfilesModulePerId id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "profile_id", referencedColumnName = "profile_id", insertable = false, updatable = false),
            @JoinColumn(name = "module_id", referencedColumnName = "module_id", insertable = false, updatable = false),
            @JoinColumn(name = "submodule_id", referencedColumnName = "submodule_id", insertable = false, updatable = false)
    })
    private PermissionProfilesModule permissionProfilesModule;

    @Column(name = "action_name")
    private String actionName;

    @Column(name = "check_allowed")
    private Character check;
}