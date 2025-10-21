package com.cubox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cubox.model.Marca;

@Repository

public interface IMarcaRepository extends JpaRepository<Marca, Integer>{

}
