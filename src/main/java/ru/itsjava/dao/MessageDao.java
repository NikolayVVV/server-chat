package ru.itsjava.dao;

import ru.itsjava.domain.Message;

public interface MessageDao {
    void writeMessage(String message, String fromUser, String toUser);

    String showMessages();
}
