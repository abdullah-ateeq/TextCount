# TextCount 📝

**TextCount** is a modern, fast, and feature-packed Android text analysis application built with **100% Kotlin** and **Jetpack Compose (Material 3)**. It provides real-time, deep statistical analysis of any text input, from character classification to grapheme cluster counting and word metrics.

---

## ✨ Features

- 📊 **Comprehensive Text Statistics**
  - **Characters**: Total count (with/without spaces/whitespace).
  - **Words**: Total word count with customizable tokenizer modes.
  - **Lines & Paragraphs**: Accurate line breaks, newline counts, and paragraph detection.
  - **Whitespace Analysis**: Separate tracking for spaces, tabs, newlines, and total whitespace.
  - **Character Classification**: Granular count of letters, digits, punctuation marks, and symbols.
  - **Word Analytics**: Average word length, longest word, and shortest word metrics.

- ⚙️ **Advanced Counting Modes**
  - **Character Modes**:
    - **User-Perceived (Grapheme Clusters)**: Counts visual characters accurately (including emojis, combining characters, etc.).
    - **Unicode Code Points**: Standard Unicode code point counting.
    - **UTF-16 Code Units**: Raw code unit count.
  - **Word Modes**:
    - **Smart Unicode**: Context-aware word tokenization adhering to Unicode standard rules.
    - **Whitespace Delimited**: Simple space-separated word counting.

- ⚡ **Real-Time Live Counting**
  - Instant live calculations with debouncing for large text inputs (supports up to 1,000,000 characters).

- 📜 **History & Restore**
  - Save text analysis snapshots for future reference.
  - One-tap restore to reload saved texts into the editor.
  - Option to restore the last active text upon reopening the app.

- 🎨 **Modern Material 3 UI & Customization**
  - **Theme Options**: Light, Dark, or System Default.
  - **Dynamic Color**: Full support for Material You dynamic themes on supported Android versions.
  - **User Experience**: Haptic feedback, smooth animations, and customizable average precision settings.

- 📋 **Export & Copy Reports**
  - Generate clean, formatted summary text reports to easily copy and share statistics.

---

## 🛠️ Architecture & Tech Stack

TextCount is built following modern Android development best practices and clean architecture principles:

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with **Material 3**
- **Architecture**: MVVM (Model-View-ViewModel) with Unidirectional Data Flow (UDF)
- **State Management**: `StateFlow` and `StateFlow` reactive streams
- **Navigation**: [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- **Target SDK**: Android 16 (API 37) / Min SDK: Android 8.0 (API 26)
- **Testing**: JUnit 4, Kotlinx Coroutines Test, Jetpack Compose UI Test, Espresso

---

## 📁 Project Structure

```text
com.example.textcount/
├── data/
│   ├── model/          # Data models (TextCountResult, TextCountHistory, Mode enums)
│   └── repository/     # Repositories for History and Settings persistence
├── domain/
│   ├── analysis/       # Core text analysis engine, classifiers & calculators
│   └── tokenizer/      # Unicode word tokenizer logic
├── ui/
│   ├── about/          # About screen UI
│   ├── history/        # History screen UI
│   ├── home/           # Main text editor & analysis results screen
│   ├── navigation/     # Jetpack Compose Navigation Graph
│   ├── settings/       # Settings and preference management screen
│   ├── splash/         # Splash screen
│   ├── theme/          # Material 3 colors, typography, and theme definitions
│   └── TextCountViewModel.kt # Main UI ViewModel
└── util/               # Formatter and helper utilities
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Ladybug (2024.2.1) or newer recommended.
- **JDK**: Java 11 or higher.
- **Android Device / Emulator**: Running Android 8.0 (API level 26) or higher.

### Building the Project

1. **Clone the repository**:
   ```bash
   git clone https://github.com/abdullah-ateeq/TextCount.git
   cd TextCount
   ```

2. **Open in Android Studio**:
   Open Android Studio and choose **Open an Existing Project**, selecting the `TextCount` directory.

3. **Build via Gradle Wrapper**:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run Unit Tests**:
   ```bash
   ./gradlew test
   ```

---

## 🤝 Contributing

Contributions are welcome! If you'd like to improve TextCount, report a bug, or propose a feature:

1. Fork the project repository.
2. Create your feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE) - see the LICENSE file for details.
