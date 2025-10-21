document.getElementById("mobileMenuBtn").addEventListener("click", () => {
    const menu = document.getElementById("mobileMenu");
    menu.style.display = menu.style.display === "flex" ? "none" : "flex";
});

//SLAIDER DE LOS BANNER
 const slides = document.querySelectorAll('.slide');
  let index = 0;

  setInterval(() => {
    slides[index].classList.remove('active');
    index = (index + 1) % slides.length;
    slides[index].classList.add('active');
  }, 5000); // cada 5 segundos



/* */

 const contenedor = document.querySelector('.contenedor-carrusel');
  const puntos = document.querySelectorAll('.punto');
  let indexa = 0;
  const total = puntos.length;

  function cambiarSlide(i) {
    indexa = i;
    contenedor.style.transform = `translateX(-${i * 100}%)`;
    actualizarPuntos();
  }

  function actualizarPuntos() {
    puntos.forEach((p, idx) => {
      p.classList.toggle('activo', idx === indexa);
    });
  }

  puntos.forEach((punto, i) => {
    punto.addEventListener('click', () => cambiarSlide(i));
  });

  setInterval(() => {
    indexa = (indexa + 1) % total;
    cambiarSlide(indexa);
  }, 3000);
