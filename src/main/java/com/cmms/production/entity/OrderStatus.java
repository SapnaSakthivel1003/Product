package com.cmms.production.entity;

public enum OrderStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;
    public boolean isValidTransition(OrderStatus nextStatus) {
        return switch (this) {
            case PENDING -> nextStatus == IN_PROGRESS;
            case IN_PROGRESS -> nextStatus == COMPLETED || nextStatus == CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
    }

}
