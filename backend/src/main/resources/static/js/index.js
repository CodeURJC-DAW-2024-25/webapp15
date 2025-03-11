document.addEventListener('DOMContentLoaded', function () {
    const productSwipers = document.querySelectorAll('.product-store .swiper-container');

    productSwipers.forEach(function (swiperElement) {
        new Swiper(swiperElement, {
            slidesPerView: 5,
            spaceBetween: 20,
            navigation: {
                nextEl: swiperElement.parentElement.querySelector('.swiper-button-next'),
                prevEl: swiperElement.parentElement.querySelector('.swiper-button-prev')
            },
            breakpoints: {
                320: {
                    slidesPerView: 1,
                    spaceBetween: 10
                },
                576: {
                    slidesPerView: 2,
                    spaceBetween: 15
                },
                768: {
                    slidesPerView: 3,
                    spaceBetween: 15
                },
                992: {
                    slidesPerView: 4,
                    spaceBetween: 20
                },
                1200: {
                    slidesPerView: 5,
                    spaceBetween: 20
                }
            },
            watchOverflow: true,
            grabCursor: true,
            resistance: true,
            resistanceRatio: 0.85,
            speed: 400
        });
    });
});
