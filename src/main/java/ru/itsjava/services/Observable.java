package ru.itsjava.services;

public interface Observable {//издательство
    void addObserver(Observer observer);

    void deleteObserver(Observer observer);

    void notifyObservers(String message);

    void notifyObserverExpectMe(String message, Observer observer);
}
