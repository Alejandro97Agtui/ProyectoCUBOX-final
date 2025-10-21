const imagen_principal = document.querySelector(".contenedorImgGrande"); 
        const miniaturas = document.querySelectorAll(".miniatura"); 


        //agregar un evento click a cada miniaura

        miniaturas.forEach(miniatura =>{
            miniatura.addEventListener("click",()=>{

                //camnbiar el src de la imagen al de la mniatura que se le dio click 
                imagen_principal.src=miniatura.src; // con esto cambiamos la iamgen que seleccionaos y nos muetra en el contenedor 

                //si existe una miniatura activa, quitar la clase 'seleted-active'

                const miniaturaActiva = document.querySelector(".selected-active"); 

                if(miniaturaActiva){
                    miniaturaActiva.classList.remove("selected-active"); 
                }


                //agregar la clase 'selected-active' a la miniatura clickeada para resaltar el borde negro
                miniatura.classList.add("selected-active"); 
        })
})