package com.huskyplan.prereq;

import com.huskyplan.course.Course;
import com.huskyplan.course.CourseRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prerequisites")
public class PrerequisiteController {
    private final CourseRepository courseRepository;

    public PrerequisiteController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @PostMapping("/check")
    public ResponseEntity<PrerequisiteCheckResponse> check(
            @Valid @RequestBody PrerequisiteCheckRequest request
    ) {
        return courseRepository.findByCodeIgnoreCase(normalize(request.courseCode()))
                .map(course -> ResponseEntity.ok(buildResponse(course, request.completedCourses())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private PrerequisiteCheckResponse buildResponse(Course course, List<String> completedCourses) {
        Set<String> completed = completedCourses.stream()
                .map(this::normalize)
                .collect(Collectors.toSet());

        List<RequirementGroup> required = course.getPrerequisiteGroups().stream()
                .map(group -> new RequirementGroup(
                        group.getOptions().stream().map(this::normalize).sorted().toList()
                ))
                .toList();

        List<RequirementGroup> missing = course.getPrerequisiteGroups().stream()
                .filter(group -> group.getOptions().stream()
                        .map(this::normalize)
                        .noneMatch(completed::contains))
                .map(group -> new RequirementGroup(
                        group.getOptions().stream().map(this::normalize).sorted().toList()
                ))
                .toList();

        return new PrerequisiteCheckResponse(course.getCode(), missing.isEmpty(), required, missing);
    }

    private String normalize(String code) {
        return code.trim().toUpperCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    public record PrerequisiteCheckRequest(
            @NotBlank String courseCode,
            @NotNull List<String> completedCourses
    ) {}

    public record RequirementGroup(List<String> options) {}

    public record PrerequisiteCheckResponse(
            String courseCode,
            boolean eligible,
            List<RequirementGroup> required,
            List<RequirementGroup> missing
    ) {}
}
