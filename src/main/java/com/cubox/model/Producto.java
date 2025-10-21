package com.cubox.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "productos")

public class Producto {
	
	@Id
	@Column(columnDefinition = "char(5)")
	private String idproducto;
	private int idcategoria;
	private String idmarca;
	@Column(length = 100)
	private String nombre;
	private String descripcion;
	@Column(precision = 10, scale = 2)
	private BigDecimal precio;
	@Column(precision = 10, scale = 2)
	private BigDecimal precio_oferta;
	private int stock = 0;
	@Column(length = 100)
	private String imagen_principal;
	@Column(length = 100)
	private String imagen_dos;
	@Column(length = 100)
	private String imagen_tres;
	
	@ManyToOne
	@JoinColumn(name = "idmarca", insertable = false, updatable = false)
	private Marca objMarca;
	
	@ManyToOne
	@JoinColumn(name = "idcategoria", insertable = false, updatable = false)
	private Categoria objCategoria;

}


