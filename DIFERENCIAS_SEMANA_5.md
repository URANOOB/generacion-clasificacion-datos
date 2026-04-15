# Diferencias entre la semana anterior y la semana 5

## Semana anterior

- El proyecto estaba enfocado principalmente en la generacion de archivos planos de prueba.
- La clase principal trabajada era `GenerateInfoFiles`.
- Se generaban productos, vendedores y archivos de ventas pseudoaleatorios.
- La carpeta `files` funcionaba como entrada para el problema principal, pero todavia no existia el procesamiento completo de reportes.

## Semana 5

- Se agrego una version preliminar del proyecto completo.
- Se creo la clase `main`, segunda clase con metodo `main`, como pide el documento guia.
- Se implemento la lectura de `products.txt`, `salesmen_info.txt` y los archivos `sales_*.txt`.
- Se agrego el calculo del dinero recaudado por cada vendedor.
- Se agrego el calculo de unidades vendidas por producto.
- Se generan reportes CSV ordenados:
  - `files/salesmen_report.csv`
  - `files/products_report.csv`
- Se agregaron validaciones basicas de formato e informacion incoherente.
- Se documento el estado de la entrega en `entrega_2_semana_5.txt`.
- Se actualizo el README para explicar objetivo, estructura, ejecucion y resultados.

## Tecnologias usadas

- Java 8 como lenguaje principal.
- Eclipse IDE for Java Developers como entorno de desarrollo.
- Programacion orientada a objetos con clases `Product`, `Salesman`, `DataRepository`, `GenerateInfoFiles` y `main`.
- Archivos `.txt` separados por punto y coma como fuente de datos.
- Archivos `.csv` para los reportes generados.
- Git para control de versiones.
- GitHub para publicar el repositorio.
