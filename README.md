# UsersCRUD RESTful API
This project implements a RESTful API for managing user resources using Spring Boot. It provides endpoints for creating, updating, and deleting users, as well as searching for users by birth date range.

It utilizes Spring Boot (it was initially built using [Spring Initializr](https://start.spring.io/)), providing endpoints for CRUD operations on user data. The API follows best practices for RESTful design and includes error handling and unit tests using Mockito for robustness.

## Requirements
To run this project, you need:
- Java (version of your choice, in this project Java 17 was used)
- Spring Boot (any version)
  
## Getting Started
### Installation
1. Clone the repository:

    `git clone https://github.com/malikoyv/Users-CRUD-Application.git`

2. Open a project in the preferred IDE.
   - [IntelliJ Idea](https://www.jetbrains.com/idea/download/?section=windows)

3. Change username and password as per your installation

    - open `src/main/resources/application.properties`

    - change `spring.datasource.username` and `spring.datasource.password` as per your installation

4. Build and run the app using maven

    ```
    mvn package
    java -jar target/spring-boot-rest-api-tutorial-0.0.1-SNAPSHOT.jar
    ```
Alternatively, you can run the app without packaging it using

`mvn spring-boot:run`

or simply navigate to the project graphically

  - run the application. The app will start running at http://localhost:8080.

## Usage
Once the application is running, you can access the API endpoints using a REST client or tools like [Postman](https://www.postman.com/downloads/). Below are the available endpoints and their functionalities.

### Endpoints

**POST /addUser**: Creates a new user with the provided information.

**PUT /updateUsersFieldById/{id}/{field}/{value}**: Updates the specified field of a user with the given ID.

**PUT /updateUserFullById/{id}**: Updates all fields of a user with the given ID.

**DELETE /deleteUserById/{id}**: Deletes the user with the specified ID.

**GET /getUsersByRange/{start}/{end}**: Retrieves users whose birth dates fall within the specified range.

## Testing
The project includes unit tests to ensure the functionality of the API. You can run the tests using Maven:

`mvn test`

or using your IDE:

  - navigate to `/test/java/com.example.UsersCRUD/controller` folder.
  - run `UserControllerTest`.

## Contributing
Contributions are welcome! Feel free to open issues or submit pull requests for any improvements or additional features.

## License
This project is licensed under the [GNU Public License v3.0](https://github.com/malikoyv/Users-CRUD-Application/blob/main/LICENSE)
