package com.example.smsweb.api.generic;

import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface IRepository<T> {
    void save(T t);
    void update(T t);
    void delete(int id);
    List<T> findAll();
    T findOne(int id);
}
