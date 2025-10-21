package com.cubox.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cubox.repository.IProductoRepository;

import jakarta.servlet.http.HttpSession;

public class CatalogoController {
	
	@Autowired
	private IProductoRepository repoProd;
	
	@GetMapping("/catalogo")
	private String abriCatalogo(Model model, HttpSession session) {
		
		// "Log de session id" 
	    System.out.println("Accediendo a catalogo.html - ID de sesión: " + session.getId());
	    
	    model.addAttribute("lstProductos", repoProd.findAll());
		
		return "catalogo";
		
	}

}
