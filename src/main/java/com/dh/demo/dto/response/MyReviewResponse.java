package com.dh.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Estado del usuario del token frente a un hotel: si puede valorar y su resena actual (o null). */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyReviewResponse {

    private boolean canReview;
    private ReviewResponse review;
}
