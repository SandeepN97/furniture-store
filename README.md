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
`GET /api/recommendations/user` returns up to five products from the same
category as the user's most recent order.

When an order is placed the backend sends a confirmation email using Spring Mail
and a Thymeleaf template. Configure `spring.mail.*` properties in
`application.properties` to enable email delivery.


`POST /api/products/upload-image` accepts a multipart file and returns a URL to store in the product's `imageUrl` field.

`GET /api/products` returns paged products. Use `page`, `size` and `sort` query parameters for pagination, plus optional `categoryId` and `name` filters. Each product contains a `stockQuantity` showing available inventory. When an order is placed stock decreases and ordering more than the available quantity fails.
`GET /api/products/{id}` returns a single product including its name, price, description and category information.

## Frontend

The frontend uses React with Vite.

```bash
cd frontend
npm install
npm run dev
```

The checkout page at `/checkout` can start a Stripe Checkout session by posting the
cart contents to `/api/payments/create-checkout-session`. Alternatively, a PayPal
order can be created by posting to `/api/payments/paypal/create-order`.
The backend returns a URL or order id that the browser navigates to for payment.
Login using the form at `/login`. View your past orders at `/orders`.
Admins can manage products at `/admin`, including uploading images and editing or deleting items. New products can be added at `/add-product`.
Admins can also view sales analytics at `/dashboard` showing total users, orders, revenue and top products.
Admins receive real-time notifications when a new order is placed via a WebSocket connection. If a product's stock falls below five units a **low stock** message is also published.

Guest checkout is available at `/checkout` even when not logged in. A "Save cart" button stores your items on the server so they persist across devices. Use coupon codes at checkout for discounts.
Each order begins with a `PENDING` status that admins can update to `SHIPPED` or `DELIVERED`.
Use the moon icon in the navbar to toggle dark mode.

The main product list uses query parameters for pagination and filtering by category, e.g. `?page=1&categoryId=2`.
Clicking a product navigates to `/products/:id` where the details page shows its image, price, description and category.
UI components are styled with Tailwind CSS loaded from a CDN. Product cards display in a responsive grid with hover effects and toast notifications appear when items are added to the cart.
If you are logged in, a "Recommended for you" section shows items from the same
category as your last order.

Visit `/blog` for company news and decorating tips. The home page also includes a newsletter signup form that posts emails to `/api/newsletter/signup`.

The app is installable as a PWA thanks to a service worker and manifest file. Open Graph meta tags allow products to be shared on social media with rich previews.

Passwords are encrypted with BCrypt and login attempts are rate limited. JWT
tokens expire after 24h and are validated on each request. CORS is configured to
allow the frontend URL.

The app ships with English, Spanish and Nepali translations. Use the language selector in the navigation bar to switch languages at runtime. Translations are powered by `react-i18next`.

The dev server proxies API requests to the backend.

## Mobile App

The `mobile` directory contains a basic React Native app built with Expo.
Install dependencies and start the development server with:
```bash
cd mobile
npm install
npm start
```

The app fetches products from the backend and displays them in a mobile-friendly list.

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

## Continuous Deployment

GitHub Actions builds the backend and frontend and deploys to Render/Heroku on every push to `master`. Configure `RENDER_DEPLOY_HOOK` or the Heroku secrets in the repository settings.

## Invoices

`GET /api/orders/{id}/invoice` returns a PDF invoice for an order. The order history page shows an **Invoice** button to download the file.

## Sales Reports

Admins can download overall sales figures from `/api/admin/reports/sales.csv` or `/api/admin/reports/sales.pdf`.

## End-to-End Tests

Cypress tests live under `frontend/cypress`. Run them with:

```bash
npm run cy:run --prefix frontend
```

## Stripe Subscriptions

The `/api/subscriptions` endpoint creates a Stripe subscription for the authenticated user. View your subscriptions at `/api/subscriptions/me`.

## GraphQL API

A GraphQL endpoint is available at `/graphql`. Example queries:

```graphql
query {
  products {
    id
    name
    price
  }
}
```

Use a tool like GraphiQL or Apollo Client to execute queries.

## Search API

Elasticsearch powers a `/api/search?q=chair` endpoint that returns matching products with typo tolerance.

## AI Chatbot

Send a POST to `/api/chat` with `{ "message": "Do you ship?" }` to receive an automated reply generated by OpenAI.
Set `OPENAI_API_KEY` in your environment to enable.

## Feature Flags

LaunchDarkly is integrated so new features can be enabled gradually. Configure the SDK key using the `LAUNCHDARKLY_SDK_KEY` environment variable.

## Multi-Tenant Support

Requests may include an `X-Tenant-ID` header. Products are scoped per tenant so multiple vendors can share the same application instance.
