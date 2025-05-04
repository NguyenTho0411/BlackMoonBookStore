package com.shopme.order;

public class OrderReturnResponse {

		private Integer orderId;

		public Integer getOrderId() {
			return orderId;
		}
		public void setOrderId(Integer orderId) {
			this.orderId = orderId;
		}
	
		public OrderReturnResponse(Integer orderId) {
			this.orderId = orderId;

		}
		public OrderReturnResponse() {

		}
		
		
	}
