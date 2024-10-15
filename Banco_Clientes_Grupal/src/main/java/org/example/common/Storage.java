package org.example.common;

import io.reactivex.rxjava3.core.Observable;
import java.io.File;

/**
 * Interfaz que define los métodos para gestionar la importación y exportación de archivos.
 *
 * @param <T> El tipo de los elementos que se importan o exportan.
 */
public interface Storage<T> {

    /**
     * Importa los datos de un archivo y los convierte en una secuencia de elementos.
     *
     * @param file El archivo desde el cual se importarán los datos.
     * @return Un Observable que emite los elementos importados del archivo.
     */
    Observable<T> importFile(File file);

    /**
     * Exporta una secuencia de elementos a un archivo.
     *
     * @param file  El archivo al cual se exportarán los datos.
     * @param items Un Observable que emite los elementos a exportar.
     */
    void exportFile(File file, Observable<T> items);
}
