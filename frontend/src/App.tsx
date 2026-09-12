import {
  FormEvent,
  useEffect,
  useMemo,
  useState
} from 'react';

import {
  addPlanItem,
  checkPrerequisites,
  deletePlanItem,
  findCoursePath,
  getCourse,
  getPlan,
  searchCourses,
  validateAdd,
  validatePlan
} from './api';

import type {
  Course,
  CourseDetails,
  CoursePathResponse,
  PlanItem,
  PlanValidationItem,
  PrerequisiteCheck
} from './types';

const quarterOrder = [
  'Winter',
  'Spring',
  'Summer',
  'Autumn'
];

function formatRequirement(
  group: { options: string[] }
): string {
  return group.options.length > 1
    ? `(${group.options.join(' OR ')})`
    : group.options[0];
}

function parseCompletedCourses(
  value: string
): string[] {
  return value
    .split(',')
    .map(course => course.trim())
    .filter(Boolean);
}

export default function App() {
  const [query, setQuery] =
    useState('');

  const [courses, setCourses] =
    useState<Course[]>([]);

  const [plan, setPlan] =
    useState<PlanItem[]>([]);

  const [
    planValidation,
    setPlanValidation
  ] =
    useState<PlanValidationItem[]>([]);

  const [quarter, setQuarter] =
    useState('Autumn');

  const [year, setYear] =
    useState(2026);

  const [
    completedInput,
    setCompletedInput
  ] = useState(() => {
    return (
      localStorage.getItem(
        'huskyplan.completedCourses'
      ) ??
      'CSE 121, CSE 122, CSE 123'
    );
  });

  const [selected, setSelected] =
    useState<Course | null>(null);

  const [check, setCheck] =
    useState<PrerequisiteCheck | null>(
      null
    );

  const [details, setDetails] =
    useState<CourseDetails | null>(
      null
    );

  const [
    detailsLoading,
    setDetailsLoading
  ] =
    useState(false);

  const [
    searchLoading,
    setSearchLoading
  ] =
    useState(false);

  const [pathTarget, setPathTarget] =
    useState('');

  const [
    coursePath,
    setCoursePath
  ] =
    useState<CoursePathResponse | null>(
      null
    );

  const [
    pathLoading,
    setPathLoading
  ] =
    useState(false);

  const [error, setError] =
    useState('');

  useEffect(() => {
    void loadCourses('');
    void refreshPlan();
  }, []);

  useEffect(() => {
    localStorage.setItem(
      'huskyplan.completedCourses',
      completedInput
    );
  }, [completedInput]);

  useEffect(() => {
    if (plan.length === 0) {
      setPlanValidation([]);
      return;
    }

    void refreshPlanValidation();
  }, [plan, completedInput]);

  useEffect(() => {
    if (!error) {
      return;
    }

    const timer =
      window.setTimeout(() => {
        setError('');
      }, 4000);

    return () => {
      window.clearTimeout(timer);
    };
  }, [error]);

  useEffect(() => {
    function onKeyDown(
      e: KeyboardEvent
    ) {
      if (e.key === 'Escape') {
        setDetails(null);
      }
    }

    window.addEventListener(
      'keydown',
      onKeyDown
    );

    return () => {
      window.removeEventListener(
        'keydown',
        onKeyDown
      );
    };
  }, []);

  async function loadCourses(
    q: string
  ) {
    try {
      setSearchLoading(true);
      setError('');

      setCourses(
        await searchCourses(q)
      );
    } catch (e) {
      setError(message(e));
    } finally {
      setSearchLoading(false);
    }
  }

  async function refreshPlan() {
    try {
      setPlan(
        await getPlan()
      );
    } catch (e) {
      setError(message(e));
    }
  }

  async function refreshPlanValidation() {
    try {
      const completed =
        parseCompletedCourses(
          completedInput
        );

      setPlanValidation(
        await validatePlan(
          completed
        )
      );
    } catch (e) {
      setError(message(e));
    }
  }

  async function onSearch(
    e: FormEvent
  ) {
    e.preventDefault();

    await loadCourses(query);
  }

  async function onAdd(
    course: Course
  ) {
    try {
      setError('');

      const completed =
        parseCompletedCourses(
          completedInput
        );

      const validation =
        await validateAdd(
          course.code,
          quarter,
          year,
          completed
        );

      if (!validation.eligible) {
        const missing =
          validation.missing
            .map(
              formatRequirement
            )
            .join(' AND ');

        setError(
          `Cannot add ${course.code} to ${quarter} ${year}. Missing prerequisite: ${missing}.`
        );

        return;
      }

      await addPlanItem(
        course.code,
        quarter,
        year
      );

      await refreshPlan();
    } catch (e) {
      setError(message(e));
    }
  }

  async function onDelete(
    id: number
  ) {
    try {
      await deletePlanItem(id);

      await refreshPlan();
    } catch (e) {
      setError(message(e));
    }
  }

  async function onCheck(
    course: Course
  ) {
    try {
      setSelected(course);
      setError('');

      const completed =
        parseCompletedCourses(
          completedInput
        );

      setCheck(
        await checkPrerequisites(
          course.code,
          completed
        )
      );
    } catch (e) {
      setError(message(e));
      setCheck(null);
    }
  }

  async function onDetails(
    course: Course
  ) {
    try {
      setDetailsLoading(true);
      setError('');

      const result =
        await getCourse(
          course.code
        );

      setDetails(result);
    } catch (e) {
      setError(message(e));
    } finally {
      setDetailsLoading(false);
    }
  }

  async function addFromDetails() {
    if (!details) {
      return;
    }

    await onAdd(details);
  }

  async function onFindPath(
    e: FormEvent
  ) {
    e.preventDefault();

    if (!pathTarget.trim()) {
      setError(
        'Enter a target course first.'
      );
      return;
    }

    try {
      setPathLoading(true);
      setError('');
      setCoursePath(null);

      const completed =
        parseCompletedCourses(
          completedInput
        );

      const result =
        await findCoursePath(
          pathTarget,
          completed
        );

      setCoursePath(result);
    } catch (e) {
      setError(message(e));
      setCoursePath(null);
    } finally {
      setPathLoading(false);
    }
  }

  const validationById =
    useMemo(() => {
      const map =
        new Map<
          number,
          PlanValidationItem
        >();

      for (
        const item of planValidation
      ) {
        map.set(
          item.id,
          item
        );
      }

      return map;
    }, [planValidation]);

  const grouped =
    useMemo(() => {
      const map =
        new Map<
          string,
          PlanItem[]
        >();

      for (const item of plan) {
        const key =
          `${item.year}-${item.quarter}`;

        map.set(
          key,
          [
            ...(map.get(key) ?? []),
            item
          ]
        );
      }

      return [
        ...map.entries()
      ].sort(([a], [b]) => {
        const [ya, qa] =
          a.split('-');

        const [yb, qb] =
          b.split('-');

        if (
          Number(ya) !==
          Number(yb)
        ) {
          return (
            Number(ya) -
            Number(yb)
          );
        }

        return (
          quarterOrder.indexOf(qa) -
          quarterOrder.indexOf(qb)
        );
      });
    }, [plan]);

  return (
    <div className="app-shell">

      <header className="hero">

        <div>

          <div className="eyebrow">
            Full-stack course planner
          </div>

          <h1>
            HuskyPlan
          </h1>

          <p>
            Build a multi-quarter plan,
            validate prerequisites, and
            find a path to the courses
            you want to take.
          </p>

        </div>

        <div className="hero-badge">
          React · Spring Boot · PostgreSQL
        </div>

      </header>

      {error && (
        <div className="error-toast">

          <div className="error-toast-icon">
            !
          </div>

          <div className="error-toast-content">

            <strong>
              Action blocked
            </strong>

            <span>
              {error}
            </span>

          </div>

          <button
            className="error-toast-close"
            onClick={
              () =>
                setError('')
            }
            aria-label="Close notification"
          >
            ×
          </button>

        </div>
      )}

      <main className="dashboard">

        <section className="panel">

          <span className="section-kicker">
            Catalog
          </span>

          <h2>
            Find courses
          </h2>

          <form
            className="search-row"
            onSubmit={onSearch}
          >

            <input
              value={query}
              onChange={
                e =>
                  setQuery(
                    e.target.value
                  )
              }
              placeholder="Search CSE 331, algorithms..."
            />

            <button
              disabled={searchLoading}
            >
              {
                searchLoading
                  ? 'Loading...'
                  : 'Search'
              }
            </button>

          </form>

          {searchLoading && (
            <p className="muted">
              Waking up server and
              loading courses...
            </p>
          )}

          <div className="plan-controls">

            <label>

              Quarter

              <select
                value={quarter}
                onChange={
                  e =>
                    setQuarter(
                      e.target.value
                    )
                }
              >
                <option>Autumn</option>
                <option>Winter</option>
                <option>Spring</option>
                <option>Summer</option>
              </select>

            </label>

            <label>

              Year

              <input
                type="number"
                min="2020"
                max="2100"
                value={year}
                onChange={
                  e =>
                    setYear(
                      Number(
                        e.target.value
                      )
                    )
                }
              />

            </label>

          </div>

          <div className="course-list">

            {courses.map(
              course => (

                <article
                  className="course-card"
                  key={course.code}
                >

                  <div>

                    <span className="course-code">
                      {course.code}
                    </span>

                    <h3>
                      {course.title}
                    </h3>

                    <small>
                      {course.credits}{' '}
                      credits
                    </small>

                  </div>

                  <div className="course-actions">

                    <button
                      className="secondary"
                      onClick={
                        () =>
                          void onDetails(
                            course
                          )
                      }
                    >
                      Details
                    </button>

                    <button
                      className="secondary"
                      onClick={
                        () =>
                          void onCheck(
                            course
                          )
                      }
                    >
                      Check prereqs
                    </button>

                    <button
                      onClick={
                        () =>
                          void onAdd(
                            course
                          )
                      }
                    >
                      Add
                    </button>

                  </div>

                </article>
              )
            )}

          </div>

        </section>

        <aside className="side-column">

          <section className="panel path-panel">

            <span className="section-kicker">
              Path planner
            </span>

            <h2>
              Find your path
            </h2>

            <p className="muted">
              Enter a goal course and
              HuskyPlan will work backward
              through its prerequisites
              using your completed courses.
            </p>

            <form
              className="path-form"
              onSubmit={onFindPath}
            >

              <input
                value={pathTarget}
                onChange={
                  e =>
                    setPathTarget(
                      e.target.value
                    )
                }
                placeholder="CSE 451"
                aria-label="Target course"
              />

              <button
                disabled={pathLoading}
              >
                {
                  pathLoading
                    ? 'Finding...'
                    : 'Find path'
                }
              </button>

            </form>

            {pathLoading && (
              <p className="muted path-loading">
                Building prerequisite path...
              </p>
            )}

            {coursePath && (
              <div className="path-result">

                <div className="path-heading">
                  Goal:{' '}
                  <strong>
                    {
                      coursePath.targetCourse
                    }
                  </strong>
                </div>

                {coursePath.alreadyCompleted ? (

                  <div className="check-result success">
                    <strong>
                      ✓ Goal already completed
                    </strong>

                    You already have{' '}
                    {
                      coursePath.targetCourse
                    }{' '}
                    in your completed courses.
                  </div>

                ) : (

                  <>

                    <div className="path-summary">
                      Recommended course sequence
                    </div>

                    <div className="path-steps">

                      {
                        coursePath
                          .recommendedPath
                          .map(
                            (
                              course,
                              index
                            ) => {

                              const isGoal =
                                index ===
                                coursePath
                                  .recommendedPath
                                  .length -
                                  1;

                              return (
                                <div
                                  className="path-step-wrapper"
                                  key={
                                    `${course}-${index}`
                                  }
                                >

                                  <div
                                    className={
                                      `path-step ${
                                        isGoal
                                          ? 'path-goal'
                                          : ''
                                      }`
                                    }
                                  >

                                    <div className="path-number">
                                      {
                                        index + 1
                                      }
                                    </div>

                                    <strong>
                                      {course}
                                    </strong>

                                    {isGoal && (
                                      <span className="goal-badge">
                                        Goal
                                      </span>
                                    )}

                                  </div>

                                  {!isGoal && (
                                    <div className="path-arrow">
                                      ↓
                                    </div>
                                  )}

                                </div>
                              );
                            }
                          )
                      }

                    </div>

                  </>
                )}

              </div>
            )}

          </section>

          <section className="panel">

            <span className="section-kicker">
              Eligibility
            </span>

            <h2>
              Prerequisite check
            </h2>

            <p className="muted">
              Enter completed course
              codes separated by commas,
              then choose a course.
            </p>

            <textarea
              rows={4}
              value={completedInput}
              onChange={
                e =>
                  setCompletedInput(
                    e.target.value
                  )
              }
            />

            {selected && (
              <p>
                Selected:{' '}
                <strong>
                  {selected.code}
                </strong>
              </p>
            )}

            {check && (
              <div
                className={
                  `check-result ${
                    check.eligible
                      ? 'success'
                      : 'warning'
                  }`
                }
              >

                <strong>
                  {
                    check.eligible
                      ? 'Eligible based on this demo data.'
                      : 'Missing prerequisites.'
                  }
                </strong>

                <div>
                  Required:{' '}
                  {
                    check.required.length
                      ? check.required
                          .map(
                            formatRequirement
                          )
                          .join(' AND ')
                      : 'None'
                  }
                </div>

                {!check.eligible && (
                  <div>
                    Missing:{' '}
                    {
                      check.missing
                        .map(
                          formatRequirement
                        )
                        .join(' AND ')
                    }
                  </div>
                )}

              </div>
            )}

          </section>

          <section className="panel">

            <span className="section-kicker">
              Schedule
            </span>

            <h2>
              Saved plan
            </h2>

            {grouped.length === 0 && (
              <p className="muted">
                Your plan is empty.
              </p>
            )}

            <div className="quarter-list">

              {grouped.map(
                ([key, items]) => {

                  const [y, q] =
                    key.split('-');

                  return (
                    <div
                      className="quarter-card"
                      key={key}
                    >

                      <strong>
                        {q} {y}
                      </strong>

                      {items.map(
                        item => {

                          const validation =
                            validationById.get(
                              item.id
                            );

                          return (
                            <div
                              className="planned-course"
                              key={item.id}
                            >

                              <div>

                                <div>
                                  <strong>
                                    {
                                      item.courseCode
                                    }
                                  </strong>
                                </div>

                                {validation && (
                                  <div className="plan-validation">

                                    {
                                      validation.eligible
                                        ? (
                                          <span className="plan-valid">
                                            ✓ Prerequisites satisfied
                                          </span>
                                        )
                                        : (
                                          <span className="plan-invalid">
                                            ⚠ Missing:{' '}
                                            {
                                              validation.missing
                                                .map(
                                                  formatRequirement
                                                )
                                                .join(
                                                  ' AND '
                                                )
                                            }
                                          </span>
                                        )
                                    }

                                  </div>
                                )}

                              </div>

                              <button
                                className="icon-button"
                                onClick={
                                  () =>
                                    void onDelete(
                                      item.id
                                    )
                                }
                                aria-label={
                                  `Remove ${item.courseCode}`
                                }
                              >
                                ×
                              </button>

                            </div>
                          );
                        }
                      )}

                    </div>
                  );
                }
              )}

            </div>

          </section>

        </aside>

      </main>

      <footer>
        Demo course data only —
        not official UW advising information.
      </footer>

      {detailsLoading && (
        <div className="modal-backdrop">
          <div className="modal-card modal-loading">
            Loading course...
          </div>
        </div>
      )}

      {details && !detailsLoading && (
        <div
          className="modal-backdrop"
          onMouseDown={
            e => {
              if (
                e.target ===
                e.currentTarget
              ) {
                setDetails(null);
              }
            }
          }
        >

          <div
            className="modal-card"
            role="dialog"
            aria-modal="true"
          >

            <button
              className="modal-close"
              onClick={
                () =>
                  setDetails(null)
              }
              aria-label="Close course details"
            >
              ×
            </button>

            <span className="section-kicker">
              Course details
            </span>

            <div className="modal-course-code">
              {details.code}
            </div>

            <h2>
              {details.title}
            </h2>

            <div className="detail-row">

              <span>
                Credits
              </span>

              <strong>
                {details.credits}
              </strong>

            </div>

            <div className="detail-section">

              <h3>
                Prerequisites
              </h3>

              <p>
                {
                  details.prerequisites.length
                    ? details.prerequisites
                        .map(
                          formatRequirement
                        )
                        .join(' AND ')
                    : 'None'
                }
              </p>

            </div>

            <div className="detail-section">

              <h3>
                Description
              </h3>

              <p>
                {
                  details.description ||
                  'No description available.'
                }
              </p>

            </div>

            <div className="modal-actions">

              <button
                className="secondary"
                onClick={
                  () =>
                    setDetails(null)
                }
              >
                Close
              </button>

              <button
                onClick={
                  () =>
                    void addFromDetails()
                }
              >
                Add to {quarter} {year}
              </button>

            </div>

          </div>

        </div>
      )}

    </div>
  );
}

function message(
  e: unknown
) {
  return e instanceof Error
    ? e.message
    : 'Something went wrong.';
}