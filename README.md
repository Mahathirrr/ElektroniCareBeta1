# ElektroniCare

ElektroniCare is an Android application that connects users with electronic repair services. Users can request repairs for various electronic devices, track repair status, and find technicians.

## Features

- User authentication (login/register)
- Dashboard with recent repairs
- Repair history
- Service catalog
- User profile management
- Firebase integration for real-time data

## Setup Instructions

### Prerequisites

- Android Studio Arctic Fox (2020.3.1) or newer
- JDK 11 or newer
- Android SDK 29+
- Firebase account

### Firebase Setup

This application requires Firebase for authentication and data storage. Please follow the instructions in [FIREBASE_SETUP.md](FIREBASE_SETUP.md) to set up Firebase for this project.

### Running the Application

1. Clone the repository:
   ```
   git clone https://github.com/agus-septiawan/ElektroniCareBeta1.git
   ```

2. Open the project in Android Studio.

3. Make sure you have added the `google-services.json` file to the app directory.

4. Sync the project with Gradle files.

5. Run the application on an emulator or physical device.

### Data Migration

To populate your Firebase database with sample data, you can use the provided migration script:

1. Install the required Python packages:
   ```
   pip install firebase-admin
   ```

2. Generate a Firebase Admin SDK private key:
   - Go to the Firebase Console
   - Navigate to Project Settings > Service Accounts
   - Click "Generate new private key"
   - Save the JSON file securely

3. Run the migration script:
   ```
   python firebase_migration.py --credentials path/to/serviceAccountKey.json --user-id YOUR_FIREBASE_USER_ID
   ```

   Replace `YOUR_FIREBASE_USER_ID` with the UID of your authenticated user (you can find this in the Firebase Authentication console after creating a user).

## Project Structure

- `app/src/main/java/com/example/elektronicarebeta1/`
  - `firebase/` - Firebase helper classes
  - `models/` - Data models
  - `*.kt` - Activity classes

- `app/src/main/res/`
  - `layout/` - XML layout files
  - `drawable/` - Icons and drawable resources
  - `values/` - Strings, colors, and styles

## Implementation Details

### Firebase Integration

The application uses Firebase for:
- Authentication (Email/Password)
- Firestore Database (user data, repairs, services, technicians)

### Dashboard

The dashboard displays:
- User information
- Recent repairs
- Categories of services
- Bottom navigation to other sections

### Navigation

The application has four main sections:
1. Home (Dashboard)
2. History (Repair history)
3. Services (Available services)
4. Profile (User profile)

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.