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


`GET /api/products` returns paged products. Use `page`, `size` and `sort` query parameters for pagination, plus optional `categoryId` and `name` filters.

## Frontend

The frontend uses React with Vite.

```bash
cd frontend
npm install
npm run dev
```

The checkout page at `/checkout` starts a Stripe Checkout session by posting the
cart contents to `/api/payments/create-checkout-session`. The backend returns a
URL that the browser navigates to for payment.

Login using the form at `/login`. View your past orders at `/orders`.
Admins can manage products at `/admin`, including uploading images and editing or deleting items. New products can be added at `/add-product`.

The main product list uses query parameters for pagination and filtering by category, e.g. `?page=1&categoryId=2`.
UI components are styled with Tailwind CSS loaded from a CDN. Product cards display in a responsive grid with hover effects and toast notifications appear when items are added to the cart.

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

Set the `STRIPE_SECRET` environment variable in `docker-compose.yml` or your
shell to your Stripe secret key so that the payment endpoint can create
Checkout sessions.
