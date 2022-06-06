package ru.itsjava;

import ru.itsjava.dao.UserDao;
import ru.itsjava.dao.UserDaoImpl;
import ru.itsjava.domain.User;
import ru.itsjava.services.ServerService;
import ru.itsjava.services.ServerServiceImpl;
import ru.itsjava.utils.Props;


public class Application {


    public static void main(String[] args) {
        ServerService serverService = new ServerServiceImpl();
        serverService.start();

//        Props props = new Props();
//        System.out.println("props.getValue(\"db.url\") = " + props.getValue("db.url"));

//        UserDao userDao = new UserDaoImpl(new Props());
//        System.out.println("userDao.findByNameAndPassword(\"U1\", \"P1\") = "
//                + userDao.findByNameAndPassword("U1", "P1"));


    }
}
