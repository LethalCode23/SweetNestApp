package com.dh.demo.specification;

import com.dh.demo.dto.HotelFilterDto;
import com.dh.demo.entity.Hotel;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
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

            if (filter.getCategoryIds() != null && !filter.getCategoryIds().isEmpty()) {

                query.distinct(true); // distinct for duplicates for many to many
                Join<Object, Object> categoryJoin = root.join("Categories");
                predicates.add(categoryJoin.get("CatSec").in(filter.getCategoryIds()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}