# Blog Platform - Deployment Guide

## 🚀 Quick Start

### Prerequisites
- Java 21 or higher
- Docker and Docker Compose
- PostgreSQL (if not using Docker)

### Option 1: Docker Deployment (Recommended)

1. **Clone and navigate to the project:**
   ```bash
   cd instagram-backend
   ```

2. **Run the deployment script:**
   ```bash
   ./deploy.sh
   ```

3. **Access the application:**
   - API Base URL: `http://localhost:8080/api`
   - Health Check: `http://localhost:8080/api/auth/exists?userName=test`

### Option 2: Manual Deployment

1. **Build the application:**
   ```bash
   ./gradlew clean build -x test
   ```

2. **Set up PostgreSQL database:**
   - Create database: `million_story`
   - Username: `rayadav`
   - Password: `postgres`

3. **Run the application:**
   ```bash
   java -jar build/libs/blog-platform-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
   ```

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_URL` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/million_story` |
| `DATABASE_USERNAME` | Database username | `rayadav` |
| `DATABASE_PASSWORD` | Database password | `postgres` |
| `JWT_SECRET` | JWT signing secret | `blogplatformsecretkey...` |
| `JWT_EXPIRATION` | JWT token expiration (ms) | `86400000` |
| `SERVER_PORT` | Application port | `8080` |
| `FILE_UPLOAD_PATH` | File upload directory | `./uploads/` |

### Production Configuration

The application uses `application-prod.properties` for production settings:
- Reduced logging levels
- Optimized JPA settings
- Environment variable support
- Security configurations

## 📋 API Endpoints

### Authentication
- `POST /api/auth/signup` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `POST /api/auth/forgot-password` - Password reset request
- `POST /api/auth/reset-password` - Password reset
- `GET /api/auth/exists` - Check if user exists

### Blog Posts
- `GET /api/blog-posts` - Get all blog posts
- `GET /api/blog-posts/paginated` - Get paginated blog posts
- `GET /api/blog-posts/{id}` - Get blog post by ID
- `POST /api/blog-posts` - Create blog post (authenticated)
- `PUT /api/blog-posts/{id}` - Update blog post (authenticated)
- `DELETE /api/blog-posts/{id}` - Delete blog post (authenticated)

### Websites
- `GET /api/websites` - Get all websites
- `GET /api/websites/{id}` - Get website by ID
- `POST /api/websites` - Add website
- `DELETE /api/websites/{id}` - Delete website
- `PUT /api/websites/{id}/toggle` - Toggle website status

### Scraping
- `POST /api/scrape/all` - Scrape all websites
- `POST /api/websites/{id}/scrape` - Scrape specific website
- `POST /api/websites/scrape` - Scrape website by URL

### Statistics
- `GET /api/stats` - Get platform statistics

## 🔒 Security

### JWT Authentication
- All protected endpoints require a valid JWT token
- Include token in Authorization header: `Bearer <token>`
- Token expiration: 24 hours (configurable)

### CORS Configuration
- Cross-origin requests are enabled for development
- Configure CORS settings for production deployment

## 📊 Database

### PostgreSQL Migration
The application uses Flyway for database migrations:
- Migration files: `src/main/resources/db/migrations/`
- Automatic migration on startup
- Version control for schema changes

### Database Schema
- User management tables
- Blog post and website tables
- Authentication and session tables
- Notification and chat tables

## 🐳 Docker Commands

### Build and Run
```bash
# Build the application
docker-compose build

# Start services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

### Database Operations
```bash
# Access PostgreSQL
docker-compose exec postgres psql -U rayadav -d million_story

# Backup database
docker-compose exec postgres pg_dump -U rayadav million_story > backup.sql

# Restore database
docker-compose exec -T postgres psql -U rayadav -d million_story < backup.sql
```

## 🔍 Monitoring and Logs

### Application Logs
```bash
# View application logs
docker-compose logs -f app

# View database logs
docker-compose logs -f postgres
```

### Health Checks
- Application health: `GET /api/auth/exists?userName=test`
- Database connectivity: Check application logs

## 🚨 Troubleshooting

### Common Issues

1. **Port 8080 already in use:**
   ```bash
   # Change port in docker-compose.yml or application.properties
   SERVER_PORT=8081
   ```

2. **Database connection failed:**
   - Check PostgreSQL is running
   - Verify database credentials
   - Ensure database exists

3. **JWT token issues:**
   - Verify JWT_SECRET is set
   - Check token expiration settings

4. **File upload issues:**
   - Ensure uploads directory exists
   - Check file permissions
   - Verify FILE_UPLOAD_PATH setting

### Performance Optimization

1. **Database Optimization:**
   - Add database indexes
   - Configure connection pooling
   - Monitor query performance

2. **Application Optimization:**
   - Adjust JVM memory settings
   - Configure caching
   - Optimize JPA queries

## 📝 Development

### Local Development
```bash
# Run with development profile
./gradlew bootRun

# Run tests
./gradlew test

# Build without tests
./gradlew build -x test
```

### Code Quality
- All syntax errors have been resolved
- Dependencies are compatible
- PostgreSQL migration scripts are ready
- Security configuration is updated for Spring Security 6.x

## 🎯 Production Checklist

- [ ] Set secure JWT secret
- [ ] Configure production database
- [ ] Set up SSL/TLS certificates
- [ ] Configure logging and monitoring
- [ ] Set up backup strategy
- [ ] Configure firewall rules
- [ ] Set up CI/CD pipeline
- [ ] Performance testing
- [ ] Security audit

## 📞 Support

For deployment issues or questions:
1. Check the troubleshooting section
2. Review application logs
3. Verify configuration settings
4. Test database connectivity

---

**Ready for Production Deployment! 🚀**



