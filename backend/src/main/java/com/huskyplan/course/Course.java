package com.huskyplan.course;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int credits;

    @Column(length = 1600)
    private String description;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PrerequisiteGroup> prerequisiteGroups = new ArrayList<>();

    protected Course() {}

    public Course(String code, String title, int credits, String description) {
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.description = description;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }
    public String getDescription() { return description; }
    public List<PrerequisiteGroup> getPrerequisiteGroups() { return prerequisiteGroups; }

    public void requireOneOf(String... courseCodes) {
        prerequisiteGroups.add(new PrerequisiteGroup(this, Arrays.asList(courseCodes)));
    }
}
