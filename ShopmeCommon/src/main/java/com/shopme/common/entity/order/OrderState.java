package com.shopme.common.entity.order;

//com.shopme.common.state.OrderState



public interface OrderState {
 void next(Order order);
 void previous(Order order);
 String getStateName();
}
