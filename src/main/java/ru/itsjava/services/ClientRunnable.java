package ru.itsjava.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.itsjava.dao.UserDao;
import ru.itsjava.dao.UserDaoImpl;
import ru.itsjava.domain.User;
import ru.itsjava.utils.Props;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@RequiredArgsConstructor  //реализация runnable чтобы реализовать метод run
public class ClientRunnable implements Runnable, Observer {//клиент заходит и попадает в отдельный поток
    private final Socket socket;//финальное поле, потому что у каждого клиента оно будет свое
    private final ServerService serverService;
    private User user;//под каждого clientrunnable будет свой user
    private final UserDao userDao;

    @SneakyThrows
    @Override
    public void run() {
        System.out.println("Client connected");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));//здесь мы
        //считываем сообщения от клиента
        String messageFromClient;

        if (bufferedReader.readLine().equals("1")) {
            if (authorization(bufferedReader)) {
                serverService.addObserver(this);
                while ((messageFromClient = bufferedReader.readLine()) != null) {//бесконечно вычитываем сообщения
                    //от клиента и отправляем их на сервер
                    System.out.println(user.getName() + ":" + messageFromClient);//здесь сообщения приходят на сервер
//                serverService.notifyObservers(user.getName() + ":" + messageFromClient);
                    serverService.notifyObserverExpectMe(user.getName() + ":" + messageFromClient, this);
                }
            } else if (nameAndPasswordAreNotFound(bufferedReader) == 0) {
                PrintWriter clientWriter = new PrintWriter(socket.getOutputStream());
                clientWriter.println("Неправильный логин или пароль");
                clientWriter.flush();
            }

        } else if (bufferedReader.readLine().equals("2")) {
            if (registration(bufferedReader))
                serverService.notifyObservers("User added");
        }
    }

    @SneakyThrows
    private boolean authorization(BufferedReader bufferedReader) {
        String authorizationMessage;
        while ((authorizationMessage = bufferedReader.readLine()) != null) {
            //!autho!login:password
            if (authorizationMessage.startsWith("!autho!")) {
                String login = authorizationMessage.substring(7).split(":")[0];
                String password = authorizationMessage.substring(7).split(":")[1];
                user = userDao.findByNameAndPassword(login, password);
                return true;
            }
        }
        return false;
    }

    @SneakyThrows
    private int nameAndPasswordAreNotFound(BufferedReader bufferedReader) {
        String authorizationMessage;
        while ((authorizationMessage = bufferedReader.readLine()) != null) {
            if (authorizationMessage.startsWith("!autho!")) {
                String login = authorizationMessage.substring(7).split(":")[0];
                String password = authorizationMessage.substring(7).split(":")[1];
                if (userDao.NameAndPasswordAreNotFound(login, password) == 0) {
                    return 0;
                }
            }
        }
        return -1;
    }

    @SneakyThrows
    private boolean registration(BufferedReader bufferedReader) {
        String registrationMessage;
        while ((registrationMessage = bufferedReader.readLine()) != null) {
            //!regis!login:password
            if (registrationMessage.startsWith("!regis!")) {
                String name = registrationMessage.substring(7).split(":")[0];
                String password = registrationMessage.substring(7).split(":")[1];
                userDao.creationUser(name, password);
                return true;
            }
        }
        return false;
    }

    @SneakyThrows
    @Override
    public void notifyMe(String message) {
        PrintWriter clientWriter = new PrintWriter(socket.getOutputStream());//мы отсылаем сообщения клиентам
        clientWriter.println(message);//сообщение, которое мы им пишем
        clientWriter.flush();
    }
}
