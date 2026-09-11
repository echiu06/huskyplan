package com.huskyplan.course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CourseRepository extends JpaRepository<Course,Long> {
  Optional<Course> findByCodeIgnoreCase(String code);
  List<Course> findTop20ByCodeContainingIgnoreCaseOrTitleContainingIgnoreCaseOrderByCodeAsc(String a,String b);
  List<Course> findTop20ByOrderByCodeAsc();
}
