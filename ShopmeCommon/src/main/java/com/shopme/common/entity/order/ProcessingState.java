package com.shopme.common.entity.order;

//com.shopme.common.state.ProcessingState


import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.OrderStatus;

public class ProcessingState implements OrderState {
 @Override
 public void next(Order order) {
     order.setStatus(OrderStatus.SHIPPING);
     // order.setState(new ShippingState()); // nếu bạn muốn
 }

 @Override
 public void previous(Order order) {
     order.setStatus(OrderStatus.PAID);
//     order.setState(new PaidState());
 }

 @Override
 public String getStateName() {
     return "PROCESSING";
 }
}
