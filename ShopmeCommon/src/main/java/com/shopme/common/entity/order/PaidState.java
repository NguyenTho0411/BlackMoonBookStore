package com.shopme.common.entity.order;


import java.util.Date;



public class PaidState implements OrderState {
    @Override
    public void next(Order order) {
        order.setStatus(OrderStatus.PROCESSING);

        OrderTrack track = new OrderTrack();
        track.setOrder(order);
        track.setStatus(OrderStatus.PROCESSING);
        track.setNotes(OrderStatus.PROCESSING.defaultDescription());
        track.setUpdatedTime(new Date());

        order.getOrderTracks().add(track);
    }

    @Override
    public void previous(Order order) {
        // Có thể để trống hoặc quay về NEW nếu cần
    }

    @Override
    public String getStateName() {
        return "Paid";
    }
}
