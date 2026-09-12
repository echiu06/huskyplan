export type Course = {
  code: string;
  title: string;
  credits: number;
};

export type RequirementGroup = {
  options: string[];
};

export type CourseDetails = Course & {
  description: string;
  prerequisites: RequirementGroup[];
};

export type PlanItem = {
  id: number;
  courseCode: string;
  quarter: string;
  year: number;
};

export type PrerequisiteCheck = {
  courseCode: string;
  eligible: boolean;
  required: RequirementGroup[];
  missing: RequirementGroup[];
};

export type PlanValidationItem = {
  id: number;
  courseCode: string;
  quarter: string;
  year: number;
  eligible: boolean;
  missing: RequirementGroup[];
};

export type AddValidationResult = {
  courseCode: string;
  eligible: boolean;
  missing: RequirementGroup[];
};

export type CoursePathResponse = {
  targetCourse: string;
  alreadyCompleted: boolean;
  completedCourses: string[];
  recommendedPath: string[];
};