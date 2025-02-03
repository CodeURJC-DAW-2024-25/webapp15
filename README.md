# WebApp15

# Application Name: ---------

## Team Members:
- Gabriel (g.mirogranada.2022@alumnos.urjc.es)


## Class Diagram

Poned aqui la foti 

## Theme:
A cinema management platform where users can buy tickets, reserve seats, and review movies.

## Main Features:
- User registration and authentication.
- Viewing the movie schedule.
- Ticket purchase and seat reservation.
- Management of movies, rooms, and sessions by the administrator.
- Uploading images for movie posters and user avatars.
- Sales and attendance statistics.

## Entities:
1. **User**: Information about registered users (name, email, password, avatar).
2. **Movie**: Details of movies (title, genre, duration, poster, synopsis).
3. **Room**: Information about cinema rooms (room number, capacity, room type).
4. **Session**: Movie sessions (date, time, movie, room, available seats).

## User Types and Permissions:
- **Anonymous User**: Can view the movies and reviews.
- **Registered User**: Can buy tickets, reserve seats, and view purchase history.
- **Administrator**: Can manage movies, rooms, sessions, and view sales statistics.

## Images:
- Users can upload profile picrures.
- Movies have posters uploaded to the system.

## Charts:
- Bar chart to display the most-watched movies.
- Pie chart to show the sales distribution to the admin.

## Complementary Technology:
- Users receive an email with a summary of their purchase.

## Advanced Algorithm or Query:
- **Recommendation System**: Based on movies watched by the user, recommends other movies of the same genre or director.


### Entities and Relationships:
- **User**:
  - Fields: id, name, email, password, avatar.
  - Relationships: A user can buy multiple tickets.
- **Movie**:
  - Fields: id, title, genre, duration, poster, synopsis.
  - Relationships: A movie can have multiple sessions.
- **Room**:
  - Fields: id, number, capacity, type (2D, 3D, VIP).
  - Relationships: A room can host multiple sessions.
- **Session**:
  - Fields: id, date, time, movie_id, room_id, available_seats.
  - Relationships: A session belongs to a movie and a room.




