"com.registration" is a registration process for web applications. Using such Spring technologies as:
Spring JPA, Spring Email, Spring Security, Spring Web, H2 Database, Thymeleaf and Spring Boot,
I created a program where a user is able to register, activate his account with provided email,
log in/log out from the system, use "Remember me" option and reset his password via email(if he forgets it).
All users are stored in the embedded database. For testing purposes, some example data was hardcoded.
Additionally, users have different authorities ('USER', 'ADMIN'...), so different context is displayed
for every authority.
Note: This project is not finished. There is almost no client-side code (CSS, JavaScript) and
exceptions handling is not provided for all cases.
Nevertheless, despite these facts, registration process is working properly.
Also, if you want to run this app, you should provide email account information for sending emails in
application.properties file.  
