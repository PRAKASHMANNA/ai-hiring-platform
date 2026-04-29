# 🚀 AI Hiring Platform — Job Portal + AI Resume Analyzer

A production-ready **Microservices-based Job Portal** with AI-powered Resume Analyzer built with Java Spring Boot, React, Kafka, Docker, and AWS.

---

## 🏗️ Architecture Overview
Client (React) → API Gateway (8080) → Microservices
├── Auth Service (8081)
├── Job Service (8082)
├── Resume Service (8083)
├── Application Service (8084)
└── Notification Service (8085)

---

## ✨ Features

### Candidate
- ✅ Register / Login with JWT Authentication
- ✅ Browse & Search Jobs (by title, location, type)
- ✅ Upload Resume (PDF, DOCX, DOC, TXT)
- ✅ AI Resume Analyzer — Match Score (0-100%) against job
- ✅ One-click Job Apply
- ✅ Track Application Status
- ✅ Email Notifications on apply & status update
- ✅ Forgot Password / Reset Password

### Recruiter
- ✅ Post / Edit / Delete Jobs
- ✅ View All Applicants per Job
- ✅ See AI Match Score per Candidate
- ✅ Update Application Status (SHORTLISTED / REJECTED / HIRED)
- ✅ Email Notification on job post & status update

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React + Bootstrap + Axios |
| Backend | Spring Boot 3.x (Java 21) |
| Architecture | Microservices (6 services) |
| Messaging | Apache Kafka |
| Auth | Spring Security + JWT |
| DB 1 | MySQL (Users, Jobs, Applications) |
| DB 2 | MongoDB (Resumes, AI Results) |
| File Parsing | Apache PDFBox + Apache POI |
| Email | Gmail SMTP + Spring Mail |
| Containerization | Docker + Docker Compose |
| API Gateway | Spring Cloud Gateway MVC |
| Build Tool | Maven |

---

## 📦 Microservices

### 1. Auth Service (Port 8081)
- User Registration & Login
- JWT Token Generation & Validation
- BCrypt Password Encoding
- Role Based Access (CANDIDATE / RECRUITER / ADMIN)
- Forgot Password with OTP
- Password Change

### 2. Job Service (Port 8082)
- Job CRUD Operations
- Search by Title, Location, Experience Level
- Kafka Producer — Job alert on job creation
- Role Based Access — Only RECRUITER can post jobs

### 3. Resume Service (Port 8083)
- Resume Upload — PDF, DOCX, DOC, TXT
- Auto Skill Extraction from resume
- AI Match Score Calculator (0-100%)
- AI Summary Generator
- MongoDB storage for resume data

### 4. Application Service (Port 8084)
- Job Apply with duplicate check
- Application Status Tracking
- Kafka Producer — Events on apply & status update
- Recruiter can update status (SHORTLISTED/REJECTED/HIRED)

### 5. Notification Service (Port 8085)
- Kafka Consumer for all events
- Dynamic Email — sends to actual user email
- Gmail SMTP integration
- Job creation, application submit, status update emails

### 6. API Gateway (Port 8080)
- Single entry point for all services
- Route management
- Spring Cloud Gateway MVC

---

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Maven 3.9+
- Docker Desktop
- IntelliJ IDEA

### 1. Clone the repository
```bash
git clone https://github.com/PRAKASHMANNA/ai-hiring-platform.git
cd ai-hiring-platform
```

### 2. Start Docker containers
```bash
docker-compose up -d
```
This will start:
- MySQL on port 3307
- MongoDB on port 27017
- Kafka on port 9092
- Zookeeper on port 2181

### 3. Configure each service
Copy `application.properties.example` to `application.properties` in each service and fill in your values:
```bash
cp application.properties.example application.properties
```

### 4. Start all services (in order)
1. auth-service
2. job-service
3. resume-service
4. application-service
5. notification-service
6. api-gateway

### 5. Access the API
All APIs are accessible through the gateway:
http://localhost:8080

---

## 📡 API Endpoints

### Auth Service
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | /api/auth/register | Register new user | ❌ |
| POST | /api/auth/login | Login user | ❌ |
| POST | /api/auth/forgot-password | Send OTP to email | ❌ |
| POST | /api/auth/reset-password | Reset password with OTP | ❌ |
| PUT | /api/auth/change-password | Change password | ✅ |

### Job Service
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | /api/jobs | Create job | ✅ RECRUITER |
| GET | /api/jobs/public/all | Get all active jobs | ❌ |
| GET | /api/jobs/public/{id} | Get job by ID | ❌ |
| GET | /api/jobs/public/search | Search jobs | ❌ |
| GET | /api/jobs/my-jobs | Get recruiter's jobs | ✅ RECRUITER |
| PUT | /api/jobs/{id} | Update job | ✅ RECRUITER |
| DELETE | /api/jobs/{id} | Delete job | ✅ RECRUITER |

### Resume Service
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | /api/resume/upload | Upload resume | ✅ CANDIDATE |
| POST | /api/resume/analyze | AI analyze resume | ✅ CANDIDATE |
| GET | /api/resume/my-resumes | Get my resumes | ✅ CANDIDATE |
| GET | /api/resume/{id} | Get resume by ID | ✅ |

### Application Service
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | /api/applications/apply | Apply for job | ✅ CANDIDATE |
| GET | /api/applications/my-applications | Get my applications | ✅ CANDIDATE |
| GET | /api/applications/job/{jobId} | Get job applications | ✅ RECRUITER |
| PUT | /api/applications/{id}/status | Update status | ✅ RECRUITER |

---

## 🗄️ Database Schema

### MySQL Databases
- `hireai_auth` — Users table
- `hireai_jobs` — Jobs table
- `hireai_applications` — Job Applications table

### MongoDB Database
- `hireai_resumes` — Resumes collection with AI results

---

## 📧 Email Notifications

| Event | Recipient | Email Content |
|---|---|---|
| Job Posted | Recruiter | Job posting confirmation |
| Job Applied | Candidate | Application submitted confirmation |
| Status: SHORTLISTED | Candidate | Congratulations — shortlisted! |
| Status: HIRED | Candidate | Congratulations — hired! |
| Status: REJECTED | Candidate | Thank you for applying |

---

## 🐳 Docker Services

```yaml
Services:
  - MySQL 8.0 → port 3307
  - MongoDB 6.0 → port 27017
  - Apache Kafka → port 9092
  - Zookeeper → port 2181
```

---

## 👨‍💻 Author

**Prakash Manna**
- GitHub: [@PRAKASHMANNA](https://github.com/PRAKASHMANNA)

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

