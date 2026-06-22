# :film_projector: FABFLIX

## :open_book: OVERVIEW
Date: June 2024\
Developer(s): Ashneet Rathore

Fabflix is a full-stack movie marketplace with search, browse, and (mock) purchase functionality for 16,000+ movies. Users can explore the movie collection using various filters, view detailed information about movies and actors, add titles to a shopping cart, and complete a digital purchase using a credit card. The application also includes employee-access features to add new movies or actors to the database.

**Tech Stack** | Java, MySQL, JavaScript, HTML, Bootstrap, Apache Tomcat, Docker, Kubernetes, AWS EC2, Google Cloud, Apache Maven

## :film_strip: DEMOS
The following is a collection of demo videos showcasing Fabflix deployment and core app functionality. Each video highlights a different version of the app (with V1 representing the initial version and V4 the final version) and focuses on the new functionality added. The description under each video contains clickable timestamps with brief notes on key steps.

[Fabflix Demo V1](https://youtu.be/zLr02dkXvww?si=8RD5tr6fShmn8hCh) | Initial Deployment and Core App Functionality\
[Fabflix Demo V2](https://youtu.be/-_t8zEtrpQI?si=rnibk6zNPNhP1pkO) | XML Parsing, Security Features, & Employee-Access Features\
[Fabflix Demo V3](https://youtu.be/g4ee-v2bQro?si=T6k6s6FKSe1H6u9U) | Load Balancing Setup and Full-text and Auto-complete Search\
[Fabflix Demo V4](https://youtu.be/O8jyhIrbK7I?si=q7kRGiQ2Lg-U-vns) | Kubernetes Deployment

> [!IMPORTANT]
> While enforced HTTPS and reCAPTCHA are included in the repository source code, these features were temporarily removed when recording Fabflix Demo V4. This was necessary to perform local testing with JMeter because a) JMeter can encounter issues with SSL (SSL is used by HTTPS) and b) it simulates bot activity, making reCAPTCHA impractical during automated testing.

## :classical_building: ARCHITECTURE
### :computer: APPLICATION LAYER
The backend is engineered with **Java Servlets** that use **JDBC (Java Database Connectivity)** to interact with a **MySQL** database. **SAX parsing** is used to process large XML datasets and integrate extracted data into the database. The frontend is built with **JavaScript**, **HTML**, and **Bootstrap**, using **jQuery AJAX** calls to communicate with the backend.

Flow of a search request:
- JavaScript captures user input and sends an HTTP request via jQuery AJAX to the appropriate Java Servlet
- The servlet queries the MySQL database via JDBC and returns a JSON response
- JavaScript processes the JSON response and dynamically updates the HTML content

### :rocket: DEPLOYMENT & SCALABILITY
Fabflix application was deployed on **AWS EC2**, with **Apache Tomcat** serving as the application server, running Java servlets and handling client requests. In this layered architecture, EC2 provides the computing resources and Tomcat runs the application on top of each instance.

Initially deployed on a single EC2 instance, an **Apache load balancer** was later configured to distribute incoming traffic across multiple backend instances. By spreading requests across multiple servers, the load balancer prevents any single instance from becoming overloaded, improving performance and scalability. **Sticky sessions** via cookies maintain session continuity, which is important for use cases like shopping cart functionality. A **Google Cloud Platform (GCP) load balancer** was also configured as a learning exercise.

To streamline deployment, Fabflix was containerized using **Docker** and deployed on a **Kubernetes (K8s) cluster** on AWS. Kubernetes automates scaling by launching and terminating pods based on demand, distributing traffic across pods with built-in load balancing, and recovering from failures without manual intervention.

At the database layer, a **primary-replica MySQL architecture** directs write operations to the primary database and read operations to a replica. This prevents the load from falling all onto one database. In the current implementation, both data sources point to the same database, but this configuration demonstrates readiness to support replication. **Connection pooling** further optimizes database access by reusing open connections instead of opening a new one per request.

### :lock: SECURITY
Fabflix implements multiple protections to strengthen system security. **Password encryption** securely stores user credentials in the database. **Prepared statements** guard against SQL injection attacks. **Authentication filters** restrict access to protected pages, requiring users to log in before accessing core application functionality. **reCAPTCHA** prevents automated abuse during login, while **enforced HTTPS** protects all client-server communication.

## :page_facing_up: PAGES AND FEATURES
User Pages\
Description of the main user-facing pages in the application:
- Login Page | Prompts users to log in
- Search Page | Supports **full-text and autocomplete searching** across multiple fields, including title, release year, director, or starring actor
- Browse Page | Supports browsing by genre or by the first character of a movie title
- Movie Results Page | Displays movies returned by search or browse actions and supports sorting by rating or title
- Movie Info Page | Displays information about a movie, including title, release year, director, genres, cast, and rating
- Star Info Page | Displays information about a star, including name, birthdate, and the movies they've appeared in
- Cart Page | Shows movies added to the cart, along with total cost, including options to adjust quantities and remove items
- Payment Page | Collects payment information, including first and last name and credit card details
- Order Confirmation Page | Provides a digital receipt with order details

Employee Pages\
Description of the pages available to users with employee access:
- Employee Login Page | Prompts employees to log in
- Metadata Page | Displays schema of tables in movie database
- Add Star Page | Adds a new star to the movie database by entering star info
- Add Movie Page | Adds a new movie to the movie database by entering movie details

## :open_file_folder: PROJECT FILE STRUCTURE
```bash
fabflix/
│── src/            
│   │── XMLParsing/       # XML parsing logic
│   └── *.java            # Servlets and other backend logic
│── scripts/              # SQL scripts for database setup, procedures, etc
│── xmlfiles/             # XML files used by SAX Parsers
│── WebContent/
│   │── META-INF/
│   │   └── context.xml   # Database connection config
│   │── WEB-INF/
│   │   └── web.xml       # Web app config
│   │── dashboard/        # Employee dashboard interface
│   │── *.js              # Frontend logic
│   └── *.html            # Frontend pages
│── pom.xml               # Maven dependencies and build config
│── fabflix.yaml          # Kubernetes pod deployment config
│── ingress.yaml          # External traffic routing rules
│── fabflixtest.jmx       # JMeter tests
│── README.md             # Project documentation
└── .gitignore            # Ignored files
```
