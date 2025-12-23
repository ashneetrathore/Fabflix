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

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {
    console.log("handleResult: populating star info from resultData");

    let starInfoElement = jQuery("#star_info");

    let star_dob = resultData[0]["star_dob"];
    if (star_dob === null) {
        star_dob = "N/A";
    }

    let rowHTML = "";
    rowHTML += "<tr>";
    rowHTML += "<th>" + resultData[0]["star_name"] + "</th>";
    rowHTML += "<th>" + star_dob + "</th>";

    let movieHTML = "";
    for (let i = 0; i < resultData.length; i++) {
        movieHTML += '<a href=' + resultData[i]['movie_id'] + '"single-movie.html?id=">'
            + resultData[i]["movie_name"] +
            '</a>';
        if (i < resultData.length - 1) {
            movieHTML += "<br>";
        }
    }
    rowHTML += "<th>" + movieHTML + "</th>";

    rowHTML += "</tr>";
    starInfoElement.append(rowHTML);

    /*
    let backButtonElement = jQuery("#back_button");
    backButtonElement.append("<p><a href='index.html'>Back to Top 20 Movie Picks</a></p>");
    */
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

let starId = getParameterByName('id');

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/single-star?id=" + starId,
    success: (resultData) => handleResult(resultData)
});
