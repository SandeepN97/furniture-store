# 🛋️ Furniture Store

## 📌 Project Overview
A full-featured e-commerce platform for selling furniture online. Customers can browse products, add them to a cart, securely check out, and track their orders. Admins manage inventory and orders through protected dashboards. The project exposes a RESTful API backend and a modern React frontend built with Vite.

## 🛠️ Tech Stack
- **Backend**: Java 17, Spring Boot, Spring Data JPA, Spring Security, JWT
- **Database**: PostgreSQL
- **Frontend**: React, Vite, React Router, Context API, Axios, Tailwind CSS
- **Testing**: JUnit 5, Mockito, Spring Boot Test, Cypress, React Testing Library
- **Deployment**: Docker, Render (backend), Vercel (frontend)

## 🌟 Key Features

### 👥 User Features
- View all products with pagination and category filters
- View detailed product pages with descriptions, images, and pricing
- Add items to a cart via React Context API and update quantities
- Review and modify cart contents
- Secure checkout with user-provided information
- JWT-based registration and login
- Access order history after purchase
- Order confirmation screen post‑checkout

### 🧑‍💼 Admin Features
- Admin login with role-based access
- Create, edit, and delete products
- Upload product images using multipart/form-data
- Access protected dashboard routes
- Track orders and update their status

## 🔐 Authentication & Authorization
- JWT tokens issued on login or registration
- Tokens stored in `localStorage` and sent via `Authorization` header
- Spring Security protects API routes and enforces roles (`USER` vs `ADMIN`)

## 🖼️ Image Upload Support
Images are uploaded through `/api/products/upload-image` using multipart requests. Files are stored under an `uploads/` directory, and the resulting URL is saved in the product data for display on the frontend.

## 🧪 Testing

### 🧪 Backend Testing
- Unit tests with JUnit and Mockito (e.g., `ProductServiceTest`)
- Integration tests using Spring Boot Test and MockMvc
- Data seeding verified through `CommandLineRunner` tests

### 🧪 Frontend Testing
- Components tested with React Testing Library
- End-to-end flows tested with Cypress (add to cart, place order)
- Run all tests with:

```bash
gradle -p backend test
npm test --prefix frontend
```

## 🧰 API Reference
The most common endpoints are listed below:

| Method | Route | Description |
| --- | --- | --- |
| `GET` | `/api/products` | List products with optional filters |
| `GET` | `/api/products/{id}` | Get product details |
| `POST` | `/api/orders` | Place a new order |
| `GET` | `/api/orders/user` | Get the current user's orders |
| `POST` | `/api/auth/register` | Register a new user |
| `POST` | `/api/auth/login` | Log in and receive a JWT |

Interactive OpenAPI docs are available at `/swagger-ui.html`.

## 🚀 Deployment Instructions

### 🔧 Backend
1. Start PostgreSQL (locally or via Docker).
2. Configure database credentials in `backend/src/main/resources/application.properties`.
3. Run the application:

```bash
./gradlew -p backend bootRun
```

### 💻 Frontend
1. From the `frontend` folder install dependencies and start Vite:

```bash
npm install
npm run dev
```

2. Set `VITE_API_BASE_URL` if the backend runs on a different host/port.

### 📦 Docker
A `docker-compose.yml` is provided to run PostgreSQL, the backend, and the Nginx‑served frontend:

```bash
docker-compose up --build
```

## 🔄 CI/CD
GitHub Actions run tests on every push and build Docker images for deployment.

## 📈 Analytics & Admin Dashboard
Admins can view metrics such as total orders, revenue, and top-selling products. Charts are implemented with Chart.js on the dashboard.

## 📦 Folder Structure Overview

```text
backend/
├── controller/
├── service/
├── model/
├── repository/
└── application.properties

frontend/
├── src/components/
├── src/pages/
├── src/context/
└── App.jsx
```

## 📸 Screenshots
_Placeholders for screenshots: homepage, cart, and admin panel._

## 📄 License
This project is released under the MIT License.

## 💡 Future Enhancements
- Wishlist functionality
- Stripe/PayPal payment integration
- Product reviews and ratings
- Delivery tracking
- Mobile app version with React Native

## 🤝 Contributing
1. Fork the repo and create your feature branch.
2. Commit your changes and open a pull request.
3. Make sure tests pass before submitting.

## 📫 Contact
For questions or support, reach out via [GitHub](https://github.com/yourusername).
