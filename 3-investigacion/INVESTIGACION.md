# Punto 3 - Otras librerias para sistemas distribuidos con RMI/RPC

## Que se investigo

Se comparan librerias que permiten que un programa invoque procedimientos que se
ejecutan en otra maquina (RPC) o que use objetos remotos (RMI), en Java y en
otros lenguajes.

| Libreria | Lenguaje | Transporte | Contrato | Serializacion | Ideal para |
| -------- | -------- | ---------- | -------- | ------------- | ---------- |
| **RMI estandar** (`java.rmi`) | Java | TCP (JRMP) | Interfaz Java que extiende `Remote` | Java serialization | Ejemplos academicos y sistemas internos Java |
| **LipeRMI** | Java (y port Android) | TCP propio, sin `rmiregistry` | Interfaz Java normal | Java serialization | Practicar RMI con poco codigo y un solo puerto |
| **gRPC** | Java, C#, Go, Python, Node, PHP, Dart | HTTP/2 | Archivo `.proto` | Protocol Buffers (binario) | Microservicios y comunicacion entre lenguajes |
| **Apache Thrift** | Multiples lenguajes | TCP / HTTP | Archivo `.thrift` | Formato propio (binario) | Sistemas grandes con muchos lenguajes |
| **Apache Dubbo** | Java (y otros) | TCP / HTTP | Interfaz Java | Hessian2, JSON, Protobuf | Microservicios Java con registro y balanceo |
| **Spring Remoting** | Java | RMI, HTTP, Hessian, JMS | Interfaz Java | Segun el protocolo | Aplicaciones que ya usan Spring |
| **JSON-RPC** (jsonrpc4j, jayson) | Java, Node, Python | HTTP | Metodos con nombre y parametros JSON | JSON (texto) | APIs sencillas y faciles de depurar |
| **Cap'n Proto RPC** | C++, Java, Python | TCP | Archivo `.capnp` | Propia (muy rapida, sin copia) | Alto rendimiento y tiempo real |
| **Pyro5** | Python | TCP | Objetos Python | Pickle / serpent | Prototipos rapidos en Python |
| **RPyC** | Python | TCP | Objetos Python | Propia | Acceso remoto transparente en Python |
| **`net/rpc`** | Go | TCP / HTTP | Metodos exportados de Go | gob | Ejemplos y servicios internos en Go |
| **jayson / grpc-js** | Node.js | TCP / HTTP2 | JSON-RPC o `.proto` | JSON o Protobuf | Servicios Node que hablan con otros lenguajes |
| **.NET Remoting / gRPC** | C# | TCP / HTTP2 | Interfaz o `.proto` | Binaria | Ecosistema .NET |

## Observaciones de la investigacion

1. Casi todas las librerias siguen la misma idea: **se describe el servicio en
   un contrato** (una interfaz o un IDL), el cliente obtiene un *stub* y llama
   al metodo como si fuera local.
2. La diferencia principal esta en **como se describen los datos**: RMI y
   LipeRMI usan serializacion de objetos Java, mientras que gRPC y Thrift usan
   un IDL que permite generar codigo para varios lenguajes.
3. RMI y LipeRMI son comodos cuando **cliente y servidor son Java**; gRPC y
   Thrift son la opcion cuando conviven varios lenguajes o se quiere HTTP/2.
4. Para prototipos en otros lenguajes, Pyro5 (Python) o `net/rpc` (Go) permiten
   un ejemplo funcional con muy pocas lineas.

## Ejemplo muy simple desarrollado: gRPC con Java

Se eligio **gRPC** porque es la libreria de RPC mas usada hoy, es multilenguaje
y se apoya en HTTP/2 y Protocol Buffers.

### Archivos del ejemplo

```
ejemplo-grpc-java/
|-- pom.xml
`-- src/main/
    |-- proto/imc.proto                 Contrato del servicio (IDL)
    `-- java/com/jcaa/imc/grpc/
        |-- ServidorGrpc.java           Servidor (implementa el servicio)
        `-- ClienteGrpc.java            Cliente (usa el stub generado)
```

El contrato completo ocupa 15 lineas:

```proto
syntax = "proto3";
package imc;
option java_package = "com.jcaa.imc.grpc";
option java_multiple_files = true;

message DatosImc {
  double peso = 1;
  double altura = 2;
}

message ResultadoImc {
  double imc = 1;
  string interpretacion = 2;
}

service CalculoImc {
  rpc Calcular (DatosImc) returns (ResultadoImc);
}
```

A partir de ese archivo, Maven genera las clases `DatosImc`, `ResultadoImc` y el
stub `CalculoImcGrpc`; el programador solo escribe la implementacion y la
llamada.

### Como ejecutarlo

```bash
cd ejemplo-grpc-java
mvn clean package          # descarga protoc y genera el codigo

# Terminal 1: servidor
java -cp "target/classes;target/cp.txt" com.jcaa.imc.grpc.ServidorGrpc

# Terminal 2: cliente (peso y altura como argumentos)
java -cp "target/classes;target/cp.txt" com.jcaa.imc.grpc.ClienteGrpc 70 1.75
```

### Resultado obtenido al ejecutarlo

```
Peso: 70.00 kg - Altura: 1.75 m
IMC: 22.86
Interpretacion: Estas bien de peso
```

Con un peso invalido (0 kg) el servidor responde:

```
IMC: 0.00
Interpretacion: ERROR: El peso y la altura deben ser mayores que 0
```

### Comparacion rapida con los puntos 1 y 2

| Aspecto | LipeRMI / RMI estandar | gRPC |
| ------- | ---------------------- | ---- |
| Contrato | Interfaz Java | Archivo `.proto` |
| Generacion de codigo | No hay (se escribe a mano) | Automatica (protoc) |
| Datos | Serializacion Java | Protocol Buffers (binario y compacto) |
| Transporte | TCP propio | HTTP/2 |
| Cliente en otros lenguajes | Practicamente solo Java | Java, Python, C#, Go, Node, etc. |
| Curva de aprendizaje | Baja | Media |
