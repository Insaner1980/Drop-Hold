# Drop & Hold — Game Design Specification

## Document Purpose

This is the complete design specification for **Drop & Hold: Balance Game**, a 3D physics-based balance game for Android. This document is intended to be read by Claude Code, which will create its own implementation plan. Do not treat this as a prompt — treat it as a product requirements document.

---

## 1. Game Overview

### Concept

Drop & Hold is a premium-feeling, offline 3D balance game controlled by the phone's gyroscope (with accelerometer fallback). The player tilts their physical phone to keep a floating platform balanced as objects drop onto it. Each object has weight and shape that shifts the center of gravity. The player must compensate by tilting the phone in real time.

The game ends when the platform tilts too far and objects slide off, or when the platform itself flips.

### Core Identity

- **Genre**: Physics puzzle / dexterity / survival
- **Platform**: Android (Kotlin)
- **Control**: Gyroscope primary, accelerometer fallback
- **Monetization**: Free with ads (interstitial between runs) + one-time Pro IAP
- **Backend**: None. Fully offline. All data stored locally.
- **Target audience**: All ages (casual gamers, anyone who picks up a phone)

### Design Pillars

1. **Instant understanding** — A new player must understand the game within 3 seconds of seeing gameplay. No text needed.
2. **Premium feel** — The game must look and feel like it was made by a professional studio, not a hobbyist. Polished 3D visuals, smooth animations, satisfying audio and haptics.
3. **"One more round"** — Sessions are 30-120 seconds. The restart friction must be near zero. The player should feel they can always do better.
4. **Physical connection** — The gyroscope control makes the game feel physical, not just digital. The phone IS the controller.

---

## 2. Core Gameplay

### The Platform

- A 3D platform (visualized as a floating disc, slab, or stylized board depending on theme) hovers in the center of the screen.
- The platform responds to the phone's physical tilt in real time.
- The platform has a maximum tilt angle. If exceeded, objects begin sliding off. If severely exceeded, the platform flips and the game ends.
- The platform should feel weighty — it doesn't snap instantly to the phone's angle but follows with slight inertia (tunable physics parameter).

### Objects

Objects drop onto the platform from above at regular intervals.

Object properties:
- **Weight**: Affects how much the center of gravity shifts.
- **Shape**: Sphere, cube, cylinder, irregular. Affects how the object settles and whether it rolls.
- **Size**: Varies. Larger objects are heavier and harder to balance.
- **Material** (visual only): Wood, metal, stone, crystal, rubber. Conveys weight visually.
- **Special objects** (appear rarely):
  - **Magnet**: Attracts nearby objects, grouping weight.
  - **Balloon**: Briefly lifts the platform slightly, giving a moment of relief.
  - **Ice cube**: Slides across the platform surface unpredictably.
  - **Bomb**: Ticking timer. If not knocked off by tilting, it explodes and scatters objects.

Drop behavior:
- Objects drop from a visible "incoming" indicator above the platform.
- The player can see the next 1-2 objects in a preview area.
- Drop interval starts slow (every 3 seconds) and accelerates over time.
- Object weight and variety increase with game progression within a run.

### Physics

- Objects have realistic 3D physics: they roll, slide, stack, collide with each other.
- When the platform tilts, gravity relative to the platform surface causes objects to slide in the tilt direction.
- Objects that slide off the edge fall away (with a satisfying visual/audio cue).
- Losing an object is not instant game over — it reduces score multiplier and the platform becomes lighter (easier temporarily).
- Game over occurs when: the platform tilt exceeds the flip threshold, OR all objects have slid off, OR the platform is empty for too long after dropping has started.

### Gyroscope / Accelerometer Input

- Primary input: Hardware gyroscope sensor for precise tilt detection.
- Fallback: If gyroscope is unavailable, use accelerometer (less precise but functional on all devices).
- The game must detect which sensor is available at startup and use the best one.
- Sensitivity setting: The player can adjust how responsive the platform is to tilt (1-10 scale, default 5).
- Calibration: At the start of each run, the current phone orientation is treated as "level". The player does not need to hold the phone perfectly flat — whatever angle they hold it at becomes the neutral position.
- Dead zone: A small dead zone around neutral prevents jitter from causing unwanted platform movement.

---

## 3. Game Modes

### 3.1 Classic (Free)

The core endless mode. Objects drop with increasing frequency and weight. Survive as long as possible and score as high as possible. This is the mode 90% of players will play most of the time.

### 3.2 Daily Challenge (Free)

A seeded run generated from the current date. Everyone gets the same object sequence on the same day. Local streak tracking (current streak, best streak, days completed). No backend required — seed is derived from `YYYYMMDD`.

### 3.3 Quick Run (Free)

A shorter, faster-paced mode. Objects drop more frequently from the start. Runs last ~30 seconds. Good for very quick sessions.

### 3.4 Zen Mode (Pro)

No game over condition. Objects drop slowly. No score. Just a relaxing balance experience. Ambient music. The platform never flips — it just wobbles. Good for stress relief.

### 3.5 Extreme Mode (Pro)

Much faster drops, heavier objects, more special objects. For players who want a real challenge. Separate leaderboard (local).

### 3.6 Challenge Mode (Pro)

A set of structured challenges with specific objectives. Examples:
- Balance 15 objects simultaneously
- Survive 90 seconds with only metal objects
- Clear 5 bombs without losing any other objects
- Reach score 1000 with sensitivity set to 1

30-50 handcrafted challenges at launch, grouped into difficulty tiers (Bronze, Silver, Gold, Platinum). Each completed challenge awards currency.

---

## 4. Scoring

### Base scoring

- Points are awarded continuously while objects remain on the platform.
- More objects = more points per second (exponential curve).
- Heavier objects contribute more to the score multiplier.
- Each object that slides off the platform reduces the multiplier by a step.

### Multiplier system

- Multiplier starts at x1.0.
- Every 10 seconds of survival without losing an object: multiplier increases by x0.2.
- Successfully balancing a special object (bomb, ice cube): bonus multiplier bump.
- Losing an object: multiplier drops by x0.3 (minimum x1.0).

### Streak bonus

- Consecutive runs in a single session multiply final scores slightly (x1.05, x1.10, x1.15...).
- This encourages "one more round" behavior.

---

## 5. Progression System

### Currency: "Shards"

Earned from:
- Completing runs (proportional to score)
- Daily challenge completion
- Achievement completion
- Challenge mode completion

Spent on:
- Unlocking platform skins
- Unlocking environment themes
- Unlocking object visual sets

### Unlockable Cosmetics

#### Platform Skins (visual only, no gameplay effect)
- Default: Clean white marble disc
- Wood: Natural wood grain
- Obsidian: Dark glass with subtle purple glow
- Neon: Glowing wireframe edges
- Gold: Luxurious gold surface
- Crystal: Transparent with light refraction
- Additional skins as the game grows

#### Environment Themes (background + lighting + ambient audio)
- **Skyfall** (default): Floating above clouds, blue sky, soft sunlight
- **Void**: Dark space, stars, subtle nebula glow
- **Sunset**: Warm orange/pink sky, long shadows
- **Arctic**: Cold blue lighting, snow particles
- **Neon City**: Cyberpunk cityscape below, neon reflections
- **Forest**: Green canopy, dappled light, bird sounds

Free players get 2-3 themes. Pro unlocks all.

#### Object Visual Sets
- Default: Realistic wood/metal/stone
- Geometric: Clean low-poly shapes
- Candy: Bright, toy-like materials
- Cosmic: Glowing, translucent, star-filled

---

## 6. Statistics

### Basic Stats (Free)

- Best score (overall and per mode)
- Highest number of simultaneous objects balanced
- Total objects balanced (lifetime)
- Total runs
- Total play time
- Longest single run (seconds)
- Best chain (longest time without losing an object)
- Daily challenge streak (current + best)

### Advanced Stats (Pro)

- Score history graph (last 30 runs)
- Average score per mode
- Object type breakdown (how many of each type balanced)
- Special object success rate (bombs cleared, ice cubes survived)
- Sensitivity usage data (which setting produces best scores)
- Mode-by-mode performance comparison
- Daily challenge history and scores

---

## 7. Achievements

### Beginner
- First Balance: Complete your first run
- Getting Started: Score 100 points
- Steady Hands: Balance 5 objects at once

### Intermediate
- Centurion: Score 1,000 points
- Object Juggler: Balance 10 objects simultaneously
- Marathon: Survive 60 seconds
- Bomb Disposal: Successfully clear 3 bombs in one run
- Ice Walker: Balance 3 ice cubes at the same time
- Weekly Player: Complete 7 daily challenges

### Advanced
- Master: Score 5,000 points
- Tower of Balance: Balance 15 objects simultaneously
- Iron Will: Survive 120 seconds
- Streak Lord: Maintain a 30-day daily challenge streak
- Perfectionist: Complete a 60-second run without losing a single object

### Hidden
- Flip Out: Flip the platform completely upside down
- Speed Demon: Survive 30 seconds in Extreme mode
- Zen Master: Spend 10 minutes total in Zen mode

Achievements should have visual badges that display on the achievements screen and unlock a small Shard reward.

---

## 8. Settings

### Gameplay
- Sensitivity: Slider 1-10 (default 5). Controls how much the platform responds to phone tilt.
- Vibration/Haptics: On/Off (default On)
- Sound Effects: On/Off (default On)
- Music: On/Off (default On)
- Sound volume: Slider
- Music volume: Slider

### Display
- Theme: Light / Dark / System (affects menus only; in-game uses environment theme)
- Reduced Motion: On/Off (reduces particle effects and animations for accessibility/performance)
- Colorblind Mode: On/Off (adjusts object colors to be distinguishable for common types of color vision deficiency)
- FPS Counter: On/Off (hidden in advanced settings)

### About
- Privacy Policy link
- Version info
- Licenses / open source credits
- Reset all data (with confirmation dialog)
- Restore purchases

---

## 9. Visual Design Direction

### Overall Aesthetic

The game must look **premium, polished, and professional**. Think: the quality of a Monument Valley or Alto's Odyssey. Not hyper-realistic, but stylized and beautiful.

Key visual qualities:
- **Clean geometry**: Smooth surfaces, rounded edges, no jagged or cheap-looking elements.
- **Rich lighting**: Dynamic lighting that creates depth. Soft shadows, ambient occlusion.
- **Color harmony**: Each theme has a carefully chosen color palette. Colors are vibrant but not garish.
- **Depth of field**: Subtle blur on far background elements to create focus.
- **Particle effects**: Subtle dust motes, light rays, falling leaves, or snow depending on theme. Never excessive.

### 3D Rendering Requirements

- The game is rendered in real-time 3D using a physics engine.
- Minimum 60 FPS on mid-range devices (2022+ phones).
- Objects must have physically correct lighting (not flat-shaded).
- The platform must cast shadows on itself and on objects.
- Objects should have subtle reflections or specular highlights.
- The background/environment should have parallax depth (not a flat image).

### Typography

- Use a clean, modern sans-serif font for all UI.
- In-game HUD text (score, timer) should be large, bold, and easy to read at a glance.
- Menu text should be well-spaced and follow Material Design 3 typography scale.
- Numbers in the score display should use a monospace or tabular-figure variant to prevent layout jumping.

### Color Palette (Default "Skyfall" theme)

Primary palette:
- Sky gradient: `#87CEEB` to `#4A90D9` (light blue to medium blue)
- Cloud white: `#F0F4F8`
- Platform: `#E8E0D8` (warm marble)
- Accent warm: `#FF8C42` (used for highlights, score pop, buttons)
- Accent cool: `#5B9BD5` (secondary interactive elements)
- Text dark: `#1A1A2E`
- Text light: `#FFFFFF`
- Danger: `#E74C3C` (tilt warning, near-flip state)
- Success: `#2ECC71` (achievements, positive feedback)

Each environment theme overrides this with its own palette.

---

## 10. UI and Navigation Structure

### Navigation Model

Bottom navigation with 3 destinations:
1. **Play** — Main screen, access to game modes
2. **Collection** — Unlockables, achievements, stats
3. **Settings** — All settings

### Screen Map

#### 10.1 Play Screen (Home)

The first screen the player sees. Purpose: get into a game as fast as possible.

```
┌──────────────────────────────────┐
│  [logo/title: Drop & Hold]        │
│                                  │
│  ┌────────────────────────────┐  │
│  │   Best Score: 4,280        │  │
│  │   Objects Record: 14       │  │
│  └────────────────────────────┘  │
│                                  │
│  ╔════════════════════════════╗  │
│  ║      ▶  PLAY CLASSIC      ║  │
│  ╚════════════════════════════╝  │
│                                  │
│  ┌─Daily──┐ ┌─Quick──┐          │
│  │ Day 12 │ │ 30 sec │          │
│  │ ★★★    │ │  mode  │          │
│  └────────┘ └────────┘          │
│                                  │
│  ┌─Zen────┐ ┌Extreme─┐ ┌Chall─┐│
│  │  PRO   │ │  PRO   │ │ PRO  ││
│  └────────┘ └────────┘ └──────┘│
│                                  │
│  [────Play────|──Collection──|──Settings──]│
└──────────────────────────────────┘
```

- Large prominent "Play Classic" button
- Quick access cards for other modes
- Pro modes visible but marked with subtle Pro badge
- Current best score and record displayed prominently

#### 10.2 Pre-Game Screen

Brief screen shown before each run starts.

```
┌──────────────────────────────────┐
│                                  │
│     Hold your phone steady...    │
│                                  │
│     [visual: phone icon with     │
│      level indicator showing     │
│      calibration happening]      │
│                                  │
│     Calibrating in 3... 2... 1   │
│                                  │
│     Mode: Classic                │
│     Best: 4,280                  │
│                                  │
└──────────────────────────────────┘
```

This screen serves dual purpose:
1. Calibrates the gyroscope neutral position
2. Gives the player a moment to prepare physically

After calibration (2-3 seconds), the game starts immediately with a smooth transition.

#### 10.3 In-Game Screen (Most Important Screen)

```
┌──────────────────────────────────┐
│  Score: 1,240    x2.4   ⏸ Pause │
│  Objects: 8      Time: 0:47     │
│                                  │
│         [Next object preview]    │
│                ↓                 │
│                                  │
│      ┌──────────────────┐        │
│      │                  │        │
│      │   3D PLATFORM    │        │
│      │   WITH OBJECTS   │        │
│      │                  │        │
│      └──────────────────┘        │
│                                  │
│  [Tilt warning indicators        │
│   at screen edges when           │
│   platform nears danger zone]    │
│                                  │
└──────────────────────────────────┘
```

HUD elements:
- **Top left**: Current score (large, bold) + current multiplier
- **Top right**: Pause button
- **Below score**: Object count + elapsed time
- **Top center**: Next object preview (small 3D preview of what's coming)
- **Center**: The 3D game scene (platform + objects + environment)
- **Screen edges**: Tilt danger indicators — subtle red glow appears on the edge toward which the platform is dangerously tilting

The HUD should be minimal and not obstruct the 3D scene. Semi-transparent overlays.

#### 10.4 Pause Screen

Overlay on top of the game scene (blurred background).

Options:
- Resume
- Restart
- Settings (quick access to sensitivity)
- Quit to Menu

#### 10.5 Game Over Screen

Presented as a Material 3 bottom sheet sliding up, or a full-screen overlay.

```
┌──────────────────────────────────┐
│                                  │
│          GAME OVER               │
│   (or "NEW BEST!" in gold)       │
│                                  │
│     Score:  2,840                │
│     Best:   4,280                │
│     Time:   1:12                 │
│     Objects: 11                  │
│     Best Chain: 34s              │
│                                  │
│     +45 Shards earned            │
│                                  │
│  ╔═══════════════════════════╗   │
│  ║     ▶  PLAY AGAIN        ║   │
│  ╚═══════════════════════════╝   │
│                                  │
│  [Share Result]    [Home]        │
│                                  │
└──────────────────────────────────┘
```

- If new best score: celebratory animation + "NEW BEST!" text in gold
- Play Again button is the most prominent element
- Share generates a result card image (see section 13)

#### 10.6 Collection Screen

Tabs or scrollable sections:

```
┌──────────────────────────────────┐
│  Collection                      │
│                                  │
│  [Platforms] [Themes] [Objects]  │
│  [Achievements] [Stats]         │
│                                  │
│  ┌─────┐ ┌─────┐ ┌─────┐       │
│  │Marb.│ │Wood │ │Obsi.│       │
│  │ ✓   │ │ 🔒  │ │ 🔒  │       │
│  │     │ │200☆ │ │500☆ │       │
│  └─────┘ └─────┘ └─────┘       │
│                                  │
│  Shards: 340 ☆                   │
│                                  │
│  [────Play────|──Collection──|──Settings──]│
└──────────────────────────────────┘
```

- Grid of unlockable items with preview thumbnails
- Locked items show the Shard cost
- Currently equipped items are highlighted
- Shard balance always visible

#### 10.7 Stats Screen

Part of Collection or accessible from it.

Displays all statistics from Section 6 in clean card-based layout.
Basic stats visible to all. Advanced stats show a "Pro" lock overlay.

#### 10.8 Settings Screen

Standard Material 3 settings layout with grouped sections as defined in Section 8.

#### 10.9 Pro Upgrade Screen

Accessible from locked content or a subtle "Go Pro" card on the Play screen.

```
┌──────────────────────────────────┐
│                                  │
│     ✦ Drop & Hold Pro ✦           │
│                                  │
│  ✓ Remove all ads                │
│  ✓ Zen Mode                      │
│  ✓ Extreme Mode                  │
│  ✓ Challenge Mode (50 levels)    │
│  ✓ All themes & skins            │
│  ✓ Advanced statistics           │
│  ✓ Support indie development     │
│                                  │
│  ╔═══════════════════════════╗   │
│  ║   Upgrade — $2.99         ║   │
│  ║   One-time purchase       ║   │
│  ╚═══════════════════════════╝   │
│                                  │
│  [Restore Purchase]              │
│                                  │
└──────────────────────────────────┘
```

---

## 11. Onboarding / Tutorial

First launch only. 3-4 screens, no text walls.

1. **Screen 1**: Visual of phone tilting → platform tilting. Caption: "Tilt your phone to balance the platform."
2. **Screen 2**: Object drops onto platform → phone tilts to compensate. Caption: "Objects will drop. Keep them balanced."
3. **Screen 3**: Platform tilts too far → objects slide off. Caption: "Don't let them fall!"
4. **Screen 4**: "Ready?" → Launches directly into first Classic run.

The onboarding should use actual 3D renders of the game, not static illustrations. Brief, visual, skippable.

After the first run, never show onboarding again. A "How to Play" option in Settings allows replaying it.

---

## 12. Audio Design

### Music

- Ambient, atmospheric soundtrack that creates mood without being distracting.
- Each environment theme has its own music loop (or at least audio variation).
- Music intensity subtly increases as the game progresses (more objects, higher danger).
- Zen mode has its own dedicated calm track.

### Sound Effects

- **Object drop**: Satisfying "thud" when object lands on platform. Varies by material (wood thunk, metal clang, stone thud, crystal chime).
- **Object slide**: Scraping/sliding sound when objects begin to move on the tilting platform.
- **Object fall**: Whoosh + distant impact when object falls off the edge.
- **Multiplier increase**: Subtle ascending chime.
- **Multiplier decrease**: Subtle descending tone.
- **Danger warning**: Low rumble or heartbeat when platform nears flip threshold.
- **Game over**: Dramatic crash/scatter sound.
- **New best score**: Celebratory fanfare.
- **UI navigation**: Subtle click/tap sounds for menu interactions.
- **Special objects**: Each special object type has a unique sound signature.

### Haptics

- **Light tap**: When object lands on platform.
- **Medium pulse**: When object slides off the edge.
- **Strong pulse**: When platform reaches danger zone tilt.
- **Impact burst**: On game over (platform flip).
- **Celebration pattern**: On new best score.
- **Subtle continuous**: Very light vibration proportional to platform instability (optional, can be intense on battery — off by default, toggle in settings as "Enhanced Haptics").

---

## 13. Share Feature

At game over, the player can tap "Share" to generate a result card image.

The card includes:
- Game logo
- Score
- Mode played
- Time survived
- Objects balanced
- A small visual of the platform (optional: last moment screenshot)
- App store link / QR code

The image is generated locally and shared via Android's native share sheet. No backend required.

---

## 14. Monetization Details

### Free Tier

- Classic, Daily Challenge, Quick Run modes
- 2-3 environment themes
- 2 platform skins
- 1 object visual set
- Basic statistics
- Interstitial ads between runs (not every run — every 3rd run or so, never mid-game)
- Banner ad on the Play screen only (not during gameplay, not on other screens)

### Pro Tier (One-time IAP, $2.99)

- All game modes (Zen, Extreme, Challenge)
- All environment themes
- All platform skins
- All object visual sets
- Advanced statistics
- No ads whatsoever
- Future content updates included

### Monetization Rules

- No ads during gameplay. Ever.
- No rewarded ads (watch ad for bonus). This cheapens the premium feel.
- No consumable IAPs. No gems, coins, or pay-to-play energy systems.
- No gameplay advantages for paying. Pro is purely content + convenience + cosmetics.
- Ad frequency should feel reasonable. Players should not feel punished for being free users.

### Implementation

- Google Play Billing Library (latest stable version)
- Ads via AdMob (interstitial + banner)
- Pro purchase state stored locally with Google Play verification

---

## 15. Technical Requirements

### Platform & Language
- Android, Kotlin
- Minimum SDK: API 26 (Android 8.0) — covers 95%+ of active devices
- Target SDK: Latest stable

### 3D Rendering
- Claude Code should evaluate and choose the best approach for 3D rendering. Options include but are not limited to: LibGDX with KTX extensions, OpenGL ES directly, Godot as embedded engine, or any other approach that produces high-quality 3D output.
- The critical requirement is: 60 FPS on mid-range devices, realistic lighting, physics-correct object behavior, and a premium visual feel.

### Physics Engine
- Realistic 3D physics for objects on the platform.
- Objects must collide with each other and with the platform edges.
- Gravity direction relative to the platform must respond to tilt.
- The physics must feel satisfying — objects should have weight and momentum.

### Sensor Handling
- Use Android `SensorManager` for gyroscope (`Sensor.TYPE_GYROSCOPE`) and accelerometer (`Sensor.TYPE_ACCELEROMETER`).
- Implement sensor fusion if beneficial (e.g., `TYPE_GAME_ROTATION_VECTOR`).
- Handle sensor unavailability gracefully.
- Low-latency sensor reading (use `SENSOR_DELAY_GAME`).

### Data Storage
- Room database or DataStore for local persistence (stats, settings, unlocks, achievements).
- SharedPreferences acceptable for simple key-value settings.

### Performance Targets
- 60 FPS on devices from ~2022 onward
- 30 FPS minimum on older devices (graceful degradation)
- Cold start to gameplay in under 3 seconds
- Memory usage under 200MB during gameplay

---

## 16. Accessibility

- **Colorblind mode**: Adjusts object colors and UI colors to be distinguishable. Use shape and pattern in addition to color.
- **Reduced motion**: Disables particle effects, reduces animation intensity. Game remains fully playable.
- **Adjustable sensitivity**: Already part of core design (Section 2).
- **Haptics toggle**: Can be turned off completely.
- **Sound toggle**: Can be turned off completely. Game is fully playable without sound.
- **Screen reader support**: Menu screens should support TalkBack for navigation. In-game is inherently visual/physical but score announcements could be made accessible.
- **One-handed play**: The game is naturally one-handed (hold phone, tilt). Ensure all menus are reachable with one hand.

---

## 17. Privacy & Compliance

- **Privacy policy**: Required for Play Store. Must be hosted at a public URL.
- **No user accounts**: No personal data collection beyond what AdMob requires.
- **No analytics** in v1 (can be added later with Firebase Analytics if desired).
- **IARC age rating**: Likely "Everyone" — no violence, no mature content.
- **GDPR/consent**: AdMob consent dialog for EU users (required by Google).
- **No internet required**: Game works fully offline. Internet only needed for ads and IAP verification.

---

## 18. Play Store Assets Needed

- **App icon**: 512x512 PNG. Should be distinctive and recognizable. Suggestion: A stylized platform with objects balanced on it, viewed from a slight angle. Clean, geometric, premium feel.
- **Feature graphic**: 1024x500 PNG.
- **Screenshots**: Minimum 4, recommended 6-8. Mix of gameplay and menu screens.
- **Promo video**: 30-second gameplay capture showing the core loop. Optional but highly recommended.
- **Short description**: ~80 characters.
- **Full description**: ~4000 characters with feature highlights.
- **Category**: Games > Puzzle or Games > Casual

---

## 19. Future Considerations (Not in v1)

These are explicitly NOT in scope for the initial release but should be considered in architecture:

- Multiplayer (competitive balance challenge via shared seed)
- Online leaderboards (Google Play Games Services)
- Cloud save (Google Play Games Services)
- Additional game modes
- Seasonal themes/events
- Apple iOS port (if using cross-platform engine)
- Watch companion (tilt with smartwatch)

The codebase should be structured so that adding these later does not require a major refactor.

---

## 20. Success Metrics

The game is successful if:
- A new player understands how to play within their first 5 seconds
- Average session length exceeds 3 minutes (multiple runs)
- Day-1 retention exceeds 30%
- The Play Store listing screenshots look professional enough that users cannot tell it was made by a solo developer
- The game runs at 60 FPS on a Google Pixel 7 or equivalent

---

## 21. Game Name

**Drop & Hold: Balance Game**

- **Drop & Hold** is the short name used in-app, on the icon, and in marketing.
- **Drop & Hold: Balance Game** is the full Play Store listing name for ASO purposes.
- "Drop" describes objects falling onto the platform. "Hold" describes the player's task of keeping them balanced. Two words, instant understanding.
- The name is unique — no existing game on Play Store or App Store uses this name.
- Package name suggestion: `com.finntek.dropandhold`

---

*End of specification.*
