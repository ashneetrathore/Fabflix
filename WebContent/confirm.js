function generateConfirmationList() {
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/confirm",
        success: (resultData) => handleConfirmationListResult(resultData)
    });
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleConfirmationListResult(resultData) {
    console.log("handleConfirmationListResult: populating confirmation table from resultData");
    console.log(resultData);

    let confirmationTableBodyElement = jQuery("#confirmation_table_body");
    confirmationTableBodyElement.empty();

    for (let i = 0; i < resultData.length - 1; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";

        rowHTML += "<th>" + resultData[i]["salesId"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movieTitle"] + "</th>";
        rowHTML += "<th>" + resultData[i]["quantity"] + "</th>";

        rowHTML += "</tr>";
        confirmationTableBodyElement.append(rowHTML);
    }
    let totalPrice = resultData[resultData.length - 1]["totalPrice"];
    let totalPriceString = "Total: $";
    totalPriceString += totalPrice;
    jQuery("#total_price").text(totalPriceString);
}

$(document).ready(function () {
    generateConfirmationList();
});
