# Group Dinner Decision Android App

## Basic App Functionality
This is an app whose purpose is to help groups of friends decide on where to eat dinner together. The app asks users to create an accout and login using firebase.<br /> 
Then, users can scroll through all the different groups or create their own group. Each group is has a password and only the group's creator can remove a group.<br /> 
Once in a group with friends, users have the ability to add restaurants to the collective group and vote on the ones they like best.<br /> 
The app searches the Zomato restaurant database and allows users to search restaurants near them using a number of criterea:
* Name of the restaurant
* Cuisine type
* Street name

Overall, as more restaurants are added and voted on, it should help users and their friends see which would be the best option for dinner.

## Creation of the App
The group dinner app was created in Android Studio, and can be run on android devices with a minimum sdk version of 19.<br /> 
The authentication and the database are done through [Firebase](https://firebase.google.com/).<br />
Restaurant information is retrieved from the Zomato API. <br />
The app requires access to the phones location so that it can search for restaurants near the user.
