package com.huskyplan.plan;
import jakarta.persistence.*;
@Entity @Table(name="plan_items")
public class PlanItem {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false) private String courseCode;
  @Column(nullable=false) private String quarter;
  @Column(nullable=false) private int year;
  protected PlanItem(){}
  public PlanItem(String courseCode,String quarter,int year){this.courseCode=courseCode;this.quarter=quarter;this.year=year;}
  public Long getId(){return id;} public String getCourseCode(){return courseCode;} public String getQuarter(){return quarter;} public int getYear(){return year;}
}
