/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    let url = window.location.href;
    target = target.replace(/[\[\]]/g, "\\$&");

    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function addToCart(movieId) {
    let cartParams = new URLSearchParams();
    cartParams.set("movie_id", movieId);
    cartParams.set("do", "add");
    let cartUrl = "api/cart?" + cartParams.toString();

    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: cartUrl,
        success: (cartResponse) => handleCartResult(cartResponse)
    });
}

function handleCartResult(cartResponse) {
    console.log("Cart action successful:", cartResponse);
    if (cartResponse.status === "success") {
        alert(cartResponse.message);
    }
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleResult(resultData) {
    console.log("handleResult: populating movie info from resultData");

    let movieInfoElement = jQuery("#movie_info");

    let rowHTML = "";
    rowHTML += "<tr>";
    rowHTML += "<th>" + resultData[0]["movie_name"] + "</th>";
    rowHTML += "<th>" + resultData[0]["movie_yr"] + "</th>";
    rowHTML += "<th>" + resultData[0]["movie_director"] + "</th>";

    // Genre links
    let genresArray = resultData[0]["movie_genres"].split(",");
    let genresHTML = "";
    for (let i = 0; i < genresArray.length; i++) {
        genresHTML += '<a href="index.html?pageNum=1&genre=' + encodeURIComponent(genresArray[i]) + '">'
            + genresArray[i]
            + '</a>';
        if (i < genresArray.length - 1) {
            genresHTML += "<br>";
        }
    }
    rowHTML += "<th>" + genresHTML + "</th>";

    // Star links
    let starsArray = resultData[0]["movie_stars"].split(",");
    let starIdsArray = resultData[0]["movie_star_ids"].split(",");
    let starsHTML = "";
    for (let i = 0; i < starsArray.length; i++) {
        starsHTML += '<a href="single-star.html?id=' + starIdsArray[i] + '">'
            + starsArray[i]
            + '</a>';
        if (i < starsArray.length - 1) {
            starsHTML += "<br>";
        }
    }
    rowHTML += "<th>" + starsHTML + "</th>";

    // Rating
    let rating = resultData[0]["movie_rating"];
    if (rating == null) {
        rating = "N/A";
    }
    rowHTML += "<th>" + rating + "</th>";

    // Add to cart button
    let addCartButtonHTML = '<button class="btn btn-primary" onclick="addToCart(\''
        + resultData[0]["movie_id"] + '\')">Add to Cart</button>';
    rowHTML += "<td>" + addCartButtonHTML + "</td>";

    rowHTML += "</tr>";
    movieInfoElement.append(rowHTML);
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
let movieId = getParameterByName('id');

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/single-movie?id=" + movieId,
    success: (resultData) => handleResult(resultData)
});