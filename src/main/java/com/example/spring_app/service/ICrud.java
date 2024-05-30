package com.example.spring_app.service;

import java.util.List;

public interface ICrud<T> {

    List<T> getAll();

    T getSingle(int id);

    T add(T object);

    T update(int id, T object);

    T delete(int id);
}
