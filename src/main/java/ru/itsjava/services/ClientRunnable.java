package ru.itsjava.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itsjava.Application;
import ru.itsjava.dao.MessageDao;
import ru.itsjava.dao.UserDao;
import ru.itsjava.domain.User;
import ru.itsjava.exception.UserNotFoundException;

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
    private final MessageDao messageDao;
    private static final Logger log = LoggerFactory.getLogger(Application.class);

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
                notifyMe("Вы успешно авторизованы, для просмотра последних ссобщений введите last messages");
                while ((messageFromClient = bufferedReader.readLine()) != null) {//бесконечно вычитываем сообщения
                    //от клиента и отправляем их на сервер
                    System.out.println(user.getName() + ":" + messageFromClient);//здесь сообщения приходят на сервер
                    if (messageFromClient.equals("last messages")) {
                        notifyMe(messageDao.showMessages());
                    }
//                    messageDao.writeMessage(messageFromClient, user.getName(), "all");
//                serverService.notifyObservers(user.getName() + ":" + messageFromClient);
                    serverService.notifyObserverExpectMe(user.getName() + ":" + messageFromClient, this);
                }
            } else {
                log.debug("Был введен неверный пароль");
                notifyMe("Неправильный логин или пароль...Попробуйте еще раз");
            }
        } else if (bufferedReader.readLine().equals("3")) {
            if (authorizationPrivateCorrespondence(bufferedReader)) {
                serverService.addObserverInPrivateCorrespondence(this);
                notifyMe("Вы успешно авторизованы");
                while ((messageFromClient = bufferedReader.readLine()) != null) {
                    System.out.println(user.getName() + ":" + messageFromClient);//вывод сообщений на сервере
                    serverService.notifyObserversInPrivateCorrespondenceExpectMe(
                            user.getName() + ":" + messageFromClient, this);
                }
            } else {
                notifyMe("Вы не имеете доступ к личной переписке");
            }
        } else if (bufferedReader.readLine().equals("2")) {
            if (registration(bufferedReader))
                notifyMe("Пользователь зарегистрирован");
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
                try {
                    user = userDao.findByNameAndPassword(login, password);
                    return true;
                } catch (UserNotFoundException userNotFoundException) {
                    System.out.println("don't find user in DB");
                    return false;
                }
            }
        }
        return false;
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
    private boolean authorizationPrivateCorrespondence(BufferedReader bufferedReader) {
        String authorizationMessage;
        while ((authorizationMessage = bufferedReader.readLine()) != null) {
            //!autho!login:password
            if (authorizationMessage.startsWith("!autho!")) {
                String login = authorizationMessage.substring(7).split(":")[0];
                String password = authorizationMessage.substring(7).split(":")[1];
                try {
                    user = userDao.findByNameAndPassword(login, password);
                    if (user.getName().equals("U2") || user.getName().equals("USER"))
                        return true;
                } catch (UserNotFoundException userNotFoundException) {
                    System.out.println("don't find user in DB");
                    return false;
                }
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
