package com.cubox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cubox.model.Usuario;

@Repository

public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {

	// Método que devolverá un usuario por correo y clave
	Usuario findByEmailAndClave(String email, String clave);
	
}
