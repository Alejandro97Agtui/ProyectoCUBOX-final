package com.cubox.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "cabecera_pedido")

public class CabeceraPedido {
	
	@Id
	@Column(columnDefinition = "char(5)")
	private String num_ped;
	
	@Column(name = "fch_ped")
	private LocalDate fechaPedido  = LocalDate.now(); // Fecha actual por defecto;
	
	@Column(name = "codigo_cliente")
	private int codigoCliente;

}
