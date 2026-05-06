# Drop & Hold: Balance Game

3D-fysiikkapohjainen tasapainopeli Androidille. Pelaaja kallistaa puhelinta pitääkseen esineet tasapainossa kelluvalla alustalla. Esineet putoavat alustalle kasvavalla tahdilla — peli päättyy kun alusta kallistuu liikaa ja esineet liukuvat pois.

## Tekniikka

| | |
|---|---|
| Kieli | Kotlin |
| Alusta | Android (minSdk 26, targetSdk 36) |
| 3D-moottori | LibGDX 1.14.0 + KTX 1.13.1 |
| Fysiikka | Bullet (C++ JNI) |
| Renderöinti | gdx-gltf 2.3.0 (PBR, IBL, varjot) — *placeholder käytössä* |
| ECS | Fleks 2.12 — *ei vielä integroitu* |
| UI (valikot) | Jetpack Compose + Material 3 |
| UI (peli-HUD) | LibGDX Scene2D |
| Persistenssi | Room 2.7.0 + Preferences DataStore |
| DI | Hilt 2.55 |
| Monetisaatio | AdMob 25.0.0 + Google Play Billing 8.3.0 |
| Build | AGP 8.10.0, Gradle 8.12, KSP 2.1.20, JVM 17 |
| Paketti | `com.finnvek.dropandhold` |

## Moduulirakenne

```
app/    — Android-sovellus: Compose UI, Hilt DI, Room, DataStore, Billing, AdMob
game/   — LibGDX-pelimoduuli: fysiikka, renderöinti, sensori-input, spawning, pisteytys, HUD
assets/ — 3D-mallit, tekstuurit, äänitiedostot (jaettu game-moduulille)
```

## Arkkitehtuuri

- **app/** käyttää Clean Architecturea: Compose → ViewModel → Repository → Room/DataStore
- **game/** ECS (Fleks) -valmis, Strategy pattern pelimoodeille (`GameModeStrategy`)
- Kommunikaatio: `PlayScreen` käynnistää `GameActivity`:n Intentillä, peli palauttaa `GameResult`-Parcelablen ActivityResultilla
- Molemmat jakavat Room-tietokannan ja DataStoren

### Pelin tilakoneen
```
INITIALIZING → CALIBRATING → COUNTDOWN → PLAYING → GAME_OVER
                                            ↕
                                          PAUSED
```

### Pelinkulku (end-to-end)
```
PlayScreen "PLAY CLASSIC"
  → GameActivity.createIntent(mode=CLASSIC)
  → CALIBRATING (2s sensori-keskiarvoistus)
  → COUNTDOWN (3s lähtölaskenta)
  → PLAYING (HUD: pisteet, kerroin, esineet, aika)
  → GAME_OVER (flip 70° tai tyhjä alusta 3s)
  → emitGameResult → setResult(GameResult Parcelable)
  → PlayViewModel.onGameFinished → tallennus runs-tauluun, isNewBest-vertailu
  → GameOverDialog: score/best/time/objects/chain/shards, "NEW BEST!" gold-otsikko
  → "PLAY AGAIN" käynnistää uudelleen, "Home" sulkee dialogin
```

## Pelimoodit

| Moodi | Tila | Toteutus |
|---|---|---|
| Classic | Ilmainen | ✅ Toteutettu (`ClassicModeStrategy`) |
| Daily Challenge | Ilmainen | ⬜ Ei toteutettu (siemenellä `YYYYMMDD`) |
| Quick Run | Ilmainen | ⬜ Ei toteutettu (30s aikaraja) |
| Zen | Pro | ⬜ Ei toteutettu (ei game overia) |
| Extreme | Pro | ⬜ Ei toteutettu (nopeampi, raskaampi) |
| Challenge | Pro | ⬜ Ei toteutettu (30–50 käsintehtua haastetta) |

---

## Toteutustilanne

### ✅ Valmiina

#### Build & infrastruktuuri
- Projektin scaffolding ja Gradle-konfiguraatio (kaikki riippuvuudet)
- Paketti `com.finnvek.dropandhold` (uudelleennimetty `finntek` → `finnvek`)
- ProGuard/R8-säännöt LibGDX, Bullet, gdx-gltf, Fleks, Room, Billing, AdMob
- detekt + ktlint + Android lint -tarkistukset (`lc` skripti)
- Semgrep + OWASP dependency-check (`sc` skripti)
- GitHub Actions: CodeQL, SonarCloud, Semgrep+OWASP, Qodana

#### `:game`-moduuli (LibGDX-pelisilmukka)
- `GameActivity` — Android host, lukee `EXTRA_MODE`, palauttaa `GameResult` ActivityResultilla
- `DropAndHoldGame` — ApplicationAdapter, mode-parametri, `onGameEnded` callback
- `PhysicsWorld` (Bullet) — alusta + `btGeneric6DofSpring2Constraint`, esineiden spawn/cleanup, flip-tunnistus
- `PlatformController` — PD-kontrolleri sensori → fysiikka-torque
- `SensorInputManager` — gyroskooppi/kiihtyvyysanturi-fallback, kalibrointi (2s keskiarvoistus), dead zone, alipäästösuodatin
- `ObjectSpawner` — aikavälin lyheneminen, materiaalipainotus, painokäyrä
- `GameStateMachine` — koko tilakoneen logiikka
- `GameRenderer` — proseduraalinen 3D (alusta, esineet, valaistus, kamera) — *PBR tulee Vaiheessa 10*
- `GameHud` (Scene2D) — pisteet, kerroin, esinemäärä, aika, status-overlay (Hold steady / Get ready / Paused / Game Over)
- `GameMode` — moodi-vakiot
- `ClassicModeStrategy` + `GameModeStrategy`-rajapinta
- `GameResult` Parcelable
- Keskitetyt configit: `PhysicsConfig`, `GameplayConfig`, `ControlConfig`, `RenderConfig`, `HudConfig`

#### `:app`-moduuli (Compose UI + data)
- `MainActivity` (`@AndroidEntryPoint`) + `DropAndHoldApp` (`@HiltAndroidApp`)
- Compose-navigaatio: Play / Collection / Settings (Bottom NavigationBar)
- `PlayScreen` — StatsCard parhailla, ActivityResultLauncher pelin käynnistämiseen, GameOverDialog tuloksen jälkeen
- `PlayViewModel` (Hilt) — observoi `RunDao`-Flow:t (best, simultaneous, total runs), tallentaa pelin tulokset, laskee `isNewBest`
- `GameOverDialog` (M3 AlertDialog) — score, best, time, objects, chain, +N shards, "NEW BEST!" gold-otsikko
- `CollectionScreen`, `SettingsScreen` — runkonäytöt (sisältö tulossa)
- Room-tietokanta: `DropAndHoldDatabase`, `RunDao` (Flow-pohjaiset kyselyt), `RunEntity` (mode/score/duration/objects/chain/shards/timestamp)
- Hilt-moduuli `AppModule`: `DropAndHoldDatabase`, `RunDao`-providerit
- M3-teema, `AppColors`, `Dimens`, `strings.xml` (kattaa kaikki spec:n stringit)

#### Build-status
- `./gradlew :app:assembleDebug` → BUILD SUCCESSFUL
- `app-debug.apk` 27 MB
- Pelaaja voi käynnistää, kalibroida, pelata, hävitä, nähdä tuloksen ja pelata uudelleen — *tarkista laitteella*

---

### 🔧 Seuraavaksi (priorisoitu)

#### Korkea prioriteetti (saa pelin julkaisuvalmiimmaksi)
- **Laitetestaus** — asenna `app-debug.apk` puhelimeen, varmista että kalibrointi/sensori/fysiikka tuntuvat hyviltä
- **Tauko-nappi** — Compose-overlay kun käyttäjä napauttaa HUD:n pause-painiketta (Resume/Restart/Settings/Quit)
- **gdx-gltf PBR -renderöinti** — korvaa proseduraalinen `GameRenderer` PBR-pohjaisella, IBL + varjot
- **Fleks ECS -integraatio** — refaktoroi `DropAndHoldGame`:n suorat kutsut Fleks-systeemeiksi (PhysicsSyncSystem, SpawnSystem, ScoringSystem, RenderSystem, HudSystem)
- **DataStore + asetukset-näytön kytkentä** — sensitivity slider, haptiikka/ääni/musiikki togglet, värisokeusmoodi, reduced motion, FPS-laskuri

#### Keskitason prioriteetti (täyttää spec:n)
- **Loput pelimoodit** — `DailyChallengeStrategy` (siemen `YYYYMMDD`), `QuickRunStrategy` (30s), `ZenModeStrategy` (Pro), `ExtremeModeStrategy` (Pro), `ChallengeModeStrategy` (Pro, 30–50 haastetta JSON:ssa)
- **Erikoisesineet** — Magnet, Balloon, IceCube, Bomb (komponentit + systeemit)
- **Ääni ja haptiikka** — `AudioManager` (libgdx-oboe lisättävä), `HapticsManager` (Vibrator API), 7 musiikkiraitaa, ~20 SFX
- **Progressio** — Shard-talous, kosmeettiset avaukset (7 alustaskinieä, 6 teemaa, 4 esinesarjaa), Collection-näytön ruudukko + equip-toiminto
- **Saavutukset** — ~20 saavutusta deklaratiivisilla ehdoilla, HUD-pop-up + Game Over -listaus
- **Tilastot-näyttö** — basic (free) + advanced (Pro-lukko) korttilayout
- **Streak-bonus** — Session-tason laskuri peräkkäisistä peleistä (+0.05x final-pisteisiin)
- **maxSimultaneous + bestChain Roomiin** — laajenna `RunEntity` ja DAO-kyselyt

#### Matala prioriteetti (julkaisua varten)
- **Monetisaatio** — Google Play Billing (`drop_and_hold_pro` IAP), AdMob (banner Play-näytöllä, interstitial joka 3. peli), UMP/GDPR-suostumus
- **Onboarding** — 3–4 Compose-pageria ensimmäisellä käynnistyksellä, "How to Play" toistaa asetuksissa
- **Jako-ominaisuus** — Canvas-pohjainen tuloskortti (1080×1350), `FileProvider` + `Intent.ACTION_SEND`
- **Saavutettavuus** — TalkBack-tuki, värisokeusmoodi (esineiden muoto-erottelu), reduced motion -toggle
- **Pre-game-näyttö** — Compose-välinäyttö "Hold your phone steady... 3, 2, 1" ennen GameActivityn käynnistämistä (nyt kalibrointi tehdään suoraan pelissä)
- **Game Over -bottom sheet** — vaihda nykyinen `AlertDialog` Material 3 `ModalBottomSheet`:ksi spec:n mukaiseksi
- **Pro Upgrade -näyttö** ja Pro-tilan tarkistukset eri näytöissä
- **Privacy Policy** -sivu (host esim. GitHub Pagesissa)
- **Play Store -assetit** — icon 512×512, feature graphic 1024×500, screenshotit, promo-video

#### Tekniset velat
- **Riippuvuusupgrade** (Linear-tehtävä FIN-11) — AGP → 9.1.0, Kotlin → 2.3.x, Compose BOM → 2026.03.00, KSP → 2.3.1, Gradle → 9.4.0. *HUOM:* AGP 9 poisti `BaseExtension`-API:n; Hilt-plugin täytyy tarkistaa migraation jälkeen — vaihtoehto on Koin (todennettu toimivaksi paikallisessa AGP 9 -kokeilussa).
- **Test-coverage** — yksikkötestit pisteytys-, kerroin-, sensori-suodatus- ja achievement-logiikalle. Compose- ja Room-integraatiotestit.
- **`useLegacyPackaging = false`** — tarkista että natives pakkautuvat APK:hon oikein release-buildissa

---

### 📋 Vaiheistettu suunnitelma

Yksityiskohtainen 16-vaiheinen toteutussuunnitelma talletettu `.planning/`-kansion ulkopuolella, käyttäjän plan-tiedostossa. Korkean tason vaiheet:

| Vaihe | Sisältö | Tila |
|---|---|---|
| 1 | Riippuvuudet ja moduulirakenne | ✅ |
| 2 | Tallennuskerros (Room + DataStore) | ✅ osittain (Room valmis, DataStore kytkemättä) |
| 3 | Compose-navigaatio ja perusnäytöt | ✅ runko, sisältö osittainen |
| 4 | LibGDX-bootstrap | ✅ |
| 5 | Sensori-input ja kalibrointi | ✅ |
| 6 | Fysiikkamaailma ja alusta | ✅ |
| 7 | Esineiden spawning, peruspeli | ✅ |
| 8 | Pisteytys, kerroin, Game Over | ✅ |
| 9 | Pelimoodit (Strategy) | 🟡 1/6 (Classic) |
| 10 | Visuaalinen polish (PBR, teemat) | ⬜ |
| 11 | Erikoisesineet | ⬜ |
| 12 | Audio + haptiikka | ⬜ |
| 13 | Progressio (shardit, kosmetiikka, saavutukset) | ⬜ |
| 14 | Onboarding, jako, saavutettavuus | ⬜ |
| 15 | Monetisaatio (Billing + AdMob + UMP) | ⬜ |
| 16 | Suorituskyky, testaus, release | ⬜ |

---

## CI/CD Pipeline

GitHub Actions workflows kansiossa `.github/workflows/`:

| Workflow | Tarkoitus | Tila |
|----------|---------|--------|
| `codeql.yml` | CodeQL security analysis (java-kotlin, JDK 17, manual build) | Active |
| `sonar.yml` | SonarCloud code quality analysis | Active |
| `security.yml` | Semgrep SAST + OWASP Dependency-Check (SARIF → Code Scanning) | Active |
| `qodana.yml` | JetBrains Qodana code quality (Community for Android) | Active |

Ulkoiset palvelut:
- **SonarCloud** — projekti `Insaner1980_Drop-Hold`, org `insaner1980`
- **Qodana Cloud** — org "Finnvek Dev", projekti "Drop-Hold"

Paikalliset työkalut:
- `scripts/security-check.sh` — Semgrep + OWASP dependency-check, tulokset `reports/`-kansioon

---

## Project Management

- **Linear:** Project "Drop & Hold" in Finnvek team, priority Medium, status In Progress
- **Linear URL:** https://linear.app/loikka1/project/drop-and-hold-9d28cd23b6d8
- **Milestone:** v0.1 — Playable Prototype
- **GitHub:** https://github.com/Insaner1980/Drop-Hold
- **Aktiivinen branch:** `rename-package-finnvek` (pohjana mainiin tehtävälle PR:lle)

---

## Laatutyökalut

```bash
lc    # ktlint + detekt + Android lint → reports/
sc    # semgrep + OWASP dependency-check → reports/
```

Local script: `scripts/security-check.sh`

## Spec

Täydellinen pelispesifikaatio: [`drop-and-hold-game-spec.md`](drop-and-hold-game-spec.md)
