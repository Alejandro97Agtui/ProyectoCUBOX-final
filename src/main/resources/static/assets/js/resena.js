const resenias=document.querySelectorAll('.resenas'); 

        const prevButton = document.querySelector('#arrowLeft'); 
        const nextButton = document.querySelector('#arrowRigth'); 

        let indiceActual=0; 

        let elementosVisibles=3; 

        function actualizarElementosVisibles(){
            if(window.innerWidth<=768){
                elementosVisibles=1; 
            }else{
                elementosVisibles=3; 
            }

            mostrarResenias(); 
        }

        function mostrarResenias(){
            resenias.forEach((resenia, index)=>{
                if(index >=indiceActual && index<indiceActual + elementosVisibles){
                    resenia.style.display='block'; 
                }else{
                    resenia.style.display='none'; 

                }
            })
        }

        function siguienteResenia(){
            if(indiceActual < resenias.length - elementosVisibles){
                indiceActual++; 
                mostrarResenias(); 
            }
        }

        function reseniaAnterior(){
            if(indiceActual>0){
                indiceActual--; 
                mostrarResenias(); 
            }
        }


        nextButton.addEventListener('click',siguienteResenia); 
        prevButton.addEventListener('click',reseniaAnterior); 

        window.addEventListener('resize',actualizarElementosVisibles); 
        actualizarElementosVisibles(); 



   