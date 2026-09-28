# Actividad: aplicaciones distribuidas con RMI

Trabajo individual de la guia **"GUIA DE DESARROLLO DE APLICACIONES DISTRIBUIDAS
USANDO RMI"** (autor: John Carlos Arrieta Arrieta). Se resolvieron los tres
puntos de la actividad academica:

| Punto | Que pide la guia | Donde esta |
| ----- | ---------------- | ---------- |
| 1 | Realizar y documentar cada paso de la guia, entregando los fuentes de los **3 proyectos** (libreria, servidor y cliente) | `1-lipermi/` |
| 2 | El mismo ejemplo usando **RMI estandar de Java**, sin LipeRMI, documentando el proceso y explicando diferencias y similitudes | `2-rmi-estandar/` |
| 3 | Investigar otras **librerias de RMI/RPC** y entregar un ejemplo muy simple | `3-investigacion/` |

El ejemplo desarrollado es el de la guia: una calculadora de IMC donde el
cliente envia el peso y la altura, y el **calculo se ejecuta en el servidor**
gracias a una invocacion remota.

El documento con las respuestas a los tres puntos de la actividad esta en
`Actividad-Sistemas-Distribuidos-RMI.pdf` (incluido en esta entrega).

## Estructura de la entrega

```
ejercicio-rmi-imc/
|-- README.md                        Este archivo
|-- 1-lipermi/                       Punto 1: 3 proyectos con la libreria LipeRMI
|   |-- DOCUMENTACION.md             Paso a paso de la guia
|   |-- EjemploLipeRmiLibImc/        Proyecto libreria (DatosImc + interfaz remota)
|   |-- EjemploLipeRmiServidorImc/   Proyecto servidor
|   `-- EjemploLipeRmiClienteImc/    Proyecto cliente (GUI)
|-- 2-rmi-estandar/                  Punto 2: los mismos 3 proyectos con RMI estandar
|   |-- DOCUMENTACION.md             Paso a paso + diferencias con LipeRMI
|   |-- EjemploRmiEstandarLibImc/
|   |-- EjemploRmiEstandarServidorImc/
|   `-- EjemploRmiEstandarClienteImc/
`-- 3-investigacion/                 Punto 3: otras librerias
    |-- INVESTIGACION.md             Comparacion de librerias y ejemplo minimo
    `-- ejemplo-grpc-java/           Ejemplo funcional con gRPC
```

## Requisitos

- JDK 17
- Maven 3.9 (o el Maven que trae IntelliJ IDEA / NetBeans)
- Para el ejemplo de gRPC, Maven descarga `protoc` automaticamente

## Como compilar y ejecutar

Cada carpeta numerada es un proyecto Maven con sus tres modulos:

```bash
# Punto 1 - LipeRMI (compila los 3 proyectos y ejecuta 4 pruebas)
cd 1-lipermi
mvn clean install

# Punto 2 - RMI estandar (compila los 3 proyectos y ejecuta 4 pruebas)
cd ../2-rmi-estandar
mvn clean install

# Punto 3 - ejemplo minimo con gRPC
cd ../3-investigacion/ejemplo-grpc-java
mvn clean package
```

Para ejecutar los servicios, primero el servidor y despues el cliente (en otra
terminal). Las instrucciones exactas, con las rutas de los JAR, estan en la
documentacion de cada punto.

## Casos de prueba del IMC

| Peso | Altura | Resultado esperado |
| ---- | ------ | ------------------ |
| 70 | 1.75 | IMC 22.86 - "Estas bien de peso" |
| 45 | 1.75 | "Debes consultar un Medico, tu peso es muy bajo" |
| 85 | 1.75 | "Debes bajar un poco de peso" |
| 120 | 1.75 | "Debes consultar un Medico, tu peso es muy alto" |
| 0 | 1.75 | "ERROR: El peso y la altura deben ser mayores que 0" |

Estos casos estan cubiertos por las pruebas automaticas de los dos servidores y
se verificaron ejecutando servidor y cliente por separado, con invocacion
remota real.

## Resumen de resultados

| Punto | Compilacion | Pruebas |
| ----- | ----------- | ------- |
| 1 - LipeRMI | 3 proyectos OK | 4 pruebas OK (dominio + integracion RMI real) |
| 2 - RMI estandar | 3 proyectos OK | 4 pruebas OK (dominio + integracion con registro RMI) |
| 3 - gRPC | proyecto OK | Servidor y cliente ejecutados: IMC 22.86 y validacion de error |
