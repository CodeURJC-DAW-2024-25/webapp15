package com.stepx.stepx.controller.web;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.stepx.stepx.dto.ReviewDTO;
import com.stepx.stepx.dto.ShoeDTO;
import com.stepx.stepx.dto.ShoeSizeStockDTO;
import com.stepx.stepx.dto.UserDTO;
import com.stepx.stepx.repository.UserRepository;
import com.stepx.stepx.service.ReviewService;
import com.stepx.stepx.service.ShoeService;
import com.stepx.stepx.service.ShoeSizeStockService;
import com.stepx.stepx.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/shop")
public class ShoeController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private ShoeSizeStockService shoeSizeStockService;

    @Autowired
    private ShoeService shoeService;

    @Autowired
    private UserRepository userRepository;

    public String getMethodName(@RequestParam String param) {
        return new String();
    }

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("showError", false);

        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("token", csrfToken.getToken());

        model.addAttribute("headerName", csrfToken.getHeaderName());

        if (isAuthenticated) {
            String username = request.getUserPrincipal().getName();
            model.addAttribute("username", username);

            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));

            UserDTO userDto = userService.findUserByUserName(username).get();

            model.addAttribute("id", userDto.id());
            model.addAttribute("email", userDto.email());
            model.addAttribute("imageBlob", userDto.imageString());
            model.addAttribute("lastName", userDto.lastName());
            model.addAttribute("firstname", userDto.firstname());
            model.addAttribute("user_id", userDto.id());

        }
    }

    @GetMapping()
    public String showShop(Model model, HttpServletRequest request) {

        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");

        model.addAttribute("token", csrfToken.getToken());
        model.addAttribute("headerName", csrfToken.getHeaderName());

        Page<ShoeDTO> shoes = shoeService.getNineShoes(0);
        boolean more = shoes.getTotalPages() > 1;
        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);

        model.addAttribute("shoes", shoes.getContent());
        model.addAttribute("hasMoreShoes", more);
        return "shop";
    }

    @GetMapping("/resetFilters")
    public String resetFilters(Model model, HttpServletRequest request) {
        Page<ShoeDTO> shoes = shoeService.getNineShoes(0);
        boolean more = 0 < shoes.getTotalPages() - 1;

        boolean isAuthenticated = request.getUserPrincipal() != null;// if its registered at least
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            String username = request.getUserPrincipal().getName();
            Optional<UserDTO> user = userService.findUserByUserName(username);
            if(!user.isPresent()){
                throw new RuntimeException("User not found");
            }
            model.addAttribute("username", user.get().username());
            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));
        }

        model.addAttribute("shoes", shoes.getContent());
        model.addAttribute("hasMoreShoes", more);
        return "partials/loadMoreShoe"; // Return only part of the products!
    }

    @PostMapping("/create")
    public String createShoe(
            @RequestParam String name,
            @RequestParam String ShortDescription,
            @RequestParam String LongDescription,
            @RequestParam BigDecimal price,
            @RequestParam(required = false) MultipartFile image1,
            @RequestParam(required = false) MultipartFile image2,
            @RequestParam(required = false) MultipartFile image3,
            @RequestParam String brand,
            @RequestParam String category,
            HttpServletRequest request, Model model) throws IOException, SQLException {

        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        String username = request.getUserPrincipal().getName();
        Optional<UserDTO> user = userService.findUserByUserName(username);
        if(!user.isPresent()){
            throw new RuntimeException("User not found");
        }

        if (isAuthenticated) {
            model.addAttribute("username", user.get().username());
            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));
        }

        shoeService.createShoeWithImagesAndDefaultStock(
            name,
            ShortDescription,
            LongDescription,
            price,
            brand,
            category,
            image1,
            image2,
            image3
        );
       
        return "redirect:/shop"; // Redirect to shop page after creation
    }

    @GetMapping("/create-product")
    public String showCreate(Model model, HttpServletRequest request) {
        boolean admin = request.isUserInRole("ROLE_ADMIN");
        if (!admin) {
            return "redirect:/errorPage?errorType=notValidPage";
        }
        return "create-product";

    }

    @PostMapping("/delete/{id}")
    public String deleteShoe(@PathVariable Long id, Model model, HttpServletRequest request) {

        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        String username = request.getUserPrincipal().getName();
        Optional<UserDTO> user = userService.findUserByUserName(username);
        if(!user.isPresent()){
            throw new RuntimeException("User not found");
        }

        if (isAuthenticated) {
            model.addAttribute("username", user.get().username());
            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));

        }

        shoeService.deleteShoe(id);

        return "redirect:/shop";
    }

    @GetMapping("/{id}/image/{imageNumber}")
    public ResponseEntity<Resource> getShoeImage(@PathVariable Long id, @PathVariable int imageNumber, Model model,
            HttpServletRequest request) {
        Optional<ShoeDTO> op = shoeService.getShoeById(id);

        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            String username = request.getUserPrincipal().getName();
            Optional<UserDTO> user = userService.findUserByUserName(username);
            if(!user.isPresent()){
                throw new RuntimeException("User not found");
            }
            model.addAttribute("username", user.get().username());
            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));
        }

        if (op.isPresent()) {
            try {
                Resource image = shoeService.getShoeImage(id, imageNumber);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                        .body(image);

            } catch (NoSuchElementException e) {
                return ResponseEntity.notFound().build();
            } catch (IOException | SQLException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/single-product/{id}")
    public String showSingleProduct(Model model, @PathVariable Long id, HttpServletRequest request) {

        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("token", csrfToken.getToken());
        model.addAttribute("headerName", csrfToken.getHeaderName());

        long maxItems = shoeService.getTotalShoes();

        if (maxItems < id) {
            return "redirect:/errorPage?errorType=greaterId";
        }

        boolean isAuthenticated = request.getUserPrincipal() != null;
        // model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            String username = request.getUserPrincipal().getName();
            Optional<UserDTO> user = userService.findUserByUserName(username);
            if(!user.isPresent()){
                throw new RuntimeException("User not found");
            }
            model.addAttribute("username", user.get().username());
            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));

        }

        Optional<ShoeDTO> op = shoeService.getShoeById(id);
        int initialReviewsCount = 2;
        //a dto
        List<ReviewDTO> reviewsDto = reviewService.getPagedReviewsByShoeId(id, 0, initialReviewsCount);

        if (op.isPresent()) {
            ShoeDTO shoe = op.get();
            boolean outOfStockS = isOutOfStock(shoe.sizeStocks(), "S");
            boolean outOfStockM = isOutOfStock(shoe.sizeStocks(), "M");
            boolean outOfStockL = isOutOfStock(shoe.sizeStocks(), "L");
            boolean outOfStockXL = isOutOfStock(shoe.sizeStocks(), "XL");
            
            model.addAttribute("stockS", outOfStockS);
            model.addAttribute("stockM", outOfStockM);
            model.addAttribute("stockL", outOfStockL);
            model.addAttribute("stockXL", outOfStockXL);

            model.addAttribute("product", shoe);
            if (reviewsDto != null) {
                model.addAttribute("review", reviewsDto);
                model.addAttribute("hasReviews", true);

            }else{
                model.addAttribute("hasReviews", false);
            }
            return "single-product";

        }
        return "shop";
    }

    private boolean isOutOfStock(List<ShoeSizeStockDTO> stocks, String size) {

        Optional<ShoeSizeStockDTO> stockForSize = stocks.stream()
            .filter(s -> s.size().equalsIgnoreCase(size))
            .findFirst();
        return stockForSize
            .map(stock -> stock.stock() == 0)
            .orElse(true);
    }

    @GetMapping("/{id}")
    public String getProductById(Model model, @PathVariable Long id, @RequestParam(required = false) String action,
            HttpServletRequest request) {

        boolean isAuthenticated = request.getUserPrincipal() != null;// false if user doesnt exist(not registered)
        model.addAttribute("isAuthenticated", isAuthenticated);// true if is registered at least
        Optional<ShoeDTO> product = shoeService.getShoeById(id);// obtain shoe

        if (isAuthenticated) {
            String username = request.getUserPrincipal().getName();
            Optional<UserDTO> user = userService.findUserByUserName(username);
            if(!user.isPresent()){
                throw new RuntimeException("User not found");
            }
            model.addAttribute("username", user.get().username());
            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));// if user is admin or not
        }

        if (product.isEmpty()) {
            model.addAttribute("error", "Product not found");
            return "partials/error-modal";
        }

        model.addAttribute("product", product.get()); // importat to do a get from the Optional that the service returns

        if ("quick".equals(action)) {

            return "partials/quick-view-modal";

        } else if ("confirmation".equals(action)) {
            // need a confirmation if the stock of the default size is 0
            Optional<Integer> stock = shoeSizeStockService.getStockByShoeAndSize(id, "M");
            if (stock.isPresent() && stock.get() == 0) {
                model.addAttribute("error", true);
            }
            return "partials/cart-confirmation-view";

        } else if ("delete".equals(action)) {
            return "partials/deleteShoeModal";
        } else {
            return "partials/error-modal";
        }
    }

    @GetMapping("/loadMoreShoes/")
    public String getMore(Model model, @RequestParam int currentPage, HttpServletRequest request) {

        Page<ShoeDTO> shoePage = shoeService.getShoesPaginated(currentPage);
        boolean more = currentPage < shoePage.getTotalPages() - 1;
        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);

        model.addAttribute("hasMoreShoes", more);
        model.addAttribute("shoes", shoePage.getContent());

        return "partials/loadMoreShoe";
    }

    @GetMapping("/{userId}/imageUser")
    public ResponseEntity<Resource> getProfileImage(@PathVariable Long userId, Model model,
            HttpServletRequest request) throws SQLException {

        UserDTO userOptional = userService.findUserById(userId);
        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);   
        if (userOptional!= null) {
            Blob image =shoeService.convertBase64ToBlob(userOptional.imageString());//dudas imagen
            if (image != null) {
                try {
                    Resource file = new InputStreamResource(image.getBinaryStream());
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_TYPE, "image/jpg")
                            .contentLength(image.length())
                            .body(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ResponseEntity.notFound().build();
    }


    @GetMapping("/{userId}/imageUserReview")
    public ResponseEntity<Resource> getProfileImageForReview(@PathVariable Long userId, Model model,
            HttpServletRequest request) throws SQLException {
        UserDTO userOptional = userService.findUserById(userId);
        if (userOptional!=null) {
            Blob image =shoeService.convertBase64ToBlob(userOptional.imageString());//dudas imagen
            if (image != null) {
                try {
                    Resource file = new InputStreamResource(image.getBinaryStream());
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_TYPE, "image/jpg")
                            .contentLength(image.length())
                            .body(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        Optional<ShoeDTO> op = shoeService.getShoeById(id);
        model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));
        if (op.isPresent()) {
            model.addAttribute("shoe", op.get());
            return "edit-product"; // Name of the edit form template
        }
        return "redirect:/shop"; // Redirect to shop page if shoe not found
    }

    @PostMapping("/edit/{id}")
    public String updateShoe(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description, // Ensure this matches the form
            @RequestParam String LongDescription,
            @RequestParam BigDecimal price,
            @RequestParam(required = false) MultipartFile image1,
            @RequestParam(required = false) MultipartFile image2,
            @RequestParam(required = false) MultipartFile image3,
            @RequestParam String brand,
            @RequestParam String category, HttpServletRequest request, Model model) throws IOException, SQLException {

        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);

        shoeService.updateShoe(
            id, name, description, LongDescription, price, image1, image2, image3, brand, category); 
            return "redirect:/shop";
    }

    @GetMapping("/getByBrand") // first 9 shoes of same brand
    public String getByBrand(@RequestParam String brand, Model model, HttpServletRequest request) {
        try {
            int currentPage = 0;
            Page<ShoeDTO> shoes = shoeService.getShoesByBrand(currentPage, brand);
            boolean more = currentPage <= shoes.getTotalPages() - 1;
            boolean isAuthenticated = request.getUserPrincipal() != null;
            model.addAttribute("isAuthenticated", isAuthenticated);

            if (isAuthenticated) {
                String username = request.getUserPrincipal().getName();
                UserDTO userDto = userService.findUserByUserName(username).orElseThrow();
                model.addAttribute("username", userDto.username());
                model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));

            }

            model.addAttribute("hasMoreShoes", more);
            model.addAttribute("shoes", shoes.getContent());
            return "partials/loadMoreShoe";
        } catch (IllegalArgumentException e) {
            return "error";
        }
    }

    @GetMapping("/getByCategory") // first 9 shoes of same category
    public String getByCategory(@RequestParam String category, Model model, HttpServletRequest request) {
        try {
            int currentPage = 0;
            Page<ShoeDTO> shoes = shoeService.getShoesByCategory(currentPage, category);
            boolean more = currentPage <= shoes.getTotalPages() - 1;

            boolean isAuthenticated = request.getUserPrincipal() != null;
            model.addAttribute("isAuthenticated", isAuthenticated);

            model.addAttribute("shoes", shoes.getContent());
            model.addAttribute("hasMoreShoes", more);
            return "partials/loadMoreShoe";

        } catch (IllegalArgumentException e) {
            System.err.println("Error: categoria no válida: " + category);
            return "error"; // if enum is not valid returns a view
        }

    }

    @GetMapping("/loadMoreShoesByBrand")
    public String getMoreByBrand(@RequestParam String brand, Model model, @RequestParam int currentPage,
            HttpServletRequest request) {
        Page<ShoeDTO> paginatedShoe = shoeService.getShoesPaginatedByBrand(currentPage, brand);
        boolean more = currentPage < paginatedShoe.getTotalPages() - 1;

        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);

        model.addAttribute("shoes", paginatedShoe.getContent());
        model.addAttribute("hasMoreShoes", more);
        return "partials/loadMoreShoe";
    }

    @GetMapping("/loadMoreShoesByCategory")
    public String loadMoreShoesByCategory(@RequestParam String category, @RequestParam int currentPage, Model model,
            HttpServletRequest request) {
        Page<ShoeDTO> paginatedShoes = shoeService.getShoesPaginatedByCategory(currentPage, category);
        boolean more = currentPage < paginatedShoes.getTotalPages() - 1;

        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            String username = request.getUserPrincipal().getName();
            UserDTO userDto = userService.findUserByUserName(username).orElseThrow();
            model.addAttribute("username", userDto.username());
            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));

        }

        model.addAttribute("shoes", paginatedShoes.getContent());
        model.addAttribute("hasMoreShoes", more);
        return "partials/loadMoreShoe";
    }

    @PostMapping("/submit/{id}")
    public String publishReview(
            @PathVariable Long id, // ID of the shoe
            @RequestParam("rating") int rating,
            @RequestParam String description, Model model, HttpServletRequest request
    ) {
        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        String username = request.getUserPrincipal().getName();
        Optional<UserDTO> user = userService.findUserByUserName(username);
        if(!user.isPresent()){
            throw new RuntimeException("User not found");
        }
        // search the shoe by id
        ShoeDTO shoe = shoeService.getShoeById(id).orElseThrow(() -> new RuntimeException("Shoe not found"));
        LocalDate date;
        date = LocalDate.now();
        // Create new review
        ReviewDTO reviewDto = new ReviewDTO(null,date,rating, description, shoe.id(), user.get().id(), user.get().username());

        // Saving the review
        reviewService.save(reviewDto);

        return "redirect:/shop/single-product/" + id + "?page=0";

    }

    @GetMapping("/{productId}/deleteReview/{id}")
    public String deleteReview(@PathVariable Long productId, @PathVariable Long id, Model model,
            HttpServletRequest request) {

        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        String username = request.getUserPrincipal().getName();
        Optional<UserDTO> user = userService.findUserByUserName(username);
        if(!user.isPresent()){
            throw new RuntimeException("User not found");
        }
        if (isAuthenticated) {
            model.addAttribute("username", user.get().username());
            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));
        }

        reviewService.deleteReview(id);

        List<ReviewDTO> reviewDto = reviewService.getReviewsByShoe(productId);//a dto
        if (reviewDto != null) {
            model.addAttribute("review", reviewDto);
            return "partials/singleProduct-reviewList";
        }
        return "error";

    }

    @PostMapping("/single-product/loadMoreReviews")
    public String loadMoreReviews(@RequestParam int page, @RequestParam Long shoeId, Model model) {
        int limit = 2;
        List<ReviewDTO> reviews = reviewService.getPagedReviewsByShoeId(shoeId, page, limit);
        model.addAttribute("review", reviews);

        return "partials/singleProduct-reviewList";
    }

}
