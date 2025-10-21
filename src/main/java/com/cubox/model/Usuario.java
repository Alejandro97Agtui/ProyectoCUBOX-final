package com.cubox.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios")

public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int idusuario;
	@Column(length = 45)
	private String nombre;
	@Column(length = 50)
	private String apellidos;
	@Column(name = "usuario", length = 150)
	private String email;
	@Column(length = 15)
	private String celular;
	@Column(length = 8)
	private String dni;
	@Column(length = 250)
	private String clave;
	@Column(length = 5)
	private String rol;
	@Column(length = 100)
	private String imagen;
	@Column(name = "fecha_registro")
	private LocalDate fechaRegistro  = LocalDate.now(); // Fecha actual por defecto;

	
}
