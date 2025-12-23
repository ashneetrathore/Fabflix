# :film_project: FABFLIX

## :open_book: OVERVIEW
Date: June 2024\
Developer(s): Ashneet Rathore\
Based on assignment instructions from Prof. Chen Li


**Tech Stack** | Java, MySQL, JavaScript, HTML, Bootstrap, Apache Tomcat, Docker, Kubernetes, AWS EC2, Google Cloud, IntelliJ IDEA, Maven, JMeter

## :film_strip: DEMO

## :classical_building: ARCHITECTURE
 

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
