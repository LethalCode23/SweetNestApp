package com.dh.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "permission_profiles_module")
public class PermissionProfilesModule implements Serializable {

    @EmbeddedId
    private PermissionProfilesModuleId id;

    @ManyToOne
    @MapsId("profileId")
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private Profile profile;

    @ManyToOne
    @MapsId("moduleId")
    @JoinColumn(name = "module_id", insertable = false, updatable = false)
    private Module module;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "profile_id", referencedColumnName = "profile_id", insertable = false, updatable = false),
            @JoinColumn(name = "module_id", referencedColumnName = "module_id", insertable = false, updatable = false)
    })
    private PermissionProfiles permissionProfiles;
}