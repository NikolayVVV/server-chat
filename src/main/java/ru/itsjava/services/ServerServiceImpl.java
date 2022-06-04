package ru.itsjava.services;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerServiceImpl implements ServerService {//Это Издатель, а подписчики все те, кто
    //Client Runnable
    public final static int PORT = 8081;
    public final List<Observer> observers = new ArrayList<>();//лист всех тех, кто находится в чате


    @SneakyThrows
    @Override
    public void start() {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("== SERVER STARTS ==");
        while (true) {//в бесконечном цикле
            Socket socket = serverSocket.accept();//сделать accept, чт
            if (socket != null) {
                Thread thread = new Thread(new ClientRunnable(socket, this));
                thread.start();//метод старт запускает новый поток, поэтому когда стартуем запускается метод run
            }
        }
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void deleteObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.notifyMe(message);
        }
    }

    @Override
    public void notifyObserverExpectMe(String message, Observer observer) {
        for (Observer obs : observers) {//мы пробегаемся по списку с подписчиками
            if (obs.equals(observer)) {//если наш Observer равен тому Observer-у,
                // который в списке, то мы его пропускаем
                continue;
            }
            observer.notifyMe(message);
        }
    }


}
