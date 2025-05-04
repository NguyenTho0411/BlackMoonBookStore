$(document).ready(function () {
	$("#bookList").on("click", ".linkRemove", function (e) {
		e.preventDefault();

		if (doesOrderHaveOnlyOneProduct()) {
			showWarningModal("Could not remove book. The order must have at least one book.");
		} else {
			removeProduct($(this));
			updateOrderAmounts();
		}
	});
});


function removeProduct(link) {
	const rowNumber = link.attr("data-row-number");
	$("#row" + rowNumber).remove();
	$("#blankLine" + rowNumber).remove();

	$(".divCount").each(function(index, element) {
		element.innerHTML = "" + (index + 1);
	});
}


function doesOrderHaveOnlyOneProduct() {
	const bookCount = $(".hiddenBookId").length;
	return bookCount === 1;
}
