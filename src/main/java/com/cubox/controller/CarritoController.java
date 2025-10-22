package com.cubox.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cubox.repository.IProductoRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/carrito")

public class CarritoController {

	@Autowired
	private IProductoRepository repoProd;
	
	
	@GetMapping("/seleccionar/{idproducto}")
	private String abrirDetalleProductoParaCompra(@PathVariable("idproducto") String producto, Model model, HttpSession session) {
		
		// "Log de session id" 
	    System.out.println("Accediendo a compraProducto.html para seleccionar la cantidad - ID de sesión: " + session.getId());
		
	    
	    
		return "compraProducto";
	}
	
}





