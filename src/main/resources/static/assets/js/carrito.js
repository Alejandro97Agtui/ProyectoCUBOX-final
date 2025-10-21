// OBTENER ELEMENTOS
const contador = document.getElementById('contador'); 
const botonDecrementar = document.getElementById('decrementar');
const botonIncrementar = document.getElementById('incrementar');
const botonEnviar = document.getElementById('agregar-carrito');

// FUNCION PARA ACTUALIZAR EL CONTADOR 
function actualizarContador(){
    contador.value = valorContador; 
}

// VALOR INICIAL 
let valorContador = 1; 

// EVENTO PARA DECREMENTAR 
botonDecrementar.addEventListener('click',()=>{
    if(valorContador > 1){
        valorContador--; 
        actualizarContador(); 
    }
});

// EVENTO PARA INCREMENTAR 
botonIncrementar.addEventListener('click',()=>{
    valorContador++; 
    actualizarContador(); 
});

// EVENTO PARA AGREGAR AL CARRITO CON VALIDACIÓN
/*botonEnviar.addEventListener('click',()=>{
    if(valorContador > 0){
        Swal.fire({
            title: '¡Producto agregado!',
            text: `Has agregado ${valorContador} producto(s) al carrito.`,
            icon: 'success',
            confirmButtonText: 'Aceptar'
        });
    } else {
        Swal.fire({
            title: 'Cantidad inválida',
            text: 'Debes seleccionar al menos 1 producto.',
            icon: 'warning',
            confirmButtonText: 'Entendido'
        });
    }
});*/
