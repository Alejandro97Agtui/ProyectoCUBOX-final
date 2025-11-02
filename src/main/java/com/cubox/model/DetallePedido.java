package com.cubox.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;           // mapea a BIGINT AUTO_INCREMENT

    @ManyToOne
    @JoinColumn(name = "cabecera_id")
    private CabeceraPedido cabecera;

    @ManyToOne
    @JoinColumn(name = "idproducto", referencedColumnName = "idproducto")
    private Producto producto;

    private Integer cantidad;
    
    private BigDecimal precio;
    
}
