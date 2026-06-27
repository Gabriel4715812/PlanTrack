package com.plantrack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.plantrack.model.Position;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

    List<Position> findByActiveTrueOrderByNameAsc();

    List<Position> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

    List<Position> findByActiveTrueAndNameContainingIgnoreCaseOrderByNameAsc(String name);

    List<Position> findByPartId(Long partId);

    List<Position> findByPartIdAndActiveTrue(Long partId);

    Optional<Position> findByCode(Integer code);

    boolean existsByCode(Integer code);

    boolean existsByCodeAndIdNot(Integer code, Long id);
}