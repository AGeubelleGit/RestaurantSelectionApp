# Group Dinner Decision Android App
## Running the app
Simply clone the project and add a new file in the folder "ZomatoAPIFiles" called "Zomato.java" which looks like: <br/>
```
package edu.illinois.finalproject.ZomatoAPIFiles;
public class Zomato {

    private static final String APIKeyString = <ZOMATO APIKEY>;

    public static String getAPIKey() {
        return APIKeyString;
    }

}
```

## Basic App Functionality
This is an app whose purpose is to help groups of friends decide on where to eat dinner together. The app asks users to create an accout and login using firebase.<br /> 
Then, users can scroll through all the different groups or create their own group. Each group is has a password and only the group's creator can remove a group.<br /> 
Once in a group with friends, users have the ability to add restaurants to the collective group and vote on the ones they like best.<br /> 
The app searches the Zomato restaurant database and allows users to search restaurants near them using a number of criterea:
* Name of the restaurant
* Cuisine type
* Street name
* User's location

Overall, as more restaurants are added and voted on, it should help users and their friends see which would be the best option for dinner.

## Creation of the App
The group dinner app was created in Android Studio, and can be run on android devices with a minimum sdk version of 19.<br /> 
The authentication and the database are done through [Firebase](https://firebase.google.com/).<br />
Restaurant information is retrieved from the Zomato API. <br />
The app requires access to the phones location so that it can search for restaurants near the user.

## Images
Home page where you vote on restaurants<br/>
![Home Page](Images/HomeScreen.png?raw=true)<br/>
Search page where you search for restaurants by name/type/street<br/>
![Search Page](Images/SearchScreen.png?raw=true)<br/>
Search results page where you select the restaurant you searched for.<br/>
![Search Page](Images/Search%20Results.png?raw=true)<br/>
Groups page where you create/join groups and set passwords to your group<br/>
![Groups Page](Images/GroupsPage.png?raw=true)<br/>

