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
