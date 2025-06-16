# Furniture Store

This project is a simple e-commerce example built with **Java**, **React** and **PostgreSQL**.

## Backend

The backend uses Spring Boot and Gradle.

```bash
cd backend
gradle bootRun
```

Database credentials are configured in `src/main/resources/application.properties`.

The API exposes `POST /api/orders` to create an order from cart data. Endpoints
under `/api/orders` are protected with JWT authentication. Use `/api/auth/register`
and `/api/auth/login` to obtain a token.
`GET /api/orders/user` returns the authenticated user's orders.

`POST /api/products/upload-image` accepts a multipart file and returns a URL to store in the product's `imageUrl` field.

## Frontend

The frontend uses React with Vite.

```bash
cd frontend
npm install
npm run dev
```

The checkout page at `/checkout` sends cart contents to the backend to create an order.
Login using the form at `/login`. View your past orders at `/orders`.
Admins can manage products at `/admin`, including uploading images and editing or deleting items. New products can be added at `/add-product`.

The dev server proxies API requests to the backend.

## Development

Run the backend and frontend tests with:

```bash
gradle -p backend test
npm test --prefix frontend
```


## Docker Compose

To run the whole stack with Docker, build the images and start the services:

```bash
docker-compose up --build
```

The backend will be available on `http://localhost:8080` and the React frontend
served by Nginx on `http://localhost`.
