# 🏛️ TimeTrek Bharat

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-SDK%2034-3DDC84.svg?style=flat&logo=android)](https://developer.android.com)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM-FF6F00.svg?style=flat)]()
[![Database](https://img.shields.io/badge/Database-Room%20DB-4285F4.svg?style=flat&logo=sqlite)](https://developer.android.com/training/data-storage/room)
[![AI Powered](https://img.shields.io/badge/AI-Google%20Gemini-8E24AA.svg?style=flat&logo=google)](https://deepmind.google/technologies/gemini/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**TimeTrek Bharat** is an immersive Android application designed to explore India’s glorious royal history, ancient civilisations, medieval dynasties, independence struggle, and modern regional developments. Powered by **Google Gemini AI**, the app serves as an interactive historical companion for students, history buffs, and curious minds.

---

## 🌟 Key Features

*   **🗺️ State-wise Regional History**: Discover detailed historical insights, royal rulers, capitals, and landmark events for states across Northern, Western, Southern, Eastern, Central, and North-Eastern India.
*   **⏳ Interactive Chronological Timeline**: Navigate through chronological eras—Ancient, Medieval, Colonial, and Modern—complete with key historical milestones and prominent rulers.
*   **🤖 Ask Historian AI (Powered by Google Gemini)**: An interactive AI Assistant capable of answering deep historical queries, detailing battles, architectural achievements, and cultural evolution.
*   **💾 Offline First Architecture**: Powered by Room DB and offline repository fallback to ensure smooth offline exploration and instant search performance.
*   **🎨 Royal Heritage UI & Material Design**: Built using custom Royal Heritage color themes (Saffron, Gold, Peacock Blue, Emerald Green) and responsive Material Design UI components.
*   **🎯 Adaptive Modern Launcher Icon**: Features a custom-designed adaptive vector icon blending the **Wheel of Time (Ashoka Chakra)** and an **Hourglass Time-Trek emblem**.

---

## 📐 Architecture & Tech Stack

The app follows the **MVVM (Model-View-ViewModel)** architectural pattern and **Clean Architecture** principles to promote maintainability, testability, and clear separation of concerns.

```
com.example.timetrekbharat
├── ai             # Gemini AI Service Integration
├── adapter        # RecyclerView Adapters for States & Timelines
├── db             # Room Database Entities, DAOs, and Converters
├── model          # Data Models for States, Eras, and Events
├── network        # Retrofit API Clients & Interfaces
├── repository     # Single Source of Truth Repository Pattern
├── ui             # Activities, Views, and Material Themes
└── viewmodel      # LiveData/Coroutines ViewModel Layer
```

### Libraries & Dependencies

*   **Language**: Kotlin 2.0 + Kotlin Coroutines & LiveData
*   **UI Engine**: ViewBinding, Material Components, ConstraintLayout, RecyclerView, SwipeRefreshLayout
*   **Networking**: Retrofit 2, Gson Converter, OkHttp3 Interceptor
*   **Local Storage**: Room Database + KSP (Kotlin Symbol Processing)
*   **Image Loading**: Glide
*   **AI Engine**: Google Generative AI SDK (`com.google.ai.client.generativeai`)

---

## 🚀 Getting Started

### Prerequisites

*   **Android Studio**: Ladybug / Jellyfish / Hedgehog or newer (2024+)
*   **JDK**: Java 11 or higher
*   **Android Device / Emulator**: API Level 24 (Android 7.0) or higher

### Installation & Build

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Karankumawa/TimeTrek-Bharat.git
   cd TimeTrek-Bharat
   ```

2. **Configure Gemini API Key** (Optional for AI Historian):
   Add your Gemini API Key in `gradle.properties` or set it in environment variables:
   ```properties
   GEMINI_API_KEY="YOUR_GEMINI_API_KEY"
   ```

3. **Build the Project**:
   Open the project in Android Studio and run:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run on Device**:
   ```bash
   ./gradlew installDebug
   ```

---

## 🎨 App Screenshots

*(Add app screenshots here)*

| Home Screen (States & Regions) | Chronological Timeline | Ask AI Historian |
| :---: | :---: | :---: |
| 🏰 | 📜 | 🤖 |

---

## 🤝 Contributing

Contributions are always welcome! Feel free to open an issue or submit a pull request:

1. Fork the Repository
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<p center="true">Made with ❤️ for India's Rich Heritage & History | <b>TimeTrek Bharat</b></p>
