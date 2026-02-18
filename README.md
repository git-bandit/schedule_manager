# Schedule Manager (Java Desktop)

## Student

Zlatovcen Florin-Petru

## Descriere

Schedule Manager este o aplicație desktop pentru managementul timpului și al taskurilor. Combina planificarea anticipativă a zilei (Plan Calendar) cu urmărirea activităților realizate (Actual Calendar), permițând utilizatorului să compare „cum a planificat” versus „cum a decurs” ziua și să obțină statistici și insight-uri despre comportamentul său. Aplicația adresează problema îmbunătățirii productivității prin măsurare, comparare și feedback clar asupra abaterilor dintre plan și realitate.

## Obiective

- **ob1** Planificare zilnică
  - sob11 Blocuri orare (Plan Calendar)
  - sob12 Legare opțională de taskuri
  - sob13 Validare intervale (end > start), prevenire suprapuneri
- **ob2** Urmărire activități
  - sob21 Înregistrare manuală sesiuni (Actual Calendar)
  - sob22 Sesiuni multiple pe același task
  - sob23 Legare opțională de taskuri
- **ob3** Statistici și comparare
  - sob31 Acuratețe cantitativă (0..1)
  - sob32 Acuratețe temporală (0..1) — overlap Plan ∩ Actual
  - sob33 Comparare task-to-task
- **ob4** Gestionare taskuri
  - sob41 Foldere ierarhice
  - sob42 Lista Today (add/remove/reorder, status TODO/DOING/DONE)
  - sob43 Atribute: titlu, folder, status, prioritate, deadline, estimare
- **ob5** Integrare AI
  - sob51 Trimite rezumat zilnic către API extern (HTTP)
  - sob52 Insight-uri și recomandări (URL: `http://localhost:8080/api/insights`)

## Arhitectura

Separare pe straturi: **ui** (Swing) → **controller** → **service** → **repository** → **domain**; plus **integration** (API AI). Baza de date MySQL, accesată prin JDBC. Operațiile DB rulează în afara thread-ului UI (SwingWorker).

## Tehnologii

- Java 17, Maven
- Swing (JFrame, JPanel)
- MySQL (mysql-connector-j)
- OkHttp, Gson (API AI)
- JUnit 5 (teste)

## Rulare

1. MySQL pornit; creează baza: `CREATE DATABASE schedule_manager;`
2. Din IDE: rulează `MainWindow.main()` (clasa `schedulemanager.ui.MainWindow`)
3. Din Maven: `mvn exec:java -Dexec.mainClass="schedulemanager.ui.MainWindow"`
4. Parolă MySQL: `-Ddb.password=parola`

Detalii: `DATABASE_SETUP.md` | Teste: `TESTE.md` | Javadoc: `mvn javadoc:javadoc`

## Funcționalități / Exemple utilizare

| Funcționalitate | Exemplu |
|-----------------|---------|
| Planificare zi | Adaugi bloc 09:00–10:30 „Meeting”, 14:00–16:00 „Coding” |
| Înregistrare activități | Notezi că ai lucrat 09:15–10:00 la Meeting, 14:30–15:45 la Coding |
| Statistici zilnice | Vezi acuratețe cantitativă și temporală, overlap minute |
| Lista Today | Selectezi taskuri din foldere, le adaugi la Today, le marchezi TODO/DOING/DONE |
| Insight-uri AI | API-ul returnează recomandări pe baza statisticilor și deviațiilor |
