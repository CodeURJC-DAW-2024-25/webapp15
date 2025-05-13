module.exports = {
  content: [
    './src/**/*.html',
    './src/**/*.ts',
    './src/**/*.component.html'
  ],
  css: [
    './src/assets/css/style.css',
    './src/assets/css/admin.css', 
    './src/assets/css/vendor.css'
  ],
  defaultExtractor: content => content.match(/[\w-/:]+(?<!:)/g) || [],
  safelist: [
    /^swiper-/,
    /^fa-/,
    /^nav-/,
    /^active$/,
    /^show$/,
    /^collaps/
  ]
}