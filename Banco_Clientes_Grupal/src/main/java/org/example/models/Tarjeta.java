package org.example.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Tarjeta {
    //Atributos de la tarjeta
    private Long id;
    private String nombreTitular;
    private String numeroTarjeta;
    private String fechaCaducidad;
}
