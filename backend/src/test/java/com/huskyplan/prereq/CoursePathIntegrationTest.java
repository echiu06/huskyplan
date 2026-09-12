package com.huskyplan.prereq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huskyplan.course.Course;
import com.huskyplan.course.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:huskyplan-path-test;DB_CLOSE_DELAY=-1;NON_KEYWORDS=YEAR",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class CoursePathIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();

        Course c123 = course(
                "CSE 123",
                "Introduction to Computer Programming III"
        );

        Course math126 = course(
                "MATH 126",
                "Calculus with Analytic Geometry III"
        );

        Course c311 = course(
                "CSE 311",
                "Foundations of Computing I"
        );
        c311.requireOneOf("CSE 123");
        c311.requireOneOf("MATH 126");

        Course c332 = course(
                "CSE 332",
                "Data Structures and Parallelism"
        );
        c332.requireOneOf("CSE 311");

        Course c351 = course(
                "CSE 351",
                "The Hardware/Software Interface"
        );
        c351.requireOneOf("CSE 123");

        Course c333 = course(
                "CSE 333",
                "Systems Programming"
        );
        c333.requireOneOf("CSE 351");

        Course c451 = course(
                "CSE 451",
                "Introduction to Operating Systems"
        );
        c451.requireOneOf("CSE 332");
        c451.requireOneOf("CSE 333");
        c451.requireOneOf("CSE 351");

        courseRepository.saveAll(
                List.of(
                        c123,
                        math126,
                        c311,
                        c332,
                        c351,
                        c333,
                        c451
                )
        );
    }

    @Test
    void buildsRecursivePathTo451() throws Exception {

        String body = json(
                "CSE 451",
                List.of(
                        "CSE 123",
                        "CSE 351"
                )
        );

        mockMvc.perform(
                        post("/api/path")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.targetCourse")
                                .value("CSE 451")
                )
                .andExpect(
                        jsonPath("$.alreadyCompleted")
                                .value(false)
                )
                .andExpect(
                        jsonPath("$.recommendedPath[0]")
                                .value("MATH 126")
                )
                .andExpect(
                        jsonPath("$.recommendedPath[1]")
                                .value("CSE 311")
                )
                .andExpect(
                        jsonPath("$.recommendedPath[2]")
                                .value("CSE 332")
                )
                .andExpect(
                        jsonPath("$.recommendedPath[3]")
                                .value("CSE 333")
                )
                .andExpect(
                        jsonPath("$.recommendedPath[4]")
                                .value("CSE 451")
                );
    }

    @Test
    void skipsCompletedPrerequisites() throws Exception {

        String body = json(
                "CSE 451",
                List.of(
                        "CSE 123",
                        "MATH 126",
                        "CSE 311",
                        "CSE 351"
                )
        );

        mockMvc.perform(
                        post("/api/path")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.recommendedPath[0]")
                                .value("CSE 332")
                )
                .andExpect(
                        jsonPath("$.recommendedPath[1]")
                                .value("CSE 333")
                )
                .andExpect(
                        jsonPath("$.recommendedPath[2]")
                                .value("CSE 451")
                );
    }

    @Test
    void returnsEmptyPathWhenTargetAlreadyCompleted()
            throws Exception {

        String body = json(
                "CSE 451",
                List.of("CSE 451")
        );

        mockMvc.perform(
                        post("/api/path")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.alreadyCompleted")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.recommendedPath")
                                .isEmpty()
                );
    }

    @Test
    void unknownTargetReturns404()
            throws Exception {

        String body = json(
                "CSE 999",
                List.of("CSE 123")
        );

        mockMvc.perform(
                        post("/api/path")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isNotFound());
    }

    private Course course(
            String code,
            String title
    ) {
        return new Course(
                code,
                title,
                4,
                "Test course"
        );
    }

    private String json(
            String targetCourse,
            List<String> completedCourses
    ) throws Exception {

        return objectMapper.writeValueAsString(
                Map.of(
                        "targetCourse",
                        targetCourse,
                        "completedCourses",
                        completedCourses
                )
        );
    }
}