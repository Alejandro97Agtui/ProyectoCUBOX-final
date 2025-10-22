package com.cubox.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cubox.model.DetallePedido;
import com.cubox.model.DetallePedidoId;

@Repository

public interface IDetallePedidoRepository extends JpaRepository<DetallePedido, DetallePedidoId>{

	List<DetallePedido> findByIdNumPed(String numPed);
	List<DetallePedido> findByIdIdproducto(String idproducto);
	
}
