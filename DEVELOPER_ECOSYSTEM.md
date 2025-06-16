# Developer Ecosystem Features

This project exposes several features that allow third-party developers to integrate with the furniture store platform.

## Why It's Rare

Open e-commerce platforms with a plugin architecture are uncommon in small demo projects. By providing API keys, plugins and documented endpoints, this repository demonstrates how a simple store can be extended by vendors and external apps.

## Public REST API

Third-party integrations can access the same product and order APIs used by the frontend by providing an `X-API-KEY` header. Admins can create new keys via `POST /api/apikeys`.

## Plugin System

Plugins are JAR files placed in the `plugins` directory. At startup the `PluginManager` loads each plugin using Java's `ServiceLoader`. Implement the `FurniturePlugin` interface and include a file under `META-INF/services` to register your plugin.

## OpenAPI Documentation

The backend now includes springdoc which serves interactive Swagger UI at `/swagger-ui.html` and the raw spec at `/v3/api-docs`.

## Theme System

The frontend includes a simple light/dark mode toggle in the navigation bar. The selected theme is stored in `localStorage`.

## Future Ideas

The following concepts are not implemented but outline potential directions for extending the platform:

- **GPT-based Auto Product Writer** – upload a photo and let AI generate the name, description and tags.
- **AI Order Summary Bot** – send customers a friendly summary and return tips after each purchase.
- **Geo‑Personalized Pricing** – automatically adjust pricing based on the user's location and demand.
- **NFT Ownership Tags** – track unique furniture designs using NFTs for proof of ownership.

## Testing Guidelines

To keep the platform reliable and secure, follow these testing practices:

- **Unit test** all services and helper classes.
- **Integration test** each REST controller to verify request/response handling.
- **E2E test** the critical flows from registration to checkout and order history.
- **Mock APIs** in React tests so frontend tests do not rely on the backend.
- **Secure** protected routes with token verification tests to ensure authorization works.
