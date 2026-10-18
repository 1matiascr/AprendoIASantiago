# 🗺️ Aprendo IA Jugando en Santiago

**Aprendo IA Jugando en Santiago** es una aplicación móvil nativa para Android diseñada para promover la exploración cultural e histórica de la ciudad de Santiago a través de la ludificación (gamificación), el uso de códigos QR y cápsulas educativas orientadas a la Inteligencia Artificial.

Los usuarios pueden registrarse, seleccionar su perfil de explorador, consultar hitos turísticos en un mapa interactivo, escanear códigos QR situados en puntos estratégicos de la ciudad, sumar puntos de experiencia y desbloquear contenido multimedia interactivo.

---

## 🚀 Características Principales

* **Gamificación y Sistema de Puntos:** Los exploradores ganan puntos de experiencia (+100 PTS) y suben de nivel a medida que escanean hitos turísticos en la ciudad.
* **Escáner QR Integrado con Google ML Kit:** Integración con `GmsBarcodeScanning` para un escaneo rápido, seguro y nativo sin necesidad de solicitar permisos manuales de cámara.
* **Procesamiento de Contenido JSON:** Capacidad para deserializar contenido en formato JSON dentro de los códigos QR para mostrar cápsulas informativas y enlaces a videos educativos.
* **Persistencia Local con SQLite:** Almacenamiento seguro en la base de datos local para la gestión de usuarios, roles (Admin/Creador/Usuario), progreso y puntos turísticos completados.
* **Interfaz Moderna y Animada (Material 3):** UI dinámica desarrollada con Jetpack Compose que incluye animaciones táctiles, estados reactivos y barras de progreso.
* **Mapa e Hitos Turísticos:** Consulta e interacción con puntos de interés histórico y cultural en Santiago.

---

## 🛠️ Tecnologías Utilizadas

* **Lenguaje:** [Kotlin](https://kotlinlang.org/)
* **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
* **Diseño:** [Material Design 3](https://m3.material.io/)
* **Escáner QR:** [Google ML Kit / Barcode Scanning (GMS)](https://developers.google.com/ml-kit/vision/barcode-scanning)
* **Persistencia de Datos:** [SQLite](https://developer.android.com/training/data-storage/sqlite) (`SQLiteOpenHelper`)
* **Arquitectura:** MVVM (Model-View-ViewModel) + Estado Unidireccional
* **Navegación:** State-based Navigation en Jetpack Compose

---

## 📂 Estructura del Proyecto

```text
com.example.aprendoiasantiago/
│
├── data/                             # Persistencia y Fuentes de Datos (SQLite)
│   ├── MapDatabaseHelper.kt          # Manejo de la BD local SQLite
│   └── PlayerDatabaseHelper.kt       # Manejo de usuarios/jugadores en SQLite
│
├── model/                            # Clases de Datos (Entidades)
│   ├── Player.kt                     # Modelo de Jugador
│   └── QrPoint.kt                    # Modelo del Punto QR / Hito
│
├── ui/                               # Interfaz de Usuario (Pantallas y ViewModels)
│   ├── theme/                        # Estilos, colores y tipografía
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   │
│   ├── components/                   # Componentes visuales reutilizables
│   │   └── FeatureItem.kt
│   │
│   ├── screens/                      # Pantallas Composable
│   │   ├── WelcomeScreen.kt          # 1. Bienvenida / Inicio
│   │   ├── PlayerSelectionScreen.kt  # 3. Selección de Jugador
│   │   ├── MapScreen.kt              # 5. Mapa de Santiago
│   │   ├── DashboardScreen.kt        # 4. Principal / Dashboard
│   │   ├── DetallePuntoScreen.kt     # 6. Detalle del Punto
│   │   └── RegistroJugadorScreen.kt  # 2. Registro de Jugador
│   │
│   └── viewmodels/                   # Lógica de Estado y Controladores (MVVM)
│       ├── DashboardViewModel.kt
│       ├── DetallePuntoViewModel.kt
│       └── RegistroViewModel.kt
│
└── MainActivity.kt                   # Punto de entrada y gestión de navegación
```

---

## 🔧 Instalación y Configuración

### Requisitos Previos
* **Android Studio:** Ladybug (2024.2.1) o superior.
* **JDK:** Versión 17 o superior.
* **SDK Mínimo:** API 24 (Android 7.0 Nougat) o superior.
* **Google Play Services:** Requerido en el dispositivo/emulador para el escáner ML Kit.

### Pasos para Ejecutar
1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/tu-usuario/aprendo-ia-santiago.git
   ```
2. **Abrir el proyecto en Android Studio.**
3. **Sincronizar Gradle:** Espera a que las dependencias (`play-services-code-scanner`, `compose`, etc.) se descarguen correctamente.
4. **Ejecutar en un dispositivo físico o emulador con Google Play Services instalado.**

---

## 📱 Guía de Uso

1. **Bienvenida:** Pantalla inicial con introducción al proyecto.
2. **Selección / Registro de Jugador:** Crea un nuevo perfil o selecciona un jugador existente guardado en la base de datos SQLite.
3. **Dashboard / Panel Principal:** Consulta tu nivel de explorador, total de puntos obtenidos y accesos directos.
4. **Mapa de Santiago:** Revisa los hitos turísticos disponibles en la ciudad.
5. **Detalle del Punto y Escaneo QR:**
   * Selecciona un hito turístico para ver su información y ubicación.
   * Haz clic en **Escanear Código QR** para iniciar el escáner de Google.
   * Apunta a un código QR con formato JSON válido como el siguiente:
     ```json
     {
       "titulo": "Plaza de Armas y la IA",
       "descripcion": "Descubre la historia del centro histórico con cápsulas dinámicas.",
       "url": "https://www.youtube.com/shorts/LcrPS1-jacs"
     }
     ```
   * ¡Al escanear correctamente recibirás **+100 PTS** de experiencia y se actualizará tu progreso!

---

## 📄 Licencia

Este proyecto está bajo la Licencia **MIT** - consulta el archivo [LICENSE](LICENSE) para más detalles.

---

Desarrollado como proyecto educativo para fomentar la tecnología, la educación en Inteligencia Artificial y el turismo cultural en Santiago de Chile. 🇨🇱✨