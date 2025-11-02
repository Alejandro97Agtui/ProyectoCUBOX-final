package com.cubox.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "cabecera_pedido")
public class CabeceraPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            // mapea a BIGINT AUTO_INCREMENT
    
    @Column(name = "num_ped")
    private String numPed;      // código legible
    
    @Column(name = "fch_ped")
    private LocalDate fchPed;

    @ManyToOne
    @JoinColumn(name = "codigo_cliente")
    private Usuario usuario;

    @OneToMany(mappedBy = "cabecera", cascade = CascadeType.ALL)
    private List<DetallePedido> detalles = new ArrayList<>();
    
}
