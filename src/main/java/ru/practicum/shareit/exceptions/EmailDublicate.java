package ru.practicum.shareit.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailDublicate extends RuntimeException {
    public EmailDublicate(String s) {
        super(s);
    }
}
