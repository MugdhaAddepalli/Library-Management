Library Management System
Overview
This project is a Java-based Library Management System developed using Swing (GUI) and JDBC (MySQL). It automates core library operations such as managing books, issuing and returning books, and tracking availability.
________________________________________
 Features
•	 Add, update, delete books
•	 Search books by ID, title, or author
•	 Issue books to users with due dates
•	 Return books with fine calculation
•	 Display book availability and issued user
•	 Real-time database integration using JDBC
________________________________________

 Programming
•	Java (Core Java + Swing)
 Database
•	SQL (DDL + DML)
 Connectivity
•	JDBC (Java Database Connectivity)
 Tools
•	Visual Studio Code
•	MySQL Workbench

________________________________________


 Concepts Used
 Object-Oriented Programming (OOP)
•	Classes & Objects
•	Encapsulation
•	Method abstraction
 Java Concepts
•	Swing (GUI development)
•	Event handling (ActionListeners)
•	Exception handling
 Database Concepts
•	SQL Joins (LEFT JOIN for issued books)
•	CRUD Operations (Create, Read, Update, Delete)
•	Primary Keys & Constraints
________________________________________
 Database Schema
 The database consists of three tables:
- Books → stores book details (id, title, author, availability)
- Transactions → tracks issued books, users, and return dates
-Users  stores users details (user ID)
Database file: [`schema.sql`](./schema.sql)
________________________________________
 Database Connection
The application connects to MySQL using JDBC.
Connection is handled in:
[`DBConnection.java`](./src/DBConnection.java)

> Note: Update your database username and password before running.
________________________________________
 Main Application Code
The main GUI and logic are implemented using Java Swing.
Main file:
[`LibraryGUI.java`](./src/LibraryGUI.java)
This file handles:
- UI creation (Swing)
- Event handling
- Database operations (CRUD, issue, return)
- Fine calculation
________________________________________
 How to Run
1.	Clone the repository
2.	Open project in VS Code
3.	Add MySQL Connector JAR in lib/
4.	Run schema.sql in MySQL Workbench
5.	Update DB credentials in DBConnection.java
6.	Run LibraryGUI.java
________________________________________
 Future Improvements
•	User login system
•	Display user names instead of IDs
•	Highlight overdue books
•	Limit number of books per user
•	Modern UI using JavaFX
________________________________________
 Conclusion
This project demonstrates integration of Java GUI + JDBC + MySQL, along with strong understanding of OOP and database operations. It simulates a real-world library system and improves efficiency over manual methods.

