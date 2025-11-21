package xyz.yangtong.sql4j;

/**
 * 异常类
 */
public class Sql4JException extends RuntimeException {
    private final String message;

    public Sql4JException(String msg) {
        super(msg);
        this.message = msg;
    }

    public Sql4JException(String msg, Throwable cause) {
        super(msg, cause);
        this.message = msg;
    }

    public Sql4JException(Throwable cause) {
        super(cause.getMessage(), cause);
        this.message = cause.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
