$(document).ready(function() {
    // Xử lý sự kiện xóa
    $(".link-delete").on("click", function(e) {
        e.preventDefault();
        showDeleteConfirmModal($(this), entityName);
    });
    
    // Xử lý sự kiện link chi tiết mặc định
    handleDefaultDetailLinkClick();
    
    // Xử lý link chi tiết sản phẩm và khách hàng
    handleViewProductDetailLink();
    handleViewCustomerDetailLink();
    
    // Xóa bộ lọc
    $(".clear-filter").on("click", function() {
        clearFilter();
    });
});

// Xóa bộ lọc
function clearFilter() {
    window.location = moduleURL;    
}

// Hiển thị modal xác nhận xóa
function showDeleteConfirmModal(link, entityName) {
    var entityId = link.attr("entityId");
    
    // Đặt URL cho nút Yes
    $("#yesButton").attr("href", link.attr("href"));    
    
    // Cập nhật văn bản xác nhận
    $("#confirmText").text("Are you sure you want to delete this " + entityName + " ID " + entityId + "?");
    
    // Mở modal xác nhận
    var confirmModal = new bootstrap.Modal(document.getElementById("confirmModal"));
    confirmModal.show();
}

// Xử lý click vào link chi tiết
function handleDetailLinkClick(cssClass, modalId) {
    $(cssClass).on("click", function(e) {
        e.preventDefault();
        var linkDetailURL = $(this).attr("href");
        
        // Mở modal bằng Bootstrap 5
        var modal = new bootstrap.Modal(document.getElementById(modalId.substring(1)));
        modal.show();
        
        // Tải nội dung vào modal
        $(modalId).find(".modal-content").load(linkDetailURL);
    });        
}

// Xử lý link chi tiết mặc định
function handleDefaultDetailLinkClick() {
    handleDetailLinkClick(".link-detail", "#detailModal");    
}

// Xử lý link chi tiết sản phẩm
function handleViewProductDetailLink() {
    handleDetailLinkClick(".link-product-detail", "#detailModal");    
}

// Xử lý link chi tiết khách hàng
function handleViewCustomerDetailLink() {
    handleDetailLinkClick(".link-customer-detail", "#smallModal");    
}
