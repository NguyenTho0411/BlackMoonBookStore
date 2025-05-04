var bookDetailCount;

$(document).ready(function () {
	bookDetailCount = $(".hiddenBookId").length;

	$("#books").on("click", "#linkAddBook", function (e) {
		e.preventDefault();

		let url = $(this).attr("href");
		let iframe = $("#addBookModal").find("iframe");

		iframe.attr("src", url); // Gán src TRƯỚC KHI mở modal

		$("#addBookModal").modal("show");
	});
});

function addProduct(bookId, bookName) {
	getShippingCost(bookId);
}

function getShippingCost(bookId) {
	const selectedCountry = $("#country option:selected");
	const countryId = selectedCountry.val();

	let state = $("#state").val();
	if (!state || state.trim() === "") {
		state = $("#city").val();
	}

	const requestUrl = contextPath + "get_shipping_cost";
	const params = { bookId: bookId, countryId: countryId, state: state };

	$.ajax({
		type: 'POST',
		url: requestUrl,
		beforeSend: function (xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: params
	})
		.done(function (shippingCost) {
			if (isNaN(shippingCost)) shippingCost = 0.0;
			getProductInfo(bookId, parseFloat(shippingCost));
		})
		.fail(function (err) {
			const message = err.responseText || "Shipping cost not available.";
			window.parent.showWarningModal(message);
			getProductInfo(bookId, 0.0);
		})
		.always(function () {
			$("#addBookModal").modal("hide");
		});
}

function getProductInfo(bookId, shippingCost) {
	const requestURL = contextPath + "books/get/" + bookId;
	$.get(requestURL, function (bookJson) {
		console.log(bookJson);

		const bookName = bookJson.name;
		const mainImagePath = contextPath.slice(0, -1) + bookJson.imagePath;
		const bookCost = $.number(bookJson.cost, 2);
		const bookPrice = $.number(bookJson.price, 2);
		const formattedShipping = $.number(shippingCost, 2);

		const htmlCode = generateProductCode(bookId, bookName, mainImagePath, bookCost, bookPrice, formattedShipping);
		$("#bookList").append(htmlCode);

		// Delay để chắc chắn DOM đã được cập nhật
		setTimeout(() => {
			updateOrderAmounts();
		}, 50);
	}).fail(function (err) {
		const message = err.responseText || "Book data not found.";
		window.parent.showWarningModal(message);
	});
}

function generateProductCode(bookId, bookName, mainImagePath, bookCost, bookPrice, shippingCost) {
	const nextCount = ++bookDetailCount;
	const rowId = "row" + nextCount;
	const quantityId = "quantity" + nextCount;
	const priceId = "price" + nextCount;
	const subtotalId = "subtotal" + nextCount;
	const blankLineId = "blankLine" + nextCount;

	return `
		<div class="border rounded p-1" id="${rowId}">
			<input type="hidden" name="detailId" value="0" />
			<input type="hidden" name="bookId" value="${bookId}" class="hiddenBookId" />
			<div class="row">
				<div class="col-1">
					<div class="divCount">${nextCount}</div>
					<div>
						<a class="fas fa-trash icon-dark linkRemove" 
						   href="javascript:void(0)" 
						   data-row-number="${nextCount}"></a>
					</div>				
				</div>
				<div class="col-3">
					<img src="${mainImagePath}" class="img-fluid" />
				</div>
			</div>
			
			<div class="row m-2">
				<b>${bookName}</b>
			</div>
			
			<div class="row m-2">
				<table>
					<tr>
						<td>Product Cost:</td>
						<td>
							<input type="text" required class="form-control m-1 cost-input"
								name="bookDetailCost"
								data-row-number="${nextCount}" 
								value="${bookCost}" style="max-width: 140px"/>
						</td>
					</tr>
					<tr>
						<td>Quantity:</td>
						<td>
							<input type="number" step="1" min="1" max="5" class="form-control m-1 quantity-input"
								name="quantity"
								id="${quantityId}"
								data-row-number="${nextCount}" 
								value="1" style="max-width: 140px"/>
						</td>
					</tr>	
					<tr>
						<td>Unit Price:</td>
						<td>
							<input type="text" required class="form-control m-1 price-input"
								name="bookPrice"
								id="${priceId}"
								data-row-number="${nextCount}" 
								value="${bookPrice}" style="max-width: 140px"/>
						</td>
					</tr>
					<tr>
						<td>Subtotal:</td>
						<td>
							<input type="text" readonly class="form-control m-1 subtotal-output"
								name="bookSubtotal"
								id="${subtotalId}" 
								value="${bookPrice}" style="max-width: 140px"/>
						</td>
					</tr>				
					<tr>
						<td>Shipping Cost:</td>
						<td>
							<input type="text" required class="form-control m-1 ship-input"
								name="bookShipCost" 
								value="${shippingCost}" style="max-width: 140px"/>
						</td>
					</tr>											
				</table>
			</div>
		</div>
		<div id="${blankLineId}" class="row">&nbsp;</div>`;
}


function isProductAlreadyAdded(bookId) {
	let bookExists = false;
	$(".hiddenBookId").each(function () {
		const aBookId = $(this).val();
		if (aBookId == bookId) {
			bookExists = true;
			return false; // break
		}
	});
	return bookExists;
}
