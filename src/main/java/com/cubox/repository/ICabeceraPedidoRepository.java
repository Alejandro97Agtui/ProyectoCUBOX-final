package com.cubox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cubox.model.CabeceraPedido;

@Repository

public interface ICabeceraPedidoRepository extends JpaRepository<CabeceraPedido, Long>{

}
