# Tabluna

![Tabluna Logo/Banner (Optional)](https://cdn.discordapp.com/attachments/1509139544644128828/1509150485863596043/Gemini_Generated_Image_4s14zp4s14zp4s14.png?ex=6a18216a&is=6a16cfea&hm=01532be1ac2c6eb36e83eecf879f19289bdd961381ab0d051999a962edfa2864&)

Tabluna ist ein leistungsstarkes und hochgradig anpassbares Spigot/PaperMC-Plugin, das es Server-Administratoren ermöglicht, die Tabliste (Spielerliste), Scoreboards und Chat-Formatierungen auf ihrem Minecraft-Server vollständig zu gestalten. Mit umfassender Unterstützung für LuckPerms und dynamische Animationen bietet Tabluna eine einzigartige Möglichkeit, das Spielerlebnis zu personalisieren.

## ✨ Features

*   **Anpassbare Tabliste (Header/Footer)**: Gestalte den Kopf- und Fußbereich der Tabliste mit dynamischen Inhalten und Animationen.
*   **Spielerlisten-Objective**: Zeige Ping, Herzen oder andere Werte direkt in der Tabliste neben den Spielernamen an.
*   **Dynamische Scoreboards**: Erstelle vollständig anpassbare Sidebars mit Platzhaltern und Animationen.
*   **Spieler-Sortierung & Formatierung**: Sortiere Spieler in der Tabliste nach Rängen (LuckPerms-Gruppen) und formatiere ihre Namen mit Präfixen/Suffixen.
*   **Erweiterte Chat-Formatierung**: Passe das Chat-Format an und integriere LuckPerms-Präfixe/Suffixe.
*   **Chat-Filter**: Blockiere unerwünschte Wörter im Chat mit konfigurierbaren Ersatztexten.
*   **Regel-System**: Implementiere ein einfaches Regel-System, das Regeln im Chat oder als Buch anzeigt.
*   **LuckPerms-Integration**: Volle Unterstützung für LuckPerms-Präfixe, Suffixe und Gruppen für alle Anzeigeelemente.
*   **Animationen**: Nutze konfigurierbare Textanimationen in der Tabliste und auf Scoreboards.
*   **Platzhalter**: Eine Vielzahl von integrierten Platzhaltern für Spielername, Online-Spieler, Ping, Welt, Zeit, Datum und mehr.
*   **MiniMessage & Hex-Farben**: Nutze die volle Kraft von MiniMessage für erweiterte Textformatierung, Farbverläufe und Hex-Farben.

## 🚀 Installation

1.  Lade die neueste Version von [hier](https://github.com/your-repo/tabluna/releases) herunter (oder von SpigotMC/Modrinth, falls zutreffend).
2.  Platziere die `Tabluna-X.X.X.jar` Datei in den `plugins/` Ordner deines Spigot/PaperMC-Servers.
3.  Starte deinen Server neu oder lade die Plugins neu (`/reload` wird nicht empfohlen, nutze `/plugman reload Tabluna` oder einen vollständigen Neustart).
4.  Nach dem ersten Start werden die Konfigurationsdateien (`config.yml`, `animations.yml`, `plugin.yml`) im Ordner `plugins/Tabluna/` erstellt.

## ⚙️ Konfiguration

Alle Konfigurationen werden in den YAML-Dateien im Ordner `plugins/Tabluna/` vorgenommen.

*   **`config.yml`**: Hauptkonfigurationsdatei für Tabliste, Scoreboard, Chat und Regeln.
*   **`animations.yml`**: Definiert alle benutzerdefinierten Textanimationen.
*   **`plugin.yml`**: Enthält grundlegende Plugin-Informationen und Befehle (wird normalerweise nicht direkt bearbeitet).

**Wichtige Hinweise zur Formatierung:**
Tabluna verwendet die [MiniMessage](https://docs.adventure.kyori.net/minimessage/format.html) Syntax für erweiterte Textformatierung, einschließlich Hex-Farben (`<#RRGGBB>`), Farbverläufen (`<gradient:#RRGGBB:#RRGGBB>`) und Standard-Minecraft-Farbcodes (`&a`, `&l`, etc.).

## 🎮 Befehle & Berechtigungen

| Befehl             | Beschreibung                                  | Berechtigung             |
| :----------------- | :-------------------------------------------- | :----------------------- |
| `/tabluna reload`  | Lädt die Konfigurationen des Plugins neu.     | `tabluna.admin`          |
| `/tabluna`         | Hauptbefehl für Tabluna.                      | (Keine, siehe Unterbefehle) |
| `/chat clear`      | Löscht den Chat.                              | `tabluna.chat.admin`     |
| `/chat`            | Hauptbefehl für Chat-Funktionen.              | (Keine, siehe Unterbefehle) |
| `/rules`           | Zeigt die Server-Regeln an.                   | (Keine)                  |
| `/sb`              | Schaltet das Scoreboard für den Spieler um.   | (Keine)                  |

## 🤝 Abhängigkeiten

*   **LuckPerms (Soft-Dependency)**: Tabluna ist so konzipiert, dass es nahtlos mit LuckPerms zusammenarbeitet. Wenn LuckPerms auf dem Server installiert ist, werden Präfixe, Suffixe und Gruppen automatisch verwendet. Das Plugin funktioniert auch ohne LuckPerms, dann sind jedoch die LuckPerms-spezifischen Platzhalter nicht verfügbar.

## 🛠️ Entwicklung & Beitrag

Wenn du zur Entwicklung von Tabluna beitragen möchtest oder das Plugin aus dem Quellcode erstellen möchtest:

1.  Klone das Repository: `git clone https://github.com/your-repo/tabluna.git`
2.  Navigiere in das Projektverzeichnis: `cd Tabluna`
3.  Erstelle das Plugin mit Maven: `mvn clean package`
    *   Die fertige `.jar`-Datei findest du im `target/`-Ordner.

## 📄 Lizenz

Dieses Projekt steht unter der [MIT-Lizenz](LICENSE).

---

Viel Spaß beim Anpassen deines Servers mit Tabluna!
