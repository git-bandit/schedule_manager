# Project structure

```text
schedule_researcher/
├── pom.xml                    # Maven: dependencies & build config
├── DATABASE_SETUP.md          # Database setup notes
├── .vscode/                   # VS Code settings (e.g. launch.json)
│
└── src/main/java/schedulemanager/
    ├── ui/                    # User interface (Swing)
    ├── controller/            # Connects UI to services
    ├── service/               # Business logic
    ├── repository/            # Database access
    ├── domain/                # Data models / entities
    └── integration/           # External services (e.g. AI)

---

# Layer-by-layer

## domain/ – data models

Plain Java classes representing core concepts.

| File | Purpose |
|---|---|
| Task.java | Task (title, status, priority, deadline, etc.) |
| TaskFolder.java | Folder to group tasks (can be nested) |
| TodayTask.java | Link between a task and a date for “Today” |
| PlanBlock.java | Planned time block on the calendar |
| ActualSession.java | Actual work session you recorded |
| DailyStatistics.java | Stats for a day (planned vs actual) |
| TaskStatus.java | Enum: TODO, DOING, DONE |
| Priority.java | Enum: LOW, MEDIUM, HIGH, URGENT |

---

## repository/ – database layer

Talks to the database (SQL) and maps rows to domain objects.

| File | Responsibility |
|---|---|
| DatabaseManager.java | Connections, DB choice (MySQL/SQLite), table creation |
| TaskRepository.java | CRUD for tasks |
| TaskFolderRepository.java | CRUD for folders (with hierarchy) |
| TodayRepository.java | Add/remove/reorder tasks in “Today” |
| PlanRepository.java | CRUD for planned blocks |
| ActivityRepository.java | CRUD for actual sessions |

---

## service/ – business logic

Holds rules and calculations, uses repositories.

| File | Responsibility |
|---|---|
| TaskService.java | Task creation/updates, validation, folder tasks |
| ScheduleService.java | Plan blocks: create, overlap checks, list by date |
| TrackingService.java | Actual sessions: create, list by date |
| StatsService.java | Planned vs actual minutes, accuracy metrics |

---

## controller/ – application flow

Single entry point between UI and services/repositories.

| File | Responsibility |
|---|---|
| ScheduleController.java | Handles UI actions and calls the right services |

---

## ui/ – user interface (Swing)

Windows, panels, dialogs, and listeners.

| File | Purpose |
|---|---|
| MainWindow.java | Main frame, layout, startup |
| FoldersPanel.java | Folder list and selection |
| TasksPanel.java | Task list, add/edit/delete, “Add to Today” |
| TodayPanel.java | Today list, status changes, remove |
| PlanCalendarPanel.java | Planned blocks (day view) |
| ActualCalendarPanel.java | Actual sessions (day view) |
| StatsPanel.java | Statistics and AI notes |
| TaskDialog.java | Create/edit task |
| PlanBlockDialog.java | Create plan block |
| ActualSessionDialog.java | Create actual session |

---

## integration/ – external APIs

| File | Purpose |
|---|---|
| AiApiClient.java | Calls external AI API for insights/recommendations |

---

# Request flow

