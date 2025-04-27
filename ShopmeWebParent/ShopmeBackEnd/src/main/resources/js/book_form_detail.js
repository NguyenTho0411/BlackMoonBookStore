



function addNextDetailSection(){
	
	allDivDetails = $("[id^='divDetail']");
	divDetailsCount = allDivDetails.length;
	
	htmlDetailSection = `	<div class="form-inline" id="divDetail${divDetailsCount}>
	<input type="hidden" name="detailIDs" value="0" />
		<label class="m-3">Name:</label>
		<input type="text" class="form-control w-25" name="detailNames" maxLength="255"/>
					<label class="m-3">Value:</label>
		<input type="text" class="form-control w-25" name="detailValues" maxLength="255"/>
	</div>`;
	
	$("#divBookDetails").append(htmlDetailSection);
	
	
	

	htmlLinkRemove = 
	`<a class="btn fas fa-times-circle fa-2x icon-dark" title="Remove this detail"
	 href="javascript:removeExtraImage(${index-1})"></a>`;
	 previousDivDetailSection = allDivDetails.last();
	previousDivDetailSection.append(htmlLinkRemove);
}