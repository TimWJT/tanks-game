# Tanks Game (Java)

A **2D tank battle game** built in **Java** using the **Processing** graphics library.  
---

## Gameplay
- Control your tank and battle against other players.
- Eliminate other players to gain points.
- Winner is declared at game end, where the player with the most amount of points wins.

## Controls
- ← / → — Move tank left/right (uses fuel).
- ↑ / ↓ — Aim barrel up/down
- W / S — Increase / decrease shot power
- Space — Fire

- R — If the game has ended: restart from level 0.  If the game is ongoing and your health < 100: use Repair Kit (heals).
- F — Get additional Fuel.
- P — Get additional Parachutes.
- X — Use Large Projectile powerup

- N — Next level
- M — Previous level
- L — Refresh current stage
- O — Restart game
---

## Tech Stack
- **Language:** Java 8
- **Build Tool:** Gradle
- **Graphics:** Processing (`org.processing:core:3.3.7`)
- **Utilities:** Google Guava
- **Testing:** JUnit 5

---

## 🚀 How to Run
### Prerequisites
- Java 8 (JDK 1.8)
- Gradle Wrapper (included — no need to install Gradle globally)

### Steps
```bash
# Clone the repository
git clone https://github.com/TimWJT/tanks-game-java.git
cd tanks-game-java

# Run the game
.\gradlew.bat :app:run   # Windows
./gradlew :app:run       # Mac/Linux
That visual impact can make your repo stand out a lot more.
