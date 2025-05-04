$(document).ready(function() {
	$(".linkVoteReview").on("click", function(e) {
		e.preventDefault();
		voteReview($(this));
	});
});

function voteReview(currentLink) {
	requestURL = currentLink.attr("href");

	$.ajax({
		type: "POST",
		url: requestURL,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(voteResult) {
		console.log(voteResult);

		if (voteResult.successful) {
			$("#modalDialog").on("hide.bs.modal", function(e) {
				updateVoteCountAndIcons(currentLink, voteResult);
			});
		}

		showModalDialog("Vote Review", voteResult.message);

	}).fail(function() {
		showErrorModal("Error voting review.");
	});
}

function updateVoteCountAndIcons(currentLink, voteResult) {
	reviewId = currentLink.attr("reviewId");
	voteUpLink = $("#linkVoteUp-" + reviewId);
	voteDownLink = $("#linkVoteDown-" + reviewId);

	$("#voteCount-" + reviewId).text(voteResult.voteCount + " Votes");

	message = voteResult.message;

	if (message.includes("successfully voted up")) {
		highlightVoteUpIcon(currentLink, voteDownLink);
	} else if (message.includes("successfully voted down")) {
		highlightVoteDownIcon(currentLink, voteUpLink);
	} else if (message.includes("unvoted down")) {
		unhighlightVoteDownIcon(voteDownLink);
	} else if (message.includes("unvoted up")) {
		unhighlightVoteDownIcon(voteUpLink);
	}
}

function highlightVoteUpIcon(voteUpLink, voteDownLink) {
	voteUpLink.find("i").removeClass("far").addClass("fas");
	voteUpLink.attr("title", "Undo vote up this review");
	voteUpLink.addClass("voted");

	voteDownLink.find("i").removeClass("fas").addClass("far");
	voteDownLink.removeClass("voted");
}

function highlightVoteDownIcon(voteDownLink, voteUpLink) {
	voteDownLink.find("i").removeClass("far").addClass("fas");
	voteDownLink.attr("title", "Undo vote down this review");
	voteDownLink.addClass("voted");

	voteUpLink.find("i").removeClass("fas").addClass("far");
	voteUpLink.removeClass("voted");
}

function unhighlightVoteDownIcon(voteDownLink) {
	voteDownLink.attr("title", "Vote down this review");
	voteDownLink.find("i").removeClass("fas").addClass("far");
	voteDownLink.removeClass("voted");
}

function unhighlightVoteUpIcon(voteUpLink) {
	voteUpLink.attr("title", "Vote up this review");
	voteUpLink.find("i").removeClass("fas").addClass("far");
	voteUpLink.removeClass("voted");
}
