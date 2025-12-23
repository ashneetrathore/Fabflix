let pay_form = jQuery("#pay_form");

/**
 * Handle the data returned by PaymentServlet
 * @param resultDataJson jsonObject
 */
function handlePaymentResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle payment response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    if (resultDataJson["status"] === "success") {
        window.location.replace("confirm.html");
    } else {
        console.log("show error message");
        console.log(resultDataJson["message"]);
        jQuery("#payment_error_message").text(resultDataJson["message"]);

        jQuery("#firstName").val("");
        jQuery("#lastName").val("");
        jQuery("#ccNum").val("");
        jQuery("#expDate").val("");
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitPaymentForm(formSubmitEvent) {
    console.log("submit payment form");
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/pay", {
            method: "POST",
            data: pay_form.serialize(),
            success: handlePaymentResult
        }
    );
}
pay_form.submit(submitPaymentForm);

function handlePriceResult(priceResponse) {
    console.log("Getting total price");

    let priceText = "Total Price: $";
    priceText += priceResponse.totalPrice;
    jQuery("#total_price").text(priceText);
}

$(document).ready(function() {
    $.ajax({
        type: "GET",
        url: "api/pay",
        success: (priceResponse) => handlePriceResult(priceResponse)
    });

});
