# Furniture Store

This project is a simple e-commerce example built with **Java**, **React** and **PostgreSQL**.

## Backend

The backend uses Spring Boot and Gradle.

```bash
cd backend
gradle bootRun
```

Database credentials are configured in `src/main/resources/application.properties`.

The API exposes `POST /api/orders` to create an order from cart data.

## Frontend

The frontend uses React with Vite.

```bash
cd frontend
npm install
npm run dev
```

The checkout page at `/checkout` sends cart contents to the backend to create an order.

The dev server proxies API requests to the backend.

## Development

Run the backend and frontend tests with:

```bash
gradle -p backend test
npm test --prefix frontend
```

