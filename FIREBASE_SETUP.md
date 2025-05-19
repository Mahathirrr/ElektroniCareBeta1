# Firebase Setup for ElektroniCare

This document provides a comprehensive guide on how to set up Firebase for the ElektroniCare application.

## Prerequisites

1. A Google account
2. Android Studio installed
3. ElektroniCare project cloned to your local machine

## Step 1: Create a Firebase Project

1. Go to the [Firebase Console](https://console.firebase.google.com/)
2. Click on "Add project"
3. Enter "ElektroniCare" as the project name
4. Follow the prompts to complete project creation
   - You can enable Google Analytics if you want
   - Accept the terms and conditions
   - Click "Create project"

## Step 2: Register Your Android App with Firebase

1. In the Firebase console, click on the Android icon to add an Android app
2. Enter the package name: `com.example.elektronicarebeta1`
3. Enter a nickname (optional): "ElektroniCare"
4. Enter the SHA-1 debug signing certificate (optional for now, but required for Google Sign-In)
   - You can get this by running the following command in your project directory:
     ```
     ./gradlew signingReport
     ```
5. Click "Register app"

## Step 3: Download and Add the Configuration File

1. Download the `google-services.json` file
2. Place it in the app-level directory of your project (`app/`)
3. Make sure the file is in the correct location: `/workspace/ElektroniCareBeta1/app/google-services.json`

## Step 4: Add Firebase SDK to Your Project

This step is already completed in the project. The necessary dependencies are already added to the build.gradle files.

## Step 5: Set Up Firebase Authentication

1. In the Firebase console, go to "Authentication"
2. Click on "Get started"
3. Enable the following sign-in methods:
   - Email/Password
   - Google Sign-In (optional)
4. For Google Sign-In, you'll need to configure the OAuth consent screen in the Google Cloud Console

## Step 6: Set Up Firestore Database

1. In the Firebase console, go to "Firestore Database"
2. Click on "Create database"
3. Choose "Start in production mode" or "Start in test mode" (for development)
4. Select a location for your database (choose the one closest to your target users)
5. Click "Enable"

## Step 7: Set Up Security Rules for Firestore

1. In the Firestore Database section, go to the "Rules" tab
2. Update the rules to secure your database:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow authenticated users to read and write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Allow authenticated users to read and write their own repairs
    match /repairs/{repairId} {
      allow read, write: if request.auth != null && resource.data.userId == request.auth.uid;
      allow create: if request.auth != null && request.resource.data.userId == request.auth.uid;
    }
    
    // Allow authenticated users to read technicians and services
    match /technicians/{technicianId} {
      allow read: if request.auth != null;
    }
    
    match /services/{serviceId} {
      allow read: if request.auth != null;
    }
  }
}
```

## Step 8: Populate Firestore with Initial Data

The application includes a `FirebaseDataSeeder` class that will automatically populate the Firestore database with mock data when a user logs in. This includes:

1. User profile data
2. Technicians
3. Services
4. Sample repair requests

You can manually seed the data using the Firebase console if needed:

1. Go to Firestore Database
2. Click on "Start collection"
3. Create the following collections:
   - `users`
   - `technicians`
   - `services`
   - `repairs`
4. Add documents to each collection based on the models defined in the app

## Step 9: Test the Firebase Integration

1. Run the application
2. Register a new user or log in with an existing user
3. Verify that the dashboard loads with user data
4. Navigate to the History, Services, and Profile sections to ensure data is being fetched correctly

## Troubleshooting

If you encounter any issues with Firebase integration:

1. Verify that the `google-services.json` file is in the correct location
2. Check that all Firebase dependencies are correctly added to the build.gradle files
3. Ensure that the package name in the Firebase console matches the one in your app
4. Check the Logcat in Android Studio for any Firebase-related errors

## Additional Firebase Features (Optional)

You can also set up:

1. **Firebase Storage**: For storing user profile images and repair photos
2. **Firebase Cloud Messaging**: For push notifications
3. **Firebase Analytics**: For tracking user behavior
4. **Firebase Crashlytics**: For crash reporting

## Firebase Structure

The application uses the following Firestore collections:

1. **users**: Stores user profile information
   - Fields: fullName, email, phone, address, profileImageUrl, createdAt

2. **repairs**: Stores repair requests
   - Fields: userId, deviceType, deviceModel, issueDescription, serviceId, technicianId, status, estimatedCost, scheduledDate, completedDate, location, createdAt

3. **technicians**: Stores technician information
   - Fields: fullName, specialization, experience, rating, totalReviews, bio, profileImageUrl, location, contactNumber, email, availableDays, createdAt

4. **services**: Stores service information
   - Fields: name, description, category, basePrice, estimatedTime, imageUrl, createdAt