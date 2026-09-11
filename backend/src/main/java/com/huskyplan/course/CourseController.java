package com.huskyplan.course;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public List<CourseSummary> search(@RequestParam(required = false) String query) {
        List<Course> courses = (query == null || query.isBlank())
                ? courseRepository.findTop20ByOrderByCodeAsc()
                : courseRepository.findTop20ByCodeContainingIgnoreCaseOrTitleContainingIgnoreCaseOrderByCodeAsc(
                    query.trim(), query.trim()
                );
        return courses.stream().map(CourseSummary::from).toList();
    }

    @GetMapping("/{code}")
    public ResponseEntity<CourseDetails> getByCode(@PathVariable String code) {
        return courseRepository.findByCodeIgnoreCase(code)
                .map(course -> ResponseEntity.ok(CourseDetails.from(course)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public record CourseSummary(String code, String title, int credits) {
        static CourseSummary from(Course course) {
            return new CourseSummary(course.getCode(), course.getTitle(), course.getCredits());
        }
    }

    public record RequirementGroup(List<String> options) {}

    public record CourseDetails(
            String code,
            String title,
            int credits,
            String description,
            List<RequirementGroup> prerequisites
    ) {
        static CourseDetails from(Course course) {
            return new CourseDetails(
                    course.getCode(),
                    course.getTitle(),
                    course.getCredits(),
                    course.getDescription(),
                    course.getPrerequisiteGroups().stream()
                            .map(group -> new RequirementGroup(group.getOptions()))
                            .toList()
            );
        }
    }
}
