package br.com.pb.compass.exception;

public class PostAlreadyExistsException extends RuntimeException{
    public PostAlreadyExistsException(String message) {
        super(message);
    }
}
