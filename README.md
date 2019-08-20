# Cómo montar y probar el ejercico

A continuación se indican los pasos, en orden secuencial, para montar y probar el ejercicio.


## Montar el servidor

Para este paso es necesario tener docker instalado en la máquina.

Desde el directorio raíz en consola, ejecutar los siguientes comandos:

```
$ cd iot-server
$ ./mvnw clean package
$ docker build -f src/main/docker/Dockerfile.jvm -t quarkus/iot-server-jvm .
```


## Montar el cliente

Para este paso es necesario tener docker instalado en la máquina.

Desde el directorio raíz, ejecutar los siguientes comandos:

```
$ cd iot-client
$ ./mvnw clean package
$ docker build -f src/main/docker/Dockerfile.jvm -t quarkus/iot-client-jvm .
```


## Levantar el sistema

Para este paso es necesario tener docker-compose instalado en la máquina.

Desde el directorio raíz, ejecutar los comandos

```
$ docker-compose up
```


## Verificar las instancias

Introduciremos las siguientes URLs en un navegador : 

```
http://localhost:9080/health
```

```
http://localhost:9081/health
```

En ambos casos deberá aparecer el siguiente mensaje en formato JSON, que indica que los contenedores docker se han levantado satisfactoriamente

```
{
    "status": "UP",
    "checks": [
    ]
}
```


## Trasteando la API del cliente

Desde la siguiente URL aparecerá una interfaz de swagger con los 2 endpoints del cliente. 

```
http://localhost:9080/swagger-ui
```

En ambos casos se puede probar el funcionamiento de los endpoints insertando un mensaje de texto y pulsando sobre el botón 'Execute'.

_Nota 1_: actualmente solo funciona el endpoint *MQTT*

El resultado quedará reflejado por la consola de docker-compose. Tenía planeado montar un recurso en el servidor para mostrar por UI los mensajes enviados desde el cliente, usando SSE, pero desgraciadamente no me ha dado tiempo.


## Finalizar el ejercicio

Desde el directorio raíz en consola, pulsar Ctrl+C para parar los contenedores docker y ejecutar el siguiente comando para eliminar las imágenes.

```
$ docker-compose down
```


