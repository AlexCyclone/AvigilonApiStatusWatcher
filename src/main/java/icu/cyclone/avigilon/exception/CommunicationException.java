package icu.cyclone.avigilon.exception;

/**
 * @author Aleksey Babanin
 * @since 2021/02/02
 */
public class CommunicationException extends RuntimeException {
    public CommunicationException(String message) {
        super(message);
    }

    public CommunicationException(Throwable cause) {
        super(cause);
    }
}
