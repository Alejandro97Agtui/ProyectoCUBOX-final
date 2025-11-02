package com.cubox.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;    // @Transactional
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cubox.model.CabeceraPedido;
import com.cubox.model.DetallePedido;
import com.cubox.model.Producto;
import com.cubox.model.Usuario;
import com.cubox.repository.ICabeceraPedidoRepository;
import com.cubox.repository.IDetallePedidoRepository;
import com.cubox.repository.IProductoRepository;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/carrito")

public class CarritoController {
	
	
	
	private final IProductoRepository repoProd;
    private final ICabeceraPedidoRepository repoCab;
    private final IDetallePedidoRepository repoDet;
    
    // Inyección por constructor (recomendado)
    @Autowired                // <- explícito para dejar claro a Spring
    public CarritoController(IProductoRepository repoProd,
                             ICabeceraPedidoRepository repoCab,
                             IDetallePedidoRepository repoDet) {
    	
    	// PRINT en el constructor (antes de cualquier otra cosa)
        System.out.println("CTOR this = " + this + " id=" + System.identityHashCode(this) + " repoProd(before assign)=" + repoProd);

    	
        this.repoProd = repoProd;
        this.repoCab = repoCab;
        this.repoDet = repoDet;
        
        // PRINT después de asignar campos finales
        System.out.println("CTOR (post-assign) this = " + this + " id=" + System.identityHashCode(this) + " repoProd(after assign)=" + this.repoProd);

    }
    
    
    @PostConstruct
    public void postConstruct() {
    	System.out.println("POSTCONSTRUCT this = " + this + " id=" + System.identityHashCode(this));
    	System.out.println("POSTCL - classloader = " + this.getClass().getClassLoader());
        System.out.println("POSTCONSTRUCT - repoProd = " + repoProd);
        System.out.println("POSTCONSTRUCT - repoCab = " + repoCab);
        System.out.println("POSTCONSTRUCT - repoDet = " + repoDet);
        System.out.println("POSTCONSTRUCT - this = " + this);
        System.out.println("POSTCONSTRUCT - repoProd = " + repoProd);
        System.out.println("POSTCL - classloader = " + this.getClass().getClassLoader());
    }
	
	
	@GetMapping("/seleccionar/{idproducto}")
	public String abrirDetalleProductoParaCompra(@PathVariable("idproducto") String idproducto, Model model, HttpSession session) {
		
		// "Log de session id" 
	    System.out.println("Accediendo a compraProducto.html para seleccionar la cantidad - ID de sesión: " + session.getId());
	    
	    //System.out.println("LLEGÓ petición /carrito/seleccionar/" + idproducto);
	    //System.out.println("repoProd en request = " + repoProd);
	    //System.out.println("HANDLER this = " + this);
	    //System.out.println("HANDLER repoProd = " + repoProd);
        System.out.println("HANDLER this = " + this + " id=" + System.identityHashCode(this));
        System.out.println("HANDLER - classloader = " + this.getClass().getClassLoader());
        System.out.println("HANDLER repoProd = " + repoProd);
        System.out.println("LLEGÓ petición /carrito/seleccionar/" + idproducto);
		
	    // usa orElse para evitar NoSuchElementException si no existe
        Producto productoSeleccionado = repoProd.findById(idproducto).orElse(null);
        if (productoSeleccionado == null) {
            System.out.println("Producto no encontrado con repoProd");
            return "redirect:/catalogo/abrir";
        }
	    
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
	
	
	
	/*
	 * @PostMapping("/agregar") private String
	 * agregarProductoSeleccionadoACanasta(@RequestParam("cantidad") int cantidad,
	 * Model model, HttpSession session) {
	 * 
	 * Producto productoSeleccionado = (Producto)
	 * session.getAttribute("productoSeleccionado");
	 * 
	 * System.out.println(
	 * "-------------------------------------------------------------------");
	 * System.out.println("Producto Seleccionado: " + productoSeleccionado);
	 * System.out.println("Cantidad del producto Seleccionado a comprar: " +
	 * cantidad);
	 * 
	 * Integer cantArt = (Integer) session.getAttribute("cantArticulos");
	 * 
	 * model.addAttribute("cantArticulos", cantArt);
	 * 
	 * return "canasta"; }
	 */
	
	
	
	@PostMapping("/agregar")
	public String agregarProductoSeleccionadoACanasta(@RequestParam("cantidad") int cantidad, Model model, HttpSession session) {
		
		Producto productoSeleccionado = (Producto) session.getAttribute("productoSeleccionado");
		
		System.out.println("-----------------------------------------------------------------------------------------------------");
		System.out.println("Producto Seleccionado: " + productoSeleccionado);
		System.out.println("Cantidad del producto Seleccionado a comprar: " + cantidad);
		System.out.println("-----------------------------------------------------------------------------------------------------");
		
		
		// "Log de session id" 
	    System.out.println("Accediendo a canasta.html para visualizar los productos agregados al carrito de compras." + session.getId());
	    
		
		// obtener carro desde sesión (o crear uno nuevo)
		
		List<DetallePedido> carro = (List<DetallePedido>) session.getAttribute("carro"); 
		
		if (carro == null) {
	        carro = new ArrayList<>();
	        session.setAttribute("carro", carro);
	    }
		
		
		//  ✅ Determinar precio vigente (considerando oferta si existe)
	    
	    BigDecimal precioUnit = productoSeleccionado.getPrecio_oferta() != null 
	            && productoSeleccionado.getPrecio_oferta().compareTo(BigDecimal.ZERO) > 0
	            ? productoSeleccionado.getPrecio_oferta()
	            : productoSeleccionado.getPrecio();
	    
	    // ver si ya existe el producto en la canasta (por idproducto) — si existe, sumar cantidad
	    
	    boolean encontrado = false;
	    
	    for (DetallePedido d : carro) {
	        if (d.getProducto() != null && productoSeleccionado.getIdproducto().equals(d.getProducto().getIdproducto())) {
	            d.setCantidad(d.getCantidad() + cantidad);
	            d.setPrecio(precioUnit); // actualizar precio por si cambió
	            encontrado = true;
	            break;
	        }
	    }
	    
	    // Si no existe, agregar nuevo producto
	    if (!encontrado) {
	    	
	        // crear nuevo DetallePedido DTO temporal (no persistido)
	    	
	        DetallePedido item = new DetallePedido();
	        item.setProducto(productoSeleccionado);
	        item.setCantidad(cantidad);
	        item.setPrecio(precioUnit);
	        carro.add(item);
	        
	    }

	    
	    // ✅ Guardar la lista actualizada "carro "en sesión
	    
	    session.setAttribute("carro", carro);
	    model.addAttribute("carro", carro);  // envia la lista de productos del carrito actualizada al canasta.html
	    System.out.println("Carrito actualizado: " + carro);
	    
	    
	    // ✅ Calcular total de artículos
	    
	    // La otra solucion:  int totalArticulos = carro.stream().mapToInt(d -> d.getCantidad()).sum();
		
	    int totalArticulos = 0; 
	    
	    for (DetallePedido d : carro) {
	    	
	    	Integer cantidadArticulos = d.getCantidad();          // si cantidad es Integer
	        if (cantidadArticulos == null) cantidadArticulos = 0;          // defensa contra null
	        totalArticulos += cantidadArticulos;
	    	
	    }
	    
	    // guardar en sesión y mostrar en el Canasta.html
	    
	    session.setAttribute("cantArticulos", totalArticulos);
	    model.addAttribute("cantArticulos", totalArticulos);
	    
		
	    // ✅ Calcular subtotal general
	    
	    BigDecimal subTotalVenta = BigDecimal.ZERO;

	    for (DetallePedido d : carro) {
	    	
	        // 1️. Defensa por si la cantidad o el precio son null
	        Integer cantitadProducto = d.getCantidad();
	        if (cantitadProducto == null) cantitadProducto = 0;

	        BigDecimal precioProducto = d.getPrecio();
	        if (precioProducto == null) precioProducto = BigDecimal.ZERO;

	        // 2️. Calcular subtotal de este producto = precio * cantidad
	        BigDecimal subtotalProducto = precioProducto.multiply(BigDecimal.valueOf(cantitadProducto));
	        
	        // 3️. Acumular en el total general
	        subTotalVenta = subTotalVenta.add(subtotalProducto);
	        
	    }

	    // 4️. Guardar en sesión y pasar al modelo para mostrarlo en la vista
	    session.setAttribute("subTotalVenta", subTotalVenta);
	    model.addAttribute("subTotalVenta", subTotalVenta);
	    
	    
	    System.out.println("----------------------------------------------------");
	    System.out.println("Producto agregado al carrito: " + productoSeleccionado.getNombre());
	    System.out.println("Cantidad total de artículos: " + totalArticulos);
	    System.out.println("Subtotal actual: S/ " + subTotalVenta);
	    System.out.println("----------------------------------------------------");
	    
		
		// redirigir a la vista de carrito para que se carguen los datos
		return "canasta";
		
	}

	
	
	@PostMapping("/eliminar")
	public String eliminarPorIdProductoPost(@RequestParam("idproducto") String idproducto, HttpSession session) {

	    @SuppressWarnings("unchecked")
	    List<DetallePedido> carro = (List<DetallePedido>) session.getAttribute("carro");
	    
	    if (carro == null) {
	    	
	        // nada que hacer
	        return "redirect:/carrito/mostrar";
	        
	    }

	    // eliminar todas las líneas con ese idproducto (normalmente una sola)
	    carro.removeIf(d -> d.getProducto() != null && idproducto.equals(d.getProducto().getIdproducto()));

	    // recalcular totales
	    
	    int totalArticulos = 0;
	    BigDecimal subTotalVenta = BigDecimal.ZERO;
	    
	    for (DetallePedido d : carro) {
	    	
	        Integer cantidad = d.getCantidad() == null ? 0 : d.getCantidad();
	        BigDecimal precio = d.getPrecio() == null ? BigDecimal.ZERO : d.getPrecio();
	        totalArticulos += cantidad;
	        subTotalVenta = subTotalVenta.add(precio.multiply(BigDecimal.valueOf(cantidad)));
	        
	    }

	    // guardar cambios en sesión
	    session.setAttribute("carro", carro);
	    session.setAttribute("cantArticulos", totalArticulos);
	    session.setAttribute("subTotalVenta", subTotalVenta);

	    return "redirect:/carrito/mostrar";
	}
	
	
	
	@PostMapping("/confirmar")
	@Transactional
	public String confirmarCompra(HttpSession session, RedirectAttributes redirectAttrs) {
		
		// "Log de session id" 
	    System.out.println("Realizando transacción desde carrito de compras - Id de sesión: " + session.getId());
		
		
		// 1) recuperar carrito y usuario desde la sesión
	    
	    @SuppressWarnings("unchecked")
	    List<DetallePedido> carro = (List<DetallePedido>) session.getAttribute("carro");
	    Usuario usuarioObtenidoDeSesion = (Usuario) session.getAttribute("user");
	    
	    // Validaciones básicas
	    
	    if (usuarioObtenidoDeSesion == null) {  // si no hay usuario en sesión redirigimos al login
	        return "redirect:/login"; 
	    }
		
		/*
		 * if (carro == null || carro.isEmpty()) { session.setAttribute("mensaje",
		 * "El carrito está vacío. Agrega productos antes de pagar.");
		 * session.setAttribute("cssmensaje", "alert alert-warning"); return
		 * "redirect:/carrito/mostrar"; }
		 */
	    
	    
	    // 2) crear cabecera de pedido
	    
	    CabeceraPedido cab = new CabeceraPedido();
	    cab.setNumPed(generarNumeroPedido());                // código legible único
	    cab.setFchPed(LocalDate.now());
	    cab.setUsuario(usuarioObtenidoDeSesion);
		
	    System.out.println("------------------------------------------------------------------------------");
	    System.out.println("Fecha Pedido: " + cab.getFchPed());
	    System.out.println("Número de Pedido: " + cab.getNumPed());
	    System.out.println("Usuario que realizará la transacción: " + cab.getUsuario());
	    System.out.println("------------------------------------------------------------------------------");
	    
	    
	    // 3) crear detalles de pedido y ajustar el stock
	    
	    List<DetallePedido> detallesParaGuardar = new ArrayList<>();
	    
	    BigDecimal totalVenta = BigDecimal.ZERO;
	    
	    for (DetallePedido d : carro) {
	    	
	    	 DetallePedido detalle = new DetallePedido();

	         // relacionar producto y cantidades/precio
	    	 
	         detalle.setProducto(d.getProducto());
	         detalle.setCantidad(d.getCantidad());
	         detalle.setPrecio(d.getPrecio());

	         // asociar la cabecera (para la relación @ManyToOne)
	         detalle.setCabecera(cab);

	         detallesParaGuardar.add(detalle);
	    	
	         // actualizar stock del producto (persistir cambios)
	         
	         Producto prod = d.getProducto();
	         Integer q = d.getCantidad() == null ? 0 : d.getCantidad();
	         BigDecimal precio = d.getPrecio() == null ? BigDecimal.ZERO : d.getPrecio();
	         
	         int nuevoStock = Math.max(0, prod.getStock() - q);
	         System.out.println("---------------------------------------------------------------------");
	         System.out.println("Producto de id : " + prod.getIdproducto());
             System.out.println("Producto anterior stock : " + prod.getStock());
             prod.setStock(nuevoStock);
             System.out.println("Producto nuevo stock : " + prod.getStock());
             System.out.println("---------------------------------------------------------------------");
             
             totalVenta = totalVenta.add(precio.multiply(BigDecimal.valueOf(q)));
             
             repoProd.save(prod); // guarda el nuevo stock
             
	    }
	    
	    cab.setTotal(totalVenta);
	    
	    // agregar detalles a la cabecera (si tu CabeceraPedido tiene setDetalles)
	    
	    cab.setDetalles(detallesParaGuardar);
	    
	    
	    // 4) persistir en DB (gracias a CascadeType.ALL en Cabecera, los detalles se guardan)
	    
	    repoCab.save(cab);
	    
	    
	    // 5) limpiar carrito en sesión y colocar mensaje de éxito
	    
	    //session.removeAttribute("carro");   // elimina completamente el atributo de la sesión — no solo lo limpia, sino que lo quita del mapa de atributos asociado al objeto HttpSession
	    session.setAttribute("carro", new ArrayList<DetallePedido>()); // ahora la vista siempre recibe una lista (vacía)
	    session.setAttribute("cantArticulos", 0);
	    session.setAttribute("subTotalVenta", BigDecimal.ZERO);

	    //session.setAttribute("mensaje", "Compra realizada con éxito. Nº pedido: " + cab.getNumPed());
	    //session.setAttribute("cssmensaje", "alert alert-success");
	    
	    redirectAttrs.addFlashAttribute("mensaje", "Compra realizada con éxito. Nº pedido: " + cab.getNumPed());
	    redirectAttrs.addFlashAttribute("cssmensaje", "alert alert-success");
	    // Usar Flash Attributes (RedirectAttributes) para el mensaje de éxito en vez de guardarlo en sesión, es más "limpio" porque el mensaje vive solo para la redirección
	    // Spring guarda temporalmente ese atributo en un “Flash Map” (no en la sesión directamente).
	    //Después del redirect:, el atributo se pasa solo a la siguiente petición HTTP y luego se elimina automáticamente.
	    //Por eso, en el método @GetMapping("/mostrar"), el mensaje ya está disponible en el Model, no necesitas obtenerlo desde HttpSession.
	    
	    
		// redirigir para mostrar la canasta (vacía) y el mensaje
		
	    return "redirect:/carrito/mostrar";
	    
	}
	
	
	
//-----------------------------------------------------------------------------------------------------------------------------------------------//	
	
	
	@GetMapping("/mostrar")
	public String mostrarCarrito(Model model, HttpSession session) {
		
		// "Log de session id" 
	    System.out.println("Accediendo a canasta.html para visualizar los productos agregados al carrito de compras." + session.getId());
		
		model.addAttribute("cantArticulos", session.getAttribute("cantArticulos"));
		model.addAttribute("subTotalVenta", session.getAttribute("subTotalVenta"));
		model.addAttribute("carro", session.getAttribute("carro"));
		
		// ⚡ No necesitas traer el mensaje desde la sesión, 
	    // porque los flash attributes se agregan automáticamente al model
	    // si vienes de un redirect.
		
		/*
		 * Object mensaje = session.getAttribute("mensaje"); Object css =
		 * session.getAttribute("cssmensaje");
		 * 
		 * if (mensaje != null) {
		 * 
		 * model.addAttribute("mensaje", mensaje); model.addAttribute("cssmensaje",
		 * css); // opcional: limpiar mensaje de sesión para que no persista
		 * session.removeAttribute("mensaje"); session.removeAttribute("cssmensaje");
		 * 
		 * }
		 */

		
		return "canasta";
		
	}
	
	
	
	public String generarNumeroPedido() {
		
	    // Ejemplo: T-20251102-161530 -> T-YYYYMMDD-HHMMSS
	    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
	    
	    // Añadir un sufijo aleatorio corto (evita colisiones con muy alta probabilidad){
	    String suffix = String.valueOf(ThreadLocalRandom.current().nextInt(1000, 9999));
	    
	    return "T-" + LocalDateTime.now().format(fmt) + "-" + suffix;   // Ej: T-20251102-161530-4721
	    
	}

	
	
	/*
	 * @GetMapping("/mostrar") public String mostrarCarrito(Model model, HttpSession
	 * session) {
	 * 
	 * // "Log de session id" System.out.
	 * println("Accediendo a canasta.html para visualizar los productos agregados al carrito de compras."
	 * + session.getId());
	 * 
	 * model.addAttribute("cantArticulos", session.getAttribute("cantArticulos"));
	 * model.addAttribute("subTotalVenta", session.getAttribute("subTotalVenta"));
	 * model.addAttribute("carro", session.getAttribute("carro"));
	 * 
	 * // traer mensaje (si existe) y añadir al modelo para que la vista lo muestre
	 * 
	 * Object mensaje = session.getAttribute("mensaje"); Object css =
	 * session.getAttribute("cssmensaje");
	 * 
	 * if (mensaje != null) {
	 * 
	 * model.addAttribute("mensaje", mensaje); model.addAttribute("cssmensaje",
	 * css); // opcional: limpiar mensaje de sesión para que no persista
	 * session.removeAttribute("mensaje"); session.removeAttribute("cssmensaje");
	 * 
	 * }
	 * 
	 * 
	 * return "canasta";
	 * 
	 * }
	 */
	
	
	
//	private void actualizarTotalesSesion(HttpSession session) {
//	    @SuppressWarnings("unchecked")
//	    List<DetallePedido> carro = (List<DetallePedido>) session.getAttribute("carro");
//	    
//	    if (carro == null || carro.isEmpty()) {
//	        session.setAttribute("cantArticulos", 0);
//	        session.setAttribute("subTotalVenta", BigDecimal.ZERO);
//	        return;
//	    }
//
//	    int totalArticulos = carro.stream().mapToInt(Det -> Det.getCantidad()).sum();
//	    BigDecimal subTotal = carro.stream()
//	            .map(it -> it.getPrecio().multiply(BigDecimal.valueOf(it.getCantidad())))
//	            .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//	    session.setAttribute("cantArticulos", totalArticulos);
//	    session.setAttribute("subTotalVenta", subTotal);
//	}

	
	
	
}





