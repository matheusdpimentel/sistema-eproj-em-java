# E-Proj System

Academic Java (Swing) Project

------------------------------------------------------------------------

## About the Project

E-Proj was my first complete Java application. It was developed during
the Computational Programming course with the goal of moving from theory
into building a fully functional application from start to finish.

The system simulates a simple organizational environment, allowing
management of users, teams, projects, and tasks. More than just
implementing features, the main objective was to understand how to
structure a real application: organizing code, separating
responsibilities, and integrating with a relational database.

This was the project where many programming concepts truly became
practical for me.

------------------------------------------------------------------------

## Features

-   User registration and authentication\
-   Role-based access control:
    -   **Administrator** -- full access\
    -   **Manager** -- manages teams, projects, and tasks\
    -   **User** -- restricted operational access\
-   Dynamic UI behavior based on authenticated role\
-   Team management\
-   Project management\
-   Task management\
-   MySQL persistence\
-   Secure password storage using BCrypt hashing

Implementing role-based access control was one of the most important
learning aspects of this project, as it required applying business rules
directly within the user interface.

------------------------------------------------------------------------

## Project Structure

The application is organized into layers:

-   **Model** -- Domain entities (User, Project, Team, Task)\
-   **DAO** -- Data access layer responsible for database communication\
-   **View** -- Java Swing graphical user interface\
-   **External configuration** for database connection properties

Structuring the system in layers significantly improved my understanding
of separation of concerns and application organization.

------------------------------------------------------------------------

## Demo Mode

To make the project easier to run in a portfolio environment, a demo
mode was implemented.

In demo mode, the application can be executed without configuring a
database. Database-dependent features remain disabled while the overall
interface structure is accessible.

Run the class:

DemoStart.java

------------------------------------------------------------------------

## Technologies Used

-   Java 17\
-   Java Swing\
-   MySQL\
-   JDBC\
-   BCrypt\
-   Maven

------------------------------------------------------------------------

## What I Learned

-   Building a complete structured Java application\
-   Practical integration between application and relational database\
-   Implementing secure password hashing\
-   Applying role-based access control\
-   Organizing code into layers\
-   Thinking about application structure beyond simply implementing
    features

Today, I recognize that there are areas that can be improved, but this
project represents an important foundation in my development journey.

------------------------------------------------------------------------

## Future Improvements

-   Refactoring parts of the codebase for better organization\
-   Resolving minor inconsistencies identified during development\
-   Improving user experience (UX)\
-   Enhancing the visual interface (UI)\
-   Adding automated tests\
-   Potential migration to a more modern architecture in the future
