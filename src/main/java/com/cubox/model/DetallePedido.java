package com.cubox.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "detalle_pedido")

public class DetallePedido {
	
	@EmbeddedId   // @EmbeddedId define la clave compuesta.
    private DetallePedidoId id;
	
	@Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "precio", precision = 10, scale = 2)
    private BigDecimal precio;
    
    
    @MapsId("numPed")  // Se utiliza cuando la entidad tiene una clave compuesta (por ejemplo @EmbeddedId) y una parte de esa clave corresponde a la clave primaria de otra entidad.
    // Cuando tienes la tabla detalle_pedido con clave compuesta (num_ped, idproducto) — podría usarse @MapsId para enlazar cada parte de la clave compuesta con la entidad correspondiente (CabeceraPedido, Producto).
    // Cuando haces esto, el EmbeddedId de DetallePedido tendrá los campos numPed y idProducto. Las relaciones many‑to‑one rellenan esos campos automáticamente cuando asignas la entidad. Esto mejora la consistencia entre clave primaria y relación.
    @ManyToOne  // muchos detalles pueden tener el mismo producto
    @JoinColumn(name = "num_ped", insertable = false, updatable = false)
    private CabeceraPedido objCabeceraPedido;
    
    
    @MapsId("idproducto")
    @ManyToOne
    @JoinColumn(name="idproducto", insertable = false, updatable = false)
    private Producto objProducto;
    
//    Esto quiere decir que una cabecera de pedido puede tener muchos detalles de pedido (uno‑a‑muchos), y cada detalle de pedido pertenece a una única cabecera de pedido.
    
	
	/*
	 * @MapsId("numPed") dice: “el campo numPed del DetallePedidoId se mapea con la
	 * clave primaria de CabeceraPedido”.
	 * 
	 * @MapsId("idProducto") dice: “el campo idProducto del DetallePedidoId se mapea
	 * con la clave primaria de Producto”.
	 * 
	 * De este modo, cuando haces detalle.setCabeceraPedido(cab);
	 * detalle.setProducto(prod);, automáticamente se rellenan id.numPed =
	 * cab.getNumPed() y id.idProducto = prod.getIdproducto().
	 */

}
