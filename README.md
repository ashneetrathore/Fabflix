# :film_project: FABFLIX

## :open_book: OVERVIEW
Date: June 2024\
Developer(s): Ashneet Rathore\
Based on assignment instructions from Prof. Chen Li


**Tech Stack** | Java, MySQL, JavaScript, HTML, Bootstrap, Apache Tomcat, Docker, Kubernetes, AWS EC2, Google Cloud, IntelliJ IDEA, Maven, JMeter

## :film_strip: DEMOS

## :classical_building: ARCHITECTURE
### :gear: BACKEND
The backend is engineered with **Java Servlets** that use **JDBC (Java Database Connectivity)** to interact with a **MySQL** movie database, performing queries and inserting data. The servlets receive a HTTP request from the frontend, execute database operations, and return a response in JSON format for rendering. Additionally, **SAX parsing** was used to efficiently process large XML datasets and integrate the extracted data into the movie database.

### :computer_mouse: FRONTEND
The frontend is built with **JavaScript**, **HTML**, and **Bootstrap** styling, providing a dynamic and responsive interface. JavaScript manages client-side behavior and user interactions by communicating with the backend through **jQuery AJAX** calls. Responses from the backend are returned as structured JSON data, which JavaScript processes to dynamically update the HTML content.

### :rocket: DEPLOYMENT & SCALABILITY


### :lock: SECURITY
Fabflix implements multiple protections to strengthen system security. **Password encryption** is used to securely store user credentials in the database, ensuring sensitive information is not kept in plain text. On the backend, **prepared statements** in servlets guard against SQL injection attacks. **Authentication filters** restrict access to protected pages, requiring users to log in before accessing core application functionality. A **reCAPTCHA** is used to prevent automated abuse during login, while **enforced HTTPS** protects all client-server communication.

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
