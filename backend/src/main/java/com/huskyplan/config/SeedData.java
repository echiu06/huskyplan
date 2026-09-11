package com.huskyplan.config;

import com.huskyplan.course.Course;
import com.huskyplan.course.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SeedData {

    @Bean
    CommandLineRunner seedCourses(CourseRepository repository) {
        return args -> {
            if (repository.count() > 0) return;

            Course c121 = course("CSE 121", "Introduction to Computer Programming I", 4, "Introductory programming and problem solving.");
            Course c122 = course("CSE 122", "Introduction to Computer Programming II", 4, "Continuation of introductory programming.");
            c122.requireOneOf("CSE 121");
            Course c123 = course("CSE 123", "Introduction to Computer Programming III", 4, "Programming abstractions, data structures, and larger programs.");
            c123.requireOneOf("CSE 122");
            Course c143 = course("CSE 143", "Computer Programming II", 5, "Legacy introductory programming course retained as a prerequisite option.");
            Course m126 = course("MATH 126", "Calculus with Analytic Geometry III", 5, "Multivariable calculus.");
            Course m135 = course("MATH 135", "Accelerated Honors Calculus", 5, "Honors calculus.");

            Course c311 = course("CSE 311", "Foundations of Computing I", 4, "Logic, sets, induction, algebraic structures, automata, and computability.");
            c311.requireOneOf("CSE 123", "CSE 143");
            c311.requireOneOf("MATH 126", "MATH 135");

            Course c312 = course("CSE 312", "Foundations of Computing II", 4, "Enumeration, probability, randomness, complexity, and NP-completeness.");
            c312.requireOneOf("CSE 311");

            Course c331 = course("CSE 331", "Software Design and Implementation", 4, "Reliable and maintainable software design, specifications, testing, and event-driven programming.");
            c331.requireOneOf("CSE 123", "CSE 143");

            Course c332 = course("CSE 332", "Data Structures and Parallelism", 4, "Data structures, graph algorithms, asymptotic analysis, multithreading, and parallelism.");
            c332.requireOneOf("CSE 311");

            Course c333 = course("CSE 333", "Systems Programming", 4, "C/C++, memory management, operating-system services, and concurrency.");
            c333.requireOneOf("CSE 351");

            Course c340 = course("CSE 340", "Interaction Programming", 4, "User interfaces, event handling, layout, accessibility, and interactive frameworks.");
            c340.requireOneOf("CSE 123", "CSE 143");

            Course c341 = course("CSE 341", "Programming Languages", 4, "Programming-language concepts, functional programming, typing, scope, and semantics.");
            c341.requireOneOf("CSE 123", "CSE 143");

            Course c344 = course("CSE 344", "Introduction to Data Management", 4, "Database systems, SQL, data modeling, transactions, security, and data management.");
            c344.requireOneOf("CSE 311");

            Course c351 = course("CSE 351", "The Hardware/Software Interface", 4, "Machine representation, assembly, C, memory, processes, and architecture.");
            c351.requireOneOf("CSE 123", "CSE 143");

            Course c369 = course("CSE 369", "Introduction to Digital Design", 3, "Boolean algebra, circuits, FPGAs, and digital design.");
            c369.requireOneOf("CSE 311");

            Course c401 = course("CSE 401", "Introduction to Compiler Construction", 4, "Compiler construction and implementation.");
            c401.requireOneOf("CSE 332"); c401.requireOneOf("CSE 351");

            Course c402 = course("CSE 402", "Design and Implementation of Domain-Specific Languages", 4, "Domain-specific language design and implementation.");
            c402.requireOneOf("CSE 332"); c402.requireOneOf("CSE 351");

            Course c403 = course("CSE 403", "Software Engineering", 4, "Software engineering through a substantial group project.");
            c403.requireOneOf("CSE 331"); c403.requireOneOf("CSE 332");

            Course c440 = course("CSE 440", "Introduction to HCI", 5, "Human-computer interaction, interface design, prototyping, and evaluation.");
            c440.requireOneOf("CSE 332");

            Course c441 = course("CSE 441", "Advanced HCI", 5, "Advanced interface design, prototyping, and evaluation.");
            c441.requireOneOf("CSE 440");

            Course c442 = course("CSE 442", "Data Visualization", 4, "Visual encoding, interaction, graphical perception, and visualization systems.");
            c442.requireOneOf("CSE 332");

            Course c443 = course("CSE 443", "Digital Accessibility", 4, "Accessible computing systems and inclusive design.");
            c443.requireOneOf("CSE 340");

            Course c444 = course("CSE 444", "Database Systems Internals", 4, "Database implementation, query processing, transactions, recovery, and concurrency.");
            c444.requireOneOf("CSE 332"); c444.requireOneOf("CSE 344", "CSE 414");

            Course c451 = course("CSE 451", "Introduction to Operating Systems", 4, "Processes, memory, storage, resource allocation, and operating-system implementation.");
            c451.requireOneOf("CSE 332"); c451.requireOneOf("CSE 333"); c451.requireOneOf("CSE 351");

            Course c452 = course("CSE 452", "Introduction to Distributed Systems", 4, "Distributed systems, cloud computing, storage systems, and distributed caches.");
            c452.requireOneOf("CSE 332"); c452.requireOneOf("CSE 333");

            Course c453 = course("CSE 453", "Datacenter Systems", 4, "Datacenter compute, networking, storage, isolation, provisioning, and performance.");
            c453.requireOneOf("CSE 332"); c453.requireOneOf("CSE 333");

            Course c454 = course("CSE 454", "Advanced Internet and Web Services", 5, "Search, indexing, ranking, scalable web services, and web data mining.");
            c454.requireOneOf("CSE 332"); c454.requireOneOf("CSE 351"); c454.requireOneOf("CSE 331", "CSE 352");

            Course c455 = course("CSE 455", "Computer Vision", 4, "Image analysis, segmentation, motion, reconstruction, recognition, and retrieval.");
            c455.requireOneOf("CSE 332");

            Course c457 = course("CSE 457", "Computer Graphics", 4, "Image synthesis, modeling, transformations, shading, ray tracing, textures, and animation.");
            c457.requireOneOf("CSE 332");

            Course c458 = course("CSE 458", "Computer Animation", 5, "Principles and production methods for computer-generated animation.");
            c458.requireOneOf("CSE 457");

            Course c461 = course("CSE 461", "Introduction to Computer-Communication Networks", 4, "Routing, transport, congestion control, multicast, and network security.");
            c461.requireOneOf("CSE 326", "CSE 332"); c461.requireOneOf("CSE 303", "CSE 333");

            Course c484 = course("CSE 484", "Computer Security", 4, "Software, operating-system, network, web, and applied cryptography security.");
            c484.requireOneOf("CSE 332"); c484.requireOneOf("CSE 351");

            repository.saveAll(List.of(
                c121, c122, c123, c143, m126, m135,
                c311, c312, c331, c332, c333, c340, c341, c344, c351, c369,
                c401, c402, c403, c440, c441, c442, c443, c444,
                c451, c452, c453, c454, c455, c457, c458, c461, c484
            ));
        };
    }

    private Course course(String code, String title, int credits, String description) {
        return new Course(code, title, credits, description);
    }
}
