# Generacion y clasificacion de datos

Proyecto academico desarrollado en Java para el modulo **Conceptos Fundamentales de Programacion** del Politecnico Grancolombiano.

Esta version corresponde a la **Entrega 2 - Semana 5**. El proyecto ya cuenta con una version preliminar completa: genera archivos planos de prueba, lee esos archivos y crea reportes ordenados de ventas por vendedor y productos vendidos.

## Objetivo

Procesar informacion comercial almacenada en archivos de texto plano para clasificar:

- Vendedores por dinero recaudado, de mayor a menor.
- Productos por cantidad vendida, de mayor a menor.

El proyecto trabaja sin pedir datos por consola, tal como lo solicita el documento guia. Toda la informacion se toma desde la carpeta `files`.

## Tecnologias utilizadas

- Java 8.
- Eclipse IDE for Java Developers.
- Archivos planos `.txt`.
- Reportes `.csv`.
- Git y GitHub para control de versiones.

## Estructura del proyecto

```text
GeneracionClasificacionDatos/
+-- src/
|   +-- co/edu/poligran/proyecto/
|       +-- DataRepository.java
|       +-- GenerateInfoFiles.java
|       +-- Product.java
|       +-- Salesman.java
|       +-- main.java
+-- files/
|   +-- products.txt
|   +-- salesmen_info.txt
|   +-- sales_*.txt
|   +-- products_report.csv
|   +-- salesmen_report.csv
+-- entrega_2_semana_5.txt
+-- DIFERENCIAS_SEMANA_5.md
+-- .classpath
+-- .project
+-- README.md
```

## Clases principales

### `GenerateInfoFiles`

Clase ejecutable encargada de crear los archivos de entrada del proyecto.

Al ejecutarla genera:

- `files/products.txt`
- `files/salesmen_info.txt`
- Un archivo `sales_*.txt` por cada vendedor generado.

Tambien limpia archivos `sales_*.txt` anteriores antes de crear una nueva muestra, para evitar que queden ventas antiguas mezcladas con los vendedores actuales.

### `main`

Clase ejecutable encargada de procesar los archivos generados.

Al ejecutarla genera:

- `files/salesmen_report.csv`
- `files/products_report.csv`

Esta clase valida formatos basicos, vendedores no registrados, productos inexistentes y cantidades negativas.

## Flujo de ejecucion en Eclipse

1. Abrir Eclipse.
2. Importar el proyecto como proyecto Java existente.
3. Ejecutar `co.edu.poligran.proyecto.GenerateInfoFiles`.
4. Verificar que en `files` se creen o actualicen los archivos de entrada.
5. Ejecutar `co.edu.poligran.proyecto.main`.
6. Revisar los reportes generados en la carpeta `files`.

## Formato de archivos de entrada

### Productos: `products.txt`

```csv
P001;Teclado;138714,00
P002;Mouse;191948,00
P003;Monitor;50922,00
```

Formato:

```text
IDProducto;NombreProducto;PrecioPorUnidad
```

### Vendedores: `salesmen_info.txt`

```csv
CC;83566791;Daniela;Lopez
CE;72791908;Paula;Rodriguez
TI;70228856;Felipe;Morales
```

Formato:

```text
TipoDocumento;NumeroDocumento;Nombres;Apellidos
```

### Ventas por vendedor: `sales_*.txt`

```csv
CC;83566791
P010;5;
P001;9;
P003;19;
```

Formato:

```text
TipoDocumento;NumeroDocumento
IDProducto;CantidadVendida;
```

## Reportes generados

### `salesmen_report.csv`

Reporte de vendedores ordenado de mayor a menor segun el dinero recaudado.

```csv
Laura Hernandez;6336035.00
Julian Suarez;6112755.00
Felipe Morales;4779938.00
Daniela Lopez;4026386.00
Paula Rodriguez;2312137.00
```

### `products_report.csv`

Reporte de productos ordenado de mayor a menor segun la cantidad vendida.

```csv
Monitor;50922.00;73
Tablet;16603.00;65
Celular;86262.00;65
Audifonos;16675.00;51
Impresora;56159.00;36
```

## Estado de la entrega 2

La entrega queda como una version preliminar funcional del proyecto completo.

Incluye:

- Generacion automatica de datos de prueba.
- Lectura de productos, vendedores y ventas.
- Reporte de vendedores por recaudo.
- Reporte de productos por cantidad vendida.
- Documento de estado de la entrega: `entrega_2_semana_5.txt`.
- Lista separada de diferencias frente a la semana anterior: `DIFERENCIAS_SEMANA_5.md`.

Pendiente para la entrega final:

- Ampliar pruebas con archivos erroneos.
- Ajustar detalles finales segun retroalimentacion del docente.
- Crear el archivo `conslusion.txt` solicitado para la entrega final.
