package com.huskyplan.plan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PlanItemRepository extends JpaRepository<PlanItem,Long>{ List<PlanItem> findAllByOrderByYearAscQuarterAscCourseCodeAsc(); }
