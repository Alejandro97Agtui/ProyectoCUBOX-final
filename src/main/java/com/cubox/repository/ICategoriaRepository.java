package com.cubox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cubox.model.Categoria;

@Repository

public interface ICategoriaRepository extends JpaRepository<Categoria, Integer>{

}
