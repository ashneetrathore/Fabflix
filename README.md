# :film_projector: FABFLIX

## :open_book: OVERVIEW
Date: June 2024\
Developer(s): Ashneet Rathore\
Based on assignment instructions from Prof. Chen Li

Fabflix is a full-stack movie marketplace with search, browse, and (mock) purchase functionality for 16,000+ movies. The application is implemented with a complex, layered architecture supported by persistent data storage, with scalability and security as core priorities. Users can explore the movie collection using various filters, view detailed information about movies and actors, add titles to a shopping cart, and complete a digital purchase using a credit card. The application also includes employee-access features to add new movies or actors to the database.

**Tech Stack** | Java, MySQL, JavaScript, HTML, Bootstrap, Apache Tomcat, Docker, Kubernetes, AWS EC2, Google Cloud, IntelliJ IDEA, Maven, JMeter

View more of my full-stack web apps on GitHub [here](https://github.com/stars/ashneetrathore/lists/full-stack)

## :film_strip: DEMOS
The following is a collection of demo videos showcasing Fabflix deployment and core app functionality. Each video highlights a different version of the app (with V1 representing the initial version and V4 the final version) and focuses on the new functionality added. The description under each video contains clickable timestamps with brief notes on key steps.

[Fabflix Demo V1](https://youtu.be/zLr02dkXvww?si=8RD5tr6fShmn8hCh) | Initial Deployment and Core App Functionality\
[Fabflix Demo V2](https://youtu.be/-_t8zEtrpQI?si=rnibk6zNPNhP1pkO) | XML Parsing, Security Features, & Employee-Access Features\
[Fabflix Demo V3](https://youtu.be/g4ee-v2bQro?si=T6k6s6FKSe1H6u9U) | Load Balancing Setup and Full-text and Auto-complete Search\
[Fabflix Demo V4](https://youtu.be/O8jyhIrbK7I?si=q7kRGiQ2Lg-U-vns) | Kubernetes Deployment

> [!IMPORTANT]
> While enforced HTTPS and reCAPTCHA are included in the repository source code, these features were temporarily removed when recording Fabflix Demo V4. This was necessary to perform local testing with JMeter because a) JMeter can encounter issues with SSL (SSL is used by HTTPS) and b) it simulates bot activity, making reCAPTCHA impractical during automated testing.

## :classical_building: ARCHITECTURE
### :gear: BACKEND
The backend is engineered with **Java Servlets** that use **JDBC (Java Database Connectivity)** to interact with a **MySQL** movie database, handling operations such as reading and writing data. The servlets receive a HTTP request from the frontend, execute these operations, and return a response in JSON format for rendering. Additionally, **SAX parsing** is used to efficiently process large XML datasets and integrate the extracted data into the movie database. 

### :computer_mouse: FRONTEND
The frontend is built with **JavaScript**, **HTML**, and **Bootstrap** styling, providing a dynamic and responsive interface. JavaScript manages client-side behavior and user interactions by communicating with the backend through **jQuery AJAX** calls. Responses from the backend are returned as structured JSON data, which JavaScript processes to dynamically update the HTML content.

### :rocket: DEPLOYMENT & SCALABILITY
> [!NOTE]
> Components, such as load balancing and Kubernetes, are primarily implemented through infrastructure configuration and deployment steps rather than application-level source code. To see these components in action, check out the [Demos](#film_strip-demos) section, which features videos launching EC2 instances, displaying active Kubernetes clusters on the terminal, and more.

Fabflix's deployment strategy emphasizes traffic distribution and scalability to handle high user load reliably. The application was deployed on **AWS EC2**, with each instance functioning as an independent virtual machine hosting the full stack application. **Apache Tomcat** serves as the application server, running Java servlets and handling client requests. In this layered architecture, EC2 provides the computing resources and Tomcat runs the application on top of each instance.

Initially, Fabflix was deployed on a single EC2 instance. Later, an **Apache load balancer** running on an EC2 instance was configured to distribute incoming traffic across multiple backend instances. By spreading requests across multiple servers, the load balancer prevents any single instance from becoming overloaded, improving performance and scalability. To maintain session continuity - for example, for shopping cart functionality - the load balancer uses **sticky sessions**, implemented with cookies. This approach ensures each user's requests are consistently routed to the same server to preserve session data while still supporting scalable traffic distribution. In addition to the EC2-based Apache load balancer, a **Google Cloud Platform (GCP) load balancer** was also configured as a learning exercise to explore cloud load balancing in a different environment.

Manually launching new EC2 instances and configuring the load balancer was time-consuming and prone to human error. To streamline deployment, Fabflix was later containerized using **Docker**, packaging the application with all its dependencies into a single, portable unit. These containers were then deployed on a **Kubernetes (K8s) cluster** on AWS, where each container ran inside a pod (the basic unit of deployment). Kubernetes automates scaling and management by launching and terminating pods based on demand, distributing traffic across pods with built-in load balancing, and recovering from failures without manual intervention. This system eliminates the need for manual instance management and makes deployment much more efficient and scalable.

At the database layer, Fabflix is structured to support scalability through a **primary-replica MySQL architecture**. In this setup, all write operations, such as adding movies or sales records, are directed to the primary database, while read operations, like browsing or searching, are handled by a replica database. This prevents the load from falling all onto one database. In the current implementation, both data sources in the `context.xml` point to the same database, so the scalability benefits are not yet active. However, this configuration demonstrates the system's readiness to support replication in the future. Additionally, **connection pooling** optimizes database access by reusing open connections instead of opening a new one for every request, improving efficiency and response time.

### :lock: SECURITY
Fabflix implements multiple protections to strengthen system security. **Password encryption** securely stores user credentials in the database, ensuring sensitive information is not kept in plain text. On the backend, **prepared statements** in servlets guard against SQL injection attacks. **Authentication filters** restrict access to protected pages, requiring users to log in before accessing core application functionality. A **reCAPTCHA** prevents automated abuse during login, while **enforced HTTPS** protects all client-server communication.

## :page_facing_up: PAGES AND FEATURES
User Pages\
Description of the main user-facing pages in the application:
- Login Page | Prompts users to log in
- Search Page | Supports **full-text and autocomplete searching** across multiple fields, including title, release year, director, or starring actor
- Browse Page | Supports browsing by genre or by the first character of a movie title
- Movie Results Page | Displays movies returned by search or browse actions and supporting sorting by rating or title
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

Links within pages are context-aware, meaning they navigate to relevant content - clicking a movie on a Star Info page opens the corresponding Movie Info page, clicking a genre on a Movie Info page shows search results for that genre, etc.

## :open_file_folder: PROJECT FILE STRUCTURE
```bash
Fabflix/
│── src/            
│   │── XMLParsing/       # Contains Java files with XML parsing logic
│   └── *.java            # Remaining Java files implementing servlets and other backend logic
│── scripts/              # Contains sql scripts for database setup, procedures, etc
│── xmlfiles/             # Contains XML files used by SAX Parsers
│── WebContent/
│   │── META-INF/
│   │   └── context.xml   # Configures database connections
│   │── WEB-INF/
│   │   └── web.xml       # Defines welcome page and registers database DataSources
│   │── dashboard/        # Contains .js and .html files for employee dashboard interface
│   │── *.js              # Remaining JavaScript files for frontend logic
│   └── *.html            # Remaining HTML files for frontend logic
│── pom.xml               # Defines Maven dependencies and build config
│── fabflix.yaml          # Deploys Fabflix pods in Kubernetes
│── ingress.yaml          # Routes external traffic to the Fabflix service in Kubernetes
│── fabflixtest.jmx       # Executes JMeter tests (for testing purposes only)
│── README.md             # Project documentation
└── .gitignore            # Excludes files and folders from version control
```
