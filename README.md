# 🥗 NutriTrack / Calorie Tracker App

Una aplicación nativa de Android desarrollada en **Kotlin** y **Jetpack Compose** diseñada para llevar un seguimiento diario de la ingesta de calorías y macronutrientes (proteínas, carbohidratos y grasas).

---

## 🚀 Características

- 📊 **Resumen Diario:** Visualización del total de calorías consumidas en comparación con el objetivo diario mediante una barra de progreso.
- 🥩 **Desglose de Macronutrientes:** Cálculo en tiempo real del total de Proteínas, Carbohidratos y Grasas.
- ➕ **Registro de Alimentos:** Formulario rápido e intuitivo para añadir comidas personalizadas con sus respectivos valores nutricionales.
- 🎯 **Objetivos Personalizables:** Diálogo interactivo para modificar las metas de calorías según las necesidades del usuario.
- 🎨 **Material Design 3:** Interfaz moderna y adaptable con componentes nativos de Jetpack Compose.

---

## 🛠️ Tecnologías y Librerías

- **Lenguaje:** [Kotlin](https://kotlinlang.org/)
- **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Arquitectura:** Componentes basados en estado reactivo (`remember`, `mutableStateOf`, `mutableStateListOf`).

---

## 📋 Requisitos Previos

- **Android Studio:** Jellyfish o superior (versión recomendada con soporte actualizado de Compose).
- **Min SDK:** 24 (Android 7.0 Nougat) o superior.
- **Target SDK / Compile SDK:** 34 o posterior.
- **JDK:** Version 17.

---

## ⚙️ Configuración del Proyecto

### 1. Clonar o copiar los archivos al proyecto

Asegúrate de que la estructura de carpetas en tu proyecto corresponda al paquete configurado:

```text
app/src/main/java/com/example/health/
└── MainActivity.kt
```

### 2. Dependencias Gradle (`app/build.gradle.kts`)

Verifica que las siguientes dependencias estén incluidas en tu archivo Gradle del módulo app:

```kotlin
dependencies {
    // Jetpack Compose BOM y Material 3
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.8.2")
}
```

### 3. Configuración del Manifest (`AndroidManifest.xml`)

La actividad principal debe estar registrada como la pantalla de inicio:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="NutriTrack"
        android:theme="@style/Theme.Material3.DayNight.NoActionBar">
        
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

---

## 🗂️ Próximas Mejoras Proyectadas (Roadmap)

- [ ] Persistencia de datos local utilizando **Room Database**.
- [ ] Gráficos avanzados de macronutrientes.
- [ ] Base de datos integrada con búsqueda rápida de alimentos comunes.
- [ ] Soporte para histórico y calendario de días anteriores.