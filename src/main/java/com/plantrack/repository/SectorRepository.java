package com.plantrack.repository;

import com.plantrack.model.Sector;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SectorRepository extends JpaRepository<Sector, Long> {

    List<Sector> findByActiveTrueOrderByNameAsc();

    Optional<Sector> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    @Query("""
            SELECT s
            FROM Sector s
            WHERE s.active = true
              AND (
                    LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            ORDER BY s.name ASC
            """)
    List<Sector> searchActiveByKeyword(@Param("keyword") String keyword);
}