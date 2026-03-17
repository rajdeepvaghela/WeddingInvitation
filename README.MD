# Wedding Invitation

> Online wedding invitation website and Android app built using Compose Multiplatform (CMP), targeting both JS (web) and Android platforms.

<p align="center">
  <a href="https://kotlinlang.org/">
    <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.0.0-blue?logo=kotlin">
  </a>
  <a href="https://supabase.io/">
    <img alt="Supabase" src="https://img.shields.io/badge/Supabase-Realtime%20DB-3ECF8E?logo=supabase">
  </a>
  <a href="https://rajdeepvaghela.github.io/WeddingInvitation">
    <img alt="Live Demo" src="https://img.shields.io/badge/Live-Demo-brightgreen?logo=google-chrome">
  </a>
  <br>
  <img alt="Platform" src="https://img.shields.io/badge/Platform-Web%20|%20Android-informational">
  <img alt="Made With" src="https://img.shields.io/badge/Made%20With-Compose%20Multiplatform-blueviolet">
  <img alt="Languages" src="https://img.shields.io/badge/Kotlin-98.9%25-orange?logo=kotlin">
  <img alt="Languages" src="https://img.shields.io/badge/HTML-1.1%25-yellow?logo=html5">
</p>

---

## ✨ Features

- **Personalized Invitations**:  
  Each guest receives a unique invitation URL, containing a base64-encoded JSON hash with their name and preferences. The website decodes this information client-side to display a tailored invitation.

- **Cross-Platform**:  
  Built with Kotlin Compose Multiplatform (CMP) — runs as a modern web app and a native Android application from a shared codebase.

- **Supabase Realtime Analytics**:  
  Integrated with [Supabase](https://supabase.io/) for:
  - Tracking real-time analytics of website events (button clicks, page visits, etc.).
  - Storing custom analytics like "Add to Calendar", "Venue Clicked", or generating Kankotri PDFs.
  - Logging guest location (city/country, via IP) when loading the invite (IP location gathered through a 3rd-party service, as seen in `strings.xml`).

- **WhatsApp Sharing via Android**:  
  - Android app allows hosts to generate customized links for each guest.
  - Direct sharing to WhatsApp (and other installed apps) using native share APIs.
  - Configurable invitation options (e.g., show/hide contact number, details, reception info).

- **Rich UI and Visuals**:
  - Themed design using Compose's `Material3`.
  - Responsive, decorative backgrounds and invitation layout.

- **Event Information and Actions**:
  - Guests can add the wedding event directly to Google Calendar.
  - View wedding/reception venue on Google Maps.
  - Download PDF invites (Gujarati Kankotri and Reception Card).
  - See stay details, contact number, and more—configurable per guest.

- **Persistent Settings**:  
  App uses multiplatform settings to locally preserve guest/viewer configurations.

- **Developer-friendly**:
  - Clean separation of multiplatform code.
  - Easy to extend with new invitation fields or analytics events.
  - Uses modern libraries for HTTP, serialization, and DI.

---

## 🖥️ Screenshots

<!-- Replace with actual screenshots -->
| Web/Desktop          | Android App          |
|----------------------|---------------------|
| ![Web Screenshot](docs/web_screenshot.png) | ![Android Screenshot](docs/android_screenshot.png) |

---

## ⚡ Usage

### Web

Deployed at: [rajdeepvaghela.github.io/WeddingInvitation](https://rajdeepvaghela.github.io/WeddingInvitation)

Personalized link example:
```
https://rajdeepvaghela.github.io/WeddingInvitation#<base64-hash>
```
Where `<base64-hash>` is a base64-encoded JSON string like:
```json
{
  "name": "Guest Name",
  "showReceptionDetails": true,
  "showStayDetails": false,
  "showContactNumber": true
}
```

### Android

- Build from source or download the release.
- App enables creating, customizing, and sharing personalized invitation links directly to WhatsApp or other apps.

---

## 🛠️ Tech Stack

- **Kotlin 2.x**
- **Compose Multiplatform (CMP)**: Shared UI — Web & Android (`composeApp` module)
- **Supabase**: Realtime DB and analytics
- **Ktor**: HTTP client (for web & Android)
- **Napier**: Logging
- **Multiplatform Settings**: for local guest/session storage

See [`composeApp/build.gradle.kts`](composeApp/build.gradle.kts) and [`gradle/libs.versions.toml`](gradle/libs.versions.toml) for dependencies.

---

## 📊 Analytics

- **Supabase Table Structure**: Logs event name, userId, and optionally city/country from IP.
- **Event tracking example** (Kotlin):
  ```kotlin
  supabase?.sendEvent("Add to Calendar Clicked", userId)
  ```
- **Location/IP Tracking**: On first visit (web), the app requests IP info and stores location analytics in Supabase.

---

## 🚦 Customizing Invites

To customize recipient content, encode your desired JSON (example above) to base64 and append it as the URL hash.

- Android app takes care of this when generating/sharing links.
- You can also manually construct/invite via the website.

---

## 📂 Project Structure

- `composeApp/` — Main multiplatform Compose application
- `composeApp/src/commonMain/kotlin/...` — Shared business/UI logic
- `composeApp/src/commonMain/composeResources/` — Shared resources (strings, images)
- `androidApp/` (if present) — Android launcher module

---

## 👤 Author

**[Rajdeep Vaghela](https://github.com/rajdeepvaghela)**

---

## 🔗 More

- [Source code on GitHub](https://github.com/rajdeepvaghela/WeddingInvitation)
- [Live Demo (Web)](https://rajdeepvaghela.github.io/WeddingInvitation)
