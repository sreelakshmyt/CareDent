
package com.example.caredent.repository;

import com.example.caredent.bean.CancellationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CancellationRequestRepository extends JpaRepository<CancellationRequest, Long> {
    // Spring Data JPA automatically provides CRUD methods (save, findById, etc.)
    // You can add custom queries here if needed, but for the controller logic,
    // the default JpaRepository methods are sufficient.
}