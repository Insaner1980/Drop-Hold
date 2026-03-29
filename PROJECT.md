# Drop & Hold: Balance Game

3D-fysiikkapohjainen tasapainopeli Androidille. Pelaaja kallistaa puhelinta pitääkseen esineet tasapainossa kelluvalla alustalla. Esineet putoavat alustalle kasvavalla tahdilla — peli päättyy kun alusta kallistuu liikaa ja esineet liukuvat pois.

## Tekniikka

| | |
|---|---|
| Kieli | Kotlin |
| Alusta | Android (minSdk 26, targetSdk 36) |
| 3D-moottori | LibGDX 1.14.0 + KTX 1.13.1 |
| Fysiikka | Bullet (C++ JNI) |
| Renderöinti | gdx-gltf 2.3.0 (PBR, IBL, varjot) |
| ECS | Fleks 2.12 |
| UI (valikot) | Jetpack Compose + Material 3 |
| UI (peli-HUD) | LibGDX Scene2D |
| Persistenssi | Room 2.7.0 + Preferences DataStore |
| DI | Hilt 2.55 |
| Monetisaatio | AdMob 25.0.0 + Google Play Billing 8.3.0 |
| Build | AGP 8.10.0, Gradle 8.12, KSP 2.1.20 |

## Moduulirakenne

```
app/    — Android-sovellus: Compose UI, Hilt DI, Room, DataStore, Billing, AdMob
game/   — LibGDX-pelimoduuli: fysiikka, renderöinti, sensori-input, spawning, pisteytys
assets/ — 3D-mallit, tekstuurit, äänitiedostot (jaettu game-moduulille)
```

## Arkkitehtuuri

- **app/** käyttää Clean Architecture: Compose → ViewModel → UseCase → Repository → Room/DataStore
- **game/** käyttää ECS (Fleks): Systems + Components + Entities, Strategy pattern pelimoodeille
- Kommunikaatio: app käynnistää GameActivityn Intentillä, peli palauttaa GameResult-Parcelablen ActivityResultilla
- Molemmat jakavat Room-tietokannan ja DataStoren

## Pelimoodit

| Moodi | Tila | Kuvaus |
|---|---|---|
| Classic | Ilmainen | Loputon, kasvava vaikeus |
| Daily Challenge | Ilmainen | Päivän siemenellä generoitu sekvenssi |
| Quick Run | Ilmainen | 30 sekunnin nopea moodi |
| Zen | Pro | Ei game overia, rentouttava |
| Extreme | Pro | Nopeampi, raskaampi |
| Challenge | Pro | 30-50 käsintehtua haastetta |

## Toteutustilanne

### Valmista
- Projektin scaffolding ja Gradle-konfiguraatio (kaikki riippuvuudet)
- Bullet-fysiikkamaailma (PhysicsWorld): alusta + constraint, esineiden spawning/tuhoaminen, flip-tunnistus
- Sensori-input (SensorInputManager): gyroskooppi/kiihtyvyysanturi, kalibrointi, dead zone, suodatus
- PD-kontrolleri (PlatformController): sensori → fysiikka-torque
- Perus 3D-renderöinti (GameRenderer): alusta, esineet, valaistus, kamera
- Esineiden spawning (ObjectSpawner): aikaväli, materiaalit, painon kasvu
- Pelisilmukka (GameStateMachine): kalibrointi → countdown → playing → game over
- Pisteytys: kerroin, menetysrangaistus
- Compose-navigaatio: Play / Collection / Settings (rungot)
- Room-tietokanta: runs-taulu
- Keskitetyt vakiot: PhysicsConfig, GameplayConfig, ControlConfig, RenderConfig
- Keskitetyt UI-vakiot: AppColors, Dimens, strings.xml

### Seuraavaksi
- HUD-overlay (pisteet, kerroin, tauko-nappi)
- Game Over -näyttö + Play Again
- gdx-gltf PBR -renderöinti (nykyinen on proseduraalinen placeholder)
- Fleks ECS -integraatio (korvaa nykyinen suora kutsumalli)
- Ääni ja haptiikka
- Loput pelimoodit
- Progressio (shardit, avattavat, saavutukset)
- Monetisaatio (AdMob, IAP)
- Onboarding, jako-ominaisuus, asetukset-näyttö

## CI/CD Pipeline

GitHub Actions workflows in `.github/workflows/`:

| Workflow | Purpose | Status |
|----------|---------|--------|
| `codeql.yml` | CodeQL security analysis (java-kotlin, JDK 17, manual build) | Active |
| `sonar.yml` | SonarCloud code quality analysis | Active |
| `security.yml` | Semgrep SAST + OWASP Dependency-Check (SARIF → Code Scanning) | Active |
| `qodana.yml` | JetBrains Qodana code quality (Community for Android) | Active |

External services:
- **SonarCloud** — project `Insaner1980_Drop-Hold`, org `insaner1980`
- **Qodana Cloud** — org "Finnvek Dev", project "Drop-Hold"

Local tools:
- `scripts/security-check.sh` — runs Semgrep + OWASP dependency-check locally, results in `reports/`

---

## Project Management

- **Linear:** Project "Drop & Hold" in Finnvek team, priority Medium, status In Progress
- **Linear URL:** https://linear.app/loikka1/project/drop-and-hold-9d28cd23b6d8
- **Milestone:** v0.1 — Playable Prototype
- **GitHub:** https://github.com/Insaner1980/Drop-Hold

### Pending Tasks
- Dependency upgrade: AGP → 9.1.0, Kotlin → 2.3.x, Compose BOM → 2026.03.00, KSP → 2.3.1, Gradle → 9.4.0

---

## Laatutyökalut

```bash
lc    # ktlint + detekt + Android lint → reports/
sc    # semgrep + OWASP dependency-check → reports/
```

Local script: `scripts/security-check.sh`

## Spec

Täydellinen pelispesifikaatio: `drop-and-hold-game-spec.md`
Toteutussuunnitelma: `.claude/plans/streamed-finding-floyd.md`
