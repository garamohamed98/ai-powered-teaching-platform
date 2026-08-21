# AI-Powered Teaching & Learning Platform

An open-source platform where **users create courses and lessons**, and the system **auto-generates and grades exercises with Google Gemini AI**. Learners start attempts, submit answers, and receive instant scoring plus personalized AI feedback grounded in lesson content.

> **Repository:** `https://github.com/garamohamed98/ai-powered-teaching-platform`  
> **Monorepo layout:** `ai-teaching-platform-backend/` (Spring Boot) + `ai-teaching-platform-frontend/` (Angular)

---

## Table of Contents

- [What Is This Project?](#what-is-this-project)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Project Structure](#project-structure)
- [API Endpoints](#api-endpoints)
- [How to Try This Project](#how-to-try-this-project)
- [How to Contribute](#how-to-contribute)

---

## What Is This Project?

An **AI-powered teaching and learning platform** for creating, delivering, and assessing courses and exercises end-to-end:

- **Manage courses and lessons** — create courses, rename or delete them, and build out each course with multiple lessons. Every lesson has a title and rich-text content you can edit anytime, just like a document.

- **Build exercises linked to your lessons** — create exercises and attach them to one or more lessons. You can write the content yourself fully, or let the AI fill in the correct answers for you.

- **Generate exercises automatically with AI** — select lessons, pick an exercise type, and the platform uses AI to generate a complete, ready-to-use exercise (title, instructions, and questions) grounded directly in your lesson content.

- **Take exercises and get assessed instantly** — learners **start** an exercise (they see only the questions, not the answers), **submit** their responses, and immediately receive:
  - an automatic **score**,
  - a detailed **comparison** of their answers vs. correct answers,
  - **time taken** to complete it,
  - and **personalized AI feedback** that explains mistakes and what to review next, based on the original lesson material — not generic praise.

- **Refine with AI assistance** — regenerate or re-correct answers for existing exercises with one action if lesson content changes.

In short: you go from **creating content → generating assessments with AI → practicing → getting intelligent feedback**, all in one place, whether you're a teacher building a curriculum or a learner testing your understanding.

---

## Tech Stack

### Backend — `ai-teaching-platform-backend/pom.xml:1`

| Layer | Technology | Version / Notes |
|-------|------------|-----------------|
| Language | **Java** | `17` (`pom.xml:30`) |
| Framework | **Spring Boot** | `4.0.6` (`pom.xml:8`) — `spring-boot-starter-data-jpa`, `spring-boot-starter-webmvc`, `spring-boot-starter-security`, `spring-boot-starter-validation` |
| Database | **PostgreSQL** | `postgresql:runtime` + `spring.jpa.hibernate.ddl-auto: update` (`application.yml:16`) |
| ORM | **Hibernate / Spring Data JPA** | `show-sql: true`, `format_sql: true` |
| Mapping | **MapStruct** | `1.5.5.Final` (`pom.xml:108`) |
| Boilerplate | **Lombok** | `optional` |
| JSON | **Jackson** | `jackson-databind` (explicit) + `spring-boot-jackson2`; polymorphic `JsonNode` with `@JdbcTypeCode(SqlTypes.JSON)` on `jsonb` columns |
| AI | **Google Gemini** | Via `RestClient` in `AI/client/GeminiClient.java:1` → `https://{baseUrl}/v1beta/models/{model}:generateContent?key={apiKey}`; prompts in `PromptBuilderService.java:1`, parsing in `AI/parsers/GeminiResponseParser.java:1` |
| Cache/Infra | **Jedis (Redis)** | `6.1.0` (`pom.xml:127`) — dependency present, not yet wired in controllers |
| Security | **Spring Security** | `SecurityConfig.java:1` — CSRF disabled, CORS for `http://localhost:4200`, `formLogin` default, all API routes `permitAll` |
| Testing | **JUnit 5 + Testcontainers** | `testcontainers-postgresql`, `spring-boot-testcontainers`, `spring-boot-starter-*test` |
| Build | **Maven** | Wrapper `mvnw` / `mvnw.cmd`, `maven-compiler-plugin` with Lombok+MapStruct annotation processors |
| Config | **Dotenv-style** | `spring.config.import: "optional:file:.env[.properties]"` (`application.yml:6`) |

### Frontend — `ai-teaching-platform-frontend/package.json:1`

| Layer | Technology | Version |
|-------|------------|---------|
| Framework | **Angular** | `19.2.0` (`@angular/core`), CLI `19.2.25`, `typescript ~5.7.2` (`target: ES2022`, `strict: true`) |
| UI Kit | **PrimeNG** | `19.1.4` + `@primeng/themes 21.0.4` + `@primeuix/themes 2.0.3` + `@angular/cdk 19.2.19` + `primeicons 7.0.0` |
| Theme | **Aura** | `providePrimeNG({ theme: { preset: Aura, options: { darkModeSelector: '.dark-mode' } } })` (`src/app/app.config.ts:1`) |
| Rich Text | **Quill** | `2.0.3` via PrimeNG `<p-editor>` (`features/courses/components/course-editor/course-editor.component.ts:1`) |
| Styling | **SCSS** | `inlineStyleLanguage: scss`, global `src/styles.scss:1` (Nunito + `#f8fafc`) |
| State | **Angular Signals** | `signal()`, `inject()` in standalone components |
| HTTP | **HttpClient + Interceptor** | `provideHttpClient(withInterceptors([errorInterceptor]))` (`app.config.ts:1`), `error.interceptor.ts:1` → `MessageService` toasts |
| Build | **Angular Build System** | `@angular-devkit/build-angular:application`, budgets `500kB warn / 1MB error` (`angular.json:42`) |
| Testing | **Karma + Jasmine** | `karma 6.4.0`, `jasmine-core 5.6.0` |

### DevOps

- **CI:** GitHub Actions `Backend CI` (`.github/workflows/backend-ci.yml:1`) — triggers on `push` to `main`/`develop` and PRs to `main` when `ai-teaching-platform-backend/**` changes; runs `setup-java@v4` (Temurin 17, Maven cache) then `./mvnw clean verify`.
- **Containers (tests):** Testcontainers `postgres:latest` for integration tests.

---

## Features

### Courses

- **Create course** — create a new course with a title.
  > `POST /api/course`

- **List courses** — get all courses.
  > `GET /api/course`

- **Update course title** — rename an existing course.
  > `PATCH /api/course/{courseId}/title`

- **Delete course** — remove a course and its lessons.
  > `DELETE /api/course/{id}`

### Lessons

- **Create lesson** — add a lesson to a specific course.
  > `POST /api/course/{courseId}/lesson`

- **List lessons of a course** — see all lessons belonging to a course.
  > `GET /api/course/{courseId}/lesson`

- **Get lesson details** — view a single lesson with its content.
  > `GET /api/lesson/{lessonId}`

- **Update lesson title** — change the lesson's title.
  > `PATCH /api/lesson/{lessonId}/title`

- **Update lesson content** — edit the lesson's rich-text content.
  > `PATCH /api/lesson/{lessonId}/content`

- **Delete lesson** — remove a lesson.
  > `DELETE /api/lesson/{lessonId}`

### Exercises

- **Create exercise manually** — create a Multiple Choice or Fill in the Blank exercise linked to lessons. Can auto-fill correct answers with AI.
  > `POST /api/exercise`

- **Generate exercise with AI** — automatically generate a full exercise from your lesson content using Gemini.
  > `POST /api/exercise/generate`

- **List exercises of a course** — see all exercises for a course.
  > `GET /api/exercise/course/{courseId}`

- **Get exercise details** — view a single exercise with all its questions and answers.
  > `GET /api/exercise/{id}`

- **Re-correct exercise** — regenerate correct answers for an existing exercise with AI.
  > `PATCH /api/exercise/{id}`

- **Delete exercise** — remove an exercise.
  > `DELETE /api/exercise/{id}`

### Exercise Attempts (Take & Submit)

- **Start an exercise** — begin an attempt, you get the questions without the correct answers.
  > `POST /api/exercise-attempt/{exerciseId}/attempt`

- **Submit an exercise** — send your answers and get your score, answer comparison, time taken, and personalized AI feedback.
  > `POST /api/exercise-attempt/{exerciseAttemptId}/submit`

### Other

- **Check database connection** — verify the backend can reach the database.
  > `GET /test/db`

---

## Project Structure

```
.
├── ai-teaching-platform-backend/   # Spring Boot 4 + Java 17
│   ├── pom.xml
│   ├── .env / .env.example         # DB + Gemini env vars
│   ├── src/main/java/com/mohamedgara/ai_teaching_platform/
│   │   ├── AiTeachingPlatformApplication.java
│   │   ├── SecurityConfig.java
│   │   ├── config.java
│   │   ├── AI/{client,config,dto,exceptions,parsers,services}
│   │   ├── courses/{controller,service,entity,repository,mappers,dto,exception,projection}
│   │   ├── exercises/{controller,services,entities,repositories,mappers,dto,domain,exceptions}
│   │   ├── exception/              # GlobalExceptionHandler
│   │   └── test/TestDbController.java
│   ├── src/main/resources/application.yml
│   └── src/test/java/...           # Unit + integration + Testcontainers
├── ai-teaching-platform-frontend/  # Angular 19 + PrimeNG + Quill
│   ├── package.json / angular.json / tsconfig.json
│   └── src/app/
│       ├── app.component.ts / app.routes.ts / app.config.ts / error.interceptor.ts
│       └── features/
│           ├── courses/{pages,components,models,courses.service.ts,courses.routes.ts}
│           ├── exercises/{exercises.service.ts,containers/course-exercises-list}
│           └── not-found/
├── .github/workflows/backend-ci.yml
└── README.md
```

---

## API Endpoints

Base URL: `http://localhost:8080` (`application.yml:24` — `server.port: ${SERVER_PORT:8080}`)

All endpoints listed below are currently **public** (`permitAll` in `SecurityConfig.java:22`). No auth header required.

### Courses — `courses/controller/CourseController.java:1` (`/api/course`)

| Method | Path | Request Body | Response | Description |
|--------|------|--------------|----------|-------------|
| `POST` | `/api/course` | `CreateCourseRequest` `{ title: string (3–50, @NotBlank) }` | `201 Created` + `Location: /api/course/{id}` → `CourseResponse { id: UUID, title }` | Create course |
| `GET` | `/api/course` | — | `200` → `CourseResponse[]` | List all courses |
| `DELETE` | `/api/course/{id}` | — | `204 No Content` | Delete course (cascade deletes lessons) |
| `PATCH` | `/api/course/{courseId}/title` | `CourseTitleUpdateRequest { title }` | `200` → `CourseResponse` | Update course title |
| `POST` | `/api/course/{courseId}/lesson` | `CreateLessonRequest { title }` | `201` + `Location: /api/lesson/{id}` → `LessonResponse { id, title, content, course_id }` | Create lesson under course |
| `GET` | `/api/course/{courseId}/lesson` | — | `200` → `LessonSummaryResponse[] { id, title, course_id }` | List lessons for course |

### Lessons — `courses/controller/LessonController.java:1` (`/api/lesson`)

| Method | Path | Request Body | Response | Description |
|--------|------|--------------|----------|-------------|
| `PATCH` | `/api/lesson/{lessonId}/title` | `LessonTitleUpdateRequest { title }` | `200` → `LessonResponse` | Update lesson title |
| `PATCH` | `/api/lesson/{lessonId}/content` | `LessonContentUpdateRequest { content: string }` | `200` → `LessonResponse` | Update lesson rich-text content |
| `GET` | `/api/lesson/{lessonId}` | — | `200` → `LessonResponse` | Get lesson by id |
| `DELETE` | `/api/lesson/{lessonId}` | — | `204` | Delete lesson |

### Exercises — `exercises/controller/ExerciseController.java:1` (`/api/exercise`)

| Method | Path | Request Body | Response | Description |
|--------|------|--------------|----------|-------------|
| `POST` | `/api/exercise` | `CreateExerciseRequest { lesson_id_list: UUID[], type: MULTIPLE_CHOICE\|FILL_IN_BLANK, title, instructions, correctAnswers?: boolean, content: ExerciseContent }` (polymorphic via `CreateExerciseRequestDeserializer.java:1`) | `200` → `CreateExerciseResponse { id, lesson_id_list, type, title, instructions, content }` | Manually create exercise (optionally auto-generate correct answers via AI) |
| `GET` | `/api/exercise/course/{courseId}` | — | `200` → `ExerciseSummaryResponse[] { id, title, type, lesson: LessonSummaryResponse[] }` | List exercises for course |
| `GET` | `/api/exercise/{id}` | — | `200` → `ExerciseResponse { id, lesson_id_list, type, title, instructions, content }` | Get exercise by id (full JSONB content) |
| `DELETE` | `/api/exercise/{id}` | — | `204` | Delete exercise (sets `deletedAt`) |
| `PATCH` | `/api/exercise/{id}` | — | `200` → `ExerciseResponse` | Re-generate / correct answers for exercise via AI |
| `POST` | `/api/exercise/generate` | `GenerateExerciseRequest { lesson_id_list: UUID[], course_id: UUID, type }` | `200` → `ExerciseResponse` | **AI-generate** exercise from lesson content + example structure |

**Exercise content variants** (`exercises/domain/` + `exercises/dto/request/content/`):

- `MULTIPLE_CHOICE` → `MultipleChoiceContent` (questions/options with `correct_answer`)
- `FILL_IN_BLANK` → `FillInBlankContent` containing `FillInBlankSentence[]` with `answers: string[]`

### Exercise Attempts — `exercises/controller/ExerciseAttemptController.java:1` (`/api/exercise-attempt`)

| Method | Path | Request Body | Response | Description |
|--------|------|--------------|----------|-------------|
| `POST` | `/api/exercise-attempt/{exerciseId}/attempt` | — | `200` → `StartExerciseResponse { id (exerciseId), exercise_attempt_id, type, title, instructions, content: ExerciseStartContent }` | Start attempt (strips correct answers) |
| `POST` | `/api/exercise-attempt/{exerciseAttemptId}/submit` | `SubmitExerciseAttemptRequest { exercise_type, attempt: MultipleChoiceAttempt \| FillInBlankAttempt }` (via `SubmitExerciseAttemptRequestDeserializer.java:1`) | `200` → `SubmitExerciseResponse { attempt_id, exercise_id, lesson_id_list, type, title, instructions, compared_answer, score, ai_feedback, time_taken }` | Submit answers → scoring + AI feedback |

### Utility

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/test/db` | DB connectivity check → `"Database connect successful"` or failure message |

**Error codes** (from `AI/exceptions/` + `exercises/exceptions/` + `courses/exception/`):

- `400` — validation / `InvalidExerciseContentException` / `InvalidAttemptTypeException`
- `404` — `CourseNotFoundException`, `LessonNotFoundException`, `ExerciseNotFoundException`, `ExerciseAttemptNotFoundException`
- `402` — `AiQuotaExceededException`
- `429` — `AIRateLimitException`
- `503` — `AiServiceUnavailableException`
- Standard Spring validation errors via `GlobalExceptionHandler.java:1`

> Tip: after starting the backend, explore the API with `curl` or import the endpoints into Postman/Insomnia. Example:
> ```bash
> curl http://localhost:8080/api/course
> curl -X POST http://localhost:8080/api/course -H "Content-Type: application/json" -d '{"title":"Intro to AI"}'
> ```

---

## How to Try This Project

### Prerequisites

| Requirement | Version | Check |
|-------------|---------|-------|
| **Java** | `17` | `java -version` |
| **Maven** | `3.9+` (or use wrapper `./mvnw`) | `./mvnw -version` |
| **Node.js** | `18+` (Angular 19 needs `^18.19` or `^20.11`) | `node -v` |
| **npm** | `10+` | `npm -v` |
| **PostgreSQL** | `14+` | `psql --version` |
| **Google Gemini API Key** | — | https://aistudio.google.com/app/apikey |
| **Git** | any | `git --version` |

### 1. Clone

```bash
git clone https://github.com/garamohamed98/ai-powered-teaching-platform.git
cd ai-powered-teaching-platform
```

### 2. Configure the Backend

Create `ai-teaching-platform-backend/.env` from the example:

```bash
cp ai-teaching-platform-backend/.env.example ai-teaching-platform-backend/.env
```

Edit `.env` (`application.yml:8` loads it via `optional:file:.env[.properties]`):

```properties
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=teaching_platform
DB_USER=teaching_platform_user
DB_PASSWORD=password

# Server Configuration
SERVER_PORT=8080

# Frontend URL (CORS)
CORS_ORIGINS=http://localhost:4200

# AI Configuration — get a key at https://aistudio.google.com/app/apikey
AI_API_KEY=your_gemini_api_key
AI_MODEL=gemini-2.5-flash
AI_BASE_URL=generativelanguage.googleapis.com
```

Create the database/user (PostgreSQL):

```sql
CREATE DATABASE teaching_platform;
CREATE USER teaching_platform_user WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE teaching_platform TO teaching_platform_user;
-- For Hibernate ddl-auto:update on a fresh DB, also:
-- Connect to teaching_platform and grant schema privileges if needed:
-- GRANT ALL ON SCHEMA public TO teaching_platform_user;
```

> **Docker alternative:**
> ```bash
> docker run --name pg-teaching -e POSTGRES_DB=teaching_platform -e POSTGRES_USER=teaching_platform_user -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres:16
> ```

### 3. Run the Backend

```bash
cd ai-teaching-platform-backend

# Run directly (uses wrapper, no global Maven needed)
./mvnw spring-boot:run
# Windows
.\mvnw.cmd spring-boot:run

# Or build a jar
./mvnw clean package -DskipTests
java -jar target/ai-teaching-platform-0.0.1-SNAPSHOT.jar
```

Verify:

```bash
curl http://localhost:8080/test/db
# → Database connect successful

curl http://localhost:8080/api/course
# → []
```

Logs show SQL (`show-sql: true`) and `DEBUG` for `com.mohamedgara` (`application.yml:26`).

### 4. Run the Frontend

```bash
cd ai-teaching-platform-frontend
npm install
npm start
# equivalent to: npx ng serve
```

Open `http://localhost:4200` — you will be routed to `/courses` (or `/**` → `NotFoundComponent` if you hit `/`). The frontend is hardcoded to `http://localhost:8080/api/course` (`features/courses/courses.service.ts:1` — `baseUrl`).

**Other commands:**

```bash
npm run build        # production build → dist/ai-teaching-platform-frontend/
npm test             # Karma + Jasmine
npm run watch        # ng build --watch --configuration development
```

### 5. Quick Smoke Test (end-to-end without UI)

```bash
# 1. Create a course
curl -X POST http://localhost:8080/api/course \
  -H "Content-Type: application/json" \
  -d '{"title":"Biology 101"}' | jq
# → { "id": "...", "title": "Biology 101" }

# 2. Create a lesson
COURSE_ID=<id from above>
curl -X POST http://localhost:8080/api/course/$COURSE_ID/lesson \
  -H "Content-Type: application/json" \
  -d '{"title":"Cell Structure"}' | jq

# 3. Add lesson content
LESSON_ID=<lesson id>
curl -X PATCH http://localhost:8080/api/lesson/$LESSON_ID/content \
  -H "Content-Type: application/json" \
  -d '{"content":"<p>The cell is the basic unit of life. Mitochondria produce energy.</p>"}' | jq

# 4. Generate an AI exercise from the lesson
curl -X POST http://localhost:8080/api/exercise/generate \
  -H "Content-Type: application/json" \
  -d "{\"lesson_id_list\":[\"$LESSON_ID\"],\"course_id\":\"$COURSE_ID\",\"type\":\"MULTIPLE_CHOICE\"}" | jq

# 5. Start and submit an attempt
EXERCISE_ID=<exercise id>
ATTEMPT_JSON=$(curl -s -X POST http://localhost:8080/api/exercise-attempt/$EXERCISE_ID/attempt | jq)
ATTEMPT_ID=$(echo $ATTEMPT_JSON | jq -r .exercise_attempt_id)
curl -X POST http://localhost:8080/api/exercise-attempt/$ATTEMPT_ID/submit \
  -H "Content-Type: application/json" \
  -d '{"exercise_type":"MULTIPLE_CHOICE","attempt":{"answers":["..."]}}' | jq
```

### Troubleshooting

- **Backend fails to start — DB connection refused:** check `DB_HOST/DB_PORT` and that PostgreSQL is running; `GET /test/db` reports the exact error.
- **AI calls fail (`429`/`503`/`402`):** verify `AI_API_KEY`, `AI_MODEL`, `AI_BASE_URL`; check Gemini quota at https://aistudio.google.com.
- **CORS error in browser:** backend only allows `http://localhost:4200` (`SecurityConfig.java:39`). If you serve the frontend elsewhere, update `corsConfigurationSource()` or set `CORS_ORIGINS`.
- **Frontend cannot reach backend:** ensure backend is on `8080` and `baseUrl` in `courses.service.ts:1` matches.
- **Tests need Docker:** `Testcontainers` requires a running Docker daemon for `./mvnw clean verify`.

---

## How to Contribute

Contributions are welcome — bug fixes, new exercise types, auth, UI for exercises, docs, and tests.

### Ground Rules

- Be respectful and constructive. Follow standard open-source etiquette.
- Keep PRs focused (one feature/fix per PR).
- Add or update tests for new behavior.
- Do not commit secrets (`.env` is gitignored — `ai-teaching-platform-backend/.gitignore:35`).

### Setup for Contributors

1. **Fork** the repo on GitHub, then clone your fork:
   ```bash
   git clone https://github.com/<your-username>/ai-powered-teaching-platform.git
   cd ai-powered-teaching-platform
   git remote add upstream https://github.com/garamohamed98/ai-powered-teaching-platform.git
   ```
2. Follow [How to Try This Project](#how-to-try-this-project) to get both apps running locally.
3. Create a branch:
   ```bash
   git checkout -b feat/your-feature-name
   # or fix/your-bug-fix
   ```

### Development Workflow

```bash
# Backend — run tests (requires Docker for Testcontainers)
cd ai-teaching-platform-backend
./mvnw clean verify

# Frontend — run tests
cd ai-teaching-platform-frontend
npm test
# Lint/format (if configured)
npx ng lint
```

- Backend follows standard Maven layout; new code goes under `src/main/java/com/mohamedgara/ai_teaching_platform/` with matching tests under `src/test/java/`.
- Frontend uses **standalone components + signals**; co-locate `*.ts`, `*.html`, `*.scss`, `*.spec.ts` per feature (see `features/courses/` as reference).
- CI must pass: `.github/workflows/backend-ci.yml:1` runs `./mvnw clean verify` on `main`/`develop`.

### Commit & PR Guidelines

- Use clear, conventional messages: `feat:`, `fix:`, `docs:`, `test:`, `refactor:`, `chore:`.
- Reference issues where applicable (`Closes #123`).
- Keep commits atomic and rebased on `develop`:
  ```bash
  git fetch upstream
  git rebase upstream/develop
  ```
- **Before opening a PR:**
  - Run backend and frontend tests locally.
  - Update `README.md` or docs if you change endpoints, env vars, or setup.
  - Ensure no secrets or generated files (`target/`, `dist/`, `node_modules/`) are committed.

**PR checklist:**

- [ ] Descriptive title and summary (what & why)
- [ ] Screenshots/GIFs for UI changes
- [ ] Tests added or updated
- [ ] CI green (`Backend CI`)
- [ ] No merge conflicts with `develop`

Open your PR against the `develop` branch (or `main` if the repo prefers — check recent PRs: `git log --oneline` shows `develop` as integration branch). A maintainer will review and may request changes.

### Good First Issues

- Wire `CourseDetailsComponent` into `courses.routes.ts:1` (add `{ path: ':id', component: CourseDetailsComponent }`).
- Implement `ExercisesService` HTTP methods and `exercises` routes in `app.routes.ts:1`.
- Make frontend `baseUrl` configurable via `environment.ts` instead of hardcoded `localhost:8080`.
- Add `PUT` or `PATCH` for exercise `title`/`instructions` updates.
- Introduce JWT auth (replace `permitAll` in `SecurityConfig.java:22` with a filter + `User` entity).
- Add pagination / filtering to `GET /api/course` and `GET /api/exercise/course/{courseId}`.
- Increase test coverage for `ExerciseAttemptService` and AI prompt edge cases.

### Reporting Bugs / Requesting Features

Open an issue at `https://github.com/garamohamed98/ai-powered-teaching-platform/issues` with:

- **Bug:** steps to reproduce, expected vs actual, logs, versions (`java -version`, `node -v`), and whether Docker/DB is running.
- **Feature:** use case, proposed API/UI, and whether you’d like to implement it.

### Questions?

Open a discussion/issue or reach out to the maintainers via GitHub. For OpenCode-related help, see https://github.com/anomalyco/opencode.

---
