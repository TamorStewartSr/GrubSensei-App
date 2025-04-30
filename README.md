
# GrubSensei App

Welcome to the GrubSensei App! This is a Spring Boot-based web application that allows users to submit dining reviews, manage restaurants, and view aggregated ratings. Admins can moderate reviews for quality assurance.

## Features

- **User Management**: Users can register, log in, and manage their accounts.
- **Restaurant Management**: Add, view, and search for restaurants.
- **Dining Reviews**: Submit, view, and manage dining reviews.
- **Admin Dashboard**: Approve or reject pending reviews.
- **Allergy Ratings**: Users can rate restaurants based on allergy safety (peanut, egg, and dairy).
- **Responsive UI**: Built with HTML, CSS, and JavaScript for a seamless user experience.

## Tech Stack

- **Backend:** Java, Spring Boot
- **Database:** H2 (in-memory)
- **Frontend:** HTML, CSS, JavaScript
- **Build Tool:** Maven
- **Version Control:** Git

## Setup Instructions

1. **Clone the Repository**
    ```bash
    git clone <repository-url>
    cd reviewApp
    ```

2. **Configure Application**
    - The app uses H2 Database. No additional setup required.
    - Configuration is in `src/main/resources/application.properties`.

3. **Run the Application**
    ```bash
    ./mvnw spring-boot:run
    ```
    Or on Windows:
    ```bash
    mvnw.cmd spring-boot:run
    ```

4. **Access the Application**
    - User Interface: `http://localhost:8080`
    - Admin Dashboard: `http://localhost:8080/adminDashboard.html`
    - H2 username/password is empty. just hit connect.

## Usage

- **User**: Submit dining reviews and view restaurant ratings.
- **Admin**: Log in with hardcoded credentials to approve or reject reviews.

## Admin Credentials
> Username: `admin`  
> Password: `password`  
(Note: Update these in the AdminController for security in production)

## Project Structure

```bash
src
├── main
│   ├── java
│   │   └── com.springboot.reviewApp
│   │       ├── controller
│   │       ├── dto
│   │       ├── exception
│   │       ├── model
│   │       ├── repository
│   │       └── validation
│   └── resources
│       ├── static (HTML, CSS, JS)
│       └── application.properties
└── test

```

## Future features
* User Features: Profile management, favorites, review editing,   reactions, comments, and follow feature.
* Restaurant Features: Owner accounts, menu management, reservations, operating hours, and verification badges.
* Admin Features: Review moderation, analytics dashboard, and user management.
* Search & Filtering: Advanced filters, sort by ratings, map view, and geo-location search.
* Security Features: 2FA, email verification, and password recovery.
* Recommendation & Analytics: Personalized recommendations, sentiment analysis, and trending restaurants.
* Technical Enhancements: Redis caching, pagination, microservices, Docker, and CI/CD pipeline.
* Community Features: Badges, leaderboards, and restaurant promotions.

## Contribution

Contributions are welcome! Please follow these steps:
	1.	Fork the repo.
	2.	Create a new branch (feature/your-feature).
	3.	Commit your changes.
	4.	Push to your branch and create a pull request.

## License

This project is licensed under the [MIT License](./LICENSE.txt). If using any code from this project, please give credit.
