package xyz.ytora.bean;

public class Order {
    private Long id;
    private Long userId;
    private Double orderAmount;

    // Getter方法
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }
}
