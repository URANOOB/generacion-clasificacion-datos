# Generacion y clasificacion de datos

Este proyecto fue realizado en **Java** usando **Eclipse**.

La idea del proyecto es generar archivos de prueba con datos de productos, vendedores y ventas. Luego se leen esos archivos para crear reportes que muestran cuanto vendio cada vendedor y que productos se vendieron mas.

## Entrega 2 - Semana 5

En la entrega anterior el proyecto estaba enfocado principalmente en generar archivos planos.

Para esta entrega se avanzo un poco mas y ya se agrego una parte preliminar del programa completo. Ahora el proyecto no solo genera los datos, sino que tambien los lee y crea reportes.

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
- Archivos `.csv`.

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

## Como se ejecuta?

Primero se debe ejecutar la clase:

```text
GenerateInfoFiles
```

Esta clase crea los archivos de prueba dentro de la carpeta `files`.

Despues se ejecuta la clase:

```text
main
```

## Reportes generados

### Reporte de vendedores

El archivo `salesmen_report.csv` muestra los vendedores ordenados desde el que recaudo mas dinero hasta el que recaudo menos.

Ejemplo:

```text
Laura Hernandez;6336035.00
Julian Suarez;6112755.00
Felipe Morales;4779938.00
```

### Reporte de productos

El archivo `products_report.csv` muestra los productos ordenados por cantidad vendida.

Ejemplo:

```text
Monitor;50922.00;73
Tablet;16603.00;65
Celular;86262.00;65
```
