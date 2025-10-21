package com.cubox.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cubox.repository.IProductoRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/catalogo")

public class CatalogoController {
	
	@Autowired
	private IProductoRepository repoProd;
	
	@GetMapping("/abrir")
	private String abriCatalogo(Model model, HttpSession session) {
		
		// "Log de session id" 
	    System.out.println("Accediendo a catalogo.html - ID de sesión: " + session.getId());
	    
	    
	    // 1) Asegurar que la sesión contiene el objeto "usuario"
	    
        // Si en tu flujo de login ya guardas el usuario en sesión con:
        // session.setAttribute("usuario", usuario);  <-- entonces no hace falta más.
        // Aquí lo comprobamos y, si existe, opcionalmente lo dejamos en sesión y/o lo añadimos al model.
	    
	    Object usuarioEnSesion = session.getAttribute("user");
	    
	    if (usuarioEnSesion != null) {
	    	
            // Si quieres usarlo también desde el model (no es estrictamente necesario porque
            // Thymeleaf puede acceder a session.usuario), puedes descomentar:
	    	
            // model.addAttribute("user", usuarioEnSesion);

            // (reinsertarlo en sesión por si acaso)
            session.setAttribute("user", usuarioEnSesion);
            
        } else {
        	
            // Si no hay usuario en sesión y quieres redirigir a login, podrías devolver "redirect:/login"
            // Pero como pediste no cambiar la estructura, sólo dejo este comentario.
        	
            System.out.println("No existe 'user' en sesión. Asegúrate de setearlo en el login.");
            
        }
	    
	    
	    // 2) Cantidad de artículos en la canasta (opcional: evita NPE en la vista)
	    
        Integer cant = (Integer) session.getAttribute("cantArticulos");
        
        if (cant == null) {
            cant = 0;
        }
        
        model.addAttribute("cantArticulos", cant);
        
        
        // 3) Lista de productos para el catálogo
	    
	    model.addAttribute("lstProductos", repoProd.findAll());
		
		return "catalogo";
		
	}

}




