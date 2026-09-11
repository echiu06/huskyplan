package com.huskyplan.plan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:huskyplan-test;DB_CLOSE_DELAY=-1;NON_KEYWORDS=YEAR",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class PlanIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlanItemRepository planItemRepository;

    @BeforeEach
    void clearPlan() {
        planItemRepository.deleteAll();
    }

    /*
     * CSE 332 requires CSE 311.
     *
     * If CSE 311 has not been completed or planned earlier,
     * the course should be rejected.
     */
    @Test
    void cse332IsBlockedWithoutCse311()
            throws Exception {

        mockMvc.perform(
                        post("/api/plan/validate-add")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "courseCode": "CSE 332",
                                          "quarter": "Autumn",
                                          "year": 2026,
                                          "completedCourses": [
                                            "CSE 121",
                                            "CSE 122",
                                            "CSE 123",
                                            "MATH 126"
                                          ]
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.eligible")
                                .value(false)
                )
                .andExpect(
                        jsonPath("$.missing[*].options[*]")
                                .value(
                                        hasItem("CSE 311")
                                )
                );
    }

    /*
     * CSE 311 requires:
     *
     * CSE 123 OR CSE 143
     *
     * AND
     *
     * MATH 126 OR MATH 135
     */
    @Test
    void cse311IsAllowedWhenPrerequisitesAreCompleted()
            throws Exception {

        mockMvc.perform(
                        post("/api/plan/validate-add")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "courseCode": "CSE 311",
                                          "quarter": "Autumn",
                                          "year": 2026,
                                          "completedCourses": [
                                            "CSE 123",
                                            "MATH 126"
                                          ]
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.eligible")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.missing")
                                .isEmpty()
                );
    }

    /*
     * A course planned in an EARLIER quarter
     * should satisfy a later prerequisite.
     *
     * Autumn 2026:
     * CSE 311
     *
     * Winter 2027:
     * CSE 332
     */
    @Test
    void earlierPlannedCourseSatisfiesLaterPrerequisite()
            throws Exception {

        addCourse(
                "CSE 311",
                "Autumn",
                2026
        );

        mockMvc.perform(
                        post("/api/plan/validate-add")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "courseCode": "CSE 332",
                                          "quarter": "Winter",
                                          "year": 2027,
                                          "completedCourses": [
                                            "CSE 123",
                                            "MATH 126"
                                          ]
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.eligible")
                                .value(true)
                );
    }

    /*
     * Future courses cannot satisfy past prerequisites.
     *
     * Autumn 2026:
     * CSE 332
     *
     * Winter 2027:
     * CSE 311
     *
     * CSE 332 must still be rejected.
     */
    @Test
    void futureCourseDoesNotSatisfyEarlierPrerequisite()
            throws Exception {

        addCourse(
                "CSE 311",
                "Winter",
                2027
        );

        mockMvc.perform(
                        post("/api/plan/validate-add")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "courseCode": "CSE 332",
                                          "quarter": "Autumn",
                                          "year": 2026,
                                          "completedCourses": [
                                            "CSE 123",
                                            "MATH 126"
                                          ]
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.eligible")
                                .value(false)
                )
                .andExpect(
                        jsonPath("$.missing[*].options[*]")
                                .value(
                                        hasItem("CSE 311")
                                )
                );
    }

    /*
     * The same course should not appear twice
     * anywhere in the saved plan.
     */
    @Test
    void duplicateCourseIsRejected()
            throws Exception {

        addCourse(
                "CSE 351",
                "Autumn",
                2026
        );

        mockMvc.perform(
                        post("/api/plan")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "courseCode": "CSE 351",
                                          "quarter": "Winter",
                                          "year": 2027
                                        }
                                        """)
                )
                .andExpect(
                        status().isConflict()
                );
    }

    private void addCourse(
            String courseCode,
            String quarter,
            int year
    ) throws Exception {

        mockMvc.perform(
                        post("/api/plan")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "courseCode": "%s",
                                          "quarter": "%s",
                                          "year": %d
                                        }
                                        """.formatted(
                                        courseCode,
                                        quarter,
                                        year
                                ))
                )
                .andExpect(
                        status().isOk()
                );
    }
}