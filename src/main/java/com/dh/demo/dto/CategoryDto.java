package com.dh.demo.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private Integer catSec;
    private String catName;
    private Character catEst;
}