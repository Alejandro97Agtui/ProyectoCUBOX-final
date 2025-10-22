package com.cubox.model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class DetallePedidoId implements Serializable {

    @Column(name = "num_ped", columnDefinition = "char(5)")
    private String numPed;

    @Column(columnDefinition = "char(5)")
    private String idproducto;
    
//    // Constructor sin‑argumento (requerido por JPA)
//    public DetallePedidoId() {
//    }
//
//    // (Opcional) Constructor con parámetros para facilitar creación
//    public DetallePedidoId(String numPed, String idproducto) {
//        this.numPed = numPed;
//        this.idproducto = idproducto;
//    }
    
}