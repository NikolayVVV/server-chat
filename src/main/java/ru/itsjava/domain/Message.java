package ru.itsjava.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Message {
    private final String fromUser;
    private final String textOfMessage;
    private final String toUser;
}
