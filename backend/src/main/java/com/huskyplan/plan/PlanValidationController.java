package com.huskyplan.plan;

import com.huskyplan.course.Course;
import com.huskyplan.course.CourseRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/plan")
public class PlanValidationController {

    private final PlanItemRepository planItemRepository;
    private final CourseRepository courseRepository;

    public PlanValidationController(
            PlanItemRepository planItemRepository,
            CourseRepository courseRepository
    ) {
        this.planItemRepository = planItemRepository;
        this.courseRepository = courseRepository;
    }

    @PostMapping("/validate")
    public List<PlanValidationResult> validatePlan(
            @Valid @RequestBody PlanValidationRequest request
    ) {

        Set<String> availableCourses = request.completedCourses()
                .stream()
                .map(this::normalize)
                .collect(Collectors.toSet());

        List<PlanItem> plan = new ArrayList<>(
                planItemRepository.findAll()
        );

        plan.sort(
                Comparator
                        .comparingInt(PlanItem::getYear)
                        .thenComparingInt(
                                item -> quarterNumber(item.getQuarter())
                        )
        );

        List<PlanValidationResult> results = new ArrayList<>();

        int index = 0;

        while (index < plan.size()) {

            PlanItem first = plan.get(index);

            int currentYear = first.getYear();
            String currentQuarter = first.getQuarter();

            List<PlanItem> sameQuarter = new ArrayList<>();

            while (
                    index < plan.size()
                            && plan.get(index).getYear() == currentYear
                            && plan.get(index)
                            .getQuarter()
                            .equalsIgnoreCase(currentQuarter)
            ) {
                sameQuarter.add(plan.get(index));
                index++;
            }

            for (PlanItem item : sameQuarter) {

                Optional<Course> optionalCourse =
                        courseRepository.findByCodeIgnoreCase(
                                item.getCourseCode()
                        );

                if (optionalCourse.isEmpty()) {

                    results.add(
                            new PlanValidationResult(
                                    item.getId(),
                                    item.getCourseCode(),
                                    item.getQuarter(),
                                    item.getYear(),
                                    false,
                                    List.of(
                                            new RequirementGroup(
                                                    List.of("Unknown course")
                                            )
                                    )
                            )
                    );

                    continue;
                }

                Course course = optionalCourse.get();

                List<RequirementGroup> missing =
                        findMissingRequirements(
                                course,
                                availableCourses
                        );

                results.add(
                        new PlanValidationResult(
                                item.getId(),
                                item.getCourseCode(),
                                item.getQuarter(),
                                item.getYear(),
                                missing.isEmpty(),
                                missing
                        )
                );
            }

            /*
             * Courses from this quarter only become available
             * after every course in the quarter has been checked.
             */
            for (PlanItem item : sameQuarter) {
                availableCourses.add(
                        normalize(item.getCourseCode())
                );
            }
        }

        return results;
    }

    /*
     * Checks whether a course can be added to a particular quarter.
     *
     * Only completed courses and courses planned in EARLIER
     * quarters can satisfy prerequisites.
     */
    @PostMapping("/validate-add")
    public AddValidationResult validateAdd(
            @Valid @RequestBody AddValidationRequest request
    ) {

        String courseCode =
                normalize(request.courseCode());

        Optional<Course> optionalCourse =
                courseRepository.findByCodeIgnoreCase(
                        courseCode
                );

        if (optionalCourse.isEmpty()) {

            return new AddValidationResult(
                    courseCode,
                    false,
                    List.of(
                            new RequirementGroup(
                                    List.of("Unknown course")
                            )
                    )
            );
        }

        Set<String> availableCourses =
                request.completedCourses()
                        .stream()
                        .map(this::normalize)
                        .collect(Collectors.toSet());

        List<PlanItem> plan =
                planItemRepository.findAll();

        /*
         * Only courses occurring before the requested
         * quarter count as completed prerequisites.
         */
        for (PlanItem item : plan) {

            if (
                    isEarlier(
                            item.getYear(),
                            item.getQuarter(),
                            request.year(),
                            request.quarter()
                    )
            ) {
                availableCourses.add(
                        normalize(
                                item.getCourseCode()
                        )
                );
            }
        }

        Course course =
                optionalCourse.get();

        List<RequirementGroup> missing =
                findMissingRequirements(
                        course,
                        availableCourses
                );

        return new AddValidationResult(
                courseCode,
                missing.isEmpty(),
                missing
        );
    }

    private List<RequirementGroup>
    findMissingRequirements(
            Course course,
            Set<String> availableCourses
    ) {

        return course.getPrerequisiteGroups()
                .stream()
                .filter(group ->
                        group.getOptions()
                                .stream()
                                .map(this::normalize)
                                .noneMatch(
                                        availableCourses::contains
                                )
                )
                .map(group ->
                        new RequirementGroup(
                                group.getOptions()
                                        .stream()
                                        .map(this::normalize)
                                        .sorted()
                                        .toList()
                        )
                )
                .toList();
    }

    private boolean isEarlier(
            int existingYear,
            String existingQuarter,
            int targetYear,
            String targetQuarter
    ) {

        if (existingYear < targetYear) {
            return true;
        }

        if (existingYear > targetYear) {
            return false;
        }

        return quarterNumber(existingQuarter)
                < quarterNumber(targetQuarter);
    }

    private int quarterNumber(
            String quarter
    ) {

        return switch (
                quarter.trim()
                        .toLowerCase(Locale.ROOT)
        ) {
            case "winter" -> 1;
            case "spring" -> 2;
            case "summer" -> 3;
            case "autumn", "fall" -> 4;
            default -> 5;
        };
    }

    private String normalize(
            String code
    ) {

        return code
                .trim()
                .toUpperCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }

    public record PlanValidationRequest(
            @NotNull
            List<String> completedCourses
    ) {}

    public record AddValidationRequest(
            @NotNull
            String courseCode,

            @NotNull
            String quarter,

            int year,

            @NotNull
            List<String> completedCourses
    ) {}

    public record RequirementGroup(
            List<String> options
    ) {}

    public record PlanValidationResult(
            Long id,
            String courseCode,
            String quarter,
            int year,
            boolean eligible,
            List<RequirementGroup> missing
    ) {}

    public record AddValidationResult(
            String courseCode,
            boolean eligible,
            List<RequirementGroup> missing
    ) {}
}