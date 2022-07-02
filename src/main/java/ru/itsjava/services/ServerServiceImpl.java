package ru.itsjava.services;

import lombok.SneakyThrows;
import ru.itsjava.dao.MessageDao;
import ru.itsjava.dao.MessageDaoImpl;
import ru.itsjava.dao.UserDao;
import ru.itsjava.dao.UserDaoImpl;
import ru.itsjava.domain.User;
import ru.itsjava.utils.Props;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerServiceImpl implements ServerService {//Это Издатель, а подписчики все те, кто
    //Client Runnable
    public final static int PORT = 8081;
    public final List<Observer> observers = new ArrayList<>();//лист всех тех, кто находится в чате
    public final List<Observer> observersInPrivateCorrespondence = new ArrayList<>();//лист всех тех, кто находится в чате
    private final UserDao userDao = new UserDaoImpl(new Props());
    private final MessageDao messageDao = new MessageDaoImpl(new Props());
    private User user;


    @SneakyThrows
    @Override
    public void start() {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("== SERVER STARTS ==");

        while (true) {//в бесконечном цикле
            Socket socket = serverSocket.accept();//сделать accept, чт
            if (socket != null) {
                Thread thread = new Thread(new ClientRunnable(socket, this, userDao, messageDao));
                thread.start();//метод старт запускает новый поток, поэтому когда стартуем запускается метод run
            }
        }
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void addObserverInPrivateCorrespondence(Observer observer) {
        observersInPrivateCorrespondence.add(observer);
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
            obs.notifyMe(message);
        }
    }

    @Override
    public void notifyObserversInPrivateCorrespondenceExpectMe(String message, Observer observer) {
        for (Observer obs : observersInPrivateCorrespondence) {
            if (obs.equals(observer)) {
                continue;
            }
            obs.notifyMe(message);
        }
    }


//    @Override  // делал этот метод для того, чтобы понять как выглядят обсерверы
//    public void showAllObservers() {
//        for (Observer obs : observers) {
//            System.out.println(obs);
//        }
//    }
//
//
//    public void notifySomeObserver(String message, Observer observer) {//этот метод делал когда
    //игрался с личной перепиской, но он так и не пригодился пока
//        for (Observer obs : observers) {//мы пробегаемся по списку с подписчиками
//            if (!obs.equals(observer)) {//если наш Observer равен тому Observer-у,
//                // который в списке, то мы ему направляем сообщение
//                continue;
//            }
//            obs.notifyMe(message);
//        }
//    }


}
