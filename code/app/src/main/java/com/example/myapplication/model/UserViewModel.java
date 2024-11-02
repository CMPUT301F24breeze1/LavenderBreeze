package com.example.myapplication.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<User> user = new MutableLiveData<>();

    public void setUser(User updatedUser) {
        user.setValue(updatedUser);
    }

    public LiveData<User> getUser() {
        return user;
    }
}
