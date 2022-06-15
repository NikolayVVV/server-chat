package ru.itsjava.dao;

import ru.itsjava.domain.User;

public interface UserDao {
    User findByNameAndPassword(String name, String password);

    void creationUser(String name, String password);

    int NameAndPasswordAreNotFound(String name, String password);
}
