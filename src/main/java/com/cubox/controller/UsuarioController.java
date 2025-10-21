package com.cubox.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cubox.model.Usuario;
import com.cubox.repository.IUsuarioRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuarios")

public class UsuarioController {
	
	@Autowired
	private IUsuarioRepository repoUsu;
	
	@GetMapping("/admin")
	private String abrirUsuariosAdmin(Model model, HttpSession session) {
		
		// "Log de session id" al acceder al usuariosAdmin.html ...
	    System.out.println("Accediendo a usuariosAdmin.html - ID de sesión: " + session.getId());
	    
	    String rol = "admin";
	    
	    List<Usuario> lstUsuariosAdmin = repoUsu.findAllByRol(rol);
	    
	    model.addAttribute("lstUsuariosAdmin", lstUsuariosAdmin);
		
		return "usuariosAdmin";
	}
	
	
	
	@GetMapping("/web")
	private String abrirUsuariosWeb(Model model, HttpSession session) {
		
		// "Log de session id" al acceder al usuariosAdmin.html ...
	    System.out.println("Accediendo a usuariosWeb.html - ID de sesión: " + session.getId());
	    
	    String rol = "web";
	    
	    List<Usuario> lstUsuarioWeb = repoUsu.findAllByRol(rol);
	    
	    model.addAttribute("lstUsuarioWeb", lstUsuarioWeb);
		
		return "usuariosWeb";
	}
	
	
	
//------------------------------------------------------------------------------------------------------------------------------
	

}






