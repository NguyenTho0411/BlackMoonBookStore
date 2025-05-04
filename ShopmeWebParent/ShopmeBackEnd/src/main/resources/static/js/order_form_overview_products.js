var fieldBookCost;
var fieldSubtotal;
var fieldShippingCost;
var fieldTax;
var fieldTotal;

$(document).ready(function() {

	fieldBookCost = $("#bookCost");
	fieldSubtotal = $("#subtotal");
	fieldShippingCost = $("#shippingCost");
	fieldTax = $("#tax");
	fieldTotal = $("#total");

	formatOrderAmounts();
	formatBookAmounts();

	$("#bookList").on("change", ".quantity-input", function(e) {
		updateSubtotalWhenQuantityChanged($(this));
		updateOrderAmounts();
	});

	$("#bookList").on("change", ".price-input", function(e) {
		updateSubtotalWhenPriceChanged($(this));
		updateOrderAmounts();
	});

	$("#bookList").on("change", ".cost-input", function(e) {
		updateOrderAmounts();
	});

	$("#bookList").on("change", ".ship-input", function(e) {
		updateOrderAmounts();
	});
});

function updateOrderAmounts() {
	totalCost = 0.0;

	$(".cost-input").each(function(e) {
		costInputField = $(this);
		rowNumber = costInputField.attr("rowNumber");
		quantityValue = $("#quantity" + rowNumber).val();


		bookCost = getNumberValueRemovedThousandSeparator(costInputField);
		totalCost += bookCost * parseInt(quantityValue);
	});

	setAndFormatNumberForField("bookCost", totalCost);

	orderSubtotal = 0.0;

	$(".subtotal-output").each(function(e) {
		bookSubtotal = getNumberValueRemovedThousandSeparator($(this));
		orderSubtotal += bookSubtotal;
	});

	setAndFormatNumberForField("subtotal", orderSubtotal);

	shippingCost = 0.0;

	$(".ship-input").each(function(e) {
		bookShip = getNumberValueRemovedThousandSeparator($(this));
		shippingCost += bookShip;
	});

	setAndFormatNumberForField("shippingCost", shippingCost);

	tax = getNumberValueRemovedThousandSeparator(fieldTax);
	orderTotal = orderSubtotal + tax + shippingCost;
	setAndFormatNumberForField("total", orderTotal);
}

function setAndFormatNumberForField(fieldId, fieldValue) {
	formattedValue = $.number(fieldValue, 2);
	$("#" + fieldId).val(formattedValue);
}

function getNumberValueRemovedThousandSeparator(fieldRef) {
	fieldValue = fieldRef.val().replace(",", "");
	return parseFloat(fieldValue);
}

function updateSubtotalWhenPriceChanged(input) {
	priceValue = getNumberValueRemovedThousandSeparator(input);
	rowNumber = input.attr("rowNumber");

	quantityField = $("#quantity" + rowNumber);
	quantityValue = quantityField.val();
	newSubtotal = parseFloat(quantityValue) * priceValue;

	setAndFormatNumberForField("subtotal" + rowNumber, newSubtotal);
}

function updateSubtotalWhenQuantityChanged(input) {
	quantityValue = input.val();
	rowNumber = input.attr("rowNumber");
	priceValue = getNumberValueRemovedThousandSeparator($("#price" + rowNumber));
	newSubtotal = parseFloat(quantityValue) * priceValue;

	setAndFormatNumberForField("subtotal" + rowNumber, newSubtotal);
}

function formatBookAmounts() {
	$(".cost-input").each(function(e) {
		formatNumberForField($(this));
	});

	$(".price-input").each(function(e) {
		formatNumberForField($(this));
	});

	$(".subtotal-output").each(function(e) {
		formatNumberForField($(this));
	});

	$(".ship-input").each(function(e) {
		formatNumberForField($(this));
	});
}

function formatOrderAmounts() {
	formatNumberForField(fieldBookCost);
	formatNumberForField(fieldSubtotal);
	formatNumberForField(fieldShippingCost);
	formatNumberForField(fieldTax);
	formatNumberForField(fieldTotal);
}

function formatNumberForField(fieldRef) {
	fieldRef.val($.number(fieldRef.val(), 2));
}

function processFormBeforeSubmit() {
	setCountryName();

	removeThousandSeparatorForField(fieldBookCost);
	removeThousandSeparatorForField(fieldSubtotal);
	removeThousandSeparatorForField(fieldShippingCost);
	removeThousandSeparatorForField(fieldTax);
	removeThousandSeparatorForField(fieldTotal);

	$(".cost-input").each(function(e) {
		removeThousandSeparatorForField($(this));
	});

	$(".price-input").each(function(e) {
		removeThousandSeparatorForField($(this));
	});

	$(".subtotal-output").each(function(e) {
		removeThousandSeparatorForField($(this));
	});

	$(".ship-input").each(function(e) {
		removeThousandSeparatorForField($(this));
	});

	return true;
}

function removeThousandSeparatorForField(fieldRef) {
	fieldRef.val(fieldRef.val().replace(",", ""));
}

function setCountryName() {
	selectedCountry = $("#country option:selected");
	countryName = selectedCountry.text();
	$("#countryName").val(countryName);
}
$('a[data-bs-toggle="tab"]').on('shown.bs.tab', function (e) {
	if ($(e.target).attr("href") === "#books") {
		formatBookAmounts();
		formatOrderAmounts();
	}
});
