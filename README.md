# Distributed Systems Tracking App

This project is a distributed systems tracking application similar to Strava. It consists of a backend server implemented in Java and a frontend mobile application developed using Java in Android Studio.

## Features

- **Tracking:** Users can track their outdoor activities such as running, cycling, or walking using the mobile application. The app captures the user's location, distance, speed, and other relevant data.
- **Achievements and Challenges:** Users can view their statistics and compare them with the average statistics of other users.
- **Importing GPX Files:** Users can import GPX files recorded from other devices or applications into our tracking app. 
    This allows them to analyze and store their past activities within our system. The imported GPX files are parsed and the relevant data such as route, distance, duration, and elevation are extracted.
- **Exporting GPX Files:** Users can export their activity data in the form of GPX files. This allows them to share their activities with others or use the data in external applications. 
    The exported GPX files contain all the necessary information about the activity, making it compatible with various GPS and tracking applications.
    
## Technologies Used

- **Backend:**
  - Java: The backend server is implemented using Java, providing the business logic and handling requests from the frontend.

- **Frontend:**
  - Java Android Studio: The frontend mobile application is developed using Java in Android Studio, targeting Android devices.
  - Google Maps API: We integrate the Google Maps API to display the user's location, track their activities, and show routes.
