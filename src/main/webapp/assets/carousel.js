/**
 * 
 */
const imagesContainer = document.querySelector('.carousel-images');
const prevArrow = document.getElementById('prevArrow');
const nextArrow = document.getElementById('nextArrow');

let currentIndex = 0;

function updateCarousel() {
    const imageWidth = imagesContainer.children[0].offsetWidth;
    imagesContainer.style.transform = `translateX(-${currentIndex * imageWidth}px)`;
}

prevArrow.addEventListener('click', () => {
    if (currentIndex > 0) {
        currentIndex--;
        updateCarousel();
    }
});

nextArrow.addEventListener('click', () => {
    if (currentIndex < imagesContainer.children.length - 1) {
        currentIndex++;
        updateCarousel();
    }
});

window.addEventListener('resize', updateCarousel);
