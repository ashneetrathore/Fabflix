function generateCartList() {
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/cart?",
        success: (resultData) => handleCartListResult(resultData)
    });
}

function updateCart(movieId, doThis) {
    let cartParams = new URLSearchParams();
    cartParams.set("movie_id", movieId);
    cartParams.set("do", doThis);
    let cartUrl = "api/cart?" + cartParams.toString();

    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: cartUrl,
        success: generateCartList
    });
}

function handleCartListResult(cartData) {
    let cartTableBodyElement = jQuery("#cart_table_body");
    cartTableBodyElement.empty();

    for (let i = 0; i < cartData.length - 1; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<td>" + cartData[i]["title"] +"</td>";
        rowHTML += "<td>" + cartData[i]["quantity"] +"</td>";
        rowHTML += "<td>" + cartData[i]["price"] +"</td>";

        let increaseButtonHTML = '<button class="btn btn-primary" onclick="increase(\'' + cartData[i]["id"] + '\')">+</button>';
        let decreaseButtonHTML = '<button class="btn btn-primary" onclick="decrease(\'' + cartData[i]["id"] + '\')">-</button>';
        let removeButtonHTML = '<button class="btn btn-primary" onclick="remove(\'' + cartData[i]["id"] + '\')">Remove</button>';
        rowHTML += "<td>" + increaseButtonHTML + " " + decreaseButtonHTML + " " + removeButtonHTML + "</td>";

        rowHTML += "</tr>";
        cartTableBodyElement.append(rowHTML);
    }
    let totalPrice = cartData[cartData.length - 1]["totalPrice"];

    if (totalPrice !== 0) {
        let totalPriceString = "Total: $";
        totalPriceString += totalPrice;
        jQuery("#total_price").text(totalPriceString);
        jQuery("#paymentButton").show();
    }
    else {
        console.log("no items");
        jQuery("#total_price").text("Total: $0.00");
        jQuery("#paymentButton").hide();
    }
}

function increase(movieId) {
    updateCart(movieId, "increase");
}

function decrease(movieId) {
    updateCart(movieId, "decrease");
}

function remove(movieId) {
    updateCart(movieId, "remove");
}

$(document).ready(function () {
    generateCartList();

    jQuery("#paymentButton").click(function() {
        window.location.replace("pay.html");
    });
});