Descripción general

La librería san-audobs-bamoecoexislib ha sido desarrollada para gestionar la lógica de convivencia de procesos BAMOE en España.
Su objetivo es facilitar la integración de microservicios con los sistemas de coexistencia y con Appian, proporcionando servicios reutilizables para el ciclo de vida de tareas, el disparo de eventos y la interacción con Case-and-Go.

Está implementada en Java y utiliza el framework Quarkus para su integración en aplicaciones de negocio.

Configuración

Los consumidores deben incluir la siguiente configuración en su application.yml:

bamoeecoexis:
  coexistence:
    base-url: https://saneventoscoexis.santander.dev.corp/api/v1/coexistence
    client-id: PRCOS2

tokenprovider:
  uid: aplapp
  password: prueba
  realm: SantanderBCE
  url: https://srvnuarintra.santander.dev.corp/sas/authenticate/credentials

appian-api:
  base-url: https://bpm-appian-tareas.santander.dev.corp/bpm-appian-tareas
  client-id: PRCOS2
  channel: INT

Módulos principales
CoexistenceService

Gestiona la sincronización del ciclo de vida de las tareas en el sistema de convivencia.
Métodos principales:

void setTaskCoexistence(UserTaskStateEvent event) → Registra la tarea cuando se encuentra lista.

void deleteTaskCoexistence(UserTaskStateEvent event) → Elimina la tarea cuando se completa.

CaseRelationDTO getCaseRelationByPpaasCaseId(String ppaasCaseId) → Recupera la relación de un caso externo asociado a un ID de proceso.

AppianEventService

Proporciona métodos de alto nivel para disparar eventos en Appian a partir de información de caso, usuario y punto de acceso.
Métodos principales:

AppianEventResponseDTO triggerEvent(AppianEventRequestDTO request) → Dispara un evento en Appian construyendo automáticamente el payload.

String getCaseNumberByIdCaso(Integer idCaso) → Obtiene el número de caso externo en base a identificadores internos.

Ejemplo de uso:

@Inject
AppianEventService appianEventService;

AppianEventRequestDTO request = AppianEventRequestDTO.builder()
    .idCaso(1234)
    .idAccessPoint(999)
    .processCode("COD001")
    .event("EVENT_NAME")
    .customersIdentification("ABC123")
    .specificInformation(Map.of("extraField", "value"))
    .build();

AppianEventResponseDTO response = appianEventService.triggerEvent(request);

AppianCaseAndGoService

Facilita la interacción con Appian Case-and-Go, permitiendo iniciar procesos rápidos y obtener las próximas tareas disponibles para el usuario.

Método principal:

StartFastProcessResponseDTO startFastProcess(String processCode, StartFastProcessRequestDTO request) → Inicia un proceso y devuelve las siguientes tareas disponibles.
