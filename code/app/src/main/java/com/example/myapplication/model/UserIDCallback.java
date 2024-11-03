package com.example.myapplication.model;

/*Interface used for retrieving userID from User class
* the problem was that the event would be created before the retrieval user ID is done
* so onUserIDLoaded, it waits for the userId to be loaded then the event is created
* this is from chatGPT*/

public interface UserIDCallback {
    void onUserIDLoaded(String userID);
    void onNewUserCreated();
}
