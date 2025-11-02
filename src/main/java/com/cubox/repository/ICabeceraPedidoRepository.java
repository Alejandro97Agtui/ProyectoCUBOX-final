package com.cubox.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cubox.model.CabeceraPedido;
import com.cubox.model.Usuario;

@Repository

public interface ICabeceraPedidoRepository extends JpaRepository<CabeceraPedido, Long>{
	
	List<CabeceraPedido> findAllByUsuario(Usuario usuario);
	
}
