package com.huskyplan.prereq;

import com.huskyplan.course.Course;
import com.huskyplan.course.CourseRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/path")
public class CoursePathController {

    private final CourseRepository courseRepository;

    public CoursePathController(
            CourseRepository courseRepository
    ) {
        this.courseRepository = courseRepository;
    }

    @PostMapping
    public PathResponse findPath(
            @Valid @RequestBody PathRequest request
    ) {

        String target =
                normalize(request.targetCourse());

        Course targetCourse =
                courseRepository
                        .findByCodeIgnoreCase(target)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Unknown target course: " + target
                                )
                        );

        Set<String> completed =
                request.completedCourses()
                        .stream()
                        .map(this::normalize)
                        .collect(Collectors.toSet());

        if (completed.contains(target)) {
            return new PathResponse(
                    targetCourse.getCode(),
                    true,
                    new ArrayList<>(completed),
                    List.of()
            );
        }

        LinkedHashSet<String> path =
                buildPath(
                        target,
                        completed,
                        new HashSet<>()
                );

        return new PathResponse(
                targetCourse.getCode(),
                false,
                new ArrayList<>(completed),
                new ArrayList<>(path)
        );
    }

    private LinkedHashSet<String> buildPath(
            String courseCode,
            Set<String> completed,
            Set<String> visiting
    ) {

        String normalized =
                normalize(courseCode);

        LinkedHashSet<String> result =
                new LinkedHashSet<>();

        if (completed.contains(normalized)) {
            return result;
        }

        if (visiting.contains(normalized)) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Circular prerequisite detected involving "
                            + normalized
            );
        }

        visiting.add(normalized);

        Optional<Course> courseOptional =
                courseRepository
                        .findByCodeIgnoreCase(normalized);

        /*
         * If a prerequisite exists in UW's prerequisite data
         * but is not yet stored in our catalog, treat it as a
         * leaf course instead of failing the entire path.
         */
        if (courseOptional.isEmpty()) {
            result.add(normalized);
            visiting.remove(normalized);
            return result;
        }

        Course course =
                courseOptional.get();

        Set<String> available =
                new HashSet<>(completed);

        for (var group :
                course.getPrerequisiteGroups()) {

            /*
             * If one option in this OR group has already been
             * completed or planned, the requirement is satisfied.
             */
            boolean alreadySatisfied =
                    group.getOptions()
                            .stream()
                            .map(this::normalize)
                            .anyMatch(available::contains);

            if (alreadySatisfied) {
                continue;
            }

            LinkedHashSet<String> bestPath =
                    null;

            String bestOption =
                    null;

            for (String rawOption :
                    group.getOptions()) {

                String option =
                        normalize(rawOption);

                Set<String> branchVisiting =
                        new HashSet<>(visiting);

                LinkedHashSet<String> candidate =
                        buildPath(
                                option,
                                available,
                                branchVisiting
                        );

                if (
                        bestPath == null ||
                        candidate.size()
                                < bestPath.size() ||
                        (
                                candidate.size()
                                        == bestPath.size() &&
                                (
                                    bestOption == null ||
                                    option.compareTo(bestOption) < 0
                                )
                        )
                ) {
                    bestPath = candidate;
                    bestOption = option;
                }
            }

            if (bestPath != null) {

                result.addAll(bestPath);

                available.addAll(bestPath);
            }
        }

        result.add(normalized);

        visiting.remove(normalized);

        return result;
    }

    private String normalize(
            String code
    ) {
        return code
                .trim()
                .toUpperCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }

    public record PathRequest(
            @NotBlank String targetCourse,
            @NotNull List<String> completedCourses
    ) {}

    public record PathResponse(
            String targetCourse,
            boolean alreadyCompleted,
            List<String> completedCourses,
            List<String> recommendedPath
    ) {}
}