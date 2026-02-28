# EcoRegApp — ECOLIM S.A.C.
## Aplicación Android para gestión de residuos sólidos

---

## 📋 Descripción
App móvil Android que digitaliza el registro de residuos sólidos en instalaciones industriales. Permite registrar, importar, exportar y reportar datos de residuos cumpliendo la norma **NTP 900.058**.

---

## 🚀 Cómo abrir en Android Studio

1. Abrir **Android Studio** (versión Hedgehog 2023.1.1 o superior)
2. `File → Open` → seleccionar la carpeta `EcoRegApp/`
3. Esperar que Gradle sincronice las dependencias (puede tardar 2-3 minutos)
4. Conectar un dispositivo Android (API 24+) o usar el emulador
5. Presionar ▶️ **Run**

---

## 🔐 Credenciales de prueba
- **ID Operario:** OP-01, OP-42, OP-10 o ADMIN
- **Contraseña:** `ecolim2026`

---

## 📦 Estructura del proyecto

```
EcoRegApp/
├── app/src/main/
│   ├── java/com/ecolim/ecoregapp/
│   │   ├── data/
│   │   │   ├── local/
│   │   │   │   ├── entity/Residuo.java       ← Modelo de datos Room
│   │   │   │   ├── dao/ResiduoDao.java        ← Queries SQL
│   │   │   │   └── AppDatabase.java           ← Base de datos SQLite
│   │   │   └── repository/ResiduoRepository.java
│   │   ├── viewmodel/ResiduoViewModel.java    ← MVVM ViewModel
│   │   ├── ui/
│   │   │   ├── activities/
│   │   │   │   ├── LoginActivity.java
│   │   │   │   └── MainActivity.java
│   │   │   ├── fragments/
│   │   │   │   ├── HomeFragment.java          ← Dashboard
│   │   │   │   ├── RegistroFragment.java      ← Formulario nuevo residuo
│   │   │   │   ├── ImportarFragment.java      ← Importar CSV/PDF
│   │   │   │   ├── ReportesFragment.java      ← Reportes + exportar
│   │   │   │   ├── HistorialFragment.java     ← Lista con filtros
│   │   │   │   └── SuccessFragment.java
│   │   │   └── adapters/ResiduoAdapter.java
│   │   └── utils/
│   │       ├── FileManager.java               ← Import/Export CSV y PDF
│   │       └── SessionManager.java            ← Sesión del operario
│   └── res/
│       ├── layout/                            ← Todos los XMLs de pantallas
│       ├── navigation/nav_graph.xml           ← Navegación entre pantallas
│       ├── menu/bottom_nav_menu.xml           ← Menú inferior
│       ├── values/
│       │   ├── colors.xml
│       │   ├── strings.xml
│       │   └── themes.xml
│       └── drawable/eco_reg_logo.png
```

---

## ⚙️ Funciones principales

### 📝 Registro de residuos
- Selección de tipo: Plástico, Orgánico, Papel, Metal, Vidrio, Peligroso
- Ingreso de peso en kg (calcula volumen automáticamente)
- Campos de ubicación y zona
- Para residuos **peligrosos**: checklist EPP obligatorio (guantes, mascarilla, lentes)

### ⬇️ Importar datos
- **CSV**: formato con columnas tipo, peso, fecha, ubicacion, zona
- **PDF/TXT**: una línea por registro `tipo,peso,fecha,ubicacion,zona`
- Vista previa de cantidad de registros antes de importar

### 📊 Reportes y exportación
- Filtros por período: Hoy / 7 días / Este mes / Todo
- Filtros por tipo de residuo
- **Exportar PDF**: reporte completo con resumen y tabla detallada
- **Exportar CSV**: datos tabulados para Excel o Google Sheets
- Compartir directamente por WhatsApp, Email, Drive, etc.

### 📋 Historial
- Búsqueda en tiempo real
- Filtros por tipo y estado de sincronización
- Vista detallada de cada registro
- Eliminar registros

---

## 🛠️ Tecnologías usadas

| Componente | Librería |
|---|---|
| UI | Material Design 3 |
| Navegación | Navigation Component |
| Base de datos local | Room (SQLite) |
| Arquitectura | MVVM + LiveData |
| Sync background | WorkManager |
| API REST | Retrofit 2 |
| Exportar PDF | iText 7 |
| Importar/Exportar CSV | OpenCSV |

---

## 📄 Formato CSV para importar

```csv
tipo,peso,fecha,ubicacion,zona
plastico,5.2,2026-02-22 10:30:00,Planta A,Zona B
organico,12.0,2026-02-22 11:15:00,Planta A,Zona A
peligroso,2.5,2026-02-22 12:40:00,Planta C,Zona C
papel,8.4,2026-02-21 09:10:00,Planta A,Pasillo 1
metal,14.2,2026-02-21 16:05:00,Planta D,Zona D
```

---

## 📌 Notas para el desarrollo

- La sincronización con API REST está preparada en `ResiduoRepository` pero requiere un backend real
- El endpoint configurado es: `https://ecolim-backend.eco/api/v1`
- Los íconos vectoriales (`@drawable/ic_*`) deben crearse en Android Studio via `File → New → Vector Asset`
- El logo `eco_reg_logo.png` ya está incluido en `res/drawable/`

---

*EcoRegApp v1.0.0 · ECOLIM S.A.C. · NTP 900.058 · 2026*
