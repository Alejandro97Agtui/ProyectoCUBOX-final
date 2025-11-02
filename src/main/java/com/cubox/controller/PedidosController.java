package com.cubox.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cubox.model.CabeceraPedido;
import com.cubox.model.DetallePedido;
import com.cubox.model.Usuario;
import com.cubox.repository.ICabeceraPedidoRepository;
import com.cubox.repository.IDetallePedidoRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/pedidos")

public class PedidosController {
	
	@Autowired
	private ICabeceraPedidoRepository RepoCabe;
	
	@Autowired
	private IDetallePedidoRepository RepoDeta;
	
	
	@GetMapping("/mostrar")
	private String mostrarPedidosRealizadosPorUser(Model model, HttpSession session) {
		
		Usuario usuarioSesion = (Usuario) session.getAttribute("user");
		
		//int idusuario = usuarioSesion.getIdusuario();
		
		List<CabeceraPedido> lstPedidosRealizados = RepoCabe.findAllByUsuario(usuarioSesion);
		
		model.addAttribute("lstPedidosRealizados", lstPedidosRealizados);
		
		return "transaccionesRealizadas";
		
	}
	
	
	
	@GetMapping("/consultar/{id}")
	private String consultarDetallePedido(@PathVariable("id") Long id, Model model, HttpSession session) {
		
		CabeceraPedido pedido = RepoCabe.findById(id).get();
		
		model.addAttribute("pedido", pedido);
		
		List<DetallePedido> lstDetalles = pedido.getDetalles();
		
		model.addAttribute("lstDetalles", lstDetalles);
		
		return "detalleCompra";
		
	}
	
	
	
}
