package com.cubox.controller;

import java.util.List;
import jakarta.servlet.http.HttpSession; // Spring Boot 3 (Jakarta EE)

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cubox.model.Producto;
import com.cubox.model.Usuario;
import com.cubox.repository.IProductoRepository;
import com.cubox.repository.IUsuarioRepository;

@Controller
@RequestMapping("/")

public class LoginSignInController {
	
	@Autowired
	private IUsuarioRepository repoUsu;
	
	@Autowired
	private IProductoRepository repoProd;
	
	@GetMapping("/login")
	private String cargarLogin() {
		return "login";
	}
	
	@GetMapping("/register")
	private String cargarRegistrarUsuario(Model model) {
		model.addAttribute("usuario", new Usuario());
		return "register";
	}
	
//	@PostMapping("/login")
//	private String validarAcceso(@RequestParam("txtEmail") String correo, @RequestParam("txtClave") String clave, Model model) {
//		
//		System.out.println(correo + " - " + clave);
//		
//		// obtener y capturar un usuario segun el correo y clave, validando si existe
//		Usuario usuarioObtenido = repoUsu.findByEmailAndClave(correo, clave);
//		
//		if(usuarioObtenido != null) {  // Existe!!!
//			
//			List<Producto> lstPDestacados = repoProd.findAll();
//			
//			model.addAttribute("lstPDestacados", lstPDestacados);
//			
//			return "principal";
//			
//		} else { // no existe...
//			
//			model.addAttribute("mensaje", "usuario o clave incorrecto");
//			model.addAttribute("cssmensaje", "alert alert-danger");
//			
//			return "login";
//			
//		}
//		
//	}
	
	
	@PostMapping("/register")
	private String registrarNuevoUsuario(@ModelAttribute Usuario usuario, @RequestParam("clave2") String clave2, Model model) {
		
		System.out.println("Usuario a registrar: " + usuario);
		System.out.println("Clave 2: " + clave2);
		
		try {
			
			String clave1 = usuario.getClave();
			
//			if ("1".equals(usuario.getRol())) {
//	            usuario.setRol("admin");
//	        } else if ("2".equals(usuario.getRol())) {
//	            usuario.setRol("web");
//	        }
			
			if(clave1.equals(clave2)) {
				
				repoUsu.save(usuario);
				
				model.addAttribute("mensaje", "Usuario registrado exitosamente.");
				model.addAttribute("cssmensaje", "alert alert-success");
				
				model.addAttribute("usuario", new Usuario());
				
			} else {
				
				model.addAttribute("mensaje", "No se pudo registrar el usuario. Algún campo o las claves no coinciden...");
				model.addAttribute("cssmensaje", "alert alert-danger");
				
			}
			
		} catch (Exception e) {
			
			model.addAttribute("mensaje", "Error al registrar nuevo usuario.");
			model.addAttribute("cssmensaje", "alert alert-danger");
			
		}
		
		return "register";
	}
	
	
	@PostMapping("/login")
	private String validarAcceso(@RequestParam("txtEmail") String correo, @RequestParam("txtClave") String clave, Model model, HttpSession session) {
		
		System.out.println("Intento de Login: " + correo + " - " + clave);
		
		// obtener y capturar un usuario segun el correo y clave, validando si existe
		Usuario usuarioObtenido = repoUsu.findByEmailAndClave(correo, clave);
		
		if(usuarioObtenido != null) {  // Existe!!!
			
			// Guarda usuario en sesión (clave "u", igual que tu template)
			session.setAttribute("user", usuarioObtenido);
			System.out.println("Usuario grabado en sesión: " + usuarioObtenido.getIdusuario() + 
					" - " + usuarioObtenido.getNombre() + " " + usuarioObtenido.getApellidos() + " - " + 
					" - DNI: " + usuarioObtenido.getDni());  //sysout
			
			// Mostrar Id de Sesión
			System.out.println("ID de sesión: " + session.getId());
			
			// opcional: cantidad de artículos en la canasta (0 por defecto)
			//session.setAttribute("cantArticulos", 0);
			
			// Lista de productos destacados:
			//List<Producto> lstPDestacados = repoProd.findAll();
			
			//model.addAttribute("lstPDestacados", lstPDestacados);
			
			//return "principal";
			
			// Redirijo a "/panel" (GET mapping abajo)
			return "redirect:/panel";
			
		} else { // no existe...
			
			model.addAttribute("mensaje", "usuario o clave incorrecto");
			model.addAttribute("cssmensaje", "alert alert-danger");
			
			return "login";
			
		}
		
	}
	
	
	/**GET /panel  --> renderiza panel.html
	 * Lee user + datos desde la sesión y los pasa al modelo. */
	@GetMapping("/panel")
	private String abrirPanel(Model model, HttpSession session) {
		
		// Recuperar usuario de sesión
		Usuario u = (Usuario) session.getAttribute("user");
		
		if (u == null) {
	        // No hay sesión -> volver a login
	        return "redirect:/login";
	    }
		
		// Pasar usuario al modelo (thymeleaf puede usar ${u} desde el model o desde session)
		model.addAttribute("u", u);
		
		// Cantidad de artículos en canasta
	    //Integer cant = (Integer) session.getAttribute("cantArticulos");
	    //model.addAttribute("cantArticulos", cant == null ? 0 : cant);
		
		// "Log de session id" también al cargar panel...
	    System.out.println("Accediendo a Panel - ID de sesión: " + session.getId());
		
		return "panel"; // Thymeleaf buscará templates/panel.html
		
	}
	
	
	
	@GetMapping("/principal")
	private String validarAcceso(Model model, HttpSession session) {
		
		// Recuperar usuario de sesión
		Usuario u = (Usuario) session.getAttribute("user");
		
		// Pasar usuario al principal.html
		model.addAttribute("u", u);
		
		// "Log de session id" también al cargar principal...
	    System.out.println("Accediendo a Principal - ID de sesión: " + session.getId());
		
		List<Producto> lstPDestacados = repoProd.findAll();
			
		model.addAttribute("lstPDestacados", lstPDestacados);
		
		return "principal";
		
	}
	
	
	@GetMapping("/logout")
	private String cerrarSesion(HttpSession session) {
		
		if(session != null) {
			
			System.out.println("Cerrando sesión. ID de sesión: " + session.getId());
			session.invalidate(); // destruye la sesión
			
		}
		
		return "redirect:/login"; // redirige a login.html
		
	}
	

}






