// Common Javascript code for product reviews/rating
$(document).ready(function() {
	formatRatingNumber();
});

$(".product-detail-rating-star").rating({
	displayOnly: true,
	theme: 'krajee-svg',
	step: 0.1,
	min: 0,
	max: 5,
	showCaption: false,
	showClear: false
});

function formatRatingNumber() {
	let ratingText = $("#ratingNumber").text();
	let ratingFloat = parseFloat(ratingText);
	$("#ratingNumber").text(ratingFloat.toFixed(1)); // Chỉ còn 1 số thập phân
}
