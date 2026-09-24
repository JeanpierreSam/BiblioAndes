# BiblioAndes

Aplicación Kotlin Multiplatform (Android + iOS) para consultar el catálogo de la
biblioteca, solicitar préstamos y controlar las fechas de devolución.
Examen Parcial U1 — Desarrollo de Aplicaciones Móviles — UPeU 2026-2.

**Integrante:** Jeanpierre Miranda

## Tecnologías
Kotlin 2.4 · Compose Multiplatform 1.11 · Material 3 · Navigation Compose ·
Koin 4.2 · Corrutinas + StateFlow · kotlinx-datetime. Sin red ni base de datos:
los datos son simulados en memoria.

## Estructura de paquetes
```
shared/src/commonMain/kotlin/pe/upeu/biblioandes/
├── domain/
│   ├── model/        Libro, Prestamo, EstadoPrestamo (sealed), Estudiante, ReglasPrestamo (RN-01..04)
│   ├── repository/   BibliotecaRepository (interfaz)
│   └── usecase/      ObtenerCatalogo, FiltrarCatalogo, ObtenerLibro, ObtenerPrestamos,
│                     SolicitarPrestamo, DevolverPrestamo, ObtenerEstudiante, ObtenerResumenInicio
├── data/
│   ├── local/        DatosSimulados, ProveedorFechaSistema
│   └── repository/   BibliotecaRepositoryFake (retardo 800 ms + bandera de error)
├── presentation/
│   ├── components/   LibroItem, PrestamoItem, Etiquetas, EstadosPantalla, Controles
│   ├── inicio/ catalogo/ detalle/ prestamos/ perfil/   (Route + Screen + ViewModel + UiState)
│   ├── navigation/   AppNavHost, Destinos
│   └── theme/        Color, Type, BiblioAndesTheme
├── di/               AppModule (Koin)
└── App.kt
```

## Decisiones de arquitectura
- **Clean + MVVM:** `presentation → domain ← data`. El dominio no conoce Compose, Koin ni la fuente de datos.
- **Reglas en el dominio:** RN-01..RN-04 viven en `ReglasPrestamo` y se aplican en `SolicitarPrestamoUseCase` y `ObtenerPrestamosUseCase`. Ningún composable las repite.
- **Estado del préstamo como sealed class:** cada estado lleva solo su dato (días restantes, fecha de devolución o días de atraso).
- **Sustitución localizada:** para usar la API basta crear `BibliotecaRepositoryRemoto` que implemente `BibliotecaRepository` y cambiar una línea en `AppModule.kt`.
- **UiState + StateFlow:** cada ViewModel expone un `StateFlow` de solo lectura; las pantallas son funciones del estado (state hoisting).
- **Fechas relativas:** los préstamos semilla se calculan desde hoy para que conserven su estado el día de la evaluación.

## Ejecución
- **Android:** abrir en Android Studio → configuración `androidApp` → Run.
- **iOS (Mac):** configuración `iosApp` o abrir `iosApp/iosApp.xcodeproj` en Xcode → Run.
- **Pruebas:** `./gradlew :shared:testAndroidHostTest`
- **Estado de error:** en `di/AppModule.kt` cambiar `simularErrorCatalogo = true`.

## Flujo Git
`main` (estable) ← `develop` (integración) ← `feature/<funcionalidad>-<apellido>`.
Commit evaluado: etiqueta `v1.0-unidad1`.
