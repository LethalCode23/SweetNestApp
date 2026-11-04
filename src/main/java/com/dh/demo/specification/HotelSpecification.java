package com.dh.demo.specification;

import com.dh.demo.dto.HotelFilterDto;
import com.dh.demo.entity.Hotel;
import com.dh.demo.entity.HotelUnit;
import com.dh.demo.entity.Reservation;
import com.dh.demo.entity.enums.ReservationStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class HotelSpecification {

    public static Specification<Hotel> withFilters(HotelFilterDto filter) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getCitName() != null && !filter.getCitName().isBlank()) {

                predicates.add(cb.like(cb.lower(root.get("city").get("citName")),
                        "%" + filter.getCitName().toLowerCase() + "%"));
            }

            if (filter.getMinCost() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("hotCost"), filter.getMinCost()));
            }

            if (filter.getMaxCost() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("hotCost"), filter.getMaxCost()));
            }

            if (filter.getCheckIn() != null && filter.getCheckOut() != null) {

                Subquery<Integer> unitSub = query.subquery(Integer.class);
                Root<HotelUnit> unit = unitSub.from(HotelUnit.class);

                Subquery<Integer> resSub = unitSub.subquery(Integer.class);
                Root<Reservation> res = resSub.from(Reservation.class);

                resSub.select(cb.literal(1)).where(
                        cb.equal(res.get("unit"), unit),
                        cb.notEqual(res.get("resStatus"), ReservationStatus.CANCELLED),
                        cb.lessThan(res.get("resCheckIn"), filter.getCheckOut()),
                        cb.greaterThan(res.get("resCheckOut"), filter.getCheckIn())
                );

                unitSub.select(cb.literal(1)).where(
                        cb.equal(unit.get("hotel"), root),
                        cb.equal(unit.get("uniState"), 'A'),
                        cb.not(cb.exists(resSub))
                );

                predicates.add(cb.exists(unitSub));
            }

            if (filter.getCategoryIds() != null && !filter.getCategoryIds().isEmpty()) {

                query.distinct(true); // distinct for duplicates for many to many
                Join<Object, Object> categoryJoin = root.join("Categories");
                predicates.add(categoryJoin.get("CatSec").in(filter.getCategoryIds()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}