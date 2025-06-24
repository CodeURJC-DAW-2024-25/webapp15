# WebApp15

# Phase 0
# Application Name: 👟 StepX

## 👥 Team Members:
| Name and surname    | URJC mail      | GitHub user      |
|:------------: |:------------:| :------------:|
| Gabriel Miro-Granada Lluch             | g.mirogranada.2022@alumnos.urjc.es    | Gabim23       |
| Elinee Nathalie Freites Muñoz          | en.freites.2022@alumnos.urjc.es       | ElineeF       |
| Ronald Sebastian Silvera Llimpe        | rs.silvera.2022@alumnos.urjc.es       | D4ng3r25      |
| Alexander Matias Pearson Huaycochea    | a.pearson.2022@alumnos.urjc.es        | Pearson33     |

## 🖼 Class Diagram
📌 *Visual representation of the system architecture:* 

![ER_Diagram](https://github.com/user-attachments/assets/672a6dbd-1dca-41bf-91ab-3dcb288ec145)



## 🎭 **Theme & Description** 
A shoe ecommerce website where you can buy shoes, make reviews, ckeck your purchase history and apply coupons.

## 🚀 **Main Features** 
- User registration and authentication.
- Viewing the different shoes.
- Shoe purchase and coupon apply.
- Purchase history
- shopping cart
- Management and creation of products by the administrator.
- Uploading images for shoes from admin account.
- Uploading profile images from registered users account.
- Sales and attendance statistics.

## 🏗 **Entities**:
1. **User**: Information about registered users (username, email, password, avatar).
2. **Product**: Details of the shoes (id, size, genre, category, price, reviews).
3. **Order**: Information about orders (id, date, products bought).
4. **Coupon**: Details of the coupons (code, discount).
5. **Review**: User reviews about shoes (rating, description, user).
6. **History**: Where orders of every user are stored (orders).

## 🛠 User Types and Permissions:
- **Anonymous User**: Can view the shoes.
- **Registered User**: Can buy shoes, view purchase history, modify profile, make and delete reviews.
- **Administrator**: Can create shoes, view sales statistics, modify profile and delete products and reviews.

## 🏞 Images:
- Users and administrators can upload profile pictures.
- Shoes have pictures that are uploaded by the admin.

## 📊 Charts:
- Sales statistics for admin (money)
- Number of products sold by date
- Money spent in shoes for registered users

## 💡 Complementary Technology:
- Users receive a coupon by email when they sign up and click the button to receive the discount.
- Users can download a pdf of the order. 

## ⭐ Advanced Algorithm or Query:
- **Recommendation System**: Based on past purchases, shoes are recommended to registered users
- **Best Sellers System**: Based on all purchases made by all users, shoes are recommended to registered users
  
## 🏗 **Screens**:

### **Home Page**:
The home page tab is responsible for displaying what all new users can initially see, all users can see the variety ad the info of each product but the ones that are not registered can't purchase, the unregistered or anonymous ones can only have access to login or register button on the navbar, once the user is registered or authenticated it will have access to its own profile and their own cart. If the authenticated user is an admin then in the navbar will have access to general stadistics and their own profile. The navbar is regulated and shows especific buttons or icons depending on the type of user that access.

![index](https://github.com/user-attachments/assets/a8f2f96a-16b5-445c-aaa0-67d362d7f926)


### **Indivivual Product Screen**:
Regarding the individual product screen, it displays the corresponding images of the selected product, as well as all its relevant attributes, including the button to select the shoe size and add it to the cart. At the bottom, there are comments about this product along with a more extensive description than the one above.

![single-Product](https://github.com/user-attachments/assets/47224e65-de78-4be2-a735-471971923697)


### **Shop Scren**:
The shop page is responsible for showcasing the available products, with the option for different types of customers to perform specific activities. Additionally, filters are available on the left side of the page. It has to be mentioned that is the user that acces is an admin, could delete, create, and edit each product in the shop page,but if the access is from a registered user, could only add it to cart. For each acces with or without ROLE, the quick view is gonna be available.

![shop](https://github.com/user-attachments/assets/0c847b01-0a2d-4a9d-9de1-e9cb6ad6443e)


### **Edit Product Screen**:
The edit product tab is only available to administrator-type users. On this page, there is a form filled with the product's information (name, description, images, etc.).

![editProductBig](https://github.com/user-attachments/assets/e7bda4f4-55b1-45f7-96e5-e1f4ac6010e2)


### **Create Product Screen**:
Similarly to the edit item tab, the create item tab is available only to users who are administrators. This form contains the same fields as the form on the edit item tab. Both pages are accessed by clicking the respective buttons available within the shop.

![createProductBig](https://github.com/user-attachments/assets/8afbbba9-c1f2-4dd3-b5a4-af1b0f42675e)


### **Check-Out**:
The checkout screen allows users to complete their purchase by entering their shipping information, such as country, name, email, address and telephone, in addition to applying discount coupons. It also shows an order summary with product list, image, adjustable quantity, subtotal, shipping cost and automatically calculated total. Includes a "Continue with Order" button to complete the purchase. Its design is clear and functional, facilitating data validation and order adjustment in real time.

![registeredUserCheckOut](https://github.com/user-attachments/assets/ff099493-9f00-41fe-becb-291987a1f699)


### **Cart**:
The cart screen shows a summary of the selected products, including name, image, adjustable quantity and price. It also calculates the subtotal and offers the "Checkout" option to proceed with the purchase, this makes it easier to manage the order before payment, and see what products the order contains.

![cartBig](https://github.com/user-attachments/assets/13acb209-b1bc-4be9-b3be-c40b5234d5d5)



### **User Profile**:
This is the user profile screen for StepX website. Allows the user to view and edit their personal information, including first name, last name, username, email, and a short biography. It also offers the option to change the profile photo. At the bottom, the purchase history is displayed, where the user can see details of previous orders, such as the product name, order number, date, price and delivery status. The side navigation includes access to profile information, purchase history and settings, and at the end of this page there is a graph showing the spending in the store for each month. If an admin access to its own profile, is going to see his profile picture, username and email only, the rest of attributes are reserved to users, also admins could access to shop for editing, creating or deleting and stadistics from their profile.

![registereduserProfile](https://github.com/user-attachments/assets/ecf46529-f050-4350-aaa3-b3387d567cf2)
![adminProfile](https://github.com/user-attachments/assets/2d613057-f1ab-4818-a4cc-73a0b6eb0519)


### **Admin**:
This is the StepX admin panel screen. Displays an analytical summary with key metrics such as sales, profits, visitors and orders, along with their respective variations from the last week. It also includes trend graphs, such as recent sales movement and monthly profits, allowing managers to monitor business performance. The interface is visually clear, with a design focused on quick interpretation of data.

![stadisticsForAdmin](https://github.com/user-attachments/assets/c77738f4-a3be-4723-a421-45a1c800be43)


### **Login**:
This is the login screen for the StepX website. It presents a pop-up form where users can enter their username or email and password to access their account. It validates if the data in the form are users that already been registered or not, it includes a registration button for new users in case it's the first time they access.

![LoginBig](https://github.com/user-attachments/assets/b5239e45-3e97-425a-a324-abf185a8c0e4)


### **Create Account**:
This is the create user account screen. It presents a form where users can enter their username, email, and select a password to create their account.
![register](https://github.com/user-attachments/assets/45660b81-749f-412e-9f13-bdecdfeb34d0)


## 💻 Screen Navigation Diagram:

![Diagrama](https://github.com/user-attachments/assets/b6445254-acd2-46db-a180-c4dd57f9df7a)


### 📁 DIAGRAM WITH DATABASE ENTITIES
![bbddDiagram](https://github.com/user-attachments/assets/b832332f-41a3-44d3-8b97-ac9e46afdfc9)

### 📁 CLASS AND TEMPLATES DIAGRAM

![ClassDiagram](https://github.com/user-attachments/assets/5d9561c6-43cd-4be9-99e4-6c6396bd0fab)

## PHASE 1:
### EXECUTION INSTRUCTIONS:
1. **Clone the repository**:
   - The first step is to download or clone the repository onto your machine and open it with a development         environment like Visual Studio Code. 
2. **Install Java and Mave**:
   - Preferably, install Java 21 as well as Maven on the device.
3. **Set up the Database**:
   - In our case, we are using XAMPP for the database, but you can use any SQL editor of your choice, such as       MAMP, for example.
   - If you have installed XAMPP, once the program opens, you should click on Start under SQL. Then, click on       the Shell icon.
   -  Once in the shell, enter the following command:
     ```sh
     mysql -u root
     ```
    - Finally, execute the following SQL command to create the database:
     ```sh
     CREATE DATABASE stepxDatabase;
     ```
4. **Run the proyect**
   - We recommend using **Visual Studio Code (VS Code)** as the development environment. For a better               experience and easier project execution, install the following extensions in **VS Code**:  
      - **Java Extension Pack**  
      - **Spring Boot Extension Pack**  
      - **Maven for Java**
   - With these extensions installed, you can run the program with just one click from **VS Code**. 🚀
## 🤝 PARTICIPATION

### **Ronald Sebastian Silvera llimpe**
A significant portion of the time invested in the project was dedicated to developing the dynamic loading of elements and confirmations. This includes features such as modal usage and loading additional elements when clicking a button (**AJAX**).  

Additionally, considerable effort was put into implementing the necessary validations during the user's checkout process, including recalculating prices and verifying stock availability.  

Furthermore, I contributed to the **user profile section**, handling updates to user information, profile pictures, and displaying the associated orders.  

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [load more orders in profile](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/88c199b3686378d5f55ef4442f61d2abbb132880)  | [userController](https://github.com/CodeURJC-DAW-2024-25/webapp15/blob/main/backend/src/main/java/com/stepx/stepx/controller/UserController.java)   |
|2|  [recalculate funtion, load image and orders](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/55e604ac5913a8f7e1124c364cdd91f3ea7ba59c)  |  [CheckoutController](https://github.com/CodeURJC-DAW-2024-25/webapp15/blob/main/backend/src/main/java/com/stepx/stepx/controller/CheckoutController.java)     |
|3|  [cuppon apply recalculate price](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/db43ba3d6ba41e9bb3149ef51153b67555bbb748)  |  [CheckoutController](https://github.com/CodeURJC-DAW-2024-25/webapp15/blob/main/backend/src/main/java/com/stepx/stepx/controller/CheckoutController.java)   |
|4| [update user personal information](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/b7e711bffd13aeae81806f33cf4d7878b9affae5)   | [userController](https://github.com/CodeURJC-DAW-2024-25/webapp15/blob/main/backend/src/main/java/com/stepx/stepx/controller/UserController.java)   |
|5|  [all filter of shop](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/c0d498410ab3e7af2973fef5321419dff3a2187c)  | [ShoeController](https://github.com/CodeURJC-DAW-2024-25/webapp15/blob/main/backend/src/main/java/com/stepx/stepx/controller/ShoeController.java)  |

### **Alexander Matias Pearson Huaycochea**
I have been in charge of some functionalities for the administrator such as creating products through a form, on the other hand I was in charge of developing functionalities for both registered and non-registered users, such as the best-selling products on the website, recommended products, as well as we have all the functionality of the comments of a product, such as creating, showing and deleting comments with AJAX. Likewise, I was in charge of implementing an external tool that facilitates the download of PDF as a sales ticket for the user.

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Reviews Working](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/2ed97de31d110ee97657e80da562d2461344e13c)  | Review.java ReviewRepository.java ReviewService.java |
|2| [PDF functionality](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/7b7b9d87e3b27c58934fd72474015c70bf2b1db9)  | pdfService.java CheckoutController.java checkout.html |
|3| [Recommendation algorithm](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/888f9bdb9e9195c48a15689b0e28a3f451a179f1)  | ShoeController.java GeneralController.java OrderShoe.java index.html |
|4| [Add product with size](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/f3be19cc6f319fc18bae5a0e0359099d8767f372)  | OrderItemController.java ShoeController.java Single-product.html|
|5| [Register User in Database](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/917b10e1db39af9ee0a219cc807946a854d1c67e)  | GeneralController.java register-user.html|

### **Gabriel Miró-Granada Lluch**
Has been responsible for:
- All admin functions such as creating products, editing products, and deleting products.
- The implementation of the coupon system.
- The implementation of an email system that sends the user a discount coupon.
- The admin panel with data and charts.
- User profile charts to view their spending.

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Email implementation](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/bb087411399af8999dca232f176a34c25b1fe6c1)  | [EmailService](https://github.com/CodeURJC-DAW-2024-25/webapp15/blob/main/backend/src/main/java/com/stepx/stepx/service/EmailService.java) |
|2|  [Admin Pannel Charts](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/e81744fed282c0b704215b48a2eb11f8eef2be93) |  [AdminController](https://github.com/CodeURJC-DAW-2024-25/webapp15/blob/main/backend/src/main/java/com/stepx/stepx/controller/AdminController.java) |
|3|  [Edit Product](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/c4831c1175befbdc520db862728c32015a6a99f2) | [ShoeController](https://github.com/CodeURJC-DAW-2024-25/webapp15/blob/main/backend/src/main/java/com/stepx/stepx/controller/ShoeController.java)  |
|4|  [Coupon](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/608891eb0b7712fe855ff97469557d8b174f210b) | [CheckOutController](https://github.com/CodeURJC-DAW-2024-25/webapp15/blame/main/backend/src/main/java/com/stepx/stepx/controller/CheckoutController.java) |
|5|   [Create Product](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/ade7c6384b2868b59ef31288b4dcf12cbfb95773) | [ShoeController](https://github.com/CodeURJC-DAW-2024-25/webapp15/blob/main/backend/src/main/java/com/stepx/stepx/controller/ShoeController.java)|

### **Elinee Nathalie Freites Muñoz**
She taked care of:
- All functions related to login modal, verify account, error banner over modal and log out.
- Confirmation Modal Cart for adding a product with a Default size to general cart.
- Error page with specific message of error.
- All security implementation.
- Mustache customized appareance of icons according to type of access (admin, registered user, unregistered user).
- Screen Diagram
- Class and Templates Diagram

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [First steps to security](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/8f2b8f478831e4a16f2f1f3095974ca401f5c142)  | [CustomAuthenticationHandler.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/8f2b8f478831e4a16f2f1f3095974ca401f5c142#diff-484e31e659fc3d1354b67d38cdb9801064a9956160387e4070a569c915203ed2), [RepositoryUserDetailsService.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/8f2b8f478831e4a16f2f1f3095974ca401f5c142#diff-4dd92a3c29279a93e723f8c5f65c3c51b1f484f507897231eeb88722805d7f06), [WebSecurityConfig.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/8f2b8f478831e4a16f2f1f3095974ca401f5c142#diff-afe69edd6222e7e65d2bb6b71595c8552dcfae71960189821e2c3ef03d1dcd82)  |
|2| [Erase Review Button and cart in register-user](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/ef0bd92dd1acff442228f1b49a54581397aee302)  | [register-user.html](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/ef0bd92dd1acff442228f1b49a54581397aee302#diff-b962943a2b27b11adcf93c62e1d507e6a5bd36c0a4da4781867171e2f1b48019), [style.css](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/ef0bd92dd1acff442228f1b49a54581397aee302#diff-b78be019f1dc6d57753ea900c3805b114cd53ab7c0db836cc081836df1b99b7a)  |
|3| [Basic models and methods](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/4a259f5aca21f629e328d9687e069b2630e412f9)  |  [Order.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/4a259f5aca21f629e328d9687e069b2630e412f9#diff-6a5acb2862a6376464e361451a8439d404d4e40c84cb5d891a42810a114e3e1f), [Review.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/4a259f5aca21f629e328d9687e069b2630e412f9#diff-54d0c4c5893b8fcd5bdda62be4f4579e86509ee52cf9f3389b32cc50b7b67637), [Product.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/4a259f5aca21f629e328d9687e069b2630e412f9#diff-fa9d1510459b7b6725310216de1db9c0fc9effb6d7db2b719d8a62505bc8705f) |
|4| [Product Model and Update of Dependencies on POM](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/d841688b9ecff65c02cc1ce119926e4b723a12bc)  | [pom.xml](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/d841688b9ecff65c02cc1ce119926e4b723a12bc#diff-6d4453014b5a08ac36c00c4084a5d01489c56aad5fa973f41f07155f03f55b71), [Product.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/d841688b9ecff65c02cc1ce119926e4b723a12bc#diff-fa9d1510459b7b6725310216de1db9c0fc9effb6d7db2b719d8a62505bc8705f)  |
|5| [Change of images, adding form for sign up and fixing access to form](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/bc22a09f0900906141381e6e451b12e83360ada3)   |  [index.html](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/bc22a09f0900906141381e6e451b12e83360ada3#diff-0eb547304658805aad788d320f10bf1f292797b5e6d745a3bf617584da017051), [profile.html](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/bc22a09f0900906141381e6e451b12e83360ada3#diff-f32cee85e87bd5c9da57ae49c3534d8ce4795276f4f02d27b4b3486d9ec2bcea), [admin-pannel.html](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/bc22a09f0900906141381e6e451b12e83360ada3#diff-b215324cf8b37b3fa1addbb1e3c2431969d5dee009f50272ec4419a4e49e6ddb) |




## PHASE 2:
Welcome to our presentation on the technological expansion of our shoe store! In this Phase 2, we have enhanced our platform by integrating API REST and Docker, enabling a more scalable, efficient, and modern solution.

### **API REST**
with this implementation, we have significantly improved our system’s flexibility and performance. Key benefits include:

- **Seamless Integration:** APIs allow our website, mobile apps, and third-party services to connect effortlessly.

- **Scalability:** RESTful architecture ensures that our system can handle increasing traffic smoothly.

- **Faster Response Times:** Optimized API calls improve user experience and reduce loading times.

- **Better Security:** Using authentication protocol JWT ensures secure transactions.

### **Docker** 
Revolutionizes our infrastructure by containerizing our application. The main advantages include:

- **Portability:** Our application runs consistently across different environments (development, testing, production).

- **Scalability:** Easy deployment and management of multiple instances to handle high demand.

- **Resource Efficiency:** Containers use fewer resources compared to traditional virtual machines.

- **Fast Deployment:** New updates can be rolled out quickly with minimal downtime.

- **Improved Security:** Isolated containers reduce risks and enhance system stability.

### **How Our System Works Now**

- **User Interaction:** Customers browse and purchase shoes via our website and mobile app.

- **API REST Processing:** Requests for product details, orders, and payments are processed efficiently.

- **Dockerized Deployment:** Our services run in Docker containers, ensuring smooth operation across platforms.

- **Continuous Updates:** New features and fixes are deployed seamlessly without affecting the user experience.

** Shoe Store API - Deployment Guide**

## **Docker Execution Instructions**
### **Requirements:**
- Docker must be installed on your system.
- Docker Compose must be available.

### **Steps to Run the Application:**
1. Clone the repository:
   ```sh
   git clone https://github.com/your-repo/shoe-store-api.git
   ```
2. Navigate to the directory where the `docker-compose.yml` file is located:
   ```sh
   cd shoe-store-api/backend/docker
   ```
3. Start the application using Docker Compose:
   ```sh
   docker compose up -d
   ```
4. Once the application is running, you can access it at:
   ```
   https://localhost:8443/index
   ```
5. To stop the deployment, execute the following command:
   ```sh
   docker compose down
   ```

---

## **Building the Docker Image**
### **Requirements:**
- Docker must be installed on your system.

### **Steps to Build the Docker Image:**
1. Clone the repository:
   ```sh
   git clone https://github.com/your-repo/shoe-store-api.git
   ```
2. Navigate to the directory where the `create_image.sh` file is located:
   ```sh
   cd shoe-store-api/backend/docker
   ```
3. Grant execution permissions to the script:
   ```sh
   chmod +x create_image.sh
   ```
4. Execute the script to build the Docker image:
   ```sh
   ./create_image.sh
   ```

---

## **Deployment on Virtual Machine**
### **Requirements:**
- You must be connected to the correct network or use a VPN if accessing externally.
- Ensure you have downloaded the private key for SSH access to your local system.

### **Steps to Deploy the Application:**
1. Open a terminal and connect to the virtual machine using one of the following commands:
   ```sh
   ssh -i ssh-keys/shoe-store.key vmuser@10.100.139.173
   ssh -i ssh-keys/shoe-store.key vmuser@shoestore.dawgis.etsii.urjc.es
   ```
2. Clone the repository to the virtual machine:
   ```sh
   git clone https://github.com/your-repo/shoe-store-api.git
   ```
3. Navigate to the directory where the `docker-compose.yml` file is located:
   ```sh
   cd shoe-store-api/backend/docker
   ```
4. Start the application using Docker Compose:
   ```sh
   docker compose up -d
   ```
5. You can access the deployed application at:
   ```
   https://shoestore.dawgis.etsii.urjc.es:8443
   ```
---
## **API Dcoumentation**
- To access the documentation, you must first run the application and search for the following path in your browser:
   ```
   https://localhost:8443/swagger-ui/index.html#/
   ```
- Or if you prefer to view it in JSON format, you must enter the following path in the browser:
  ```
  https://localhost:8443/v3/api-docs
  ```
---

## **Application Credentials**
| Role      | Username | Password |
|-----------|---------|---------|
| Admin     | Gaby    | pass    |
| User      | Gonzalo | pass    |

Thank you for following the deployment steps! 🚀



## Updated Class and Templates Diagram (API REST Implementation)
![DiagramPhase2](https://github.com/user-attachments/assets/342d397d-f61c-48f7-ac62-17ca1075deb1)

## TEAM PARTICIPATION

### **Elinee Nathalie Freites Muñoz**
She taked care of:
- Some basic DTOs and ReviewDTO and ShoeDTO. 
- Security Implementation.
- Part of ShoeRestController.java
- Updated Class and Templates Diagrams
- Part of CRUD of Shoe
- README structure

  
| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [BasictDTOS to avoid circular relations, two restControllers and more](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/03ccf0fa2aef706a57d0b3117c9eadb569f62b38) | [pom.xml](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/03ccf0fa2aef706a57d0b3117c9eadb569f62b38#diff-34049c3bc6deee4bbf269544338e0450399140da63fec684096d1ae0ce70b4bb), [ShoeRestController.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/03ccf0fa2aef706a57d0b3117c9eadb569f62b38#diff-96061949fad1b05ac8a41ea41e7a38062e50150c27c22b87430c2f12e345d82a), [BasicShoeDTO.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/03ccf0fa2aef706a57d0b3117c9eadb569f62b38#diff-950bbb54567a2993dd106e2c1d40098256295ac30c0f2d6c47a784afc5943a0f), [BasicShoeSizeStockDTO.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/03ccf0fa2aef706a57d0b3117c9eadb569f62b38#diff-54ccc43aead3c07d44a7afd9581d92700962b82f19db81916101f23033aad411), [ReviewDTO.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/03ccf0fa2aef706a57d0b3117c9eadb569f62b38#diff-6bbb6188f89f61a2083d74b56fe6f44d9a9ccb8f486b08c911de27899ba8c98e), [ShoeDTO.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/03ccf0fa2aef706a57d0b3117c9eadb569f62b38#diff-6ee55f6113ee094c455ccf56dcfc5220d24201cc64e56d652cea75e9616b24b8), [ShoeSizeStockDTO.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/03ccf0fa2aef706a57d0b3117c9eadb569f62b38#diff-397b40b8ba1248f07e55aab411842eca262761e8ecbc246cb9b877abbf3006e1), [OrderItemService.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/03ccf0fa2aef706a57d0b3117c9eadb569f62b38#diff-328363f59ba9892490de06e303ae8d85da37c343d00a0d036ef507458a91ce15), etc |
|2| [Security Implements](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/3577c08e8e1fba0e82c33c73e4d6e57e67e99baf) | [pom.xml](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/3577c08e8e1fba0e82c33c73e4d6e57e67e99baf#diff-34049c3bc6deee4bbf269544338e0450399140da63fec684096d1ae0ce70b4bb), [CSRFHandlerConfiguration.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/3577c08e8e1fba0e82c33c73e4d6e57e67e99baf#diff-8e023600e21c77628cdc666d3825f97295b43a7ad7a5cfe0e87e3aa6c403c5cf), [SecurityConfig.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/3577c08e8e1fba0e82c33c73e4d6e57e67e99baf#diff-f30b7042564908513f5eb2baced6d20ab2e4feeab2ad7171bf295940cadac789), [JwtRequestFilter.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/3577c08e8e1fba0e82c33c73e4d6e57e67e99baf#diff-5ec671eb5e1ddfef62e2e206ae5e0254c62a6e86a0f6aeeec300f18474c8d5fd), [LoginRequest.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/3577c08e8e1fba0e82c33c73e4d6e57e67e99baf#diff-8e5c37c50ec3c7ea3176969f77a89298f5c1130ca747b1a5d167ca94cc7c54c3), etc |
|3| [CRUD shoe and shoeController](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/984ffd887f784eea5760075aca726714c2246cfc) | [OrderItems.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/984ffd887f784eea5760075aca726714c2246cfc#diff-67f24a1303db4aac2a725cc4dd7f61443c739b88bcdd4dbdc688a46f6c67fced), [ShoeRestController.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/984ffd887f784eea5760075aca726714c2246cfc#diff-d4d88815131caf9731432f25e2d6b2b56d77e6fd6b4418519e8130c253e7d756), [ReviewService.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/984ffd887f784eea5760075aca726714c2246cfc#diff-17ad8527e1ca43c32f1a8f533ca79f04a8eb810f131036480236ab77dc58ff5c), [ShoeService.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/984ffd887f784eea5760075aca726714c2246cfc#diff-9848a8577e1e3828e66984fbbc5d69f83af5b24beef0cf3b49d3721486b04b23), |
|4| [Few changes on controllers](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/58109f9c0cc99c4135d7f01c0540c4ee0ce6716f) | [GeneralRestController.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/58109f9c0cc99c4135d7f01c0540c4ee0ce6716f#diff-2f4dc0285a2c9aafdfa69389799262bd9975c218c5bc048afbbd88b4e66bf57d), [ShoeRestController.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/58109f9c0cc99c4135d7f01c0540c4ee0ce6716f#diff-d4d88815131caf9731432f25e2d6b2b56d77e6fd6b4418519e8130c253e7d756), [ShoeController.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/58109f9c0cc99c4135d7f01c0540c4ee0ce6716f#diff-c89e201ab52fc99cbc4796098be1d7020c97a677b6106060655b0ee2a9d105f7), |
|5| [Changing type Shoe, Review to ShoeDTO and ReviewDTO](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/d339f515ab2ae22f437e49a2fa819a870673a676) | [ShoeRestController.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/d339f515ab2ae22f437e49a2fa819a870673a676#diff-d4d88815131caf9731432f25e2d6b2b56d77e6fd6b4418519e8130c253e7d756) |



### **Ronald Sebastian Silvera Llimpe**
He taked care of:
- Creation of the REST controllers for Shoe, Stock, OrderItem, and OrderShoe.
- Update of the controllers to use DTOs.
- Creation of mappers
- Update of some HTML files (checkout, shop, singleProduct, profile)
- Fixing the functionalities of the HTML files
  
| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Checkout page functioning and generates tickets, reacalculate functioning and delete order item from cart too](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/78bd458c02e9cfb415f61ebfd89cacfc173c6804) |[checkout.html](https://github.com/CodeURJC-DAW-2024-25/webapp15/blob/main/backend/src/main/resources/templates/checkout.html)  |
|2| [Many RestControllers, Shop page withModals, addtocart, singleProduct page, profile page with update information and ordershoes with load more shoes and see orderitems](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/a9069c94df162b1438bf24a5f269b9ab136bfa2a) | [shop.htm](https://github.com/CodeURJC-DAW-2024-25/webapp15/blob/main/backend/src/main/resources/templates/shop.html) |
|3| [api rest OderShoes](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/87eb6f203f7dc1f14d3b1c178c4e8be4077b2339) | [OrderShoesRestController.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/blob/main/backend/src/main/java/com/stepx/stepx/controller/rest/OrderShoesRestController.java) |
|4| [Shoe controller to DTO](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/f1cf8cbf0fd98edcfcbbd6cdd2174c2bf9e99788) | [ShoeController.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/58109f9c0cc99c4135d7f01c0540c4ee0ce6716f#diff-c89e201ab52fc99cbc4796098be1d7020c97a677b6106060655b0ee2a9d105f7) |
|5| [controllers and services adapted to dots](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/b7328af214aa3a1a90069ba7d31430fa118fb11b) | [ShoeService.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/blob/main/backend/src/main/java/com/stepx/stepx/service/ShoeService.java) |

### **Gabriel Miró-Granada Lluch**
- Coupon Rest
- Admin Charts and User charts Rest
- Dto adaptation
- Docker
- User adaptations
- Fix Mappers

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Admin Rest](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/a50f4cf07ea222fc0b7004aa189c2acee17b6c18) |[AdminRestController.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/blob/main/backend/src/main/java/com/stepx/stepx/controller/rest/AdminRestController.java)  |
|2| [Docker]((https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/a9069c94df162b1438bf24a5f269b9ab136bfa2a](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/d514d70c881d3440a47535af63ee03a8eaa0830c)) | [docker-compose.yaml]((https://github.com/CodeURJC-DAW-2024-25/webapp15/blob/main/backend/src/main/resources/templates/shop.htm](https://github.com/CodeURJC-DAW-2024-25/webapp15/blob/main/backend/docker-compose.yaml)l) |
|3| [UserController adaptation](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/87eb6f203f7dc1f14d3b1c178c4e8be4077b2339](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/23b1c07547e607839d68fd5cb6f84ea2e8615225)) | [UserController.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/blob/main/backend/src/main/java/com/stepx/stepx/controller/rest/OrderShoesRestController.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/blob/main/backend/src/main/java/com/stepx/stepx/controller/web/UserController.java)) |
|4| [Coupon Rest](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/6caf8da69039adbf067ec4d6fa3552ca26487d40) | [CouponRestController.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/58109f9c0cc99c4135d7f01c0540c4ee0ce6716f#diff-c89e201ab52fc99cbc4796098be1d7020c97a677b6106060655b0ee2a9d105f7](https://github.com/CodeURJC-DAW-2024-25/webapp15/blob/main/backend/src/main/java/com/stepx/stepx/controller/rest/CouponRestController.java)) |
|5| [UserImages](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/b7328af214aa3a1a90069ba7d31430fa118fb11b](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/e14fe0ee7e9bd3ab40900c89d495801444923051)) | [GenearlController.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/blob/main/backend/src/main/java/com/stepx/stepx/service/ShoeService.java](https://github.com/CodeURJC-DAW-2024-25/webapp15/blob/main/backend/src/main/java/com/stepx/stepx/controller/web/GeneralController.java)) |

### **Alexander Pearson Huaycochea**
He taked care of:
- Responsible for some conversions between entities and DTOs
- Responsible for implementing some mappers using the mapstruct tool
- Responsible for modifying some web controllers to use DTOs
- Responsible for implementing some REST APIs such as review and user
- Responsible for creating API documentation
- Responsible for some JWT token and security implementations
  
| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [DocumetationOpenApi]([https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/78bd458c02e9cfb415f61ebfd89cacfc173c6804](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/68a14ae183cadbf1ead0ab370b30390348ccc458)) |[pom.xml](backend/pom.xml)  [webSecurityConfig.java](backend/src/main/java/com/stepx/stepx/security/SecurityConfig.java)  
|2| [BestSelling productus and Recommended Prodcuts in Api Rest](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/9d57e498c242992e03fb54b3022aaf8f8aef813f) | [indexRestController.java](backend/src/main/java/com/stepx/stepx/controller/rest/IndexRestController.java) |
|3| [Implementation of JWT and security enhancements]([https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/87eb6f203f7dc1f14d3b1c178c4e8be4077b2339](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/d15352fd9898cbb0f659b8c0bc5f4b38729f295f)) | [webSecurityConfig.java](backend/src/main/java/com/stepx/stepx/security/SecurityConfig.java) [AuthResponse.java](backend/src/main/java/com/stepx/stepx/security/jwt/AuthResponse.java)  |
|4| [UserRestController working with images](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/59bdf629c45bc290927c57df1a06d0d4e80dc111) | [GlobalExeptionHandler.java](backend/src/main/java/com/stepx/stepx/controller/rest/GlobalExceptionHandler.java) [UserRestController.java](backend/src/main/java/com/stepx/stepx/controller/rest/UserRestController.java) |
|5| [Review Controller and Data Mapping]([https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/b7328af214aa3a1a90069ba7d31430fa118fb11b](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/7b24dec76bb5c9e75e2fcedd41f4ed46deb8f71a)) | [ReviewRestController.java](backend/src/main/java/com/stepx/stepx/controller/rest/ReviewRestController.java) [ReviewMapper.java](backend/src/main/java/com/stepx/stepx/mapper/ReviewMapper.java)|


## PHASE 3:

Diagram Components

![Angular Diagram](https://github.com/user-attachments/assets/aa66d83e-6249-43fd-8c43-6c3396c10238)

[Link Diagram](https://lucid.app/lucidspark/a5c2f863-d0f2-4f6c-8e01-a6b6af8ae8d7/edit?viewport_loc=-2178%2C1353%2C7386%2C4114%2C0_0&invitationId=inv_19321df2-5013-41ab-83f2-6bc8895a55e1)


### **Ronald Sebastian Silvera Llimpe**
He taked care of:
- The creation of everything related to the Shop page
- Implementation of the cart and its modal
- All functionalities related to the Checkout page (recalculations, item removal, validations, etc.)
- Ticket generation during the Checkout process
- Modification of the corresponding HTML files and creation of their components, services, dtos, etc.
  
| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [All functionalities of checkout page recalculations, validations, ticket generation,etc](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/418fdd3f0aea0c74d54a1a101a617eb078c6a2d7#diff-0361b2c71b36e2d5c6261d328773bfb6bad5d841254e82e179153064a1f28d89) |[checkout.component.ts](https://github.com/CodeURJC-DAW-2024-25/webapp15/blob/main/frontend/src/app/components/CheckOut/checkout.component.ts)  |
|2| [Add shoe to order by cart modal](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/6ad4b9ebccd23ce0957d4d082464d8f739b5e2e7) | [add to cart modal](https://github.com/CodeURJC-DAW-2024-25/webapp15/tree/main/frontend/src/app/components/modals/addToCartModal) |
|3| [Proces related with check of stock for checkout](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/a713f13c194d2801f2821852298bda947217898e#diff-0361b2c71b36e2d5c6261d328773bfb6bad5d841254e82e179153064a1f28d89) | [Checkout](https://github.com/CodeURJC-DAW-2024-25/webapp15/tree/main/frontend/src/app/components/CheckOut) |
|4| [Modals of cart and checkout](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/49a98c441d81e9d150072d5cb704904d2317651c) | [modals](https://github.com/CodeURJC-DAW-2024-25/webapp15/tree/main/frontend/src/app/components/modals) |
|5| [Shop component with filters](https://github.com/CodeURJC-DAW-2024-25/webapp15/commit/dc9213d5b2d82e4b0636f468e5bfd929268bef1a#diff-ecf21346bf8c9ed4864d4e1ed4ea7cbb54a927fac2d52e560c60760321df02ae) | [Shop](https://github.com/CodeURJC-DAW-2024-25/webapp15/tree/main/frontend/src/app/components/shop) |

### **Alexander Pearson Huaycochea**
He taked care of:
- Responsible for 
- Responsible for 
- Responsible for 
- Responsible for 
- Responsible for 
- Responsible for 
  
| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| []() | []() | 
|2| []() | []() |
|3| []() | []() |
|4| []() | []() |
|5| []() | []()|

### **Elinee Nathalie Freites Muñoz**
He taked care of:
- Responsible for 
- Responsible for 
- Responsible for 
- Responsible for 
- Responsible for 
- Responsible for 
  
| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| []() | []() | 
|2| []() | []() |
|3| []() | []() |
|4| []() | []() |
|5| []() | []()|

### **Gabriel Miró-Granada Lluch**
He taked care of:
- Responsible for 
- Responsible for 
- Responsible for 
- Responsible for 
- Responsible for 
- Responsible for 
  
| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| []() | []() | 
|2| []() | []() |
|3| []() | []() |
|4| []() | []() |
|5| []() | []()|
