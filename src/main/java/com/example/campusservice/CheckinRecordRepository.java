package com.example.campusservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CheckinRecordRepository extends JpaRepository<CheckinRecord, Long> {

    List<CheckinRecord> findByPlanId(Long planId);

    CheckinRecord findByPlanIdAndCheckinDate(Long planId, LocalDate checkinDate);

    List<CheckinRecord> findByPlanIdAndCheckinDateAfterOrderByCheckinDateDesc(Long planId, LocalDate date);
}