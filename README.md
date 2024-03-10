# API App
The project develops a Java application using JavaFX that integrates two or more external RESTful JSON APIs. The application automates the process of connecting to different APIs based on user input and combines the responses in a meaningful way, allowing users to access information or content from multiple services without the need to utilize multiple services themselves.

## Design Overview
The application is structured around a central logic encapsulated within the `ApiApp` class, which orchestrates interactions with external RESTful JSON APIs, processes their responses, and updates the user interface accordingly. Acting as the entry point, the `ApiDriver` class handles the initialization of the JavaFX environment and manages runtime exceptions gracefully. The user interface is built using JavaFX components such as text fields, labels, buttons, and image views, providing a seamless experience for users to interact with the application. This design emphasizes modularity and separation of concerns, facilitating ease of maintenance and future extensions. The high-level architecture diagram depicts the clear delineation of responsibilities between the application logic, entry point management, and user interface components.

## Functionality
`ApiDriver`:
- Serves as the entry point for the application.
- Launches the JavaFX application ApiApp.
- Handles exceptions related to display problems or runtime issues.

`ApiApp`:
- Interacts with external RESTful JSON APIs to fetch and process data based on user queries.
- Initializes and sets up the user interface components and default values.
- Implements methods for creating the search bar UI component for users to enter search queries.
- Parses responses from the APIs and updates the user interface with the search results.
- Handles HTTP requests, error messages, and other UI updates.
- Implements various methods for initializing the application, creating UI components, and parsing API responses.

## File Structure and Content
```
api-app/
├── meta/
│   └── APPINFO.md
├── pom.xml
├── prepare-merge.sh
├── README.md
├── resources/
│   ├── config.properties
│   ├── default.png
│   ├── noimage.png
│   ├── openlibrary_logo.png
│   ├── readme-banner.png
│   ├── readme-newrepo.png
│   └── search_icon.png
├── run.sh
└── src/
    ├── main/
    │   └── java/
    │       ├── cs1302/
    │       │   └── api/
    │       │       ├── ApiApp.java
    │       │       ├── ApiDriver.java
    │       │       ├── OpenLibrarySearchApi.java
    │       │       └── PropertiesExample.java
    │       └── module-info.java
    └── site/
        ├── markdown/
        │   └── index.md.vm
        └── site.xml
```
