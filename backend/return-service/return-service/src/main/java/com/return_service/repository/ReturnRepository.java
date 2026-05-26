package com.return_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.return_service.entity.Return;

public interface ReturnRepository
        extends JpaRepository<Return, String> {

    @Query("""
            SELECT COALESCE(SUM(r.quantityReturned), 0)
            FROM ReturnItem r
            WHERE r.requestItemId = :requestItemId
            """)
    int getTotalReturnedQuantity(
            @Param("requestItemId")
            String requestItemId
    );

    List<Return> findByReturnedBy(
            String returnedBy
    );

    List<Return> findByRequestId(
            String requestId
    );
}