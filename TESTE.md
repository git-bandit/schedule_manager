# Teste unitare

## Rulare teste

### Din VS Code / Cursor

1. Deschide **Testing** în sidebar (icon fluture/benzile)
2. Click pe **Run All Tests** sau rulează testele individuale

### Din Maven (terminal)

```bash
mvn test
```

---

## Structura testelor

| Fișier | Ce testează |
|--------|-------------|
| `TaskServiceTest` | Validare task, CRUD (creare, citire, actualizare, ștergere) |
| `FolderServiceTest` | Foldere: creare, subfoldere, ștergere (cu restricții) |
| `ScheduleServiceTest` | Plan blocks: creare, validare, suprapuneri, ștergere |
| `TodayListTest` | Lista Today: adăugare, eliminare, duplicate |
| `StatsServiceTest` | Statistici: zilnice, overlap, task stats |
| `TrackingServiceTest` | Sesiuni actuale: creare, ștergere, filtrare după dată |

---

## Configurare

Testele folosesc MySQL, baza de date `schedule_manager_test`.
Creează-o înainte: `CREATE DATABASE schedule_manager_test;`
