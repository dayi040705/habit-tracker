package com.example.campusservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitPlanRepository extends JpaRepository<HabitPlan, Long> {

}