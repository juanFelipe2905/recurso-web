package com.example.practica4.interfaces;

import java.util.List;

import retrofit2.http.GET;
public interface api {
    @GET("user")
    call<List<User>> getAllUsers();

    @GET("user/{id}")
    call<User> getUser(@Path("id")int id);
}
