package ru.itsjava.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.itsjava.domain.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@RequiredArgsConstructor  //реализация runnable чтобы реализовать метод run
public class ClientRunnable implements Runnable, Observer {//клиент заходит и попадает в отдельный поток
    private final Socket socket;//финальное поле, потому что у каждого клиента оно будет свое
    private final ServerService serverService;
    private User user;//под каждого clientrunnable будет свой user

    @SneakyThrows
    @Override
    public void run() {
        System.out.println("Client connected");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));//здесь мы
        //считываем сообщения от клиента
        String messageFromClient;
        if (authorization(bufferedReader)) {
            serverService.addObserver(this);
            while ((messageFromClient = bufferedReader.readLine()) != null) {//бесконечно вычитываем сообщения
                //от клиента и отправляем их на сервер
                System.out.println(user.getName() + ":" + messageFromClient);//здесь сообщения приходят на сервер
//                serverService.notifyObservers(user.getName() + ":" + messageFromClient);
                serverService.notifyObserverExpectMe(user.getName() + ":" + messageFromClient,this);
            }
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
                user = new User(login, password);
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
