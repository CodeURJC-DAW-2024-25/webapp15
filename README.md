# WebApp15

# Application Name: Lumiere Royale Cinema

## Team Members:
| Name and surname    | URJC mail      | GitHub user      |
|:------------: |:------------:| :------------:|
| Gabriel Miro Granada-Lluch       | g.mirogranada.2022@alumnos.urjc.es       | Gabim23       |
| Elinee Nathalie Freites Muñoz       | en.freites.2022@alumnos.urjc.es       | ElineeF      |
| Ronald Sebastian Silvera Llimpe       | rs.silvera.2022@alumnos.urjc.es       | D4ng3r25       |
| Alexander Matias Pearson Huaycochea       | a.pearson.2022@alumnos.urjc.es       | Pearson33       |

## Class Diagram

![Proyecto_Parte1](https://github.com/user-attachments/assets/97d4b52e-843a-4d1b-bd48-d3ff3de154a0)


## Theme:
A cinema management platform where users can buy tickets, reserve seats, and review movies.

## Main Features:
- User registration and authentication.
- Viewing the film schedule.
- Ticket purchase and seat reservation.
- Management of films and theaters by the administrator.
- Uploading images for movie posters from admin account.
- Uploading profile images from registered users account.
- Sales and attendance statistics.

## Entities:
1. **User**: Information about registered users (name, email, password, avatar).
2. **Film**: Details of films (title, genre, duration, poster, synopsis).
3. **Theater**: Information about cinema rooms (room number, capacity, room type).
4. **Tickets**: Film sessions (date, time, film, room, available seats).
5. **Review**: User reviews about films (stars, comment, user).

## User Types and Permissions:
- **Anonymous User**: Can view the movies and reviews.
- **Registered User**: Can buy tickets, reserve seats, and view purchase history.
- **Administrator**: Can manage movies, rooms, sessions, and view sales statistics.

## Images:
- Users can upload profile picrures.
- films have posters uploaded to the system.

## Charts:
- Bar chart to display the most-watched films.
- Pie chart to show the sales distribution to the admin.

## Complementary Technology:
- Users receive an email with a summary of their purchase.

## Advanced Algorithm or Query:
- **Recommendation System**: Based on movies watched by the user, recommends other films of the same genre or director.


### Entities and Relationships:
- **User**:
  - Fields: name, password, email.
  - Relationships: An User only can be an Administrator or a RegisteredUser. 
- **Administrator**:
  - Fields: name, password, email.
  - Relationships:
    - An administrator can add, delete and modify multiple Films.
- **RegisteredUser**:
  - Fields: name, password, profilepicture, tickets, email.
  - Relationships:
     - A RegisteredUser can buy multiple film tickets.
     - A RegisteredUser can post multiple film reviews.
- **Film**:
  - Fields: name, category, length, director, premiere, actors, sinapsis, picture.
  - Relationships:
    - A Film can be played in multiple Theathers.
    - A Film have one Category.
    - A Film can have multiple Reviews.
- **Theater**:
  - Fields: id, capacity, film, seats.
  - Relationships:
    - A Theater can play one Film.
    - A Theater can be related to 30 Tickets or none.
- **Enumeration: Category**:
  - Relationships: A Category can be related to multiple Films.
- **Review**:
  - Fields: stars, comment, user.
  - Relations:
    - A review is posted by a RegisteredUser.
    - A review is related to one Film.
- **Ticket**:
  - Fields: date, filmname, price, seat.
  - Relationships:
    - A Tiquet is related to one Theater.
    - A Tiquet is bought by one RegisteredUser.



