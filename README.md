# PolyHome - Mobile Application for Home Automation  

## Project Description  
PolyHome is an Android home automation application designed to control connected devices in a house, such as rolling shutters, lights, and garage doors. This application leverages a pre-existing API to provide an intuitive interface with features tailored to user needs.  

**Link to the Virtual Model**: [PolyHome - Model](https://polyhome.lesmoulinsdudev.com?houseId=<houseId>)  

## Implemented Features  
### Core Features  
1. **User Account**  
   - Create an account with login and password.  
   - Login using an authentication token stored locally to avoid re-entering credentials after the first login.  
   - Log out from the application.

2. **Home Page**  
   - Displays the number of houses the user can control *(bonus)*.  
   - Displays the number of users with access to the user's house *(bonus)*.  

3. **House List**  
   - Shows houses associated with the user, whether as an owner or guest *(bonus)*.  

4. **User Management**  
   - List users who currently have access to the house *(bonus)*.  
   - Grant access to a house to another user *(bonus)*.  
   - Revoke access for specific users *(bonus)*.  

5. **Connected Devices**  
   - Display the list of devices in a house.  
   - Send commands to devices (lights, shutters, doors, etc.).  
   - Control devices in houses where the user has access *(bonus)*.  

### Bonus Features  
1. **Predefined Modes**  
   - **Economy Mode**: Turns off all lights to reduce energy consumption.  
   - **Night/Exit Mode**: Turns off all lights and closes all shutters and doors.  
   - **Emergency Mode**: Opens all doors and shutters and turns off all lights.  
   - **Alert Mode**: Blinks all lights and opens all shutters and doors.  

2. **Enhanced User Interface**  
   - Informational buttons provide contextual explanations for actions.  
   - Ergonomic and user-friendly design for seamless navigation.  

## Installation  

1. Open the project in **Android Studio**.  

2. Run the application on an **emulator** or a **physical device**.  

3. Launch the app and either:  
   - **Create your own account**. or
   - **Use the guest account**:  
     - Login: `guest`  
     - Password: `guest`  

4. Make sure to open the virtual model of the house in a browser using the following link:  

[https://polyhome.lesmoulinsdudev.com?houseId=`<houseId>`](https://polyhome.lesmoulinsdudev.com?houseId=<houseId>) Replace `<houseId>` with the corresponding house ID you want to control.

## Screenshots

### Login and Register
<img src="./Screenshots/Login.jpg" alt="Login Page" width="200"/>          <img src="./Screenshots/Register.jpg" alt="Register Page" width="200"/>

### Home Page
<img src="./Screenshots/HomePage.jpg" alt="Home Page" width="200"/>          <img src="./Screenshots/Drawer.jpg" alt="Drawer" width="200"/>

### Manage Users and Devices
<img src="./Screenshots/ManageUsers.jpg" alt="Manage Users" width="200"/>          <img src="./Screenshots/ManageDevices.jpg" alt="Manage Devices" width="200"/>

## Author  

This project was developed as part of the **Mobile Programming** course at the **University of Burgundy**.  

- **Name**: Noureddine-T
