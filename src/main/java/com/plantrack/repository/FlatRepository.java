package com.plantrack.repository;

import java.util.List;
import java.util.Optional;

import com.plantrack.model.Flat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FlatRepository extends JpaRepository<Flat, Long> {

    List<Flat> findByActiveTrueOrderByNameAsc();

    Optional<Flat> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    List<Flat> findByPositionId(Long positionId);

    List<Flat> findByPositionIdAndActiveTrue(Long positionId);

    List<Flat> findByDesignerId(Long designerId);

    @Query("""
            SELECT f
            FROM Flat f
            WHERE f.active = true
              AND (
                    LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(f.code) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(f.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            ORDER BY f.name ASC
            """)
    List<Flat> searchActiveByKeyword(@Param("keyword") String keyword);
}