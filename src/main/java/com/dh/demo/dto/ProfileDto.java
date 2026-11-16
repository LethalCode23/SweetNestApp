package com.dh.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {

    /** Long (no long): en el alta el JSON no trae proId y un primitivo hacia fallar el parseo (500). */
    private Long proId;
    private String proName;
    private Character proState;
    private Character isDefault;
    private Character hasControlAccess;
}