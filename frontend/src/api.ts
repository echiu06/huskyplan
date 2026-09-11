import type {
  AddValidationResult,
  Course,
  CourseDetails,
  PlanItem,
  PlanValidationItem,
  PrerequisiteCheck
} from './types';

const API_URL = 'http://localhost:8080/api';

async function request<T>(
  path: string,
  options?: RequestInit
): Promise<T> {

  const response = await fetch(
    `${API_URL}${path}`,
    {
      headers: {
        'Content-Type': 'application/json',
        ...(options?.headers ?? {})
      },
      ...options
    }
  );

  if (!response.ok) {

    const text = await response.text();

    let errorMessage =
      `Request failed with status ${response.status}`;

    try {
      const data = JSON.parse(text);

      if (
        typeof data.message === 'string' &&
        data.message.trim()
      ) {
        errorMessage = data.message;
      } else if (
        typeof data.error === 'string' &&
        data.error.trim()
      ) {
        errorMessage = data.error;
      }
    } catch {
      if (text.trim()) {
        errorMessage = text;
      }
    }

    throw new Error(errorMessage);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}

export function searchCourses(
  query: string
): Promise<Course[]> {

  const params =
    query.trim().length > 0
      ? `?query=${encodeURIComponent(query.trim())}`
      : '';

  return request<Course[]>(
    `/courses${params}`
  );
}

export function getCourse(
  code: string
): Promise<CourseDetails> {

  return request<CourseDetails>(
    `/courses/${encodeURIComponent(code)}`
  );
}

export function getPlan():
Promise<PlanItem[]> {

  return request<PlanItem[]>(
    '/plan'
  );
}

export function addPlanItem(
  courseCode: string,
  quarter: string,
  year: number
): Promise<PlanItem> {

  return request<PlanItem>(
    '/plan',
    {
      method: 'POST',

      body: JSON.stringify({
        courseCode,
        quarter,
        year
      })
    }
  );
}

export function deletePlanItem(
  id: number
): Promise<void> {

  return request<void>(
    `/plan/${id}`,
    {
      method: 'DELETE'
    }
  );
}

export function checkPrerequisites(
  courseCode: string,
  completedCourses: string[]
): Promise<PrerequisiteCheck> {

  return request<PrerequisiteCheck>(
    '/prerequisites/check',
    {
      method: 'POST',

      body: JSON.stringify({
        courseCode,
        completedCourses
      })
    }
  );
}

export function validatePlan(
  completedCourses: string[]
): Promise<PlanValidationItem[]> {

  return request<PlanValidationItem[]>(
    '/plan/validate',
    {
      method: 'POST',

      body: JSON.stringify({
        completedCourses
      })
    }
  );
}

export function validateAdd(
  courseCode: string,
  quarter: string,
  year: number,
  completedCourses: string[]
): Promise<AddValidationResult> {

  return request<AddValidationResult>(
    '/plan/validate-add',
    {
      method: 'POST',

      body: JSON.stringify({
        courseCode,
        quarter,
        year,
        completedCourses
      })
    }
  );
}