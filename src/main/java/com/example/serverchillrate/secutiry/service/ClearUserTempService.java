package com.example.serverchillrate.secutiry.service;

public interface ClearUserTempService {
    /*
    удаляет временных пользователей которые не подтвердили почту
    в течение часа
     */
    void clearNotValidUser();
}
