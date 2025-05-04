package com.shopme.checkout;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shopme.common.entity.Book;
import com.shopme.common.entity.CartItem;

import com.shopme.common.entity.ShippingRate;

@Service
public class CheckoutService {
	
	
	private static final int DIM_DIVISOR =139;
	
	public CheckoutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate) {
		CheckoutInfo checkoutInfo = new CheckoutInfo();
		float bookCost = calculateBookCost(cartItems);
		float bookTotal = calculateBookTotal(cartItems);
		
		float shippingCostTotal = calculateShippingCost(cartItems, shippingRate);
		float paymentTotal = bookTotal +shippingCostTotal;
		checkoutInfo.setBookCost(bookCost);
		checkoutInfo.setBookTotal(bookTotal);
		checkoutInfo.setDeliverDays(shippingRate.getDays());
		checkoutInfo.setCodSupported(shippingRate.isCodSupported());
		checkoutInfo.setShippingCostTotal(shippingCostTotal);
		checkoutInfo.setPaymentTotal(paymentTotal);
		
		return checkoutInfo;
	}

	private float calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {
		float shippingCostTotal = 0.0f;
		for(CartItem item : cartItems) {
			Book book = item.getBook();
			float dimWeight = (book.getLength() * book.getWeight()*book.getHeight())/DIM_DIVISOR;
			float finalWeight = book.getWeight() > dimWeight ? book.getWeight() : dimWeight;
			float shippingCost = finalWeight *item.getQuantity()* shippingRate.getRate();
			
			item.setShippingCost(shippingCost);
			shippingCostTotal+= shippingCost;
		}
		return shippingCostTotal;
	}

	private float calculateBookTotal(List<CartItem> cartItems) {
		// TODO Auto-generated method stub
		float total = 0.0f;
		
		
		for(CartItem item : cartItems) {
			total += item.getSubtotal();
		}
		return total;
	}

	private float calculateBookCost(List<CartItem> cartItems) {
		// TODO Auto-generated method stub
		float cost = 0.0f;
		
		
		for(CartItem item : cartItems) {
			cost += item.getQuantity()*item.getBook().getCost();
		}
		return cost;
	}
}
