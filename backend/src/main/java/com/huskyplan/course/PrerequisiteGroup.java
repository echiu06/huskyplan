package com.huskyplan.course;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prerequisite_groups")
public class PrerequisiteGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "prerequisite_group_options", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "course_code", nullable = false)
    private List<String> options = new ArrayList<>();

    protected PrerequisiteGroup() {}

    public PrerequisiteGroup(Course course, List<String> options) {
        this.course = course;
        this.options = new ArrayList<>(options);
    }

    public Long getId() { return id; }
    public List<String> getOptions() { return options; }
}
