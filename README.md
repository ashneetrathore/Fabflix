# :film_projector: FABFLIX

## :open_book: OVERVIEW
Date: June 2024\
Developer(s): Ashneet Rathore\
Based on assignment instructions from Prof. Chen Li


**Tech Stack** | Java, MySQL, JavaScript, HTML, Bootstrap, Apache Tomcat, Docker, Kubernetes, AWS EC2, Google Cloud, IntelliJ IDEA, Maven, JMeter

## :film_strip: DEMOS

## :classical_building: ARCHITECTURE
### :gear: BACKEND
The backend is engineered with **Java Servlets** that use **JDBC (Java Database Connectivity)** to interact with a **MySQL** movie database, performing queries and inserting data. The servlets receive a HTTP request from the frontend, execute database operations, and return a response in JSON format for rendering. Additionally, [**SAX parsing**](https://github.com/ashneetrathore/Fabflix/tree/main/src/XMLParsing) was used to efficiently process large XML datasets and integrate the extracted data into the movie database.

### :computer_mouse: FRONTEND
The frontend is built with **JavaScript**, **HTML**, and **Bootstrap** styling, providing a dynamic and responsive interface. JavaScript manages client-side behavior and user interactions by communicating with the backend through **jQuery AJAX** calls. Responses from the backend are returned as structured JSON data, which JavaScript processes to dynamically update the HTML content.

### :rocket: DEPLOYMENT & SCALABILITY
Fabflix's deployment strategy emphasized traffic distribution and scalability, ensuring the application could handle high user load reliably. The application was deployed on **AWS EC2**, where each instance acts as a separate virtual server hosting the full stack application. On each instance, **Apache Tomcat** serves as the application server, running Java servlets and serving client requests. In this layered architecture, EC2 provides the computing resources and Tomcat runs the application on top of each instance.

Initially, Fabflix was deployed on a single EC2 instance. Later, a **load balancer** was introduced to distribute incoming traffic across multiple EC2 instances. By spreading requests across multiple servers, the load balancer helped prevent any single instance from becoming overloaded, improving performance and scalability. To maintain session continuity - for example, for shopping cart functionality - the load balancer used **sticky sessions**, implemented with cookies. This ensured a user's requests were consistently routed to the same server to preserve session data while still supporting scalable traffic distribution.

Manually launching new EC2 instances and configuring the load balancer was time-consuming and prone to human error. To streamline deployment, Fabflix was later containerized using Docker, packaging the application with all its dependencies into a single, portable unit. These containers were then deployed on a Kubernetes (K8s) cluster on AWS, where each container ran inside a pod (the basic unit of deployment). Kubernetes automated scaling and management by launching and terminating pods based on demand, distributed traffic across pods with built-in load balancing, and recovered from failures without manual intervention. This approach eliminated the need for manual instance management and made the deployment process much more efficient and scalable.

At the database layer, Fabflix is structured to support scalability through a **primary-replica MySQL architecture**. In this setup, all write operations, such as adding movies or sales records, are directed to the primary database, while read operations, like browsing or searching, are handled by a replica database. This prevents the load from falling all onto one database. In the current implementation, both data sources in the `context.xml` point to the same database, so the scalability benefits are not yet active. However, this configuration demonstrates the system's readiness to support replication in the future. Additionally, **connection pooling** is implemented to optimize database access by reusing open connections instead of opening a new one for every request, improving efficiency and response time.

### :lock: SECURITY
Fabflix implements multiple protections to strengthen system security. **Password encryption** is used to securely store user credentials in the database, ensuring sensitive information is not kept in plain text. On the backend, **prepared statements** in servlets guard against SQL injection attacks. **Authentication filters** restrict access to protected pages, requiring users to log in before accessing core application functionality. A **reCAPTCHA** is used to prevent automated abuse during login, while **enforced HTTPS** (in deployed version) protects all client-server communication.

PAGES & FEATURES


## :open_file_folder: PROJECT FILE STRUCTURE
```bash
Fabflix/
│── scripts/              # Contains sql scripts for database setup, procedures, etc
│── xmlfiles/             # Contains XML files used by SAX Parsers
│── src/            
│   │── XMLParsing/       # Contains Java files with XML parsing logic
│   └── *.java            # Remaining Java files implementing servlets and other backend logic
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
