# TicnotMitkadem_Final_Project

## Background
This project is the final project for the Advanced Programming course at Bar-Ilan University, Faculty of Engineering.

In this project, we aim to implement and create a server that can be accessed remotely via a web browser.
Our goal is to design and build a fully functional and visually appealing website using all the components and techniques covered throughout the course.

The website will allow users to upload text files, which will then be processed to generate a computational graph that visually represents the input data from the uploaded file.

In addition, we will provide an option to assign output values to the graph's nodes and display both the inputs and outputs in a table, showcasing the graph's computations clearly.


# Topic Management System

A Java-based web application for managing and publishing topics with a custom HTTP server implementation.

## Project Overview

This project implements a topic management system with the following key features:
- Custom HTTP server implementation
- Topic publishing and subscription system
- Configuration-based agent management
- Static HTML file serving
- RESTful API endpoints

## Project Structure

```
├── src/
│   ├── configs/         # Configuration management
│   ├── graph/          # Topic and agent management
│   ├── server/         # HTTP server implementation
│   ├── servlets/       # Request handlers
│   └── views/          # Main application views
├── html_files/         # Static HTML files
├── uploads/           # File upload directory
└── out/               # Compiled output
```

## Features

- **Custom HTTP Server**: Implements a multi-threaded HTTP server with servlet support
- **Topic Management**: Publish and subscribe to topics with concurrent access support
- **Configuration System**: Dynamic agent configuration through config files
- **Static File Serving**: Serves HTML files and other static content
- **RESTful API**: Endpoints for topic management and file uploads

## API Endpoints

- `GET /publish` - Display topics
- `POST /upload` - Upload configuration files
- `GET /app/` - Access the web interface

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Maven or your preferred Java build tool
- Git (for cloning the repository)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/AmitLevyTzedek/TicnotMitkadem_Final_Project
unzip  finalProj2025.zip
cd tichnotmitkadm1
```
2. Create necessary directories:
```bash
mkdir -p html_files uploads out
```

3. Compile the project:
```bash
# Using javac
javac -d out src/**/*.java

# Or using Maven (if pom.xml is present)
mvn clean install
```

### Running the Application

1. Start the server:
```bash
# If using javac
java -cp out views.Main

# If using Maven
mvn exec:java -Dexec.mainClass="views.Main"
```

2. The server will start on port 8080. You can access the application at:
   - Web Interface: http://localhost:8080/app/
   - API Endpoints: 
     - http://localhost:8080/publish
     - http://localhost:8080/upload

### Testing the Application

1. Open your web browser and navigate to http://localhost:8080/app/
2. Use the web interface to:
   - Upload configuration files
   - View published topics
   - Monitor system status

### Stopping the Application

- Press `Ctrl+C` in the terminal where the server is running
- The server will gracefully shut down and close all connections

## Configuration

The system uses a configuration file format that specifies:
- Agent class names
- Subscription topics
- Publishing topics

Example configuration format:
```
com.example.Agent1
topic1,topic2
topic3,topic4
```

## Architecture

- **Server**: Custom HTTP server implementation with thread pooling
- **Topics**: Concurrent topic management using thread-safe collections
- **Agents**: Parallel agent execution with configurable thread pools
- **Configuration**: Dynamic loading of agent configurations

## Contributing
@[AmitLevyTzedeK](https://github.com/AmitLevyTzedek)

@[galgreen1](https://github.com/galgreen1)


## License

This project is licensed under the MIT License - see the LICENSE file for details.
