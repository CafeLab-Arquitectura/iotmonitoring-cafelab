# language: es
@umbrales @US-IoT-02
Característica: Configuración y evaluación de umbrales ambientales por lote
  Como administrador o barista de CafeLab
  Quiero consultar y configurar los umbrales de temperatura y humedad de un lote de café
  Para recibir alertas tempranas de degradación ambiental

  Escenario: Consulta de umbrales ambientales de un lote de café
    Dado que existe un lote de café con ID 1
    Cuando el usuario solicita los umbrales ambientales para el lote 1
    Entonces el sistema responde con código HTTP 200
    Y los umbrales contienen temperatura mínima, máxima y humedad máxima configurada

  Escenario: Actualización de umbrales ambientales para un lote específico
    Dado que existe un lote de café con ID 1
    Cuando el usuario actualiza los umbrales del lote 1 con temperatura entre 18 °C y 24 °C y humedad máxima 65 %
    Entonces el sistema responde con código HTTP 200
    Y los nuevos umbrales reflejan la humedad máxima de 65 %
