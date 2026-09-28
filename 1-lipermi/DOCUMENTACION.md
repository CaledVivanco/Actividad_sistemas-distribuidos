# Punto 1 - Cada paso de la guia con la libreria LipeRMI

Este documento registra, paso por paso, como se realizo la guia usando la
libreria de terceros **LipeRMI**. La entrega son los **3 proyectos** que indica
la guia.

## Paso 0 - Herramientas

| Herramienta | Uso |
| ----------- | --- |
| JDK 17 | Compilar y ejecutar los proyectos |
| IDE (NetBeans o IntelliJ IDEA) | Editar el codigo y ejecutar las clases `Principal` |
| Libreria **LipeRMI** | Es la libreria de terceros que facilita el RMI |

La guia indica descargar LipeRMI desde Maven Repository. En esta entrega se usa
la coordenada Maven, de modo que el IDE la descarga automaticamente:

```xml
<dependency>
  <groupId>com.tascape.qa</groupId>
  <artifactId>lipermi</artifactId>
  <version>1.0.1</version>
</dependency>
```

El archivo descargado es `lipermi-1.0.1.jar`, el mismo que aparece en la guia.

## Paso 1 - Proyecto libreria

Se creo el proyecto `EjemploLipeRmiLibImc` (en la guia: *"Crear un proyecto tipo
libreria"*). Contiene dos clases:

### 1.1 Clase para transportar los datos: `DatosImc`

Archivo: `EjemploLipeRmiLibImc/src/main/java/com/jcaa/imc/rmi/lib/DatosImc.java`

```java
public class DatosImc implements Serializable {
    private static final long serialVersionUID = 1L;   // obligatorio: viaja por la red
    private double peso;
    private double altura;
    private double resultado;
    private String interpretacion;
    // constructores y metodos get/set de cada atributo
}
```

Implementa `Serializable` porque sus objetos se convierten en bytes para viajar
del cliente al servidor y viceversa.

### 1.2 Interfaz para invocar los metodos de forma remota: `IRemotaCalculoImc`

Archivo: `EjemploLipeRmiLibImc/src/main/java/com/jcaa/imc/rmi/lib/IRemotaCalculoImc.java`

```java
public interface IRemotaCalculoImc {
    DatosImc calcularImc(DatosImc datos);
}
```

Con LipeRMI la interfaz **no** extiende `java.rmi.Remote` y el metodo **no**
declara `RemoteException`.

### 1.3 Construir el archivo distribuible (JAR)

```bash
cd EjemploLipeRmiLibImc
mvn clean install      # genera el JAR y lo deja disponible para los otros proyectos
```

Equivale a *"Construir el archivo distribuible"* de la guia (el `dist/…jar` que
NetBeans genera).

## Paso 2 - Proyecto servidor

Proyecto `EjemploLipeRmiServidorImc`, con la libreria LipeRMI y el JAR del
proyecto anterior como dependencias.

### 2.1 Clase `CalculoRmiImcImplem`

Archivo: `EjemploLipeRmiServidorImc/src/main/java/com/jcaa/imc/rmi/servidor/CalculoRmiImcImplem.java`

Implementa el contrato remoto; aqui esta la logica que se ejecuta en el
servidor:

```java
public class CalculoRmiImcImplem implements IRemotaCalculoImc {
    @Override
    public DatosImc calcularImc(DatosImc datos) {
        if (datos.getPeso() <= 0 || datos.getAltura() <= 0) {
            datos.setResultado(0);
            datos.setInterpretacion("ERROR: El peso y la altura deben ser mayores que 0");
            return datos;
        }
        double resultado = datos.getPeso() / (datos.getAltura() * datos.getAltura());
        datos.setResultado(resultado);
        datos.setInterpretacion(interpretar(resultado));
        return datos;
    }
}
```

### 2.2 Clase `Servidor`: publicar el objeto remoto

Archivo: `EjemploLipeRmiServidorImc/src/main/java/com/jcaa/imc/rmi/servidor/Servidor.java`

```java
CallHandler invocador = new CallHandler();      // 1. manejador de llamadas
Server servidor = new Server();                 // 2. servidor de la libreria
servidor.bind(puerto, invocador);               // 3. abrir el puerto (9007)
invocador.registerGlobal(IRemotaCalculoImc.class, calculoImc);   // 4. publicar el objeto
```

Con LipeRMI **no se necesita `rmiregistry`**: todo el servicio queda en un unico
puerto TCP.

### 2.3 Clase `Principal`

Arranca el servidor, muestra el puerto y la direccion IP de la maquina (el dato
que debe escribir el cliente) y queda escuchando hasta que se presione Enter.
El servidor tambien imprime `[CLIENTE CONECTADO]` con la IP del cliente, lo que
sirve como evidencia en la sustentacion.

## Paso 3 - Proyecto cliente

Proyecto `EjemploLipeRmiClienteImc`, tambien con LipeRMI y el JAR de la
libreria.

### 3.1 Clase `VentanaPrincipal`

Archivo: `EjemploLipeRmiClienteImc/src/main/java/com/jcaa/imc/rmi/cliente/vistas/VentanaPrincipal.java`

La conexion con el servidor:

```java
CallHandler invocadorRemoto = new CallHandler();
cliente = new Client(ipServidor, puerto, invocadorRemoto);
calculoImcRemoto = (IRemotaCalculoImc) cliente.getGlobal(IRemotaCalculoImc.class);
```

La invocacion remota (en un hilo aparte para no congelar la ventana):

```java
DatosImc datos = new DatosImc(peso, altura);
DatosImc respuesta = calculoImcRemoto.calcularImc(datos);
textoResultado.setText("IMC: " + respuesta.getResultado());
textoMensaje.setText(respuesta.getInterpretacion());
```

La ventana tiene los campos de IP, puerto, peso y altura, el boton
**Conectar/Desconectar**, el boton **Calcular IMC**, el area de resultado y un
registro de los mensajes enviados y recibidos.

### 3.2 Clase `Principal`

Lanza la ventana. En la guia esta clase es la que tiene el metodo `main` del
cliente.

## Paso 4 - Probar el sistema distribuido

### 4.1 Ejecutar el servidor

```bash
cd EjemploLipeRmiServidorImc
mvn clean install
java -cp "target/classes;<ruta>/lipermi-1.0.1.jar" com.jcaa.imc.rmi.servidor.Principal
```

Salida esperada:

```
=========================================================
  SERVIDOR RMI CON LIPERMI - CALCULO DEL IMC
=========================================================
  Puerto            : 9007
  IP de esta maquina: 192.168.x.x
=========================================================
```

### 4.2 Ejecutar el cliente

```bash
cd EjemploLipeRmiClienteImc
mvn clean install
java -cp "target/classes;<ruta>/lipermi-1.0.1.jar" com.jcaa.imc.rmi.cliente.Principal
```

### 4.3 Iniciar la conexion y calcular

1. En el cliente se escribe la IP que mostro el servidor y el puerto `9007`.
2. Se presiona **Conectar**: en el registro aparece "Conectado al servidor" y en
   la consola del servidor aparece `[CLIENTE CONECTADO]`.
3. Se ingresan peso y altura, y se presiona **Calcular IMC**.
4. El cliente muestra el resultado y la interpretacion calculados **en el
   servidor**.

### 4.4 Resultados verificados

| Peso | Altura | Respuesta del servidor |
| ---- | ------ | ---------------------- |
| 70 | 1.75 | IMC 22.86 - "Estas bien de peso" |
| 45 | 1.75 | "Debes consultar un Medico, tu peso es muy bajo" |
| 85 | 1.75 | "Debes bajar un poco de peso" |
| 120 | 1.75 | "Debes consultar un Medico, tu peso es muy alto" |
| 0 | 1.75 | "ERROR: El peso y la altura deben ser mayores que 0" |

Ademas, `mvn install` ejecuta automaticamente 4 pruebas: 3 de la logica de
calculo y 1 de integracion que levanta el servidor LipeRMI en un puerto libre y
lo invoca con un cliente LipeRMI real.

## Observaciones de la libreria

- LipeRMI no exige extender `Remote` ni `UnicastRemoteObject`, ni declarar
  `RemoteException`.
- No requiere ejecutar `rmiregistry`: el propio servidor abre el puerto.
- Todo el trafico va por un solo puerto, lo que simplifica la configuracion de
  firewall (util para conectarse desde otra maquina de la red).
