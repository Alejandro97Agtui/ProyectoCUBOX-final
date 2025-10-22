package com.cubox.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cubox.model.Producto;
import com.cubox.repository.ICategoriaRepository;
import com.cubox.repository.IMarcaRepository;
import com.cubox.repository.IProductoRepository;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/productos")

public class ProductoController {
	
	@Autowired
	private IProductoRepository repoProd;
	
	@Autowired
	private ICategoriaRepository repoCat;
	
	@Autowired
	private IMarcaRepository repoMar;
	
	
	@GetMapping("/crud")
	private String abrirCrudProductos(Model model, HttpSession session) {
		
		// "Log de session id"
	    System.out.println("Accediendo a manten_producto.html  - ID de sesión: " + session.getId());
	    
	    model.addAttribute("lstProductos", repoProd.findAll());
		
		return "manten_producto";
		
	}
	
	
	
	@GetMapping("/registrar")
	public String abrirRegistrarProducto(Model model, HttpSession session) {
		
		// "Log de session id"
	    System.out.println("Accediendo a registrarActualizarProducto  - ID de sesión: " + session.getId());
	    
	    model.addAttribute("lstCategorias", repoCat.findAll());
	    model.addAttribute("lstMarcas", repoMar.findAll());
	    model.addAttribute("producto", new Producto());
		
		return "registrarActualizarProducto";
	}
	
	
	
	
//----------------------------------------------------------------------------------------------------------------------------------------
	
	
	

}




