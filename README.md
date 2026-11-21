# 🧭 Aprendo IA Jugando en Santiago

Aplicación móvil gamificada desarrollada en **Kotlin** y **Jetpack Compose** bajo arquitectura **MVVM** que invita a explorar puntos culturales e históricos de Santiago de Chile mediante geolocalización, escaneo de códigos QR, cápsulas educativas en video y desafíos fotográficos.

---

## 🚀 Funcionalidades Principales

* **Exploración Georreferenciada:** Visualización interactiva de locaciones emblemáticas de Santiago con cálculo de distancia y estado de avance (completadas vs. pendientes).
* **Gamificación y Puntajes:**
  * Escaneo de códigos QR en terreno.
  * Visualización de cápsulas educativas en video (`VideoCapsule`).
  * Registro de selfies turísticas con bonificación de **+100 puntos**.
* **Gestión de Usuarios y Roles:** Registro con validación de credenciales (alias, contraseña mínima de 4 caracteres y selección de avatar) y panel de progreso (`Dashboard`).
* **Integración de Servicios REST:**
  * Conexión con backend propio (`BackendApiService`) para sincronización de jugadores, puntos QR y videos.
  * Consulta meteorológica en tiempo real para Santiago (`WeatherApiService`).

---

## 🛠️ Stack Tecnológico y Arquitectura

* **Lenguaje:** Kotlin (JVM 11)
* **Interfaz de Usuario:** Jetpack Compose + Material 3 + Navigation Compose
* **Arquitectura:** MVVM (*Model - View - ViewModel* + *Repository*)
* **Red y Serialización:** Retrofit 2.11 + Gson Converter
* **Mapas y Escaneo:** Google Maps Compose, OSMDroid y Google Play Services Code Scanner
* **Persistencia Local:** SQLite (`PlayerDatabaseHelper`, `MapDatabaseHelper`)
* **Pruebas Unitarias:** JUnit 4, MockK 1.13 y Kotlinx Coroutines Test

### Estructura del Proyecto

```text
app/src/
├── main/java/com/example/aprendoiasantiago/
│   ├── data/          # GameRepository y helpers de base de datos local (SQLite)
│   ├── model/         # Entidades de dominio (Player, QrPoint, VideoCapsule)
│   ├── network/       # Interfaces Retrofit (BackendApiService, WeatherApiService)
│   └── ui/
│       ├── components/# Componentes reutilizables de UI
│       ├── screens/   # Pantallas Compose (Dashboard, Mapa, Detalle, Registro, etc.)
│       └── viewmodels/# DashboardViewModel, DetallePuntoViewModel, RegistroViewModel
└── test/java/com/example/aprendoiasantiago/
    ├── ui/viewmodels/ # DetallePuntoViewModelTest, DashboardAndRegistroViewModelTest
    ├── GameRepositoryTest.kt
    └── NetworkAndAuthTest.kt

---

## 🧪 Pruebas Unitarias y Cobertura (IE 3.2.1)

Se implementó una suite de **22 pruebas unitarias** utilizando **JUnit 4**, **MockK** y **Coroutines Test** para validar la lógica de negocio sin dependencias de dispositivo físico:

| Suite de Pruebas | Cobertura Funcional |
| --- | --- |
| `DetallePuntoViewModelTest` | Cálculo de puntajes por QR y video educativo, y parseo de datos. |
| `DashboardAndRegistroViewModelTest` | Estado inicial del dashboard, validación de formulario de registro, manejo de visibilidad de contraseña y flujo asíncrono de creación de jugador. |
| `GameRepositoryTest` | Filtrado de locaciones completadas/pendientes, asignación de +100 puntos por selfie turística, validación de puntos positivos y operaciones CRUD en repositorio. |
| `NetworkAndAuthTest` | Autenticación por alias/contraseña, respuestas HTTP 200 y error HTTP 400 en `BackendApiService`, y manejo seguro de `IOException` en `WeatherApiService`. |

### Resultados de Cobertura (JaCoCo / Android Studio Coverage)

* **Total de pruebas ejecutadas:** 22 aprobadas (`22 tests passed`, 0 fallos).


* **Cobertura del módulo `ui.viewmodels`:** **100% de clases** y **95% de líneas cubiertas** (superando el umbral mínimo del 80% requerido):


* `DashboardViewModel`: 100% clases / 100% líneas.


* `DetallePuntoViewModel`: 100% clases / 100% líneas.


* `RegistroViewModel`: 100% clases / 90% líneas.





> **Evidencia:** Revisar captura del reporte de cobertura en `docs/evidencia_cobertura.png` (o en la raíz del repositorio).

### Ejecución de Pruebas

```bash
./gradlew testDebugUnitTest


```

---

## 📦 Firma Digital y Generación de APK Release (IE 3.3.1)

La aplicación cuenta con configuración de firma criptográfica para distribución en entorno de producción:

1. **Keystore (`.jks`):** Generado mediante Android Studio (`aprendoiasantiago_key.jks`) con validez de 25 años.
2. **Configuración Gradle (`app/build.gradle.kts`):** Bloque `signingConfigs.create("release")` vinculado al `buildType` de `release`.
3. **Ícono Adaptativo:** Generado mediante *Image Asset Studio* (`@mipmap/ic_launcher` e `@mipmap/ic_launcher_round`).
4. **Artefacto Compilado:** `app/release/app-release.apk` firmado y verificado.

### Compilación desde Terminal

```bash
./gradlew assembleRelease

```

---

## ⚙️ Instalación y Ejecución Local

1. Clonar el repositorio:
```bash
git clone https://github.com/1matiascr/AprendoIASantiago.git

```


2. Abrir el proyecto en **Android Studio** (JDK 11 / Compile SDK 35).
3. Sincronizar dependencias de Gradle (**Sync Project with Gradle Files**).
4. Ejecutar en emulador o instalar directamente el archivo firmado `app/release/app-release.apk` en un dispositivo Android (API 24+).

```

```

<img width="1918" height="1009" alt="evidencia_cobertura" src="https://github.com/user-attachments/assets/652f84b3-a446-44ac-aacf-5ccfc8d843e1" />

<img width="1904" height="862" alt="image" src="https://github.com/user-attachments/assets/219bf971-35fb-4601-aa0a-04dcf8438aed" />
https://trello.com/invite/b/6aff038304fa53b6c1c6fc7d/ATTI43ba6dcc55e3df26ec402ed8bc6d74ec7E337002/aprendo-ia-jugando-sprint
