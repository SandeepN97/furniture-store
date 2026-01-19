# 📊 Samjhana Ventures Dashboard

## 📌 Project Overview
A comprehensive **Multi-Business Management System** designed for managing multiple business ventures under one roof. Built with a focus on non-technical users, featuring large touch-friendly buttons, Nepali/English language support, and specialized modules for each business type.

## 🏢 Business Units Supported

### 1. **Samjhana Furniture**
- Inventory tracking (items like sofas, glue, wood)
- Daily sales recording
- Barcode scanning with photo fallback
- Stock level monitoring

### 2. **Petrol Pump**
- Fuel batch tracking (quantity and cost price)
- Dynamic pricing system with real-time updates
- Weighted Average Cost calculation for profit
- Automatic revenue and profit calculation based on liters sold

### 3. **EV Charging Station**
- Daily meter reading logs (opening/closing readings)
- Automatic units consumed calculation
- Electricity bill tracking
- Revenue vs. consumption comparison

### 4. **House Rental**
- Simple tenant tracking
- Monthly rent collection status
- Payment history

## 🛠️ Tech Stack

### Backend
- **Java 21** with **Spring Boot 3.2.1**
- **Maven** for dependency management
- **H2 Database** (file-based for easy backup)
- **Spring Data JPA** for database operations
- **Lombok** for reducing boilerplate
- **OpenAPI/Swagger** for API documentation

### Frontend
- **React 18.2** with **Vite 4.5**
- **Tailwind CSS** for touch-friendly UI
- **i18next** for Nepali/English translations
- **Recharts** for analytics visualization
- **react-qr-barcode-scanner** for barcode scanning
- **React Router** for navigation
- **Axios** for API communication

## 🌟 Key Features

### 📱 Touch-Friendly Design
- Large buttons (minimum 44px touch targets)
- Bottom navigation like mobile apps
- Minimal typing required
- Clear visual feedback

### 🌐 Multi-Language Support
- Default: Nepali (नेपाली)
- Toggle to English anytime
- All UI labels translated
- Easy language switching button

### 💰 Financial Tracking
- **Dad's Pocket**: Track owner withdrawals for personal use
- **Loan Tracking**: Bank loans with automatic daily interest calculation
- **Salary Management**: Employee tracking with advance payments
- **Combined P&L Dashboard**: Visual profit/loss across all businesses

### 📊 Dynamic Pricing (Petrol Pump)
- Record fuel purchases with cost price
- Update selling price via settings
- Automatic profit calculation using weighted average cost
- Price history tracking

### ⚡ Meter Reading Management (EV Charging)
- Daily opening and closing readings
- Automatic units consumed calculation
- Revenue tracking per day
- Monthly electricity bill comparison

### 📸 Smart Data Entry
- Barcode scanner for product identification
- Photo capture for unrecognized barcodes
- Images stored in `/uploads` folder for easy backup

## 📂 Project Structure

```
samjhana-ventures-dashboard/
├── backend/
│   ├── src/main/java/com/samjhana/ventures/
│   │   ├── model/              # Entity classes
│   │   │   ├── BusinessUnit.java
│   │   │   ├── Product.java
│   │   │   ├── FuelPriceLog.java
│   │   │   ├── Transaction.java
│   │   │   ├── Loan.java
│   │   │   ├── Employee.java
│   │   │   ├── EVMeterReading.java
│   │   │   └── Tenant.java
│   │   ├── repository/         # JPA repositories
│   │   ├── service/           # Business logic
│   │   │   └── PetrolPriceService.java  # Dynamic pricing
│   │   ├── controller/        # REST API endpoints
│   │   │   ├── DashboardController.java
│   │   │   ├── FuelPriceController.java
│   │   │   ├── EVMeterReadingController.java
│   │   │   └── BusinessUnitController.java
│   │   └── config/            # Configuration
│   ├── pom.xml                # Maven dependencies
│   └── src/main/resources/
│       └── application.properties
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   │   ├── GlobalDashboard.jsx      # Combined P&L view
│   │   │   ├── EVChargingComponent.jsx  # Meter readings
│   │   │   ├── BottomNav.jsx           # Mobile-style navigation
│   │   │   ├── BarcodeScanner.jsx      # Barcode scanning
│   │   │   └── LanguageToggle.jsx      # Nepali/English switch
│   │   ├── i18n/
│   │   │   ├── config.js
│   │   │   └── locales/
│   │   │       ├── ne.json             # Nepali translations
│   │   │       └── en.json             # English translations
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── package.json
│   └── tailwind.config.js
│
└── uploads/                   # Image storage folder
```

## 🚀 Getting Started

### Prerequisites
- **Java 21** or higher
- **Node.js 18** or higher
- **Maven 3.8+**

### Backend Setup

1. Navigate to backend directory:
```bash
cd backend
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The backend will start on `http://localhost:8080/api`

**H2 Console**: Access at `http://localhost:8080/api/h2-console`
- JDBC URL: `jdbc:h2:file:./data/venturesdb`
- Username: `admin`
- Password: `admin123`

### Frontend Setup

1. Navigate to frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start development server:
```bash
npm run dev
```

The frontend will start on `http://localhost:5173`

## 🧰 API Reference

### Dashboard & Analytics
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/dashboard/profit-loss?period=month` | Get combined P&L for all businesses |
| `GET` | `/api/dashboard/business/{id}/analytics` | Get specific business analytics |

### Fuel Price Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/fuel-prices/current` | Get current prices for all fuel types |
| `POST` | `/api/fuel-prices/update-price` | Update selling price |
| `POST` | `/api/fuel-prices/record-purchase` | Record new fuel batch |
| `POST` | `/api/fuel-prices/calculate-profit` | Calculate profit for a sale |

### EV Meter Readings
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/ev-meter/all` | Get all meter readings |
| `GET` | `/api/ev-meter/latest` | Get latest reading |
| `POST` | `/api/ev-meter/add` | Add new meter reading |
| `GET` | `/api/ev-meter/range?startDate&endDate` | Get readings by date range |

### Business Units
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/businesses/active` | Get all active businesses |
| `POST` | `/api/businesses/create` | Create new business unit |

## 📊 Data Models

### Key Entities

**BusinessUnit**: Represents each business (Furniture, Petrol, EV, Rental)

**FuelPriceLog**: Tracks fuel purchases and pricing with weighted average cost

**EVMeterReading**: Daily electricity meter readings with auto-calculated units

**Transaction**: Universal transaction tracking for all income/expenses

**Loan**: Bank loan tracking with automatic daily interest accrual

**Employee**: Employee management with salary and advance tracking

**Tenant**: House rental tenant information and payment status

## 🎨 UI Features

### Bottom Navigation
- 5 main sections: Dashboard, Furniture, Petrol, EV Charging, Rental
- Icons with labels for easy recognition
- Active state highlighting
- Always visible at bottom of screen

### Touch-Friendly Inputs
- All inputs minimum 48px height
- Large buttons for primary actions
- Ample spacing between elements
- Font size minimum 16px (prevents zoom on mobile)

### Language Toggle
- Fixed position button in top-right
- Shows opposite language name (shows "English" when in Nepali)
- Instant language switching
- All labels update immediately

## 📈 Business Logic Examples

### Petrol Pump - Weighted Average Cost
```java
// When fuel is sold, profit is calculated as:
// Profit = (Selling Price - Weighted Avg Cost) × Liters Sold

BigDecimal sellingPrice = getCurrentSellingPrice(fuelType);
BigDecimal averageCost = calculateWeightedAverageCost(fuelType);
BigDecimal profit = sellingPrice.subtract(averageCost).multiply(litersSold);
```

### EV Charging - Auto Calculation
```javascript
// Units consumed = Closing Reading - Opening Reading
const unitsConsumed = closingReading - openingReading;
const totalRevenue = unitsConsumed * ratePerUnit;
```

## 🔄 Deployment

### Backend Deployment
```bash
mvn clean package
java -jar target/ventures-dashboard-1.0.0.jar
```

### Frontend Deployment
```bash
npm run build
# Deploy dist/ folder to any static hosting
```

### Database Backup
H2 database is file-based at `./data/venturesdb.mv.db`
Simply copy this file to backup all data.

## 📱 Mobile-First Design Principles
1. **Bottom Navigation**: Thumb-friendly access
2. **Large Touch Targets**: Minimum 44×44px
3. **Readable Fonts**: 16px+ to prevent zoom
4. **Single Column Layouts**: Easy vertical scrolling
5. **Clear Visual Hierarchy**: Important info stands out

## 🌍 Internationalization (i18n)

The app supports Nepali and English with complete translations for:
- Navigation labels
- Form fields
- Button text
- Error messages
- Success notifications
- Dashboard metrics

## 💡 Future Enhancements
- SMS notifications for low stock
- WhatsApp integration for daily reports
- PDF export of financial statements
- Mobile app (React Native)
- Voice input for sales entry
- Integration with accounting software
- Multi-user access with roles
- Cloud backup automation

## 📄 License
This project is released under the MIT License.

## 🤝 Contributing
1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📧 Contact
For questions or support, please open an issue on GitHub.
