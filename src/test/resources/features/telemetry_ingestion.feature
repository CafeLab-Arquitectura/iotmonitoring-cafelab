# language: es
@telemetria @US-IoT-01
Característica: Registro y sincronización de telemetría ambiental
  Como barista o encargado de almacén
  Quiero registrar las lecturas ambientales de temperatura y humedad enviadas por los sensores
  Para garantizar las condiciones óptimas de conservación de los lotes de café

  Escenario: Ingesta exitosa de una nueva lectura de telemetría
    Dado que existe un lote de café con ID 1 en el sistema
    Cuando el sensor "tracksilo-001" envía una lectura con secuencia 1, temperatura 24.5 °C y humedad 60.0 %
    Entonces el sistema responde con código de estado HTTP 201
    Y la lectura queda registrada con sensor "tracksilo-001" y secuencia 1

  Escenario: Ingesta idempotente ante retransmisión por reconexión del sensor
    Dado que existe un lote de café con ID 1 en el sistema
    Y ya se encuentra registrada una lectura del sensor "tracksilo-001" con secuencia 1
    Cuando el sensor reenvía la misma lectura con secuencia 1, temperatura 24.5 °C y humedad 60.0 %
    Entonces el sistema responde con código de estado HTTP 200 o 201
    Y no se duplica el registro en la base de datos
