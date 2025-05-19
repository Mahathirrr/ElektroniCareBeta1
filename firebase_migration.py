#!/usr/bin/env python3

import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
import json
import datetime
import argparse
import os
import sys

def initialize_firebase(credentials_path):
    """Initialize Firebase with the provided credentials."""
    try:
        cred = credentials.Certificate(credentials_path)
        firebase_admin.initialize_app(cred)
        db = firestore.client()
        print("Firebase initialized successfully.")
        return db
    except Exception as e:
        print(f"Error initializing Firebase: {e}")
        sys.exit(1)

def create_mock_data():
    """Create mock data for Firebase collections."""
    # Current timestamp
    now = datetime.datetime.now()
    
    # Users collection
    users = [
        {
            "id": "user1",  # This will be replaced with the actual user ID
            "fullName": "John Doe",
            "email": "john.doe@example.com",
            "phone": "+62812345678",
            "address": "Jl. Sudirman No. 123, Jakarta",
            "profileImageUrl": "",
            "createdAt": now
        }
    ]
    
    # Technicians collection
    technicians = [
        {
            "fullName": "Ahmad Rizki",
            "specialization": "Phone Repair Specialist",
            "experience": 5,
            "rating": 4.8,
            "totalReviews": 124,
            "bio": "Experienced phone repair technician with expertise in iPhone and Samsung devices.",
            "profileImageUrl": "https://randomuser.me/api/portraits/men/1.jpg",
            "location": "Jakarta Selatan",
            "contactNumber": "+6281234567890",
            "email": "ahmad.rizki@example.com",
            "availableDays": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"],
            "createdAt": now
        },
        {
            "fullName": "Siti Nurhayati",
            "specialization": "Laptop Repair Expert",
            "experience": 7,
            "rating": 4.9,
            "totalReviews": 98,
            "bio": "Certified laptop repair technician with over 7 years of experience in fixing various laptop brands.",
            "profileImageUrl": "https://randomuser.me/api/portraits/women/2.jpg",
            "location": "Jakarta Pusat",
            "contactNumber": "+6281234567891",
            "email": "siti.nurhayati@example.com",
            "availableDays": ["Monday", "Wednesday", "Friday", "Saturday"],
            "createdAt": now
        },
        {
            "fullName": "Budi Santoso",
            "specialization": "TV & Electronics Repair",
            "experience": 10,
            "rating": 4.7,
            "totalReviews": 156,
            "bio": "Specialized in repairing TVs, home theaters, and other electronic devices.",
            "profileImageUrl": "https://randomuser.me/api/portraits/men/3.jpg",
            "location": "Jakarta Barat",
            "contactNumber": "+6281234567892",
            "email": "budi.santoso@example.com",
            "availableDays": ["Tuesday", "Thursday", "Saturday", "Sunday"],
            "createdAt": now
        },
        {
            "fullName": "Dewi Lestari",
            "specialization": "Printer & Scanner Repair",
            "experience": 4,
            "rating": 4.6,
            "totalReviews": 67,
            "bio": "Expert in printer and scanner repairs for all major brands.",
            "profileImageUrl": "https://randomuser.me/api/portraits/women/4.jpg",
            "location": "Jakarta Timur",
            "contactNumber": "+6281234567893",
            "email": "dewi.lestari@example.com",
            "availableDays": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"],
            "createdAt": now
        },
        {
            "fullName": "Eko Prasetyo",
            "specialization": "All-around Electronics Technician",
            "experience": 8,
            "rating": 4.5,
            "totalReviews": 112,
            "bio": "Versatile technician capable of fixing various electronic devices.",
            "profileImageUrl": "https://randomuser.me/api/portraits/men/5.jpg",
            "location": "Jakarta Utara",
            "contactNumber": "+6281234567894",
            "email": "eko.prasetyo@example.com",
            "availableDays": ["Wednesday", "Thursday", "Friday", "Saturday", "Sunday"],
            "createdAt": now
        }
    ]
    
    # Services collection
    services = [
        {
            "name": "Screen Replacement",
            "description": "Replace damaged or cracked screens with high-quality parts.",
            "category": "Phone",
            "basePrice": 750000.0,
            "estimatedTime": "1-2 hours",
            "imageUrl": "https://example.com/images/screen_replacement.jpg",
            "createdAt": now
        },
        {
            "name": "Battery Replacement",
            "description": "Replace old or damaged batteries with new ones for better performance.",
            "category": "Phone",
            "basePrice": 350000.0,
            "estimatedTime": "30-60 minutes",
            "imageUrl": "https://example.com/images/battery_replacement.jpg",
            "createdAt": now
        },
        {
            "name": "Water Damage Repair",
            "description": "Fix water-damaged devices with thorough cleaning and component replacement.",
            "category": "Phone",
            "basePrice": 850000.0,
            "estimatedTime": "1-3 days",
            "imageUrl": "https://example.com/images/water_damage.jpg",
            "createdAt": now
        },
        {
            "name": "Laptop Screen Replacement",
            "description": "Replace damaged laptop screens with compatible high-quality displays.",
            "category": "Laptop",
            "basePrice": 1200000.0,
            "estimatedTime": "1-2 hours",
            "imageUrl": "https://example.com/images/laptop_screen.jpg",
            "createdAt": now
        },
        {
            "name": "Laptop Keyboard Replacement",
            "description": "Replace damaged or non-functioning laptop keyboards.",
            "category": "Laptop",
            "basePrice": 550000.0,
            "estimatedTime": "1 hour",
            "imageUrl": "https://example.com/images/laptop_keyboard.jpg",
            "createdAt": now
        },
        {
            "name": "TV Panel Repair",
            "description": "Fix or replace damaged TV panels for better viewing experience.",
            "category": "TV",
            "basePrice": 1500000.0,
            "estimatedTime": "1-2 days",
            "imageUrl": "https://example.com/images/tv_panel.jpg",
            "createdAt": now
        },
        {
            "name": "Printer Maintenance",
            "description": "Clean and maintain printers for optimal performance.",
            "category": "Printer",
            "basePrice": 250000.0,
            "estimatedTime": "1 hour",
            "imageUrl": "https://example.com/images/printer_maintenance.jpg",
            "createdAt": now
        },
        {
            "name": "Data Recovery",
            "description": "Recover lost data from damaged storage devices.",
            "category": "Laptop",
            "basePrice": 800000.0,
            "estimatedTime": "1-3 days",
            "imageUrl": "https://example.com/images/data_recovery.jpg",
            "createdAt": now
        }
    ]
    
    # Repairs collection (will be populated after technicians and services are created)
    repairs = []
    
    return {
        "users": users,
        "technicians": technicians,
        "services": services,
        "repairs": repairs
    }

def migrate_data(db, data, user_id=None):
    """Migrate data to Firebase."""
    # Store document references for later use
    tech_refs = {}
    service_refs = {}
    
    # Migrate technicians
    print("Migrating technicians...")
    for tech in data["technicians"]:
        # Convert datetime to Firestore timestamp
        tech["createdAt"] = firestore.SERVER_TIMESTAMP
        
        # Add to Firestore
        tech_ref = db.collection("technicians").add(tech)
        tech_id = tech_ref[1].id
        tech_refs[tech["fullName"]] = tech_id
        print(f"  Added technician: {tech['fullName']} (ID: {tech_id})")
    
    # Migrate services
    print("\nMigrating services...")
    for service in data["services"]:
        # Convert datetime to Firestore timestamp
        service["createdAt"] = firestore.SERVER_TIMESTAMP
        
        # Add to Firestore
        service_ref = db.collection("services").add(service)
        service_id = service_ref[1].id
        service_refs[service["name"]] = service_id
        print(f"  Added service: {service['name']} (ID: {service_id})")
    
    # Migrate user if user_id is provided
    if user_id:
        print(f"\nMigrating user data for user ID: {user_id}")
        user_data = data["users"][0]
        user_data["createdAt"] = firestore.SERVER_TIMESTAMP
        
        # Set user document with the provided user_id
        db.collection("users").document(user_id).set(user_data)
        print(f"  Added user: {user_data['fullName']} (ID: {user_id})")
        
        # Create sample repairs for the user
        print("\nCreating sample repairs for the user...")
        
        # Sample repair 1 - Completed
        repair1 = {
            "userId": user_id,
            "deviceType": "Phone",
            "deviceModel": "iPhone 13",
            "issueDescription": "Cracked screen needs replacement",
            "serviceId": service_refs.get("Screen Replacement"),
            "technicianId": next((id for name, id in tech_refs.items() if "Phone" in data["technicians"][list(tech_refs.keys()).index(name)]["specialization"]), None),
            "status": "completed",
            "estimatedCost": 750000.0,
            "scheduledDate": datetime.datetime.now() - datetime.timedelta(days=10),
            "completedDate": datetime.datetime.now() - datetime.timedelta(days=9),
            "location": "Fresh Teknik Service Center",
            "createdAt": datetime.datetime.now() - datetime.timedelta(days=12)
        }
        
        # Sample repair 2 - In Progress
        repair2 = {
            "userId": user_id,
            "deviceType": "Laptop",
            "deviceModel": "MacBook Pro 2022",
            "issueDescription": "Battery drains quickly and needs replacement",
            "serviceId": service_refs.get("Battery Replacement"),
            "technicianId": next((id for name, id in tech_refs.items() if "Laptop" in data["technicians"][list(tech_refs.keys()).index(name)]["specialization"]), None),
            "status": "in_progress",
            "estimatedCost": 950000.0,
            "scheduledDate": datetime.datetime.now() + datetime.timedelta(days=1),
            "completedDate": None,
            "location": "ElektroniCare Service Center",
            "createdAt": datetime.datetime.now() - datetime.timedelta(days=2)
        }
        
        # Add repairs to Firestore
        repair_ref1 = db.collection("repairs").add(repair1)
        print(f"  Added repair: {repair1['deviceModel']} - {repair1['issueDescription']} (ID: {repair_ref1[1].id})")
        
        repair_ref2 = db.collection("repairs").add(repair2)
        print(f"  Added repair: {repair2['deviceModel']} - {repair2['issueDescription']} (ID: {repair_ref2[1].id})")
    
    print("\nMigration completed successfully!")

def main():
    parser = argparse.ArgumentParser(description="Migrate data to Firebase for ElektroniCare app")
    parser.add_argument("--credentials", required=True, help="Path to the Firebase service account credentials JSON file")
    parser.add_argument("--user-id", help="Firebase user ID to associate with the sample data")
    
    args = parser.parse_args()
    
    # Check if credentials file exists
    if not os.path.exists(args.credentials):
        print(f"Error: Credentials file not found at {args.credentials}")
        sys.exit(1)
    
    # Initialize Firebase
    db = initialize_firebase(args.credentials)
    
    # Create mock data
    data = create_mock_data()
    
    # Migrate data to Firebase
    migrate_data(db, data, args.user_id)

if __name__ == "__main__":
    main()