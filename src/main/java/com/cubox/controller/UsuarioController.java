package com.cubox.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.cubox.model.Usuario;
import com.cubox.repository.IUsuarioRepository;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuarios")

public class UsuarioController {
	
	@Autowired
	private IUsuarioRepository repoUsu;
	
	@Autowired
	private ServletContext servletContext;
	
	
	@GetMapping("/registrar")
	private String registrarUsuarios(Model model, HttpSession session) {
		
		// "Log de session id" al acceder al usuariosAdmin.html ...
	    System.out.println("Accediendo a /usuarios/registrar - ID de sesión: " + session.getId());
	    
	    model.addAttribute("user", new Usuario());
		
		return "registrarActualizarUsuario";
	}
	
	
	
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
	
	
	
	@GetMapping("/imagenUsuario")
	private void mostrarImagenUsuario(@RequestParam("idusuario") Integer idusuario, HttpServletResponse response) throws IOException {
		
		// 1) obtener el registro de usuario desde la BD
		
	    Usuario u = repoUsu.findById(idusuario).orElse(null);
	    
	    
	    // 2) decidir nombre de archivo (valor por defecto si no existe)
	    
	    String nombreArchivo = "defaultUsuario.png";
	    
	    if (u != null && u.getImagen() != null && !u.getImagen().isEmpty()) {
	        nombreArchivo = u.getImagen();
	    }
	    
	    
	    // 3) construir ruta de carpeta (ajusta si tu carpeta es distinta)
	    
	    Path rutaCarpeta = Paths.get("C:", "ProyectoImagenesLPI", "imagenesUsuario");
	    
	    Path archivo = rutaCarpeta.resolve(nombreArchivo); // ruta completa del archivo
	    
	    
	    // si no existe el archivo elegido, intentar el default; si tampoco existe -> 404
	    
	    Path archivoDefault = rutaCarpeta.resolve("defaultUsuario.png");
	    
	    if (!Files.exists(archivo)) {
	        if (Files.exists(archivoDefault)) {
	            archivo = archivoDefault;
	        } else {
	            response.sendError(HttpServletResponse.SC_NOT_FOUND);
	            return;
	        }
	    }
	    
	    
	    // 4) obtener mime type y escribir bytes en la respuesta
	    
	    String mime = servletContext.getMimeType(archivo.toString());
	    
	    if (mime == null) {
	        mime = "application/octet-stream";
	    }
	    
	    response.setContentType(mime);
	    response.setContentLengthLong(Files.size(archivo));

	    
	    // 5) stream del archivo al response
	    
	    Files.copy(archivo, response.getOutputStream());  // Es la operación que realmente envía el contenido de la imagen al navegador.
	    response.getOutputStream().flush();
	    
		
	}
	
	
	
//------------------------------------------------------------------------------------------------------------------------------

	
	@PostMapping("/grabar")
	private String grabarUsuario(@ModelAttribute Usuario user, @RequestParam(value = "imagenFile", required = false) 
		MultipartFile file, Model model, HttpSession session) {
		
		// "Log de session id" al acceder al usuariosAdmin.html ...
	    System.out.println("Procediendo a grabar Usuario - ID de sesión: " + session.getId());
	    System.out.println("Usuario a grabar: " + user);
	    
	    // Ruta fija (ajusta según tu entorno). Ejemplo similar al de marcas:
	    String rutaCarpeta = "C:" + File.separator + "ProyectoImagenesLPI" + File.separator + "imagenesUsuario";
	    
	    // Nombre por defecto si no se sube imagen
	    String nombreArchivo = "defaultUsuario.png";
		
		try {
			
			// 1) Crear la carpeta si no existe
			
	        Path ruta = Paths.get(rutaCarpeta);
	        
	        if (!Files.exists(ruta)) {
	            Files.createDirectories(ruta);
	        }
	        
	        // Determinar si es edición (id > 0) ANTES de save()
	        boolean esEdicion = (user.getIdusuario() > 0);
	        
	        
	        
	        // 2) Procesar archivo si se subió
	        
	        if (file != null && !file.isEmpty()) {
	        	
	        	String original = file.getOriginalFilename();  //obtener nombre original del archivo (sin la extension...)
	        	
	        	String extension = "";  // obtener la extensión del archivo
	        	
	        	int idx = original.lastIndexOf('.');
	            if (idx >= 0) {
	                extension = original.substring(idx).toLowerCase(); // incluye el punto, ej: ".png"
	            }
	            
	            
	            // Validación básica por extensión
	            if (!extension.equals(".png") && !extension.equals(".jpg") && !extension.equals(".jpeg")) {
	                model.addAttribute("mensaje", "Tipo de archivo no permitido. Use jpg/jpeg/png.");
	                model.addAttribute("cssmensaje", "alert alert-danger");
	                // mantener el objeto user para que Thymeleaf no falle al renderizar th:object
	                model.addAttribute("user", user);
	                return "registrarActualizarUsuario";
	            }

	            // Generar nombre único para el archivo imagen
	            nombreArchivo = UUID.randomUUID().toString() + extension;
	            
	            
	            // Copiar a la carpeta destino
	            Path destino = ruta.resolve(nombreArchivo);  // obtiene la ruta completa 
	            try (InputStream is = file.getInputStream()) {
	                Files.copy(is, destino, StandardCopyOption.REPLACE_EXISTING);
	                // copia los bytes de la imagen a la ruta "destino" y, si existe la imagen, la sobreescribe
	            }

	            
	        	
	        } else {
	        	
	        	// No hay archivo subido -> conservar imagen existente si es edición
	        	
        		if (user.getImagen() != null) {
            		
            		Usuario existente = repoUsu.findById(user.getIdusuario()).orElse(null);
            		
            		if (existente != null && existente.getImagen() != null && !existente.getImagen().isBlank()) {
            			nombreArchivo = existente.getImagen();
            		} else {
            			// Si por alguna razón la marca existente no tiene imagen, usar default
            			nombreArchivo = "defaultUsuario.png";
            		}
	        	} else {
	        		
	        		// Nuevo registro y no subieron imagen -> imagen por defecto
	                
	        		nombreArchivo = "defaultUsuario.png";
	        		
	        	}
	        	
	        }
	        	
	        	
	        // 3) Guardar nombre de imagen en el objeto Usuario y persistir
	        
	        user.setImagen(nombreArchivo);
	        
	        
	        // 4) Guardar (insertará si id 0, actualizará si id > 0)
	        
			repoUsu.save(user);
			
			
			// 5) Preparar mensaje de éxito
			
	        if (esEdicion) {
	        	
	            model.addAttribute("mensaje", "Usuario actualizado exitosamente.");
	            
	        } else {
	        	
	            model.addAttribute("mensaje", "Usuario registrado exitosamente.");
	            
	        }
	        
	        model.addAttribute("cssmensaje", "alert alert-success");

	        
	        // Limpiar el formulario (si quieres dejarlo vacío)
	        
	        model.addAttribute("user", new Usuario());

			
		} catch (Exception e) {
			
			e.printStackTrace();
			model.addAttribute("mensaje", "Error al grabar usuario.");
			//model.addAttribute("mensaje", "Error al realizar mantenimiento de usuario.");
			model.addAttribute("cssmensaje", "alert alert-danger");
			
			// devolver el objeto recibido para no romper th:object
	        model.addAttribute("user", user);
			
		}
		
		
		// 6) Volver a la misma página del formulario
		
		return "registrarActualizarUsuario";
		
	}
	
	
	

}






