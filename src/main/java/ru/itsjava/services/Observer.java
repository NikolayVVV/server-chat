package ru.itsjava.services;

public interface Observer {//Подписчик
    void notifyMe(String message);//пишет сообщение данному клиенту
}
