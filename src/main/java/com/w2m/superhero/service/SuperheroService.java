📑 Estructura recomendada para Confluence (versión en español)
Librería san-audobs-bamoeeeplib

Descripción General
La librería san-audobs-bamoeeeplib ha sido desarrollada para centralizar y reutilizar lógica común en los procesos BAMOE España, evitando duplicidades en los distintos microservicios. Incluye servicios para la gestión de inactividad, configuración de procesos, tratamiento de eventos y utilidades de formato de fecha.

Esta librería es consumida por aplicaciones Java con Quarkus, y proporciona módulos listos para integrarse en flujos de negocio orquestados en BAMOE.

Configuración
Los consumidores deben incluir la siguiente configuración en su application.yml:

tokenprovider:
  uid: aplapp
  password: prueba
  realm: SantanderBCE
  url: https://srvnuarintra.santander.dev.corp/sas/authenticate/credentials

processconfig:
  base-url: https://saneepp.santander.dev.corp/api/v1/processes_data
  client-id: cespla


Módulos incluidos

InactivityService
Proporciona lógica de negocio para determinar si un caso está inactivo según umbrales de tiempo y la última fecha de actualización.
Incluye cálculo de fecha de expiración futura según configuración de inactividad.

DateFormatUtils
Clase utilitaria para generar periodos en formato ISO-8601.
Uso frecuente en reglas de BAMOE o plazos de vencimiento.

EventMapperUtils
Clase estática para mapear eventos genéricos (Map<String, Object>) a objetos EventDTO.

ProcessConfiguration
Permite recuperar de forma segura la configuración de procesos desde una API remota.
Ejemplo de uso:

@Inject
ProcessConfigurationService configurationService;

ProcessConfigurationDTO configDto = configurationService.getConfiguration("TACR", "TACRPPAAS");
Map<String, Object> configData = configDto.getConfigurationData();


EventService
Servicio utilitario para actualizar estado y etapa de casos según datos de eventos.
Ejemplo de método:

public void changeStatusOrStageFromSignalEvent(Map<String, Object> eventMap, Integer statusId)


Formato de entrada requerido:

Key	Tipo	Descripción
caseId	String	Identificador interno de caso
processId	String	Identificador del proceso
userId	String	Usuario que dispara el evento
centerId	String	Centro de ejecución
stageId	String	Etapa asignada
accessPointId	String	Punto de acceso sobrescrito

Trazabilidad con Jira
El desarrollo de esta librería está gestionado en el ticket principal de Jira:
👉 ESPESTPROC-7900 – Librería EEPP BAMOE ESP

Subtareas asociadas:

Creación de componente librería.

Formato de fecha en ISO8601.

Service de inactividad de casos.

Gestión de Token para llamadas a API Process PaaS.

Obtención de configuración de procesos.

Funcionalidad de actualización de estados vía eventos.
