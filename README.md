# IdeaSpark Backend

A comprehensive Spring Boot backend application for IdeaSpark - an AI-powered idea generation and management platform.

## 🚀 Features

### Core Features
- **Multi-Login Authentication** - Login with email, username, or phone number
- **OTP Verification** - Email and SMS verification for secure authentication
- **Role-Based Access Control** - Admin and User roles with different permissions
- **JWT Token Security** - Secure authentication with JWT tokens
- **Redis Session Management** - Fast session storage and user blocking capabilities

### Third-Party Integrations
- **Cloudinary** - Image upload and management
- **Gmail SMTP** - Email notifications and OTP delivery
- **Twilio SMS** - SMS notifications and OTP delivery
- **Google OAuth** - Social login integration

### Advanced Features
- **PDF Export System** - Export user data and activities to PDF with professional templates
- **Comprehensive Exception Handling** - Global error handling with standardized responses
- **Activity Tracking** - User activity logging and analytics
- **Subscription Management** - User subscription and billing management

## 🛠️ Tech Stack

- **Framework:** Spring Boot 3.3.4
- **Language:** Java 21
- **Database:** MongoDB
- **Cache:** Redis
- **Security:** Spring Security + JWT
- **Documentation:** Spring Boot Actuator
- **Build Tool:** Maven
- **PDF Generation:** iText7
- **Image Storage:** Cloudinary
- **Email:** Spring Mail + Gmail SMTP
- **SMS:** Twilio
- **Authentication:** Google OAuth2

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6+
- MongoDB 4.4+
- Redis 6.0+
- Git

## ⚙️ Environment Setup

### 1. Clone the Repository
```bash
git clone https://github.com/shobhit-APP/IdeaSpark_Backend.git
cd IdeaSpark_Backend
```

### 2. Environment Variables
Create a `.env` file or set the following environment variables:

```properties
# Database Configuration
SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/ideaspark

# Redis Configuration
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
SPRING_DATA_REDIS_PASSWORD=

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-here
JWT_EXPIRATION=86400000

# Cloudinary Configuration
CLOUDINARY_CLOUD_NAME=your-cloudinary-cloud-name
CLOUDINARY_API_KEY=your-cloudinary-api-key
CLOUDINARY_API_SECRET=your-cloudinary-api-secret

# Email Configuration
SPRING_MAIL_USERNAME=your-gmail-address@gmail.com
SPRING_MAIL_PASSWORD=your-gmail-app-password

# Twilio Configuration
TWILIO_ACCOUNT_SID=your-twilio-account-sid
TWILIO_AUTH_TOKEN=your-twilio-auth-token
TWILIO_PHONE_NUMBER=your-twilio-phone-number

# Google OAuth Configuration
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret

# CORS Configuration
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:19006
```

### 3. Build and Run
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Or run the JAR file
java -jar ideaspark-api/target/ideaspark-api-1.0.0.jar
```

## 🏗️ Project Structure

```
IdeaSpark_Backend/
├── ideaspark-shared/          # Shared DTOs, entities, exceptions
│   ├── src/main/java/com/ideaspark/shared/
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── entity/           # MongoDB Entities
│   │   ├── enums/            # Enumerations
│   │   └── exception/        # Custom Exceptions
├── ideaspark-api/            # Main API Module
│   ├── src/main/java/com/ideaspark/api/
│   │   ├── config/           # Configuration Classes
│   │   ├── controller/       # REST Controllers
│   │   ├── repository/       # MongoDB Repositories
│   │   ├── service/          # Business Logic Services
│   │   ├── security/         # Security Configuration
│   │   ├── exception/        # Exception Handlers
│   │   └── aspect/           # AOP Aspects
│   └── src/main/resources/
│       └── application.properties
└── pom.xml                   # Parent Maven Configuration
```

## 🔑 API Endpoints

### Authentication
- `POST /auth/register` - User registration
- `POST /auth/login` - User login (email/username/phone)
- `POST /auth/verify-email` - Email verification
- `POST /auth/verify-phone` - Phone verification
- `POST /auth/forgot-password` - Password reset request
- `POST /auth/reset-password` - Password reset confirmation
- `POST /auth/google` - Google OAuth login

### User Management
- `GET /users/profile` - Get user profile
- `PUT /users/profile` - Update user profile
- `POST /users/upload-avatar` - Upload profile picture
- `DELETE /users/account` - Delete user account
- `POST /users/change-password` - Change password

### Admin Operations
- `GET /admin/users` - List all users
- `POST /admin/users/{id}/block` - Block/unblock user
- `GET /admin/analytics` - System analytics

### Export Operations
- `POST /export/ideas/pdf` - Export ideas as PDF
- `POST /export/activities/pdf` - Export activities as PDF
- `POST /export/full/pdf` - Export complete user data as PDF

## 🚦 Application Status

The application runs on port `8080` by default.

### Health Check
- **Endpoint:** `GET /actuator/health`
- **Response:** Application health status

### API Documentation
- **Swagger UI:** `http://localhost:8080/swagger-ui.html` (if configured)

## 🔧 Configuration

### Database
- MongoDB connection is configured via `SPRING_DATA_MONGODB_URI`
- Default database name: `ideaspark`

### Security
- JWT tokens expire after 24 hours (configurable)
- CORS is configured for frontend origins
- Rate limiting is implemented for API endpoints

### File Upload
- Maximum file size: 5MB
- Supported image formats: JPEG, PNG, GIF, WebP
- Files are stored in Cloudinary

## 🐛 Exception Handling

The application includes comprehensive exception handling:

- **Global Exception Handler** - Catches and formats all exceptions
- **Custom Exceptions** - Application-specific error types
- **Validation Errors** - Field-level validation error details
- **Security Exceptions** - Authentication and authorization errors
- **Service Exceptions** - Third-party service integration errors

## 📊 Logging

- **Framework:** SLF4J with Logback
- **Levels:** DEBUG, INFO, WARN, ERROR
- **Format:** Structured JSON logging (configurable)
- **Destinations:** Console and file (configurable)

## 🚀 Deployment

### Docker (Optional)
```dockerfile
# Create a Dockerfile for containerization
FROM openjdk:21-jdk-slim
COPY ideaspark-api/target/ideaspark-api-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment-Specific Profiles
- `application-dev.properties` - Development
- `application-prod.properties` - Production
- `application-test.properties` - Testing

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Shobhit** - *Initial work* - [shobhit-APP](https://github.com/shobhit-APP)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- MongoDB team for the robust database
- Redis team for fast caching solution
- All third-party service providers (Cloudinary, Twilio, etc.)

## 📞 Support

For support and questions:
- Create an issue on GitHub
- Contact: [Your Email]

---

**Made with ❤️ for the IdeaSpark community**