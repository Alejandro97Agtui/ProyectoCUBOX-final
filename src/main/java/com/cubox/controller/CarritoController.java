package com.cubox.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cubox.model.Producto;
import com.cubox.repository.IProductoRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/carrito")

public class CarritoController {

	@Autowired
	private IProductoRepository repoProd;
	
	
	@GetMapping("/seleccionar/{idproducto}")
	private String abrirDetalleProductoParaCompra(@PathVariable("idproducto") String idproducto, Model model, HttpSession session) {
		
		// "Log de session id" 
	    System.out.println("Accediendo a compraProducto.html para seleccionar la cantidad - ID de sesión: " + session.getId());
		
	    Producto productoSeleccionado = repoProd.findById(idproducto).get();
	    
	    // 1) guardar producto en sesión (si lo necesitas)
	    
	    session.setAttribute("productoSeleccionado", productoSeleccionado);
	    System.out.println("Producto enviado a nivel de sesion: " + session.getAttribute("productoSeleccionado"));
	    
	    
	    // 2) **añadir al modelo** para que Thymeleaf lo muestre en el navegador
	    model.addAttribute("productoSeleccionado", productoSeleccionado);
	    
	    Object usuarioEnSesion = session.getAttribute("user");
	    
	    if (usuarioEnSesion != null) {
	    	
            // (reinsertarlo en sesión por si acaso)
            session.setAttribute("user", usuarioEnSesion);
            
        } else {
        	
            System.out.println("No existe 'user' en sesión. Asegúrate de setearlo en el login.");
            
        }
	    
	    
	    // Cantidad de artículos en la canasta (opcional: evita NPE en la vista)
	    
        Integer cant = (Integer) session.getAttribute("cantArticulos");
        
        if (cant == null) {
            cant = 0;
        }
        
        model.addAttribute("cantArticulos", cant);
	    
	    
		return "compraProducto";
		
	}
	
	
	
	@PostMapping("/agregar")
	private String agregarProductoSeleccionadoACanaste(@RequestParam("cantidad") int cantidad, Model model, HttpSession session) {
		
		Producto productoSeleccionado = (Producto) session.getAttribute("productoSeleccionado");
		
		System.out.println("-------------------------------------------------------------------");
		System.out.println("Producto Seleccionado: " + productoSeleccionado);
		System.out.println("Cantidad del producto Seleccionado a comprar: " + cantidad);
		
		
		return "canasta";
	}
	
	
	
}





