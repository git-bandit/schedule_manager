
**Titlu proiect:** Schedule Manager (Java Desktop)

### Tehnologii și cerințe generale

* Limbaj: **Java 17**
* Build: **Maven**
* GUI: **Swing** (JFrame + JPanel)
* Persistență: **MySQL** (JDBC `mysql-connector-j` 8.3.0)
* Alte dependențe: OkHttp (API AI), Gson (JSON)
* Aplicație: **single-user local**
* Stocare: **local only** (fără sincronizare)
* Documentație: **Javadoc** pentru clase și metode; generare: `mvn javadoc:javadoc`
* Arhitectură: separare pe straturi **ui / controller / domain / service / repository / integration**
* Validări: intervale orare valide (end > start), câmpuri obligatorii, consistență date
* Teste: **JUnit 5** — teste unitare și de integrare (vezi `src/test/java/`)

---

## Scopul aplicației

Schedule Manager este o aplicație desktop care ajută utilizatorii (în special **oamenii care procrastinează des**) să fie mai productivi, prin combinarea:

1. **planificării anticipative a zilei** (Plan Calendar) și
2. **urmăririi activităților realizate** (Actual Calendar / jurnal),

astfel încât utilizatorul să poată compara „cum a planificat” versus „cum a decurs” ziua și să obțină statistici și insight-uri despre comportamentul său.

**Problema principală rezolvată:** utilizatorul nu își poate îmbunătăți productivitatea în mod consecvent deoarece nu măsoară sistematic diferența dintre intenție (plan) și comportament (execuție). Fără tracking și comparație, factorii care îl afectează (procrastinare, întreruperi, mutarea activităților în alte intervale) rămân invizibili, iar utilizatorul nu primește insight-uri acționabile. Schedule Manager operationalizează principiul „you can’t change what you can’t measure” prin măsurare, comparare și feedback clar asupra abaterilor dintre plan și realitate.


---

## Funcționalități pentru gestionarea timpului (Plan vs Actual)

Aplicația are **două calendare pentru aceeași zi**:

### 1) Plan Calendar (planificare)

* Utilizatorul își planifică ziua în avans folosind **blocuri orare** (ex. 09:00–10:30).
* Un bloc planificat conține:

  * `startTime`, `endTime`
  * `title/label`
  * `category`
  * `linkedTaskId` (opțional — legătură către un task)

### 2) Actual Calendar (tracker activități)

* Utilizatorul își înregistrează activitățile reale **manual** (introduce intervalul orar).
* O activitate reală poate fi împărțită în **mai multe sesiuni** (ex. același task în 3 intervale diferite).
* O sesiune reală conține:

  * `startTime`, `endTime`
  * `title/label`
  * `category`
  * `linkedTaskId` (opțional)

### 3) Comparare + insight-uri

Comparația dintre Plan și Actual permite:

* măsurarea **acurateții** (cantitativă și temporală)
* identificarea abaterilor (întârzieri, mutări dimineață→seară, suprapuneri, pauze mari etc.)
* generarea de insight-uri despre tipare de lucru (când utilizatorul respectă planul și când nu)

---

## AI (API extern)

Aplicația trimite un rezumat zilnic (statistici + deviații + listă taskuri planificate vs realizate) către un **API extern** (HTTP). URL implicit: `http://localhost:8080/api/insights`. API-ul returnează:

* un scurt text cu **insight-uri**
* **recomandări** (ex. reducerea blocurilor dimineața, planificare realistă, reordonare taskuri etc.)

---

## Statistici (minim) — definiții clare

### 1) Acuratețe cantitativă (0..1)

Măsoară cât din timpul planificat a fost realizat ca volum total.

* `plannedMinutes = total minute în Plan Calendar (pe zi)`
* `actualMinutes = total minute în Actual Calendar (pe zi)`
* `quantAccuracy = min(actualMinutes, plannedMinutes) / plannedMinutes` (dacă plannedMinutes > 0; altfel 0)

Exemplu: plan 180 min, actual 180 min ⇒ 1.0.

### 2) Acuratețe temporală (0..1)

Măsoară cât de mult din timpul planificat a fost realizat **în intervalele planificate** (overlap).

* pentru fiecare bloc planificat, se calculează **intersecția** (overlap) cu sesiunile reale
* `overlapMinutes = total minute de suprapunere Plan ∩ Actual`
* `tempAccuracy = overlapMinutes / plannedMinutes` (dacă plannedMinutes > 0; altfel 0)

Exemplu: plan dimineața 180 min, actual seara 180 min ⇒ cantitativ 1.0, temporal aproape 0.

### 3) Comparare task-to-task (cerință explicită)

Pentru fiecare task selectat/planificat:

* timp planificat pe task vs timp real pe task
* status task (TODO/DOING/DONE) corelat cu activitatea reală
* deviație de timp (minute) și deviație temporală (cât din task s-a făcut în slotul planificat)

---

## Gestionarea taskurilor (Task Manager)

### 1) Foldere ierarhice

* Taskurile sunt organizate într-o structură arborescentă de foldere/subfoldere.
* Utilizatorul navighează prin foldere și selectează taskurile pe care vrea să le facă astăzi.

### 2) Lista Today (taskuri pentru azi)

* Taskurile selectate pentru ziua curentă apar într-o listă separată în GUI: **Today**.
* În Today, utilizatorul poate:

  * adăuga/șterge/reordona taskuri: **DA**
  * marca status: **TODO / DOING / DONE**: **DA**
  * seta culoare/tag: **DA**

**Important (consistență):** Today nu duplică “un alt task”; este aceeași entitate Task:

* Today este o selecție (mapping) către Taskurile existente
* dacă un task e “DONE” în Today, este “DONE” și în folderul original

### Atribute task

* Obligatorii:

  * titlu
  * folder
  * status
  * culoare/tag
  * **prioritate (obligatorie)**
* Recomandate (opționale, dar incluse în model):

  * deadline
  * estimare (minute)
  * descriere

### Culoare/tag (regulă)

Culoarea/tag este **mapată** la:

* prioritate și categorie (ex. priorități mari = culoare distinctă; categorii diferite = nuanțe/etichetă)

---

## Persistență (MySQL)

Date persistate în baza de date **MySQL**:

* foldere taskuri (`task_folders`)
* taskuri (`tasks`)
* selecția pentru Today (`today_tasks` — taskuri selectate pe zi)
* planificările (`plan_blocks`)
* activitățile reale (`actual_sessions`)
* statistici zilnice (opțional, “snapshot”)

**Configurare:** baza `schedule_manager` pe `localhost:3306`; proprietăți: `db.host`, `db.port`, `db.name`, `db.user`, `db.password`. Detalii în `DATABASE_SETUP.md`.

---

## Livrabile și calitate

* structură pe pachete: `ui`, `controller`, `domain`, `service`, `repository`, `integration`
* Javadoc pentru clasele principale; generare: `mvn javadoc:javadoc` → `target/site/apidocs/`
* validări input (end > start, date obligatorii, prevenire intervale invalide)
* UI responsive: operațiile DB rulate în afara thread-ului UI (ex. `SwingWorker`)

---

## Rulare aplicație

1. MySQL pornit; creează baza: `CREATE DATABASE schedule_manager;`
2. Din IDE: rulează `MainWindow.main()` (clasa: `schedulemanager.ui.MainWindow`)
3. Din Maven: `mvn exec:java -Dexec.mainClass="schedulemanager.ui.MainWindow"`
4. Parolă MySQL (dacă există): `-Ddb.password=parola`

---

## Teste

* Framework: **JUnit 5**; baza pentru teste: `schedule_manager_test`
* Rulare: `mvn test` sau Testing sidebar în VS Code
* Detalii: vezi `TESTE.md`

### Fișiere utile

| Fișier | Rol |
|--------|-----|
| `DATABASE_SETUP.md` | Setup MySQL |
| `TESTE.md` | Cum rulezi testele |
| `pom.xml` | Maven: Java 17, dependențe (MySQL, OkHttp, Gson, JUnit) |

---



---

English

**Project Title:** Schedule Manager (Java Desktop)

### Technologies and General Requirements

* Language: **Java 17**
* Build: **Maven**
* GUI: **Swing** (JFrame + JPanel)
* Persistence: **MySQL** (JDBC `mysql-connector-j` 8.3.0)
* Other dependencies: OkHttp (AI API), Gson (JSON)
* Application type: **single-user local**
* Storage: **local only** (no sync)
* Documentation: **Javadoc** for classes and methods; generate: `mvn javadoc:javadoc`
* Architecture: layered separation **ui / controller / domain / service / repository / integration**
* Validation: valid time ranges (end > start), required fields, data consistency
* Testing: **JUnit 5** — unit and integration tests (see `src/test/java/`)

---

## Application Goal

Schedule Manager is a desktop application that helps users (especially **people who procrastinate often**) become more productive by combining:

1. **advance day planning** (Plan Calendar), and
2. **tracking actual completed activities** (Actual Calendar / journal),

so the user can compare “what I planned” versus “what actually happened” and obtain statistics and actionable insights about their behavior.

**Main problem addressed:** the user cannot consistently improve productivity because they do not systematically measure the gap between intention (the plan) and behavior (execution). Without tracking and comparison, influential factors (procrastination, interruptions, shifting work to different time slots) remain invisible, and the user receives no actionable feedback. Schedule Manager operationalizes the principle “you can’t change what you can’t measure” through measurement, comparison, and clear feedback on deviations between plan and reality.

---

## Time Management Features (Plan vs Actual)

The application provides **two calendars for the same day**:

### 1) Plan Calendar (planning)

* The user plans the day in advance using **time blocks** (e.g., 09:00–10:30).
* A planned block contains:

  * `startTime`, `endTime`
  * `title/label`
  * `category`
  * `linkedTaskId` (optional — link to a task)

### 2) Actual Calendar (activity tracker)

* The user records real activities **manually** (enters the time interval).
* A real activity can be split into **multiple sessions** (e.g., the same task across 3 different intervals).
* A real session contains:

  * `startTime`, `endTime`
  * `title/label`
  * `category`
  * `linkedTaskId` (optional)

### 3) Comparison + insights

Comparing Plan vs Actual enables:

* measuring **accuracy** (quantitative and temporal)
* identifying deviations (delays, morning → evening shifts, overlaps, long breaks, etc.)
* generating insights about work patterns (when the user follows the plan and when they do not)

---

## AI (External API)

The app sends a daily summary (statistics + deviations + planned vs completed task list) to an **external HTTP API**. Default URL: `http://localhost:8080/api/insights`. The API returns:

* a short text with **insights**
* **recommendations** (e.g., reduce morning blocks, plan more realistically, reorder tasks, etc.)

---

## Statistics (minimum) — clear definitions

### 1) Quantitative accuracy (0..1)

Measures how much of the planned time was completed in total volume.

* `plannedMinutes = total minutes in Plan Calendar (per day)`
* `actualMinutes = total minutes in Actual Calendar (per day)`
* `quantAccuracy = min(actualMinutes, plannedMinutes) / plannedMinutes` (if plannedMinutes > 0; otherwise 0)

Example: planned 180 min, actual 180 min ⇒ 1.0.

### 2) Temporal accuracy (0..1)

Measures how much of the planned time was completed **within the planned time windows** (overlap).

* for each planned block, compute the **intersection** (overlap) with real sessions
* `overlapMinutes = total minutes of overlap Plan ∩ Actual`
* `tempAccuracy = overlapMinutes / plannedMinutes` (if plannedMinutes > 0; otherwise 0)

Example: planned 180 min in the morning, actual 180 min in the evening ⇒ quantitative 1.0, temporal close to 0.

### 3) Task-to-task comparison (explicit requirement)

For each selected/planned task:

* planned time per task vs actual time per task
* task status (TODO/DOING/DONE) correlated with real activity
* time deviation (minutes) and temporal deviation (how much of the task happened inside the planned slot)

---

## Task Management (Task Manager)

### 1) Hierarchical folders

* Tasks are organized in a tree structure of folders/subfolders.
* The user navigates folders and selects the tasks they want to do today.

### 2) Today list (tasks for today)

* Tasks selected for the current day appear in a separate GUI list: **Today**.
* In Today, the user can:

  * add/remove/reorder tasks: **YES**
  * mark status: **TODO / DOING / DONE**: **YES**
  * set color/tag: **YES**

**Important (consistency):** Today does not duplicate tasks; it references the same Task entity:

* Today is a selection/mapping to existing tasks
* if a task is marked “DONE” in Today, it is “DONE” in its original folder as well

### Task attributes

* Required:

  * title
  * folder
  * status
  * color/tag
  * **priority (required)**
* Recommended (optional, but included in the model):

  * deadline
  * estimate (minutes)
  * description

### Color/tag rule

Color/tag is **mapped** to:

* priority and category (e.g., high priority = distinct color; different categories = different shades/labels)

---

## Persistence (MySQL)

Data persisted in the **MySQL** database:

* task folders (`task_folders`)
* tasks (`tasks`)
* Today selection (`today_tasks` — tasks selected per day)
* planning blocks (`plan_blocks`)
* real activities (`actual_sessions`)
* (optional) daily statistics saved as a “snapshot”/report

**Configuration:** database `schedule_manager` on `localhost:3306`; properties: `db.host`, `db.port`, `db.name`, `db.user`, `db.password`. See `DATABASE_SETUP.md`.

---

## Running the Application

1. MySQL running; create DB: `CREATE DATABASE schedule_manager;`
2. From IDE: run `MainWindow.main()` (class: `schedulemanager.ui.MainWindow`)
3. From Maven: `mvn exec:java -Dexec.mainClass="schedulemanager.ui.MainWindow"`
4. MySQL password (if set): `-Ddb.password=yourpassword`

---

## Testing

* Framework: **JUnit 5**
* Integration tests: MySQL, database `schedule_manager_test` (create it first)
* Run: `mvn test` or Testing sidebar in VS Code
* Structure: see `TESTE.md`

---

## Deliverables and Quality

* package structure: `ui`, `controller`, `domain`, `service`, `repository`, `integration`
* Javadoc for core classes; generate: `mvn javadoc:javadoc` → `target/site/apidocs/`
* input validation (end > start, required fields, prevention of invalid intervals)
* responsive UI: DB operations executed off the UI thread (`SwingWorker`)

### Key Files

| File | Role |
|------|------|
| `DATABASE_SETUP.md` | MySQL setup |
| `TESTE.md` | How to run tests |
| `pom.xml` | Maven: Java 17, dependencies (MySQL, OkHttp, Gson, JUnit) |
