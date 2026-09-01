# Sistema de Gestión de Ventas - Mapuescuela 📚

Este repositorio contiene el proyecto de integración de plataformas para la automatización del proceso de ventas de Mapuescuela.

## 👥 Equipo de Trabajo
* **Felipe Fernandoi:** Líder BPMN & Flowable (Modelado de procesos, diagramas AS-IS / TO-BE y automatización).
* **Andrés Araya:** Líder Backend & Gestión (Desarrollo de API REST y base de datos).

---

## 🚀 EVALUACIÓN 2: Demostración Funcional


**Resumen de la entrega:**
Para garantizar una demostración visual fluida en la plataforma, la orquestación en Flowable se implementó bajo el enfoque **"Human-in-the-loop"**. Las tareas de integración se configuraron como *User Tasks* con formularios, evidenciando el viaje de los datos. La lógica de negocio, validaciones y la ejecución de los *External Workers* están delegadas a nuestra API REST local en Spring Boot.

**Nuevos Archivos (Ev2):**
* `/mapuescuela-backend` : Código fuente depurado de la API y scripts de BD.
* `FlowableApp.zip` : App consolidada lista para importar en Flowable Design.
* `/postman` : Colección JSON con las pruebas para simular los gatillazos al backend.
* `Acta2.docx` : Registro de avances y optimización del modelo AS-IS.

---

## 📁 Estructura General del Proyecto (Ev1 & Ev2)
* `/backend` : Código fuente de los servicios REST (Java/Spring Boot) y base de datos.
* `/procesos_BPMN` : Archivos ejecutables `.bpmn` y `.form` de los modelos.
* `/evidencias` : Capturas de ejecución de Flowable y pruebas en Postman.

## 🚀 Tecnologías Utilizadas
* **Motor BPMN:** Flowable
* **Backend:** Java 17 / Spring Boot
* **Base de Datos:** MySQL
* **Pruebas de API:** Postman
