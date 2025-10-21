package com.cubox.controller;

import java.io.File;  // File -->  REGISTRAR :  String rutaCarpeta = "C:" + File.separator + "ProyectoImagenesLPI" + File.separator + "imagenesMarca";
import java.io.IOException;
import java.io.InputStream; // InputStream :  REGISTRAR --> InputStream is = file.getInputStream()
import java.nio.file.Files; // Path
import java.nio.file.Path;  // Paths
import java.nio.file.Paths; // Files
import java.nio.file.StandardCopyOption;  // StandardCopyOption :  REGISTRAR --> Files.copy(is, destino, StandardCopyOption.REPLACE_EXISTING)
import java.util.UUID;  // UUID  :  REGISTRAR --> nombreArchivo = UUID.randomUUID().toString() + extension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile; // MultipartFile

import com.cubox.model.Marca;
import com.cubox.repository.IMarcaRepository;

import jakarta.servlet.ServletContext;   // ServletContext
import jakarta.servlet.http.HttpServletResponse;  // HttpServletResponse 
import jakarta.servlet.http.HttpSession; // HttpSession

@Controller
@RequestMapping("/marcas")

public class MarcaController {
	
	@Autowired
	private IMarcaRepository repoMar;
	
	@Autowired
	private ServletContext servletContext;  // Porque necesitamos averiguar el tipo MIME del archivo (ej. image/png)

	
	@GetMapping("/consultar")
	private String abrirListaMarcas(Model model, HttpSession session) {
		
		// "Log de session id" también al cargar principal...
	    System.out.println("Accediendo a Marca.html - ID de sesión: " + session.getId());
		
		model.addAttribute("lstMarcas", repoMar.findAll());
		
		return "manten_marca";
		
	}
	
	
	// Método para cargar imágenes desde la pc usando la ruta de la pc y el nombre con el que se guardó en la bd
	@GetMapping("/ImagenMarca")
	private void mostrarImagenMarca(@RequestParam("idmarca") Integer idmarca, HttpServletResponse response) throws IOException {
		
		// 1) obtener el registro de la marca desde la BD
		Marca m = repoMar.findById(idmarca).orElse(null);
		
		// 2) decidir nombre de archivo (valor por defecto si no existe)
		String nombreArchivo = "defaultMarca.png";
		if(m != null && m.getImagen() != null && !m.getImagen().isEmpty()) {
			nombreArchivo = m.getImagen();
		}
		
		// 3) construir ruta de carpeta
		//Construye la ruta física en disco ( C:\ProyectoImagenesLPI\imagenesMarca\...).
		Path rutaCarpeta = Paths.get("C:", "ProyectoImagenesLPI","imagenesMarca");
		Path archivo = rutaCarpeta.resolve(nombreArchivo); // resolve (String)
		
		// si no existe el archivo elegido, usar el default...
		//Si no existe el archivo solicitado intenta defaultMarca.png. Si tampoco existe el default, responde 404.
		Path archivoDefault = rutaCarpeta.resolve("defaultMarca.png");
		
		if (!Files.exists(archivo)) {
	        if (Files.exists(archivoDefault)) {
	            archivo = archivoDefault;
	        } else {
	            // Si ni siquiera existe el default, retornamos 404
	            response.sendError(HttpServletResponse.SC_NOT_FOUND);
	            return;
	        }
	    }
		
		// 4) obtener mime type y escribir bytes en la respuesta
		
		//Establece Content-Type y Content-Length en la respuesta y copia el archivo con Files.copy(...) al OutputStream.
		//a. MIME es una etiqueta que dice «qué tipo de archivo» se está enviando por HTTP. Le indica al navegador cómo 
		//interpretar los bytes que recibe.
		//b. EJMS: image/png → imagen en formato PNG ; image/jpeg → imagen JPEG ; text/html → documento HTML
		// application/pdf → archivo PDF ; application/octet-stream → genérico (bytes sin tipo conocido; normalmente fuerza descarga)
		
		String mime = servletContext.getMimeType(archivo.toString());
		// El servletContext mira la extensión del archivo (por ejemplo nike.png) y devuelve un tipo MIME probable (e.g. image/png).
		
	    if (mime == null) {
	        mime = "application/octet-stream";
	    }
	    
	    // Si el servidor no sabe qué tipo corresponde a esa extensión (por ejemplo una extensión poco común), getMimeType 
	    //devuelve null.
	    // Se usa application/octet-stream como reserva: significa “datos binarios genéricos”. Los navegadores normalmente 
	    //lo tratan como descarga o lo manejan de forma segura porque no saben qué es exactamente.
	    
	    response.setContentType(mime);
	    
	    // Esto añade la cabecera HTTP Content-Type: <mime> a la respuesta.
	    // Es fundamental: al recibir esa cabecera el navegador decide si mostrar, renderizar o descargar el recurso.
	    
	    response.setContentLengthLong(Files.size(archivo));
	    
	    // Calcula el tamaño en bytes del archivo (p. ej. 34567) y coloca la cabecera Content-Length: 34567.
	    // Esa cabecera permite al navegador saber cuánto durará la descarga, mostrar barra de progreso y saber cuándo 
	    //terminó de recibir todo.
	    
	    // Si el servidor envía Content-Type: image/png, el navegador sabe “esto es una imagen PNG” y la muestra; si envía 
	    //Content-Type: application/pdf, el navegador intentará abrirlo con un visor de PDFs o descargarlos según 
	    //la configuración del cliente.

	    
	    // 5) stream de archivo
	    
	    Files.copy(archivo, response.getOutputStream());
	    
	    // Esta línea lee los bytes del archivo en disco y los escribe directamente al OutputStream de la respuesta HTTP.
	    // Es la operación que realmente envía el contenido de la imagen al cliente (navegador).
	    
	    response.getOutputStream().flush();
	    
	    // "flush()" fuerza a que cualquier dato que todavía esté en buffers intermedios se envíe inmediatamente por la red.
	    // Asegura que no quede “data” pendiente en buffers del servidor antes de completar el método.
	    
	}
// --------------------------------------------------------------------------------------------------------------------------
// --------------------------------------------------------------------------------------------------------------------------    
	    
	    
    @GetMapping("/registrar")
    private String abriRegistrarMarca(Model model, HttpSession session) {
    	
    	// "Log de session id" también al cargar principal...
	    System.out.println("Accediendo a registrarMarca.html - ID de sesión: " + session.getId());
	    
    	model.addAttribute("marca", new Marca());
    	
    	return "registrarMarca";
    	
    }
	
    
    @PostMapping("/grabar")
    private String registrarNuevaMarca(@ModelAttribute Marca marca, @RequestParam(value = "txtImagen", required = false) MultipartFile file, Model model, HttpSession session) {
    	
    	// @RequestParam(value = "txtImagen", required = false) MultipartFile file  --> “Toma el archivo que viene del campo txtImagen del formulario HTML”.
    	// required = false --> “No pasa nada si este parámetro no viene; es opcional.”
    	// "MultipartFile" es una clase especial de Spring para manejar archivos subidos desde formularios (type="file").
    	// "MultipartFile file" contiene la imagen que el usuario subió. Si no subió nada (y required=false), file será null o estará vacío.
    	
    	System.out.println("Marca obtenida del form: " + marca);
    	
    	// "Log de session id" también al grabar una nueva marca...
	    System.out.println("Accediendo a registrarMarca para guardar/actualizar - ID de sesión: " + session.getId());
    	
    	// Ruta fija 
    	String rutaCarpeta = "C:" + File.separator + "ProyectoImagenesLPI" + File.separator + "imagenesMarca";
    	
    	// Nombre por defecto si no se sube imagen
        String nombreArchivo = "defaultMarca.png";
    	
    	try {
    		
    		// 1) Crear la carpeta si no existe ---
    		//Resultado: garantizas que la carpeta destino exista antes de intentar escribir archivos allí.
            Path ruta = Paths.get(rutaCarpeta);
            if (!Files.exists(ruta)) {
                Files.createDirectories(ruta);
            }
            
            // Paths.get(rutaCarpeta) : Convierte la cadena rutaCarpeta en un objeto Path.
            // "Path" es la representación moderna de una ruta en disco. No toca el sistema de archivos aún, solo crea el objeto que lo describe.
            // Files.exists(uploadsDir) : Comprueba si esa ruta ya existe en el sistema de archivos (si la carpeta ya está creada).
            // "Files" es una utilidad para operaciones con archivos (leer, escribir, comprobar existencia, etc.).
            // Files.createDirectories(ruta): Si la carpeta (o alguna carpeta padre) no existe, la crea.
            // "createDirectories" crea toda la jerarquía necesaria (padres incluidas) y no falla si ya existe.
    		
            
            
    		// 2) Procesar archivo de imagen si se subió uno : ¿Se subió un archivo?
            
            // "file != null": Verifica que el parámetro "MultipartFile file" fue pasado al método (es decir, el formulario tenía un campo name="txtImagen").
            //Si el campo no vino en la petición, file podría ser null.
            // "!file.isEmpty()": MultipartFile.isEmpty() devuelve true si no hay contenido (usuario no seleccionó archivo).
            // Con ambas condiciones te aseguras de sólo procesar cuando hay un archivo real. Resultado: se entra al bloque sólo si efectivamente hay algo para guardar.
            if (file != null && !file.isEmpty()) {

                // A. Obtener nombre original y extensión
                String original = file.getOriginalFilename(); // equivalente a filePart.getSubmittedFileName()
                String extension = "";
                int idx = (original != null) ? original.lastIndexOf('.') : -1;
                if (idx >= 0) {
                    extension = original.substring(idx).toLowerCase(); // .png, .jpg, .jpeg
                }
                
                // "file.getOriginalFilename()" Devuelve el nombre del archivo tal como vino del cliente (por ejemplo foto.png).
                // "int idx = (original != null) ? original.lastIndexOf('.') : -1;"
                //--> Busca la posición del último punto en el nombre (se usa para aislar la extensión .png, .jpg, etc.).
                //--> Si "original" es null, evita NullPointerException y usa -1 como indicador de “no hay punto”.
                // "if (idx >= 0) { extension = original.substring(idx).toLowerCase(); }"
                //--> Si hay un punto, toma todo desde el punto hasta el final — eso devuelve la extensión incluido el "." (ejm: .png).
                //--> toLowerCase() normaliza (.PNG → .png) para poder comparar fácilmente.
                
                
                // B. Validación básica de extensión (mejor validar también MIME / magic bytes si lo necesitas)
                if (!extension.equals(".png") && !extension.equals(".jpg") && !extension.equals(".jpeg")) {
                    model.addAttribute("mensaje", "Tipo de archivo no permitido. Use jpg/jpeg/png.");
                    model.addAttribute("cssmensaje", "alert alert-danger");
                    return "registrarMarca";
                }
                
                // Comparas la extensión con las permitidas ".png, .jpg, .jpeg". Si no coincide, devuelves la vista con 
                //un mensaje de error y no guardas el archivo. Resultado: bloqueo de tipos de archivo no deseados por extensión.
                

                // C. Generar nombre único para evitar colisiones (UUID + extensión)
                nombreArchivo = UUID.randomUUID().toString() + extension;
                
                // "UUID.randomUUID().toString()"
                //--> Genera un identificador único (por ejemplo 3f7c9a2f-...) para evitar que dos subidas con el mismo nombre original sobrescriban archivos.
                //--> Concatenas la extensión para mantener el tipo de archivo visible (3f7c9a2f-....png).
                //--> Resultado: cada archivo en disco tiene un nombre único y predecible solo para el servidor.
                
                
                // D. Copiar el contenido del MultipartFile a la ruta destino -> Construir ruta destino y copiar bytes
                Path destino = ruta.resolve(nombreArchivo);
                
                // "resolve"
                //--> Combina la carpeta/Path (ruta) con el nombre del archivo para obtener la ruta completa del archivo donde se guardará.

                try (InputStream is = file.getInputStream()) {
                    Files.copy(is, destino, StandardCopyOption.REPLACE_EXISTING);
                }
                
                // "try (InputStream is = file.getInputStream()) { ... }"
                //--> Try-with-resources: abre un InputStream sobre el archivo subido y garantiza que se cierre automáticamente al terminar 
                //   (incluso si ocurre una excepción).
                //--> "file.getInputStream()" te da acceso al contenido binario del archivo que envió el cliente.
                // "Files.copy(is, destino, StandardCopyOption.REPLACE_EXISTING);"
                //--> Copia los bytes leídos del InputStream hacia la ruta destino.
                //--> "StandardCopyOption.REPLACE_EXISTING" indica que si ya existe un archivo con ese nombre, lo sobrescriba. 
                //     En tu caso, como el nombre es único por UUID, normalmente no se sobrescribirá; la opción es una seguridad adicional.
                //--> Resultado: el archivo subido se guarda físicamente en la carpeta de destino con el nombre único generado.
                
            } else { // No hay archivo subido en el form
            	
            	// Si es edición (id presente), conservar la imagen existente en BD
            	
            	if (marca.getImagen() != null) {
            		
            		Marca existente = repoMar.findById(marca.getIdmarca()).orElse(null);
            		
            		if (existente != null && existente.getImagen() != null && !existente.getImagen().isBlank()) {
                        marca.setImagen(existente.getImagen());
            		} else {
            			// Si por alguna razón la marca existente no tiene imagen, usar default
            			marca.setImagen("defaultMarca.png");
            		}
            		
            	} else {
            		
            		// Nuevo registro y no se subió imagen en el form -> usar imagen por defecto
                    marca.setImagen("defaultMarca.png");
            		
            	}
            		
            	
            }
            
    		
			
    		// 3) Guardar nombre de imagen en el objeto Marca y persistir ---
            //Guardar (insertará si id null, actualizará si id tiene valor)
            marca.setImagen(nombreArchivo);
            
            // ¿Es edición o creación?
            boolean esEdicion = (marca.getIdmarca() > 0); // calcular ANTES de save()
            
    		repoMar.save(marca);
    		
    		// 4) Preparar mensaje de éxito para la vista ---

    		if(esEdicion) {
    			
    			model.addAttribute("mensaje", "Marca actualizada exitosamente.");
    			
    		} else {
    			
    			model.addAttribute("mensaje", "Marca registrada exitosamente.");
    			
    		}
    		
			model.addAttribute("cssmensaje", "alert alert-success");
			model.addAttribute("marca", new Marca());
    		
		} catch (Exception e) {
			
			e.printStackTrace();
			model.addAttribute("mensaje", "Error al realizar mantenimiento de marca.");
			model.addAttribute("cssmensaje", "alert alert-danger");
			
		}
    	
    	// Volver a la misma página del formulario
    	return "registrarMarca";
    }
    
    
    @GetMapping("/editar/{idmarca}")
    private String editarMarca(@PathVariable("idmarca") int idmarca, Model model, HttpSession session) {
    	
    	// "Log de session id" también al cargar principal...
	    System.out.println("Accediendo a registrarMarca.html para editar - ID de sesión: " + session.getId());
	    
	    Marca marcaObtenida = repoMar.findById(idmarca).get();
	    
	    model.addAttribute("marca", marcaObtenida);
    	
    	return "registrarMarca";
    }
    
    
    @PostMapping("/eliminar/{idmarca}")
    private String eliminarMarca(@PathVariable("idmarca") int idmarca, Model model, HttpSession session) {
    	
    	// "Log de session id" también al eliminar una nueva marca...
	    System.out.println("Accediendo a /marcas/eliminar - ID de sesión: " + session.getId());
    	
	    // Ruta fija (misma que usas en registrar)
	    String rutaCarpeta = "C:" + File.separator + "ProyectoImagenesLPI" + File.separator + "imagenesMarca";
	    
    	try {
			
    		// 1) Obtener marca por el ID recibido desde el form
        	System.out.println("idmarca: " + idmarca);
        	Marca m = repoMar.findById(idmarca).orElse(null);
        	
        	if (m == null) {
                model.addAttribute("mensaje", "Marca no encontrada.");
                model.addAttribute("cssmensaje", "alert alert-warning");
                model.addAttribute("lstMarcas", repoMar.findAll());
                return "manten_marca";
            }
        	
        	
        	
        	// 2) Intentar eliminar el archivo asociado 
            String nombreArchivo = m.getImagen();
            
            if (nombreArchivo != null && !nombreArchivo.isBlank() && !nombreArchivo.equalsIgnoreCase("defaultMarca.png")) {
            	
                Path ruta = Paths.get(rutaCarpeta);
                
                // Si la carpeta no existe, no hay nada que borrar; pero la creas solo si la necesitas para consistencia
                if (Files.exists(ruta)) {
                	
                    Path archivo = ruta.resolve(nombreArchivo); // Path: objeto usado para localizar archivo en el sistema.
                    
                    try {
                    	
                    	if (Files.exists(archivo)) {
                            boolean borrado = Files.deleteIfExists(archivo); // true si se eliminó
                            // Files.deleteIfExists(archivo) --> Elimina ese "archivo" si existe en el PATH especificado llamado "archivo"
                            if (borrado) {
                                System.out.println("Archivo eliminado: " + archivo.toString());
                            } else {
                                System.err.println("No se pudo eliminar (deleteIfExists devolvió false): " + archivo.toString());
                            }
                        } else {
                            System.out.println("Archivo no existe en disco: " + archivo.toString());
                        }
    					
    				} catch (Exception ioe) {
    					
    					 // Si ocurre error al borrar el archivo, lo registramos y seguimos con la eliminación en BD
                        ioe.printStackTrace();
                        model.addAttribute("mensaje2", "Error al eliminar archivo en disco (se intentó continuar con la BD).");
                        model.addAttribute("cssmensaje2", "alert alert-warning");
                        // No hacemos return; permitimos continuar para borrar el registro en BD y mantener consistencia con la UI.
    					
    				}
                    
                } else {
                	
                    System.out.println("Carpeta de imágenes no existe: " + ruta.toString());
                    
                }
                
            } else {
            	
            	System.out.println("No hay imagen a eliminar o es imagen por defecto.");
            	
            }
        	
        	try {
        		// 3) Eliminar marca en BD
        		repoMar.delete(m);
        		
        		model.addAttribute("mensaje", "Marca eliminada exitosamente.");
        		model.addAttribute("cssmensaje", "alert alert-success");
        		
    		} catch (Exception e) {
    			
    			model.addAttribute("mensaje", "Error al eliminar marca.");
    			model.addAttribute("cssmensaje", "alert alert-danger");
    			
    		}
    		
		} catch (Exception ex) {
			
			ex.printStackTrace();
	        model.addAttribute("mensaje", "Error inesperado al eliminar la marca: " + ex.getMessage());
	        model.addAttribute("cssmensaje", "alert alert-danger");
			
		}
    	
    	// 4) Devolver la vista con la lista actualizada
    	model.addAttribute("lstMarcas", repoMar.findAll());
    	
    	return "manten_marca";
    }
	
    
//    @PostMapping("/eliminar/{idmarca}")
//    private String eliminarMarca(@PathVariable("idmarca") int idmarca, Model model, HttpSession session) {
//    	
//    	// "Log de session id" también al eliminar una nueva marca...
//	    System.out.println("Accediendo a /marcas/eliminar - ID de sesión: " + session.getId());
//    	
//    	// 1) Obtener marca por el ID recibido desde el form
//    	System.out.println("idmarca: " + idmarca);
//    	Marca m = repoMar.findById(idmarca).get();
//    	
//    	// 2) Eliminar marca
//    	
//    	try {
//			
//    		repoMar.delete(m);
//    		
//    		model.addAttribute("mensaje", "Marca eliminada exitosamente.");
//    		model.addAttribute("cssmensaje", "alert alert-success");
//    		
//		} catch (Exception e) {
//			
//			model.addAttribute("mensaje", "Error al eliminar marca.");
//			model.addAttribute("cssmensaje", "alert alert-danger");
//			
//		}
//    	
//    	model.addAttribute("lstMarcas", repoMar.findAll());
//    	
//    	return "manten_marca";
//    }
	
    
}





