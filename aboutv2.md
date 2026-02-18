
**Titlu proiect:** Schedule Manager (Java Desktop)

### Tehnologii și cerințe generale

* Limbaj: **Java**
* GUI: **Swing (JFrame + JPanel)**
* Persistență: **SQLite local** (fișier `.db`) accesată prin **JDBC** (`sqlite-jdbc`)
* Aplicație: **single-user local**
* Stocare: **local only** (fără sincronizare)
* Documentație: **Javadoc** pentru clasele principale și metodele publice
* Arhitectură: separare pe straturi **ui / controller / domain / service / repository / integration**
* Validări: intervale orare valide (end > start), câmpuri obligatorii, consistență date

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

Aplicația trimite un rezumat zilnic (statistici + deviații + listă taskuri planificate vs realizate) către un **API extern** (HTTP). API-ul returnează:

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

## Persistență (SQLite)

Date persistate în baza de date SQLite:

* foldere taskuri
* taskuri
* selecția pentru Today (taskuri selectate pe zi)
* planificările (plan blocks)
* activitățile reale (activity sessions)
* (opțional) statistici zilnice salvate ca “snapshot”/raport

DB este locală: fișier `.db` (ex. `schedule_manager.db`), acces prin JDBC.

---

## Livrabile și calitate

* structură pe pachete: `ui`, `controller`, `domain`, `service`, `repository`, `integration`
* Javadoc pentru clasele principale
* validări input (end > start, date obligatorii, prevenire intervale invalide)
* UI responsive: operațiile DB rulate în afara thread-ului UI (ex. `SwingWorker`)

---

Dacă vrei, următorul pas (tot “Cursor-ready”) este să-ți scriu:

* lista de **ecrane/panouri** din JFrame (layout + componente),
* **entitățile** (câmpuri exacte) + **tabelele SQLite** (DDL),
* interfețele `Repository` + `Service` (metode publice) ca skeleton.


---

English

**Project Title:** Schedule Manager (Java Desktop)

### Technologies and General Requirements

* Language: **Java**
* GUI: **Swing (JFrame + JPanel)**
* Persistence: **Local SQLite** (a `.db` file) accessed via **JDBC** (`sqlite-jdbc`)
* Application type: **single-user local**
* Storage: **local only** (no sync)
* Documentation: **Javadoc** for core classes and public methods
* Architecture: layered separation **ui / controller / domain / service / repository / integration**
* Validation: valid time ranges (end > start), required fields, data consistency

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

The app sends a daily summary (statistics + deviations + planned vs completed task list) to an **external HTTP API**. The API returns:

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

## Persistence (SQLite)

Data persisted in the SQLite database:

* task folders
* tasks
* Today selection (tasks selected per day)
* planning blocks (plan blocks)
* real activities (activity sessions)
* (optional) daily statistics saved as a “snapshot”/report

The DB is local: a `.db` file (e.g., `schedule_manager.db`), accessed via JDBC.

---

## Deliverables and Quality

* package structure: `ui`, `controller`, `domain`, `service`, `repository`, `integration`
* Javadoc for core classes
* input validation (end > start, required fields, prevention of invalid intervals)
* responsive UI: DB operations executed off the UI thread (e.g., using `SwingWorker`)

