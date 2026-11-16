package com.dh.demo.service.impl;

import com.dh.demo.dto.request.ReviewRequest;
import com.dh.demo.dto.response.HotelReviewsResponse;
import com.dh.demo.dto.response.MyReviewResponse;
import com.dh.demo.dto.response.ReviewResponse;
import com.dh.demo.entity.Hotel;
import com.dh.demo.entity.Review;
import com.dh.demo.entity.User;
import com.dh.demo.entity.enums.ReservationStatus;
import com.dh.demo.exception.ForbiddenOperationException;
import com.dh.demo.exception.HotelNotFoundException;
import com.dh.demo.exception.ReviewNotAllowedException;
import com.dh.demo.repository.HotelRepository;
import com.dh.demo.repository.IUserRepository;
import com.dh.demo.repository.ReservationRepository;
import com.dh.demo.repository.ReviewRepository;
import com.dh.demo.service.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * HU #28. Regla: puede valorar quien tenga en el hotel una reserva CONFIRMED o COMPLETED
 * cuyo check-out ya paso. Una valoracion por usuario + hotel; volver a enviar la actualiza.
 * Cada guardado recalcula hotRatingAvg / hotRatingCount del hotel.
 */
@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {

    /** Estados que cuentan como estadia realizada (COMPLETED hoy no lo asigna ningun proceso). */
    private static final List<ReservationStatus> FINISHED_STATES =
            List.of(ReservationStatus.CONFIRMED, ReservationStatus.COMPLETED);

    private static final int FIRST_PAGE_SIZE = 5;

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final HotelRepository hotelRepository;
    private final IUserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public HotelReviewsResponse findByHotel(Integer hotSec, Pageable pageable) {
        Hotel hotel = findHotel(hotSec);
        return buildSummary(hotel, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public MyReviewResponse findMine(Integer hotSec, String userEmail) {
        findHotel(hotSec);
        User user = findUser(userEmail);

        ReviewResponse mine = reviewRepository.findByUserUserSecAndHotelHotSec(user.getUserSec(), hotSec)
                .map(this::toDto)
                .orElse(null);

        return MyReviewResponse.builder()
                .canReview(hasFinishedStay(user, hotSec))
                .review(mine)
                .build();
    }

    @Override
    @Transactional
    public HotelReviewsResponse upsert(Integer hotSec, String userEmail, ReviewRequest request) {
        Hotel hotel = findHotel(hotSec);
        User user = findUser(userEmail);

        if (!hasFinishedStay(user, hotSec)) {
            throw new ReviewNotAllowedException(
                    "Solo puedes valorar un alojamiento despues de terminar una estadia en el");
        }

        Review review = reviewRepository.findByUserUserSecAndHotelHotSec(user.getUserSec(), hotSec)
                .orElseGet(() -> {
                    Review r = new Review();
                    r.setUser(user);
                    r.setHotel(hotel);
                    return r;
                });

        review.setRevStars(request.getStars());
        String comment = request.getComment() == null ? null : request.getComment().trim();
        review.setRevComment(comment == null || comment.isEmpty() ? null : comment);
        reviewRepository.saveAndFlush(review);

        recalculateRating(hotel);

        return buildSummary(hotel, PageRequest.of(0, FIRST_PAGE_SIZE));
    }

    @Override
    @Transactional
    public void recalculateRating(Hotel hotel) {
        Object[] stats = reviewRepository.findStatsByHotel(hotel.getHotSec()).get(0);
        Double avg = (Double) stats[0];
        long count = (Long) stats[1];

        hotel.setHotRatingAvg(avg == null ? 0 : Math.round(avg * 10) / 10.0);
        hotel.setHotRatingCount((int) count);
        hotelRepository.save(hotel);
    }

    private HotelReviewsResponse buildSummary(Hotel hotel, Pageable pageable) {
        Long[] byStars = {0L, 0L, 0L, 0L, 0L};
        for (Object[] row : reviewRepository.countByStars(hotel.getHotSec())) {
            int stars = ((Number) row[0]).intValue();
            if (stars >= 1 && stars <= 5) {
                byStars[5 - stars] = (Long) row[1];
            }
        }

        return HotelReviewsResponse.builder()
                .ratingAvg(hotel.getHotRatingAvg())
                .ratingCount(hotel.getHotRatingCount())
                .distribution(new ArrayList<>(List.of(byStars)))
                .reviews(reviewRepository
                        .findByHotelHotSecOrderByRevUpdatedAtDescRevSecDesc(hotel.getHotSec(), pageable)
                        .map(this::toDto))
                .build();
    }

    private boolean hasFinishedStay(User user, Integer hotSec) {
        return reservationRepository.existsByUserUserSecAndUnitHotelHotSecAndResStatusInAndResCheckOutLessThanEqual(
                user.getUserSec(), hotSec, FINISHED_STATES, LocalDate.now());
    }

    private ReviewResponse toDto(Review review) {
        return ReviewResponse.builder()
                .revSec(review.getRevSec())
                .stars(review.getRevStars())
                .comment(review.getRevComment())
                .userName(displayName(review.getUser()))
                .date(review.getRevUpdatedAt())
                .build();
    }

    /** "Juan P.": nombre + inicial del apellido, para no exponer el nombre completo. */
    private String displayName(User user) {
        String first = user.getUserFirstName() == null ? "" : user.getUserFirstName().trim();
        String last = user.getUserLastName() == null ? "" : user.getUserLastName().trim();
        String name = last.isEmpty() ? first : first + " " + last.charAt(0) + ".";
        return name.isBlank() ? "Huésped" : name;
    }

    private Hotel findHotel(Integer hotSec) {
        return hotelRepository.findById(hotSec)
                .orElseThrow(() -> new HotelNotFoundException("Hotel no encontrado con id: " + hotSec));
    }

    private User findUser(String userEmail) {
        return userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new ForbiddenOperationException("No se pudo identificar al usuario"));
    }
}
