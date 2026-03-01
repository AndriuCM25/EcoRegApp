#♻️ EcoRegApp

Sistema de Gestión de Residuos Industriales
📱 Aplicación Android Nativa
🏢 ECOLIM S.A.C.
📦 Versión 1.0 — Marzo 2026

1. Descripción General del Proyecto

EcoRegApp es una aplicación Android nativa desarrollada para ECOLIM S.A.C. que permite a los operarios industriales registrar, gestionar, analizar y exportar datos de residuos industriales de manera eficiente, segura y en tiempo real.

La aplicación está diseñada para funcionar en entornos industriales con conectividad limitada, almacenando los datos localmente y sincronizándolos cuando hay conexión disponible.

Tecnologías base

Plataforma: Android (Java)

Arquitectura: MVVM (Model – View – ViewModel)

Base de datos: Room (SQLite local)

Navegación: Navigation Component + BottomNavigationView

minSdk: API 24 (Android 7.0)

targetSdk: API 34 (Android 14)

2. Credenciales de Acceso (Demo)

| Usuario | Contraseña | Rol           | Turno  | Planta   |
| ------- | ---------- | ------------- | ------ | -------- |
| OP-01   | ecolim2026 | Operario      | Mañana | Planta A |
| OP-42   | ecolim2026 | Operario      | Tarde  | Planta A |
| OP-10   | ecolim2026 | Operario      | Noche  | Planta B |
| ADMIN   | ecolim2026 | Administrador | Mañana | Planta C |

3. Estructura del Proyecto

El proyecto sigue la estructura estándar de Android Studio con arquitectura MVVM:

app/src/main/java/com/ecolim/ecoregapp/
│
├── ui/
│   ├── activities/        LoginActivity, MainActivity
│   ├── fragments/         HomeFragment, RegistroFragment,
│   │                      HistorialFragment, ReportesFragment,
│   │                      ImportarFragment, ProfileFragment,
│   │                      SuccessFragment
│   └── adapters/          ResiduoAdapter
│
├── data/
│   └── local/
│       ├── entity/        Residuo.java
│       ├── dao/           ResiduoDao.java
│       └── database/      AppDatabase.java
│
├── viewmodel/             ResiduoViewModel.java
├── utils/                 SessionManager, FileManager
4. Pantallas y Módulos
4.1 Login

Autenticación por ID y contraseña

Usuarios hardcoded para pruebas

Persistencia de sesión con SharedPreferences

Redirección automática si existe sesión activa

4.2 Home

Saludo dinámico según la hora

Resumen de residuos del día

Barra de progreso de meta diaria (63 kg)

Estadísticas rápidas

Accesos directos a módulos

Últimos 5 registros recientes

4.3 Nuevo Registro

Selección de tipo mediante ChipGroup

Campos: peso, ubicación, zona y observaciones

Validación obligatoria de EPP para residuos peligrosos

Captura de foto evidencia con FileProvider

Guardado en Room con fecha automática

4.4 Historial

Lista completa de registros

Ordenados por fecha descendente

RecyclerView con adapter personalizado

4.5 Reportes

Filtros por periodo y fecha

Gráficos de barras y dona (MPAndroidChart)

Exportación a PDF e CSV

Estadísticas agregadas

4.6 Importar

Importación de archivos CSV

Inserción masiva con WorkManager

Estructura compatible definida

4.7 Perfil

Foto de perfil persistente

Edición de datos personales

Cambio de contraseña

Preferencias de sincronización

Limpieza de caché

Cierre de sesión seguro

4.8 Pantalla de Éxito

Confirmación visual del registro guardado

Acceso rápido a Home o nuevo registro

5. Arquitectura y Modelo de Datos

Entidad Residuo
| Campo         | Tipo    | Descripción                |
| ------------- | ------- | -------------------------- |
| id            | int     | PK autoincrement           |
| tipo          | String  | Tipo de residuo            |
| pesoKg        | double  | Peso en kg                 |
| fecha         | String  | yyyy-MM-dd                 |
| planta        | String  | Planta de origen           |
| zona          | String  | Zona                       |
| operarioId    | String  | ID operario                |
| turno         | String  | Turno                      |
| observaciones | String  | Notas o referencia de foto |
| eppConfirmado | boolean | Validación EPP             |
| sincronizado  | boolean | Estado de sincronización   |

Flujo de Datos
UI → ViewModel → Repository → DAO → Room (SQLite)

6. Dependencias Principales

Room

LiveData / ViewModel

Navigation Component

WorkManager

Retrofit

iText7

OpenCSV

MPAndroidChart

Lottie

Repositorio adicional requerido:

maven { url 'https://jitpack.io' }
7. Permisos y Configuración

Permisos usados:

<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

Uso obligatorio de FileProvider para cámara y exportación de archivos.

8. Navegación

La app utiliza Navigation Component con los siguientes destinos:

Home

Historial

Importar

Reportes

Perfil

Registro

Pantalla de éxito

El acceso a “Nuevo Registro” se realiza desde Home.

9. Categorías de Residuos

| Categoría | EPP requerido                    |
| --------- | -------------------------------- |
| Plástico  | No                               |
| Orgánico  | No                               |
| Papel     | No                               |
| Metal     | No                               |
| Vidrio    | No                               |
| Peligroso | Sí (guantes, mascarilla, lentes) |
| Pendiente | No                               |

10. Archivos de Datos de Prueba

| Archivo                 | Formato | Registros |
| ----------------------- | ------- | --------- |
| residuos_ecolim_100.csv | CSV     | 100       |
| residuos_ecolim_400.csv | CSV     | 400       |
| residuos_ecolim_100.pdf | PDF     | 100       |
| residuos_ecolim_100.txt | TXT     | 100       |

11. Instalación del Proyecto
Requisitos

Android Studio Hedgehog o superior

JDK 8+

Android SDK (API 24–34)

Pasos

Clonar el proyecto

Abrir en Android Studio

Verificar configuración de Gradle

Rebuild Project

Ejecutar en emulador o dispositivo físico

12. Próximos Pasos

Implementar modo oscuro

Sincronización con API REST

Estadísticas reales en perfil

Galería de fotos de evidencia

Filtros avanzados en historial

Dashboard administrativo

📜 Información Final

EcoRegApp v1.0
Proyecto académico / empresarial
ECOLIM S.A.C. — Marzo 2026
