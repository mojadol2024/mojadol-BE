package com.gnu.mojadol.specification;

import com.gnu.mojadol.entity.Board;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BoardSpecification {

    public static Specification<Board> Search(String keyword) {
        return(root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.notEqual(root.get("report"), 2));

            if (keyword != null && !keyword.isEmpty()) {
                Predicate dogNamePredicate = criteriaBuilder.like(root.get("dogName"), "%" + keyword + "%");
                Predicate nicknamePredicate = criteriaBuilder.like(root.join("user").get("nickname"), "%" + keyword + "%");
                predicates.add(criteriaBuilder.or(dogNamePredicate, nicknamePredicate));
            }

            query.orderBy(criteriaBuilder.desc(root.get("postDate"))); // 예: 게시 날짜로 내림차순 정렬
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}