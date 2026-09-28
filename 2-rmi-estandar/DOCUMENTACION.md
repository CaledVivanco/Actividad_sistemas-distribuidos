# Punto 2 - El mismo ejemplo con RMI estandar de Java

Aqui se repite el ejemplo de la guia (calculadora de IMC con invocacion remota)
usando **unicamente el paquete `java.rmi`** que trae el JDK, sin LipeRMI. Al
final se comparan las dos tecnologias.

## Paso 0 - Herramientas

Solo se necesita el JDK (ya incluye `java.rmi`) y un IDE. **No** se agrega
ninguna libreria externa al proyecto.

## Paso 1 - Proyecto libreria

Proyecto `EjemploRmiEstandarLibImc`. Contiene las mismas dos clases que en el
punto 1, pero la interfaz cambia porque RMI estandar la exige distinta.

### 1.1 `DatosImc`

Igual que con LipeRMI: implementa `Serializable`, porque los objetos que se
envian como parametro o se devuelven como resultado deben poder convertirse en
bytes.

### 1.2 `IRemotaCalculoImc` (la diferencia clave)

Archivo: `EjemploRmiEstandarLibImc/src/main/java/com/jcaa/imc/rmi/lib/IRemotaCalculoImc.java`

```java
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemotaCalculoImc extends Remote {
    String NOMBRE_SERVICIO = "CalculoImc";
    DatosImc calcularImc(DatosImc datos) throws RemoteException;
}
```

Con RMI estandar la interfaz **obliga** a:

1. extender `java.rmi.Remote`;
2. declarar `throws RemoteException` en cada metodo remoto.

### 1.3 Construir el JAR

```bash
cd EjemploRmiEstandarLibImc
mvn clean install
```

## Paso 2 - Proyecto servidor

Proyecto `EjemploRmiEstandarServidorImc`.

### 2.1 Clase `CalculoRmiImcImplem`

Archivo: `EjemploRmiEstandarServidorImc/src/main/java/com/jcaa/imc/rmi/servidor/CalculoRmiImcImplem.java`

```java
public class CalculoRmiImcImplem extends UnicastRemoteObject implements IRemotaCalculoImc {

    public CalculoRmiImcImplem() throws RemoteException { super(); }

    public CalculoRmiImcImplem(int puerto) throws RemoteException { super(puerto); }

    @Override
    public DatosImc calcularImc(DatosImc datos) throws RemoteException {
        // la misma logica de calculo y validacion del punto 1
    }
}
```

Diferencias con el punto 1:

1. extiende `UnicastRemoteObject` (asi el objeto queda *exportado* para recibir
   llamadas por la red);
2. el constructor lanza `RemoteException`;
3. el metodo remoto vuelve a declarar `throws RemoteException`.

### 2.2 Clase `Servidor`: registro y publicacion

Archivo: `EjemploRmiEstandarServidorImc/src/main/java/com/jcaa/imc/rmi/servidor/Servidor.java`

```java
implementacion = new CalculoRmiImcImplem(puertoObjeto);             // exporta el objeto
registro = LocateRegistry.createRegistry(puertoRegistro);           // crea el rmiregistry (1099)
registro.rebind(IRemotaCalculoImc.NOMBRE_SERVICIO, implementacion); // publica con un nombre
```

En RMI estandar hay **dos puertos**:

| Puerto | Para que sirve |
| ------ | -------------- |
| 1099 | el registro RMI (`rmiregistry`), donde el cliente busca el nombre del servicio |
| 1100 | el objeto remoto exportado, por donde viajan las llamadas |

El registro se puede crear dentro del programa con
`LocateRegistry.createRegistry(...)` (como en esta entrega) **o** ejecutando
aparte el comando `rmiregistry 1099` que viene con el JDK.

### 2.3 Clase `Principal`

Antes de exportar el objeto se publica la propiedad
`java.rmi.server.hostname` con la IP de la maquina, de modo que el *stub* que
recibe el cliente apunte a la direccion correcta (esto es necesario cuando el
cliente esta en otro equipo).

## Paso 3 - Proyecto cliente

Proyecto `EjemploRmiEstandarClienteImc`.

### 3.1 Conexion con el registro

Archivo: `EjemploRmiEstandarClienteImc/src/main/java/com/jcaa/imc/rmi/cliente/vistas/VentanaPrincipal.java`

```java
String url = "rmi://" + host + ":" + puertoRegistro + "/" + IRemotaCalculoImc.NOMBRE_SERVICIO;
calculoImcRemoto = (IRemotaCalculoImc) Naming.lookup(url);
```

Con LipeRMI esto se hacia con `new Client(host, puerto, invocador)` y
`cliente.getGlobal(...)`.

### 3.2 Invocacion remota

```java
DatosImc respuesta = calculoImcRemoto.calcularImc(new DatosImc(peso, altura));
```

Es identica a la del punto 1, salvo que ahora el compilador obliga a manejar
`RemoteException`.

## Paso 4 - Ejecutar y probar

### 4.1 Servidor

```bash
cd EjemploRmiEstandarServidorImc
mvn clean install
java -cp target/classes com.jcaa.imc.rmi.servidor.Principal
```

Salida esperada:

```
=========================================================
  SERVIDOR RMI ESTANDAR DE JAVA - CALCULO DEL IMC
=========================================================
  Registro RMI (rmiregistry) : puerto 1099
  Objeto remoto exportado    : puerto 1100
  URL del servicio           : rmi://192.168.x.x:1099/CalculoImc
=========================================================
```

### 4.2 Cliente

```bash
cd EjemploRmiEstandarClienteImc
mvn clean install
java -cp target/classes com.jcaa.imc.rmi.cliente.Principal
```

Se escribe la IP del servidor y el puerto del registro (`1099`), se presiona
**Conectar**, se ingresan peso y altura, y se presiona **Calcular IMC**.

### 4.3 Resultados verificados

| Peso | Altura | Respuesta del servidor |
| ---- | ------ | ---------------------- |
| 70 | 1.75 | IMC 22.86 - "Estas bien de peso" |
| 45 | 1.75 | "Debes consultar un Medico, tu peso es muy bajo" |
| 85 | 1.75 | "Debes bajar un poco de peso" |
| 120 | 1.75 | "Debes consultar un Medico, tu peso es muy alto" |
| 0 | 1.75 | "ERROR: El peso y la altura deben ser mayores que 0" |

`mvn install` ejecuta 4 pruebas: 3 de la logica y 1 de integracion que crea el
registro, publica el objeto y lo invoca con `Naming.lookup` (el mismo camino que
usa el cliente real).

## Diferencias y similitudes entre RMI estandar y LipeRMI

| Aspecto | RMI estandar (`java.rmi`) | LipeRMI |
| ------- | ------------------------- | ------- |
| Libreria | Viene incluida en el JDK | Libreria de terceros (`lipermi-1.0.1.jar`) |
| Interfaz remota | Debe extender `java.rmi.Remote` | Interfaz normal, sin herencia |
| Excepciones | Cada metodo declara `throws RemoteException` | No declara excepciones de red |
| Objeto remoto | Debe extender `UnicastRemoteObject` o exportarse | Clase normal registrada con `registerGlobal` |
| Registro | Necesita `rmiregistry` (1099) y un puerto para el objeto | No necesita registro; un solo puerto TCP |
| Conexion del cliente | `Naming.lookup("rmi://host:puerto/nombre")` | `new Client(host, puerto, handler)` + `getGlobal(...)` |
| Serializacion | Java serialization (objetos `Serializable`) | Java serialization (igual) |
| Transporte | JRMP sobre TCP | Protocolo propio de la libreria sobre TCP |
| Configuracion de red | Suele requerir abrir el 1099 y el puerto del objeto, y a veces el `hostname` | Solo el puerto del servicio |
| Arranque del servidor | Registro + exportacion + `rebind` | `bind` + `registerGlobal` |
| Codigo para invocar | `stub.calcularImc(datos)` | `proxy.calcularImc(datos)` |
| Dependencia extra | No (solo el JDK y el JAR de la libreria propia) | Si (el JAR de LipeRMI) |
| Ventaja principal | Es el estandar del lenguaje, sin dependencias externas | Menos codigo y configuracion; util con firewalls o clientes livianos |

**Similitudes:** en ambos casos se define una interfaz con los metodos remotos,
se necesita una clase de datos `Serializable` para transportar la informacion,
el cliente se conecta por red a un servicio publicado con un nombre, y la
invocacion se escribe igual que una llamada local (`calcularImc(datos)`). En los
dos casos la logica de negocio se ejecuta en el servidor y el cliente solo
recibe el resultado.

**Conclusion:** RMI estandar es la opcion natural cuando solo hay clientes Java
y se quiere evitar dependencias; LipeRMI simplifica el codigo y la configuracion
de red (un solo puerto, sin `rmiregistry`), lo que resulta comodo para ejemplos
academicos o cuando no se pueden abrir varios puertos.
