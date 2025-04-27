dropdownBrands = $("#publisher");
        dropdownCategories = $("#category");
        defaultImageThumbnailSrc ="[[@{/images/image-thumbnail.png}]]";
        
        dropdownBrands = $("#publisher");
        dropdownCategories = $("#category");

        $(document).ready(function() {
            $("#shortDescription").richText();
            $("#fullDescription").richText();

            dropdownBrands.change(function() {
                dropdownCategories.empty();
                getCategories();
            });

            getCategoriesForNewForm();
			
		
        });
		
		function getCategoriesForNewForm(){
			catIdField = $("#categoryId");
			editMode = false;
			
			if(catIdField.length){
				editMode = true;
			}
			if(!editMode ) getCategories();
		}
		function getCategories() {
		         publisherId = dropdownBrands.val();
		         url = publisherModuleURL + "/" + publisherId + "/categories";

		         $.get(url, function(responseJson) {
		             $.each(responseJson, function(index, category) {
		                 $("<option>").val(category.id).text(category.name).appendTo(dropdownCategories);
		             });
		         });
		     }
