package ru.itsjava.dao;

import lombok.AllArgsConstructor;
import ru.itsjava.domain.Message;
import ru.itsjava.exception.MessageNotFoundException;
import ru.itsjava.utils.Props;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class MessageDaoImpl implements MessageDao {
    private Props props;

    @Override
    public void writeMessage(String message, String fromUser, String toUser) {
        String insertSQL = "insert into schema_online_course.messages" +
                "(message, fromUser, toUser) values (?,?,?)";
        try (Connection connection = DriverManager.getConnection(
                props.getValue("db.url"),
                props.getValue("db.login"),
                props.getValue("db.password"));
        ) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(insertSQL);
            preparedStatement.setString(1, message);
            preparedStatement.setString(2, fromUser);
            preparedStatement.setString(3, toUser);

            int insertRows = preparedStatement.executeUpdate();
            System.out.println("Message written");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    @Override
    public String showMessages() {
        String insertSQL = "select * from schema_online_course.messages;";
        try (Connection connection = DriverManager.getConnection(
                props.getValue("db.url"),
                props.getValue("db.login"),
                props.getValue("db.password"));
        ) {

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(insertSQL);
            List list = new ArrayList();
            StringBuilder stringBuilder = new StringBuilder();
            while (resultSet.next()) {
                String mes = resultSet.getString("message");
                String fromUs = resultSet.getString("fromUser");
                String toUs = resultSet.getString("toUser");
                list.add("СООБЩЕНИЕ: " + mes + " | ОТ КОГО: " + fromUs + " | КОМУ: " + toUs + "\n");
            }
            return stringBuilder.append(list).toString();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "Messages not found";
//        throw new MessageNotFoundException("Message not found");
    }


}
