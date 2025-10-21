package service;

public class BadRequestException extends ServiceException {
    public BadRequestException(){
        super("bad request");
    }
}
