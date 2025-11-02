package com.cubox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cubox.model.DetallePedido;

@Repository

public interface IDetallePedidoRepository extends JpaRepository<DetallePedido, Long>{
	
}
