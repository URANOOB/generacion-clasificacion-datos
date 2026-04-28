# Generacion y clasificacion de datos

Este proyecto fue realizado en **Java** usando **Eclipse**.

La idea del proyecto es generar archivos de prueba con datos de productos, vendedores y ventas. Luego se leen esos archivos para crear reportes que muestran cuanto vendio cada vendedor y que productos se vendieron mas.

## Entrega final - Semanas 7 y 8

En la primera parte del proyecto se trabajo la generacion de archivos planos. Despues se agrego la parte de lectura y generacion de reportes. En esta entrega final ya queda armado el proyecto completo con las dos clases principales que pide la actividad.

## Que hace el proyecto

- Genera productos de prueba.
- Genera vendedores de prueba.
- Genera ventas por cada vendedor.
- Lee los archivos generados.
- Calcula el dinero recaudado por cada vendedor.
- Calcula la cantidad vendida de cada producto.
- Genera reportes en archivos CSV.

## Tecnologias usadas

- Java 8.
- Eclipse IDE.
- Archivos de texto `.txt`.
- Archivos `.csv`.
- Git y GitHub.

## Archivos principales

```text
src/co/edu/poligran/proyecto/
  DataRepository.java
  GenerateInfoFiles.java
  Product.java
  Salesman.java
  main.java

files/
  products.txt
  salesmen_info.txt
  sales_*.txt
  products_report.csv
  salesmen_report.csv
```

## Como se ejecuta

Primero se debe ejecutar la clase:

```text
GenerateInfoFiles
```

Esta clase crea los archivos de prueba dentro de la carpeta `files`.

Despues se ejecuta la clase:

```text
main
```

Esta clase lee los archivos y genera los reportes.

## Ejemplo de productos

```text
P001;Teclado;167134,00
P002;Mouse;59569,00
P003;Monitor;145759,00
```

El formato usado es:

```text
IDProducto;NombreProducto;Precio
```

## Ejemplo de vendedores

```text
TI;27087157;Carlos;Vargas
CC;76003117;Mariana;Morales
CE;82798565;Paula;Vargas
```

El formato usado es:

```text
TipoDocumento;NumeroDocumento;Nombre;Apellido
```

## Ejemplo de ventas

```text
CC;18886029
P001;10;
P004;3;
P009;10;
```

La primera linea identifica al vendedor. Las demas lineas indican el producto vendido y la cantidad.

## Reportes generados

### Reporte de vendedores

El archivo `salesmen_report.csv` muestra los vendedores ordenados desde el que recaudo mas dinero hasta el que recaudo menos.

Ejemplo:

```text
Camila Martinez;12169196.00
Paula Torres;11827953.00
Mariana Morales;10941165.00
```

### Reporte de productos

El archivo `products_report.csv` muestra los productos ordenados por cantidad vendida.

Ejemplo:

```text
Audifonos;180390.00;82
Impresora;69303.00;76
Portatil;10250.00;72
```

## Archivos agregados para la entrega

- `entrega_2_semana_5.txt`: indica que partes estan listas y que falta mejorar.
- `DIFERENCIAS_SEMANA_5.md`: explica las diferencias entre la semana anterior y esta entrega.
- `conslusion.txt`: contiene la reflexion final sobre lo aprendido, posibles aplicaciones y dificultades del proyecto.

## Estado actual

En este momento el proyecto ya cuenta con:

- La clase `GenerateInfoFiles` para generar los archivos de entrada.
- La clase `main` para leer esos archivos y generar los reportes.
- Reporte de vendedores ordenado por dinero recaudado.
- Reporte de productos ordenado por cantidad vendida.
- El archivo `conslusion.txt` solicitado en la guia.

## Nota

Los datos cambian cada vez que se ejecuta `GenerateInfoFiles`, porque se generan de forma pseudoaleatoria.
