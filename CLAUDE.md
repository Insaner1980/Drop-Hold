# Drop & Hold — Projektikohtaiset ohjeet

## Projektin kuvaus

Android 3D-tasapainopeli: LibGDX + Bullet-fysiikka + Jetpack Compose. Katso `PROJECT.md` tilannekuvasta ja `drop-and-hold-game-spec.md` täydellisestä spesifikaatiosta.

## Moduulit

- `app/` — Compose UI, Hilt, Room, DataStore, Billing, AdMob. Ei pelologiikkaa.
- `game/` — LibGDX peli: fysiikka, renderöinti, input, spawning, pisteytys. Ei Android UI -komponentteja (paitsi GameActivity).
- `assets/` — Jaettu game-moduulille sourceSets-konfiguraatiolla.

## Vakioiden sijainti

Älä kovakoodaa arvoja pelin koodiin. Kaikki säädettävät parametrit ovat config-tiedostoissa:

| Vakiot | Tiedosto |
|---|---|
| Fysiikka (massa, kitka, gravitaatio, timestep) | `game/.../config/PhysicsConfig.kt` |
| Pelimekaniikka (intervallit, game over -ehdot, pisteytys) | `game/.../config/GameplayConfig.kt` |
| Ohjaus (herkkyys, PD-kontrolleri, sensori, dead zone) | `game/.../config/ControlConfig.kt` |
| Renderöinti (kamera, valaistus, värit, esinekoot) | `game/.../config/RenderConfig.kt` |
| UI-värit | `app/.../ui/theme/Color.kt` (AppColors) |
| UI-mitat | `app/.../ui/theme/Dimens.kt` |
| Merkkijonot | `app/src/main/res/values/strings.xml` |

## Koodityylit

- **detekt.yml** projektin juuressa — Compose PascalCase sallittu, config magic numberit sallittu, Bullet wildcards sallittu
- **.editorconfig** — ktlint-säännöt (Compose-nimeäminen, wildcards)
- Formatointi: ktlint hoitaa automaattisesti (`ktlint -F`)

## Build

```bash
./gradlew :app:assembleDebug    # Debug APK
./gradlew :app:assembleRelease  # Release APK (vaatii signing-konfiguraation)
```

## Laatutarkistukset

```bash
lc    # ktlint + detekt + Android lint → reports/
sc    # semgrep + OWASP dependency-check → reports/
```

`reports/`-kansio on .gitignoressa.

## Arkkitehtuurisäännöt

- `game/`-moduuli ei saa riippua `app/`-moduulista (yksipuolinen riippuvuus: app → game)
- Compose-näytöt eivät kutsu pelologiikkaa suoraan — kommunikaatio Intent/ActivityResult-kautta
- Jokainen pelimoodi toteuttaa `GameModeStrategy`-rajapinnan
- Fysiikkaobjektit kierrätetään poolauksella (ei GC-paineita)

## CI/CD

GitHub Actions: CodeQL, SonarCloud, Semgrep + OWASP, Qodana (kaikki aktiivisia).
SonarCloud project: `Insaner1980_Drop-Hold`. Linear project: "Drop & Hold" (Finnvek, Medium, In Progress).
Milestone: v0.1 — Playable Prototype.

## AGP 9 -huomio

Käytetään vielä AGP 8.10 + erillistä kotlin-android-pluginia. Päivitys AGP 9.1.0 + Kotlin 2.3.x + Compose BOM 2026.03.00 odottaa — tehdään paikallisesti seuraavassa sessiossa (Linear: FIN-11).
