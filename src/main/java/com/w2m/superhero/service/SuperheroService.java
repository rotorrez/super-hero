DTOs reutilizables

Además de los servicios y utilidades descritos, esta librería define un conjunto de DTOs comunes que tienen como objetivo ser reutilizados por los distintos procesos BAMOE.
El propósito es que todos los equipos utilicen una misma estructura de datos en la medida de lo posible, evitando duplicidades y facilitando la interoperabilidad.

Ejemplos de DTOs incluidos:

ProcessConfigurationDTO → Representa la configuración de un proceso y se utiliza en conjunción con el ProcessConfigurationService.

ResponseTokenDTO → Modelo estándar para encapsular respuestas de autenticación/token.

EventDTO → Objeto que normaliza la estructura de eventos recibidos y procesados.

El uso de estos DTOs garantiza que las integraciones con BAMOE sigan una estructura coherente y fácil de mantener en el tiempo.
