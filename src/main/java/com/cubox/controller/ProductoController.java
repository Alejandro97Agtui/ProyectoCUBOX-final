package com.cubox.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cubox.repository.IProductoRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/productos")

public class ProductoController {
	
	@Autowired
	private IProductoRepository repoProd;
	
	
	@GetMapping("/CRUD")
	private String abrirCrudProductos(Model model, HttpSession session) {
		
		// "Log de session id"
	    System.out.println("Accediendo a manten_producto.html  - ID de sesión: " + session.getId());
	    
	    model.addAttribute("lstProductos", repoProd.findAll());
		
		return "manten_producto";
		
	}

}




