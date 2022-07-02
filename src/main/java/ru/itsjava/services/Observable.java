package ru.itsjava.services;

public interface Observable {//издательство

    void addObserver(Observer observer);

    void addObserverInPrivateCorrespondence(Observer observer);

    void deleteObserver(Observer observer);

    void notifyObservers(String message);

    void notifyObserversInPrivateCorrespondenceExpectMe(String message, Observer observer);

    void notifyObserverExpectMe(String message, Observer observer);

//    void showAllObservers();
//
//    void notifySomeObserver(String message, Observer observer);

}
