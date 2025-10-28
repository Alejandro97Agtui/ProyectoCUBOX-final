package com.cubox.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.cubox.model.Producto;
import com.cubox.repository.ICategoriaRepository;
import com.cubox.repository.IMarcaRepository;
import com.cubox.repository.IProductoRepository;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;


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
	
	@Autowired
	private DataSource dataSource; // javax.sql

	@Autowired
	private ResourceLoader resourceLoader; // core.io

	
	
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
	
	
	@GetMapping("/reportes")
	public void reportes(HttpServletResponse response) {
	    // opción 1
	    //response.setHeader("Content-Disposition", "attachment; filename=\"reporte.pdf\";");
	    // opción 2
	    response.setHeader("Content-Disposition", "inline;");
	    
	    response.setContentType("application/pdf");
	    try {
	        String ru = resourceLoader.getResource("classpath:/static/Reporte01.jasper").getURI().getPath();
	        JasperPrint jasperPrint = JasperFillManager.fillReport(ru, null, dataSource.getConnection());
	        OutputStream outStream = response.getOutputStream();
	        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	@GetMapping("/graficosA")
	public void obtenerGraficoA(HttpServletResponse response) {
	    // opción 1
	    //response.setHeader("Content-Disposition", "attachment; filename=\"reporte.pdf\";");
	    // opción 2
	    response.setHeader("Content-Disposition", "inline;");
	    
	    response.setContentType("application/pdf");
	    try {
	        String ru = resourceLoader.getResource("classpath:/static/grafico2.jasper").getURI().getPath();
	        JasperPrint jasperPrint = JasperFillManager.fillReport(ru, null, dataSource.getConnection());
	        OutputStream outStream = response.getOutputStream();
	        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	
	@GetMapping("/graficosB")
	public void obtenerGraficoB(HttpServletResponse response) {
	    // opción 1
	    //response.setHeader("Content-Disposition", "attachment; filename=\"reporte.pdf\";");
	    // opción 2
	    response.setHeader("Content-Disposition", "inline;");
	    
	    response.setContentType("application/pdf");
	    try {
	        String ru = resourceLoader.getResource("classpath:/static/Grafico3.jasper").getURI().getPath();
	        JasperPrint jasperPrint = JasperFillManager.fillReport(ru, null, dataSource.getConnection());
	        OutputStream outStream = response.getOutputStream();
	        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
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
	
	
	
	@GetMapping("/editar/{idproducto}")
	private String editarProducto(@PathVariable("idproducto") String idproducto, Model model, HttpSession session) {
		
		// "Log de session id"
	    System.out.println("Accediendo a registrarActualizarProducto.html para editar - ID de sesión: " + session.getId());
	    
	    Producto p = repoProd.findById(idproducto).get();
	    
	    model.addAttribute("producto", p);
	    model.addAttribute("lstCategorias", repoCat.findAll());
	    model.addAttribute("lstMarcas", repoMar.findAll());
		
		return "registrarActualizarProducto";
		
	}
	
	
	
//----------------------------------------------------------------------------------------------------------------------------------------
	
	
	
	@PostMapping("/grabar")
	private String grabarProducto(@ModelAttribute Producto producto, @RequestParam(value = "imagen1", required = false) MultipartFile imagen1,
            @RequestParam(value = "imagen2", required = false) MultipartFile imagen2,
            @RequestParam(value = "imagen3", required = false) MultipartFile imagen3, Model model, HttpSession session) { 
		
		
		System.out.println("Procediendo a grabar Producto - ID de sesión: " + session.getId());
	    System.out.println("Producto a grabar: " + producto);
	    
	    // Ruta fija (ajusta según tu entorno). Ejemplo Windows:
	    
	    String rutaCarpeta = "C:" + File.separator + "ProyectoImagenesLPI" + File.separator + "imagenesProducto";
	    
	    
        try {
        	
    	    // 1) Crear la carpeta si no existe
        	
        	Path ruta = Paths.get(rutaCarpeta);
            
            if (!Files.exists(ruta)) {
                Files.createDirectories(ruta);
            }
            
            
            // 2) detectar si es edición y cargar imágenes existentes (para conservar si no se sube nueva)
            
            boolean esEdicion = (producto.getIdproducto() != null && !producto.getIdproducto().isBlank()
                    && repoProd.existsById(producto.getIdproducto()));
            
            
            // Si es edición, obtener el producto existente para conservar imágenes no reemplazadas
            
            Producto existente = null;
            
            if (esEdicion) {
            	
                existente = repoProd.findById(producto.getIdproducto()).orElse(null);
                
            }
            
            String nombreImgPrincipal = (existente != null && existente.getImagen_principal() != null && !existente.getImagen_principal().isBlank())
                    ? existente.getImagen_principal() : "no-imagen.png";
            String nombreImgDos = (existente != null && existente.getImagen_dos() != null && !existente.getImagen_dos().isBlank())
                    ? existente.getImagen_dos() : "no-imagen.png";
            String nombreImgTres = (existente != null && existente.getImagen_tres() != null && !existente.getImagen_tres().isBlank())
                    ? existente.getImagen_tres() : "no-imagen.png";
            
            
            // 3) procesar imagen1 (principal)
            
            if (imagen1 != null && !imagen1.isEmpty()) {
            	
                String original = imagen1.getOriginalFilename();
                
                String extension = (original != null && original.contains(".")) ? original.substring(original.lastIndexOf(".")).toLowerCase() : "";
                
                if (!extension.equals(".png") && !extension.equals(".jpg") && !extension.equals(".jpeg")) {
                	
                    model.addAttribute("mensaje", "Tipo de archivo no permitido (imagen1). Use jpg/jpeg/png.");
                    model.addAttribute("cssmensaje", "alert alert-danger");
                    model.addAttribute("producto", producto);
                    return "registrarActualizarProducto";
                    
                }
                
                String nuevoNombre = UUID.randomUUID().toString() + extension;
                
                Path destino = ruta.resolve(nuevoNombre);
                
                try (InputStream is = imagen1.getInputStream()) {
                    Files.copy(is, destino, StandardCopyOption.REPLACE_EXISTING);
                }
                
                nombreImgPrincipal = nuevoNombre;
                
            }
            
            
            // 4) procesar imagen2
            
            if (imagen2 != null && !imagen2.isEmpty()) {
            	
                String original = imagen2.getOriginalFilename();
                
                String ext = (original != null && original.contains(".")) ? original.substring(original.lastIndexOf(".")).toLowerCase() : "";
                
                if (!ext.equals(".png") && !ext.equals(".jpg") && !ext.equals(".jpeg")) 
                {
                    model.addAttribute("mensaje", "Tipo de archivo no permitido (imagen2). Use jpg/jpeg/png.");
                    model.addAttribute("cssmensaje", "alert alert-danger");
                    model.addAttribute("producto", producto);
                    return "registrarActualizarProducto";
                    
                }
                
                String nuevoNombre = UUID.randomUUID().toString() + ext;
                
                Path destino = ruta.resolve(nuevoNombre);
                
                try (InputStream is = imagen2.getInputStream()) {
                    Files.copy(is, destino, StandardCopyOption.REPLACE_EXISTING);
                }
                
                nombreImgDos = nuevoNombre;
            }
            
            
            // 5) procesar imagen3
            
            if (imagen3 != null && !imagen3.isEmpty()) {
            	
                String original = imagen3.getOriginalFilename();
                
                String ext = (original != null && original.contains(".")) ? original.substring(original.lastIndexOf(".")).toLowerCase() : "";
                
                if (!ext.equals(".png") && !ext.equals(".jpg") && !ext.equals(".jpeg")) {
                	
                    model.addAttribute("mensaje", "Tipo de archivo no permitido (imagen3). Use jpg/jpeg/png.");
                    model.addAttribute("cssmensaje", "alert alert-danger");
                    model.addAttribute("producto", producto);
                    return "registrarActualizarProducto";
                    
                }
                
                String nuevoNombre = UUID.randomUUID().toString() + ext;
                
                Path destino = ruta.resolve(nuevoNombre);
                
                try (InputStream is = imagen3.getInputStream()) {
                	
                    Files.copy(is, destino, StandardCopyOption.REPLACE_EXISTING);
                    
                }
                
                nombreImgTres = nuevoNombre;
                
            }
            
            
            // 6) asignar nombres al producto y guardar
            
            producto.setImagen_principal(nombreImgPrincipal);
            producto.setImagen_dos(nombreImgDos);
            producto.setImagen_tres(nombreImgTres);
            
            repoProd.save(producto);
	    	
	    	model.addAttribute("mensaje", esEdicion ? "Producto actualizado exitosamente." : "Producto registrado exitosamente.");
	    	model.addAttribute("cssmensaje", "alert alert-success");
	        
	    	model.addAttribute("producto", new Producto());   // limpia formulario
            
        }catch (Exception e) {
			
        	e.printStackTrace();
            model.addAttribute("mensaje", "Error al grabar producto.");
            model.addAttribute("cssmensaje", "alert alert-danger");
            model.addAttribute("producto", producto); // mantener para re-renderizar
        	
		}
		
		return "registrarActualizarProducto";
		
	}
	
	
	
	@PostMapping("/eliminar/{idproducto}")
	private String eliminarProducto(@PathVariable("idproducto") String idproducto, Model model, HttpSession session) {
		
		// Log de session id
	    System.out.println("Accediendo a /productos/eliminar - ID de sesión: " + session.getId());
	    
	    // Misma ruta que usas en grabarProducto (ajusta si es necesario)
	    
	    String rutaCarpeta = "C:" + File.separator + "ProyectoImagenesLPI" + File.separator + "imagenesProducto";
	    
	    try {
	    	
	    	// 1) obtener producto
	    	
	        Producto p = repoProd.findById(idproducto).orElse(null);
	        
	        
	        // 2) Determinar la ruta de ubicación de los archivos
	        
	        Path ruta = Paths.get(rutaCarpeta);
	        
	        
	        // 3) Eliminar los archivos físicos
	        
	        try {
				
	        	String img1 = p.getImagen_principal();
	            String img2 = p.getImagen_dos();
	            String img3 = p.getImagen_tres();
	            
	            if (img1 != null && !img1.isBlank() && !img1.equals("no-imagen.png")) {
	                Files.deleteIfExists(ruta.resolve(img1)); // eliminar file si exite en la ruta completa (más el nombre de la imagen --> ruta.resolve(img1))
	                System.out.println("[EliminarProducto] eliminado archivo: " + ruta.resolve(img1));
	            }
	            
	            if (img2 != null && !img2.isBlank() && !img2.equals("no-imagen.png")) {
	                Files.deleteIfExists(ruta.resolve(img2));
	                System.out.println("[EliminarProducto] eliminado archivo: " + ruta.resolve(img2));
	            }
	            
	            if (img3 != null && !img3.isBlank() && !img3.equals("no-imagen.png")) {
	                Files.deleteIfExists(ruta.resolve(img3));
	                System.out.println("[EliminarProducto] eliminado archivo: " + ruta.resolve(img3));
	            }
	        	
			} catch (Exception fe) {
				
				// si no se pudieron borrar los archivos, no impide borrar la BD (opcional: cambia comportamiento)
				System.out.println("[EliminarProducto] no se pudo borrar algún fichero: " + fe.getMessage());
				
			}
	        
	    	
	        // 3) eliminar registro de BD
	    	
	        repoProd.delete(p);
	        
	        // 4) preparar modelo para la vista de listado
	        model.addAttribute("mensaje", "Producto eliminado exitosamente.");
	        model.addAttribute("cssmensaje", "alert alert-success");
	        
	    } catch (Exception e) {
			
	    	e.printStackTrace();
	        model.addAttribute("mensaje", "Error al eliminar producto.");
	        model.addAttribute("cssmensaje", "alert alert-danger");
	    	
		}

	    // Recargar lista y volver a manten_producto
	    
	    model.addAttribute("lstProductos", repoProd.findAll());
	    
		return "manten_producto";
		
	}
	
	
	
}




