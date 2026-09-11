package com.huskyplan.config;

import com.huskyplan.course.Course;
import com.huskyplan.course.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SeedData {

    @Bean
    CommandLineRunner seedCourses(
            CourseRepository repository
    ) {
        return args -> {

            List<CourseSeed> seeds =
                    new ArrayList<>();

            seeds.add(course(
                    "CSE 110",
                    "Computer Science Principles",
                    5,
                    "Fundamental concepts of computer science and computational thinking."
            ));

            seeds.add(course(
                    "CSE 112",
                    "Advanced Placement (AP) Computer Science A",
                    4,
                    "Course credit awarded based on qualifying AP Computer Science A examination performance."
            ));

            seeds.add(course(
                    "CSE 121",
                    "Introduction to Computer Programming I",
                    4,
                    "Introduction to computer programming and computational problem solving."
            ));

            seeds.add(course(
                    "CSE 122",
                    "Introduction to Computer Programming II",
                    4,
                    "Programming design, decomposition, data structures, and abstraction."
            ));

            seeds.add(course(
                    "CSE 123",
                    "Introduction to Computer Programming III",
                    4,
                    "Data structures, algorithms, recursion, object-oriented programming, and runtime analysis."
            ));

            seeds.add(course(
                    "CSE 131",
                    "Science and Art of Digital Photography",
                    4,
                    "Fundamentals of digital photography, computational imaging, composition, and design."
            ));

            seeds.add(
                    course(
                            "CSE 143",
                            "Computer Programming II",
                            5,
                            "Legacy programming course covering abstraction, recursion, collections, and data structures."
                    )
                            .requires("CSE 142")
            );

            seeds.add(
                    course(
                            "CSE 154",
                            "Web Programming",
                            5,
                            "Languages, tools, and techniques used to build interactive and dynamic web applications."
                    )
                            .requires(
                                    "CSE 122",
                                    "CSE 123",
                                    "CSE 142",
                                    "CSE 143",
                                    "CSE 160",
                                    "CSE 163"
                            )
            );

            seeds.add(course(
                    "CSE 160",
                    "Data Programming",
                    4,
                    "Introduction to programming using real-world data analysis and visualization."
            ));

            seeds.add(
                    course(
                            "CSE 163",
                            "Intermediate Data Programming",
                            4,
                            "Intermediate programming with data, software libraries, efficiency, and medium-scale programs."
                    )
                            .requires(
                                    "CSE 122",
                                    "CSE 123",
                                    "CSE 142",
                                    "CSE 143",
                                    "CSE 160"
                            )
            );

            seeds.add(course(
                    "CSE 170",
                    "Artificial Intelligence Principles, Applications, and Impacts",
                    4,
                    "Introduction to AI concepts, applications, responsible use, and societal impacts."
            ));

            seeds.add(course(
                    "CSE 180",
                    "Introduction to Data Science",
                    4,
                    "Introduction to data collection, management, visualization, inference, and machine learning."
            ));

            seeds.add(course(
                    "CSE 190",
                    "Current Topics in Computer Science and Engineering",
                    1,
                    "Current topics in computer science and engineering."
            ));

            seeds.add(course(
                    "CSE 195",
                    "Allen Scholars Bridge Program",
                    5,
                    "Technical fundamentals and preparation for university-level computing coursework."
            ));

            seeds.add(course(
                    "CSE 196",
                    "Allen Scholars Seminar",
                    1,
                    "Academic skill building and preparation for success in computing."
            ));

            seeds.add(course(
                    "CSE 197",
                    "Problem Solving for Computer Science and Engineering",
                    1,
                    "Collaborative problem-solving practice in computer science and engineering."
            ));

            seeds.add(course(
                    "CSE 301",
                    "CSE Internship Education",
                    1,
                    "Internship practicum integrating classroom theory with professional computing experience."
            ));

            seeds.add(course(
                    "MATH 126",
                    "Calculus with Analytic Geometry III",
                    5,
                    "Multivariable calculus and analytic geometry."
            ));

            seeds.add(course(
                    "MATH 135",
                    "Accelerated Honors Calculus",
                    5,
                    "Accelerated honors calculus."
            ));

            seeds.add(
                    course(
                            "CSE 311",
                            "Foundations of Computing I",
                            4,
                            "Logic, sets, induction, algebraic structures, automata, and computability."
                    )
                            .requires(
                                    "CSE 123",
                                    "CSE 143"
                            )
                            .requires(
                                    "MATH 126",
                                    "MATH 135"
                            )
            );

            seeds.add(
                    course(
                            "CSE 312",
                            "Foundations of Computing II",
                            4,
                            "Enumeration, probability, randomness, complexity, and NP-completeness."
                    )
                            .requires("CSE 311")
            );

            seeds.add(
                    course(
                            "CSE 331",
                            "Software Design and Implementation",
                            4,
                            "Software design, specifications, testing, correctness, and event-driven programming."
                    )
                            .requires(
                                    "CSE 123",
                                    "CSE 143"
                            )
            );

            seeds.add(
                    course(
                            "CSE 332",
                            "Data Structures and Parallelism",
                            4,
                            "Data structures, graph algorithms, asymptotic analysis, multithreading, and parallelism."
                    )
                            .requires("CSE 311")
            );

            seeds.add(
                    course(
                            "CSE 333",
                            "Systems Programming",
                            4,
                            "C and C++, memory management, operating-system services, and concurrent programming."
                    )
                            .requires("CSE 351")
            );

            seeds.add(
                    course(
                            "CSE 340",
                            "Interaction Programming",
                            4,
                            "User-interface implementation, event handling, layout, accessibility, and interactive frameworks."
                    )
                            .requires(
                                    "CSE 123",
                                    "CSE 143"
                            )
            );

            seeds.add(
                    course(
                            "CSE 341",
                            "Programming Languages",
                            4,
                            "Programming-language concepts including abstraction, typing, scope, semantics, and functional programming."
                    )
                            .requires(
                                    "CSE 123",
                                    "CSE 143"
                            )
            );

            seeds.add(
                    course(
                            "CSE 344",
                            "Introduction to Data Management",
                            4,
                            "Database systems, SQL, modeling, transactions, security, and data management."
                    )
                            .requires("CSE 311")
            );

            seeds.add(
                    course(
                            "CSE 351",
                            "The Hardware/Software Interface",
                            4,
                            "Machine representation, assembly, C, memory, processes, and computer architecture."
                    )
                            .requires(
                                    "CSE 123",
                                    "CSE 143"
                            )
            );

            seeds.add(
                    course(
                            "CSE 369",
                            "Introduction to Digital Design",
                            3,
                            "Boolean algebra, combinational and sequential circuits, FPGAs, and digital design."
                    )
                            .requires("CSE 311")
            );

            seeds.add(
                    course(
                            "CSE 371",
                            "Design of Digital Circuits and Systems",
                            5,
                            "Digital system design using hardware-description languages and modern design tools."
                    )
                            .requires(
                                    "E E 205",
                                    "E E 215"
                            )
                            .requires(
                                    "E E 271",
                                    "CSE 369"
                            )
            );

            seeds.add(
                    course(
                            "CSE 373",
                            "Data Structures and Algorithms",
                            4,
                            "Fundamental algorithms and data structures for non-majors."
                    )
                            .requires(
                                    "CSE 123",
                                    "CSE 143"
                            )
            );

            seeds.add(
                    course(
                            "CSE 401",
                            "Introduction to Compiler Construction",
                            4,
                            "Compiler construction and implementation."
                    )
                            .requires("CSE 332")
                            .requires("CSE 351")
            );

            seeds.add(
                    course(
                            "CSE 403",
                            "Software Engineering",
                            4,
                            "Software engineering through the development of a substantial group project."
                    )
                            .requires("CSE 331")
                            .requires("CSE 332")
            );

            seeds.add(
                    course(
                            "CSE 440",
                            "Introduction to HCI",
                            5,
                            "Human-computer interaction, interface design, prototyping, and evaluation."
                    )
                            .requires("CSE 332")
            );

            seeds.add(
                    course(
                            "CSE 441",
                            "Advanced HCI",
                            5,
                            "Advanced human-computer interaction, prototyping, design, and evaluation."
                    )
                            .requires("CSE 440")
            );

            seeds.add(
                    course(
                            "CSE 442",
                            "Data Visualization",
                            4,
                            "Visual encoding, graphical perception, interaction, and visualization systems."
                    )
                            .requires("CSE 332")
            );

            seeds.add(
                    course(
                            "CSE 443",
                            "Digital Accessibility",
                            4,
                            "Accessible computing systems and inclusive interaction design."
                    )
                            .requires("CSE 340")
            );

            seeds.add(
                    course(
                            "CSE 444",
                            "Database Systems Internals",
                            4,
                            "Database implementation, query processing, transactions, recovery, and concurrency."
                    )
                            .requires("CSE 332")
                            .requires(
                                    "CSE 344",
                                    "CSE 414"
                            )
            );

            seeds.add(
                    course(
                            "CSE 451",
                            "Introduction to Operating Systems",
                            4,
                            "Processes, memory, storage, resource allocation, and operating-system implementation."
                    )
                            .requires("CSE 332")
                            .requires("CSE 333")
                            .requires("CSE 351")
            );

            seeds.add(
                    course(
                            "CSE 452",
                            "Introduction to Distributed Systems",
                            4,
                            "Distributed systems, cloud computing, storage, and distributed services."
                    )
                            .requires("CSE 332")
                            .requires("CSE 333")
            );

            seeds.add(
                    course(
                            "CSE 453",
                            "Datacenter Systems",
                            4,
                            "Datacenter computing, networking, storage, isolation, provisioning, and performance."
                    )
                            .requires("CSE 332")
                            .requires("CSE 333")
            );

            seeds.add(
                    course(
                            "CSE 454",
                            "Advanced Internet and Web Services",
                            5,
                            "Search, indexing, ranking, scalable web services, and web data mining."
                    )
                            .requires("CSE 332")
                            .requires("CSE 351")
                            .requires(
                                    "CSE 331",
                                    "CSE 352"
                            )
            );

            seeds.add(
                    course(
                            "CSE 455",
                            "Computer Vision",
                            4,
                            "Image analysis, segmentation, reconstruction, recognition, and retrieval."
                    )
                            .requires("CSE 332")
            );

            seeds.add(
                    course(
                            "CSE 457",
                            "Computer Graphics",
                            4,
                            "Modeling, transformations, shading, ray tracing, textures, and animation."
                    )
                            .requires("CSE 332")
            );

            seeds.add(
                    course(
                            "CSE 458",
                            "Computer Animation",
                            5,
                            "Principles and production techniques for computer-generated animation."
                    )
                            .requires("CSE 457")
            );

            seeds.add(
                    course(
                            "CSE 461",
                            "Introduction to Computer-Communication Networks",
                            4,
                            "Routing, transport protocols, congestion control, multicast, and network security."
                    )
                            .requires(
                                    "CSE 326",
                                    "CSE 332"
                            )
                            .requires(
                                    "CSE 303",
                                    "CSE 333"
                            )
            );

            seeds.add(
                    course(
                            "CSE 484",
                            "Computer Security",
                            4,
                            "Software, operating-system, network, web, and applied cryptography security."
                    )
                            .requires("CSE 332")
                            .requires("CSE 351")
            );

            for (CourseSeed seed : seeds) {

                Course course =
                        repository
                                .findByCodeIgnoreCase(seed.code)
                                .orElseGet(
                                        () -> new Course(
                                                seed.code,
                                                seed.title,
                                                seed.credits,
                                                seed.description
                                        )
                                );

                course.updateDetails(
                        seed.title,
                        seed.credits,
                        seed.description
                );

                course.clearPrerequisites();

                for (String[] group :
                        seed.prerequisites) {

                    course.requireOneOf(group);
                }

                repository.save(course);
            }
        };
    }

    private CourseSeed course(
            String code,
            String title,
            int credits,
            String description
    ) {
        return new CourseSeed(
                code,
                title,
                credits,
                description
        );
    }

    private static class CourseSeed {

        private final String code;
        private final String title;
        private final int credits;
        private final String description;

        private final List<String[]>
                prerequisites =
                new ArrayList<>();

        private CourseSeed(
                String code,
                String title,
                int credits,
                String description
        ) {
            this.code = code;
            this.title = title;
            this.credits = credits;
            this.description = description;
        }

        private CourseSeed requires(
                String... options
        ) {
            prerequisites.add(options);
            return this;
        }
    }
}