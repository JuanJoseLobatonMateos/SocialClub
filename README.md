# 🏢 Social Club - Sistema de Gestión de Club Social

<div align="center">
  <img src="src/main/resources/images/logo.png" alt="Logo del Club Social" width="200">
  <br>
  <p><em>Sistema integral de gestión para clubes sociales</em></p>
</div>

## 📋 Descripción

Social Club es una aplicación de gestión completa desarrollada en Java que permite administrar todos los aspectos de un club social moderno. Desde la gestión de eventos hasta el control de instalaciones deportivas, este sistema ofrece una solución integral para la administración eficiente de clubes sociales.

## ✨ Características Principales

### 🎯 Gestión de Eventos
- Creación y programación de eventos
- Gestión de asistencia
- Recordatorios automáticos
- Calendario integrado

### 🏟️ Gestión de Instalaciones
- Control de instalaciones deportivas
- Reserva de espacios
- Gestión de horarios
- Mantenimiento y disponibilidad

### 👥 Gestión de Socios
- Registro de miembros
- Control de acceso
- Gestión de carnet
- Historial de actividades

### 📊 Reportes y Estadísticas
- Generación de reportes en PDF
- Exportación a Excel
- Estadísticas de uso
- Análisis de asistencia

## 🛠️ Tecnologías Utilizadas

- **Java 11+**
- **JavaFX** - Interfaz gráfica moderna
- **Hibernate** - Persistencia de datos
- **Gradle** - Gestión de dependencias
- **MySQL** - Base de datos
- **DigitalPersona** - Autenticación biométrica

## 🔐 Autenticación Biométrica

El sistema incluye autenticación biométrica mediante DigitalPersona, permitiendo:

- Registro de huellas dactilares
- Verificación de identidad mediante huella digital
- Control de acceso seguro
- Integración con el sistema de gestión de socios

### Requisitos Adicionales para DigitalPersona
- [Lector de huellas DigitalPersona compatible](https://www.crossmatch.com/global/products/digitalpersona/)
- [Drivers de DigitalPersona](https://www.crossmatch.com/global/support/downloads/)
- [SDK de DigitalPersona para Java](https://www.crossmatch.com/global/products/digitalpersona/)

## 🚀 Instalación

### Requisitos Previos
- [Java 11 o superior](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)
- [Gradle 7.0 o superior](https://gradle.org/releases/)
- [MySQL](https://dev.mysql.com/downloads/) o [PostgreSQL](https://www.postgresql.org/download/)
- [DigitalPersona SDK](https://www.crossmatch.com/global/products/digitalpersona/)

### Pasos de Instalación

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/JuanJoseLobatonMateos/social-club.git
   cd social-club
   ```

2. **Configurar la base de datos**
   - Editar el archivo `src/main/resources/hibernate.cfg.xml`
   - Configurar las credenciales de la base de datos
   - Crear la base de datos en tu servidor

3. **Instalar DigitalPersona**
   - Descargar e instalar el [SDK de DigitalPersona](https://www.crossmatch.com/global/products/digitalpersona/)
   - Instalar los [drivers del lector de huellas](https://www.crossmatch.com/global/support/downloads/)
   - Configurar las variables de entorno necesarias

4. **Compilar el proyecto**
   ```bash
   gradle build
   ```

5. **Ejecutar la aplicación**
   ```bash
   gradle run
   ```

## 📁 Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   ├── database/     # Clases DAO
│   │   ├── model/        # Entidades
│   │   ├── util/         # Utilidades
│   │   ├── controller/   # Controladores
│   │   └── service/      # Servicios
│   └── resources/
│       ├── images/       # Imágenes
│       └── fxml/         # Vistas FXML
└── test/                 # Pruebas
```

## 🧪 Ejecución de Pruebas

Para ejecutar las pruebas unitarias:
```bash
gradle test
```

## 🤝 Contribuir

¡Las contribuciones son bienvenidas! Por favor, sigue estos pasos:

1. Haz un fork del proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Haz commit de tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📝 Licencia

Este proyecto está protegido por una licencia propietaria. Ver LICENSE.txt para más información.

## 📞 Contacto

Juan José Lobatón Mateos - [jlobatonm@gmail.com](mailto:jlobatonm@gmail.com)

Link del proyecto: [https://github.com/JuanJoseLobatonMateos/social-club](https://github.com/JuanJoseLobatonMateos/social-club)
