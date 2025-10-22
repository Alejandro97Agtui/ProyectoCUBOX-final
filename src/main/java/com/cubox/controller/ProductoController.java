package com.cubox.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cubox.model.Producto;
import com.cubox.repository.ICategoriaRepository;
import com.cubox.repository.IMarcaRepository;
import com.cubox.repository.IProductoRepository;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
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
	
	@Autowired
    private ServletContext servletContext; // necesario para obtener mime type
	
	
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
	
	
	
	// Uso en la vista: /productos/ImagenProducto?idproducto=123&tipo=principal
	
	@GetMapping("/ImagenProducto")
	private void mostrarImagenesProducto(@RequestParam("idproducto") String idproducto,
            @RequestParam(value = "tipo", required = false, defaultValue = "principal") String tipo,
            HttpServletResponse response) throws IOException {
		
		// 1) obtener el registro de producto desde la BD
		
        Producto p = repoProd.findById(idproducto).orElse(null);
        
        
        // 2) decidir nombre de archivo (valor por defecto si no existe)
        
        String nombreArchivo = "no-imagen.png"; // default en la carpeta de imágenes
		
		if(p != null) {
			
			if ("dos".equalsIgnoreCase(tipo)) {
				
	            if (p.getImagen_dos() != null && !p.getImagen_dos().isEmpty()) {
	                nombreArchivo = p.getImagen_dos();
	            }
	            
	        } else if ("tres".equalsIgnoreCase(tipo)) {
	        	
	            if (p.getImagen_tres() != null && !p.getImagen_tres().isEmpty()) {
	                nombreArchivo = p.getImagen_tres();
	            }
	            
	        } else { // principal
	        	
	            if (p.getImagen_principal() != null && !p.getImagen_principal().isEmpty()) {
	                nombreArchivo = p.getImagen_principal();
	            }
	            
	        }
			
		}
        
		
		// 3) construir ruta de carpeta (ajusta según tu estructura real)
		
        // Ejemplo: C:\ProyectoImagenesLPI\imagenesProducto\{nombreArchivo}
        Path rutaCarpeta = Paths.get("C:", "ProyectoImagenesLPI", "imagenesProducto");
        Path archivo = rutaCarpeta.resolve(nombreArchivo);
        
        // si no existe el archivo elegido, intentar el default; si tampoco existe -> 404
        
        Path archivoDefault = rutaCarpeta.resolve("no-imagen.png");
        
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
        
        Files.copy(archivo, response.getOutputStream());
        response.getOutputStream().flush();

		
	}
	
	

//----------------------------------------------------------------------------------------------------------------------------------------
	
	
	
	
	
	
	
}




