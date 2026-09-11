package com.huskyplan.plan;

import com.huskyplan.course.CourseRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/plan")
public class PlanController {

    private final PlanItemRepository planItemRepository;
    private final CourseRepository courseRepository;

    public PlanController(
            PlanItemRepository planItemRepository,
            CourseRepository courseRepository
    ) {
        this.planItemRepository = planItemRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public List<PlanItem> getPlan() {
        return planItemRepository.findAll();
    }

    @PostMapping
    public PlanItem addPlanItem(
            @Valid @RequestBody AddPlanItemRequest request
    ) {

        String courseCode =
                normalize(request.courseCode());

        boolean courseExists =
                courseRepository
                        .findByCodeIgnoreCase(courseCode)
                        .isPresent();

        if (!courseExists) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unknown course: " + courseCode
            );
        }

        boolean alreadyPlanned =
                planItemRepository
                        .findAll()
                        .stream()
                        .anyMatch(item ->
                                normalize(
                                        item.getCourseCode()
                                ).equals(courseCode)
                        );

        if (alreadyPlanned) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    courseCode +
                            " is already in your saved plan."
            );
        }

        PlanItem item =
                new PlanItem(
                        courseCode,
                        request.quarter(),
                        request.year()
                );

        return planItemRepository.save(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlanItem(
            @PathVariable Long id
    ) {

        if (!planItemRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Plan item not found."
            );
        }

        planItemRepository.deleteById(id);
    }

    private String normalize(
            String code
    ) {
        return code
                .trim()
                .toUpperCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }

    public record AddPlanItemRequest(
            @NotBlank
            String courseCode,

            @NotBlank
            String quarter,

            @NotNull
            Integer year
    ) {}
}