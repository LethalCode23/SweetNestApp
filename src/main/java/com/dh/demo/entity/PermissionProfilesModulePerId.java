package com.dh.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionProfilesModulePerId implements Serializable {

    @Column(name = "profile_id")
    private Long profileId;

    @Column(name = "module_id")
    private Long moduleId;

    @Column(name = "submodule_id")
    private Long submoduleId;

    @Column(name = "code")
    private Character code;
}