package org.example.clientes.cache;

import org.example.clientes.model.Tarjeta;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class CacheImplTest {

    private Tarjeta createTarjeta () {
        return Tarjeta.builder()
                .id(1L)
                .nombreTitular("Juan Pérez")
                .numeroTarjeta("1234 5678 9012 3456")
                .fechaCaducidad(LocalDate.of(2025, 12, 31))
                .build();
    }

    @Test
    void get() {
        CacheImpl<Long, Tarjeta> cache = new CacheImpl<>(2);
        Tarjeta tarjeta1 = createTarjeta();

        cache.put(tarjeta1.getId(), tarjeta1);

        assertAll(
                "Verificar que la tarjeta se guarda y se puede recuperar correctamente",
                () -> assertNotNull(cache.get(tarjeta1.getId()),
                        "La tarjeta debería estar presente en la caché"),
                () -> assertEquals(tarjeta1, cache.get(tarjeta1.getId()),
                        "La tarjeta recuperada debería ser igual a la tarjeta almacenada")
        );
    }

    @Test
    void getNotFound() {
        CacheImpl<Long, Tarjeta> cache = new CacheImpl<>(2);
        Long idNoExistente = 4L;

        assertAll(
                "Verificar que la tarjeta no está presente en la caché",
                () -> assertNull(cache.get(idNoExistente),
                        "La tarjeta no debería estar presente en la caché y debería devolver null")
        );
    }

    @Test
    void put() {
        CacheImpl<Long, Tarjeta> cache = new CacheImpl<>(2);
        Tarjeta tarjeta1 = createTarjeta();
        Tarjeta tarjeta2 = createTarjeta();

        cache.put(tarjeta1.getId(), tarjeta1);
        cache.put(tarjeta2.getId(), tarjeta2);

        assertAll(
                "Verificar que las tarjetas se guardan y se pueden recuperar",
                () -> assertEquals(2, cache.size(), "La caché debería contener dos elementos"),
                () -> assertEquals(tarjeta1, cache.get(tarjeta1.getId()),
                        "La tarjeta1 recuperada debería ser igual a la tarjeta1 almacenada"),
                () -> assertEquals(tarjeta2, cache.get(tarjeta2.getId()),
                        "La tarjeta2 recuperada debería ser igual a la tarjeta2 almacenada")
        );
    }

    @Test
    void putOverrider() {
        CacheImpl<Long, Tarjeta> cache = new CacheImpl<>(2);
        Tarjeta tarjeta1 = createTarjeta();
        Tarjeta tarjeta2 = createTarjeta();
        Tarjeta tarjeta3 = createTarjeta();
        Tarjeta tarjeta4 = createTarjeta();

        cache.put(tarjeta1.getId(), tarjeta1);
        cache.put(tarjeta2.getId(), tarjeta2);
        cache.put(tarjeta1.getId(), tarjeta1);
        cache.put(tarjeta3.getId(), tarjeta3);
        cache.put(tarjeta4.getId(), tarjeta4);
        cache.put(tarjeta1.getId(), tarjeta1);

        // Verificaciones
        assertAll(
                "Verificar que la caché elimina el elemento menos usado cuando se supera el límite",
                () -> assertEquals(2, cache.size(), "La caché debería tener dos elementos"),
                () -> assertFalse(cache.containsKey(tarjeta2.getId()), "La caché no debería contener tarjeta2"),
                () -> assertFalse(cache.containsKey(tarjeta3.getId()), "La caché no debería contener tarjeta3"),
                () -> assertEquals(tarjeta1, cache.get(tarjeta1.getId()),
                        "La tarjeta1 debería estar aún en la caché"),
                () -> assertEquals(tarjeta4, cache.get(tarjeta4.getId()),
                        "La tarjeta4 debería estar en la caché")
        );
    }

    @Test
    void remove() {
        CacheImpl<Long, Tarjeta> cache = new CacheImpl<>(2);
        Tarjeta tarjeta1 = createTarjeta();
        Tarjeta tarjeta2 = createTarjeta();

        cache.put(tarjeta1.getId(), tarjeta1);
        cache.put(tarjeta2.getId(), tarjeta2);

        cache.clear();

        assertAll(
                "Verificar que la caché se vacía correctamente",
                () -> assertEquals(0, cache.size(), "La caché debería estar vacía"),
                () -> assertFalse(cache.containsKey(tarjeta1.getId()),
                        "La caché no debería contener tarjeta1"),
                () -> assertFalse(cache.containsKey(tarjeta2.getId()),
                        "La caché no debería contener tarjeta2")
        );
    }

    @Test
    void clear() {
        CacheImpl<Long, Tarjeta> cache = new CacheImpl<>(2); // Caché con límite de 2 elementos
        Tarjeta tarjeta1 = createTarjeta();
        Tarjeta tarjeta2 = createTarjeta();

        cache.put(tarjeta1.getId(), tarjeta1);
        cache.put(tarjeta2.getId(), tarjeta2);

        assertAll(
                "Verificar que la caché contiene elementos antes de limpiar",
                () -> assertEquals(2, cache.size(), "La caché debería contener dos elementos"),
                () -> assertTrue(cache.containsKey(tarjeta1.getId()), "La caché debería contener tarjeta1"),
                () -> assertTrue(cache.containsKey(tarjeta2.getId()), "La caché debería contener tarjeta2")
        );

        cache.clear();

        assertAll(
                "Verificar que la caché se vacía correctamente",
                () -> assertEquals(0, cache.size(), "La caché debería estar vacía"),
                () -> assertFalse(cache.containsKey(tarjeta1.getId()), "La caché no debería contener tarjeta1"),
                () -> assertFalse(cache.containsKey(tarjeta2.getId()), "La caché no debería contener tarjeta2")
        );
    }


    @Test
    void size() {
        CacheImpl<Long, Tarjeta> cache = new CacheImpl<>(3);
        Tarjeta tarjeta1 = createTarjeta();
        Tarjeta tarjeta2 = createTarjeta();
        Tarjeta tarjeta3 = createTarjeta();

        assertEquals(0, cache.size(), "La caché debería estar vacía al inicio");

        cache.put(tarjeta1.getId(), tarjeta1);
        assertEquals(1, cache.size(), "La caché debería contener un elemento");

        cache.put(tarjeta2.getId(), tarjeta2);
        assertEquals(2, cache.size(), "La caché debería contener dos elementos");

        cache.put(tarjeta3.getId(), tarjeta3);
        assertEquals(3, cache.size(), "La caché debería contener tres elementos");

        cache.clear();
        assertEquals(0, cache.size(), "La caché debería estar vacía después de limpiar");
    }


    @Test
    void valuesDebeDevolverTodosLosElementosDeLaCache() {
        CacheImpl<Long, Tarjeta> cache = new CacheImpl<>(3);
        Tarjeta tarjeta1 = createTarjeta();
        Tarjeta tarjeta2 = createTarjeta();
        Tarjeta tarjeta3 = createTarjeta();

        cache.put(tarjeta1.getId(), tarjeta1);
        cache.put(tarjeta2.getId(), tarjeta2);
        cache.put(tarjeta3.getId(), tarjeta3);

        Collection<Tarjeta> valores = cache.values();
        Collection<Tarjeta> finalValores = valores;
        assertAll(
                "Verificar que los valores devueltos son correctos",
                () -> assertEquals(3, finalValores.size(), "La colección de valores debería contener tres elementos"),
                () -> assertTrue(finalValores.contains(tarjeta1), "La colección de valores debería contener tarjeta1"),
                () -> assertTrue(finalValores.contains(tarjeta2), "La colección de valores debería contener tarjeta2"),
                () -> assertTrue(finalValores.contains(tarjeta3), "La colección de valores debería contener tarjeta3")
        );

        // Limpiar la caché
        cache.clear();
        valores = cache.values();
        assertEquals(0, valores.size(), "La colección de valores debería estar vacía después de limpiar");
    }


    @Test
    void isEmpty() {
        CacheImpl<Long, Tarjeta> cache = new CacheImpl<>(3);

        assertTrue(cache.isEmpty(), "La caché debería estar vacía al inicio");
    }


    @Test
    void isEmptyFalse() {
        CacheImpl<Long, Tarjeta> cache = new CacheImpl<>(3);
        Tarjeta tarjeta1 = createTarjeta();

        cache.put(tarjeta1.getId(), tarjeta1);

        assertFalse(cache.isEmpty(), "La caché no debería estar vacía después de añadir un elemento");
    }
}