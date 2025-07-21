## Overview

This project is a **Healthcare Online Platform** built using **ASP.NET Core Razor Pages** and **Entity Framework Core**. It allows users to search for doctors, book medical appointments, manage their health records, and access health-related information.

## Features

- **User Account Management**
  - Register and manage accounts via email or phone number.
  - Profile management for patients and doctors.
- **Doctor & Clinic Search**

  - Search for doctors by name, specialty, clinic, location, ratings, and pricing.
  - Search for healthcare facilities based on services and location.

- **Online Appointment Booking**

  - Choose a doctor or clinic, select an available time slot, confirm, and optionally make a payment.
  - Cancel or reschedule appointments easily.

- **Ratings & Reviews**

  - Users can leave reviews and ratings (1-5 stars) after using the service.
  - Public review system for transparency.

- **Online Medical Records**

  - Store medical history and diagnosis details.
  - Share medical records securely with doctors.
  - Upload and manage medical test results and diagnostic images.

- **Healthcare Information Portal**
  - Articles on common diseases, prevention methods, and treatments.
  - Updates on certified healthcare facilities.

## Technologies Used

- **ASP.NET Core 8 Razor Pages**
- **Entity Framework Core**
- **Bootstrap 5** for styling
- **JavaScript (Fetch API)** for dynamic interactions

## Architecture

![image](https://github.com/user-attachments/assets/1ecd69c9-814a-4648-8a8b-b0aded0412b7)

This project follows the **Three-Layer Architecture** pattern:

1. **BusinessObjects Layer**: Contains entity classes and business models.
2. **DataAccess Layer**: Handles database operations using Entity Framework Core.
3. **Services Layer**: Implements business logic and interacts with the DataAccess layer.
4. **WebRazorPage Layer**: The presentation layer using Razor Pages.

## Installation

### Prerequisites

- .NET 8 SDK
- SQL Server
- Visual Studio / VS Code

### Steps

1. **Clone the repository**
   ```sh
   git clone <repository-url>
   cd <project-folder>
   ```
2. **Setup the database**
   - Update `appsettings.json` with your SQL Server connection string.
   - Run migrations:
     ```sh
     dotnet ef database update
     ```
3. **Run the application**
   ```sh
   dotnet run
   ```
4. Open your browser and navigate to `https://localhost:7048/` (or the specified port).

## File Structure

```
📁 HealthcarePlatform
│── 📂 BusinessObjects  # Entity and domain models
│── 📂 DataAccess       # Database access layer
│── 📂 Business         # Business logic layer
│── 📂 WebRazorPage     # Presentation layer (Razor Pages)
│── appsettings.json
│── Program.cs
│── README.md
```

## License

This project is open-source and available under the MIT License.
