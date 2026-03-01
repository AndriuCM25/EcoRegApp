♻️ EcoRegApp

Sistema de Gestión de Residuos Industriales
📱 Android Nativo (Java)
🏢 ECOLIM S.A.C.
📦 Versión 1.0 — Marzo 2026

📌 Descripción General

EcoRegApp es una aplicación Android nativa desarrollada para ECOLIM S.A.C., diseñada para que operarios industriales puedan registrar, gestionar, analizar y exportar residuos industriales de forma eficiente, segura y en tiempo real.

La aplicación funciona correctamente en entornos con conectividad limitada, almacenando los datos localmente y sincronizándolos cuando hay conexión disponible.

🛠️ Tecnologías Utilizadas

Lenguaje: Java

Plataforma: Android

Arquitectura: MVVM

Base de datos: Room (SQLite)

Navegación: Navigation Component + BottomNavigationView

Gráficos: MPAndroidChart

Exportaciones: PDF (iText7) y CSV (OpenCSV)

📱 Compatibilidad Android
Parámetro	Versión
minSdk	API 24 (Android 7.0)
targetSdk	API 34 (Android 14)
🔐 Credenciales de Acceso (Demo)
Usuario	Contraseña	Rol	Turno	Planta
OP-01	ecolim2026	Operario	Mañana	Planta A
OP-42	ecolim2026	Operario	Tarde	Planta A
OP-10	ecolim2026	Operario	Noche	Planta B
ADMIN	ecolim2026	Administrador	Mañana	Planta C
📂 Estructura del Proyecto
app/src/main/java/com/ecolim/ecoregapp/
│
├── ui/
│   ├── activities/
│   ├── fragments/
│   └── adapters/
│
├── data/
│   └── local/
│       ├── entity/
│       ├── dao/
│       └── database/
│
├── viewmodel/
├── utils/
│
app/src/main/res/
├── layout/
├── drawable/
├── navigation/
├── menu/
├── xml/
└── values/

Arquitectura MVVM con separación clara de responsabilidades.

🧩 Módulos Principales
🔑 Login

Autenticación por ID y contraseña

Persistencia de sesión con SharedPreferences

Login automático si hay sesión activa

🏠 Home

Saludo dinámico según hora

Resumen de residuos del día

Acciones rápidas

Últimos registros recientes

➕ Nuevo Registro

Selección de tipo de residuo

Validaciones según peligrosidad

Captura de foto evidencia

Guardado local con Room

📜 Historial

Lista completa de registros

Ordenados por fecha

RecyclerView optimizado

📊 Reportes

Filtros por fecha y tipo

Gráficos de barras y dona

Exportación a PDF y CSV

📥 Importar

Importación masiva desde CSV

Procesamiento en segundo plano con WorkManager

👤 Perfil

Foto de perfil persistente

Edición de datos personales

Preferencias de sincronización

Cierre de sesión seguro

🗂️ Modelo de Datos (Residuo)
Campo	Tipo	Descripción
id	int	PK autoincrement
tipo	String	Categoría del residuo
pesoKg	double	Peso en kg
fecha	String	yyyy-MM-dd
planta	String	Planta
zona	String	Zona
operarioId	String	ID del operario
turno	String	Turno
observaciones	String	Notas / Foto
eppConfirmado	boolean	Seguridad validada
sincronizado	boolean	Estado de sync
📦 Dependencias Principales

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
🔐 Permisos Requeridos
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

Uso obligatorio de FileProvider para cámara y archivos.

📄 Archivos de Prueba Incluidos
Archivo	Formato	Registros
residuos_ecolim_100.csv	CSV	100
residuos_ecolim_400.csv	CSV	400
residuos_ecolim_100.pdf	PDF	100
residuos_ecolim_100.txt	TXT	100
⚙️ Instalación y Ejecución
Requisitos

Android Studio Hedgehog+

JDK 8+

Android SDK (API 24–34)

Pasos

Clonar el repositorio

Abrir en Android Studio

Verificar gradle.properties

Ejecutar Rebuild Project

Run en emulador o dispositivo físico

🚀 Próximas Mejoras

🌙 Modo oscuro real

🌐 Sincronización con API REST

📊 Estadísticas reales en Perfil

🖼️ Galería de evidencias

🔔 Notificaciones push

🔍 Filtros avanzados en Historial

🧠 Clasificación con ML Kit

📈 Dashboard administrativo

📜 Licencia

Proyecto académico / empresarial
© ECOLIM S.A.C. — 2026
