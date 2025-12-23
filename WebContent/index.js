function generateMovieList(searchParams) {
    let searchUrl = "api/movies?" + searchParams.toString();

    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: searchUrl,
        success: (resultData) => handleMovieListResult(resultData)
    });
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
function handleMovieListResult(resultData) {
    console.log("handleMovieListResult: populating movie table from resultData");

    let totalMovieCount = parseInt(resultData[resultData.length - 1]["totalMovies"]);
    let searchParams = new URLSearchParams(window.location.search);

    let currentPage = 1;
    if (searchParams.has("pageNum")) {
        currentPage = parseInt(searchParams.get("pageNum"));
    }

    let perPageOption = 10;
    if (searchParams.has("perPage")) {
        perPageOption = parseInt(searchParams.get("perPage"));
    }
    let totalPages = Math.ceil(totalMovieCount / perPageOption);

    if (currentPage == 1) {
        jQuery("#prevButton").hide();
    }

    if (currentPage == totalPages || totalPages == 0) {
        jQuery("#nextButton").hide();
    }

    let movieTableBodyElement = jQuery("#movie_table_body");
    movieTableBodyElement.empty();

    for (let i = 0; i < resultData.length - 1; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";

        let movieHTML = "";
        movieHTML += '<a href=' + resultData[i]["movie_id"] + '"single-movie.html?id=">'
            + resultData[i]["movie_name"] +
            '</a>';
        rowHTML += "<th>" + movieHTML + "</th>";

        rowHTML += "<th>" + resultData[i]["movie_yr"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";

        let genresArray = resultData[i]["movie_genres"].split(",");
        let genresHTML = "";
        for (let i = 0; i < genresArray.length; i++) {
            genresHTML += '<a href=' + genresArray[i] + '"index.html?pageNum=1&genre=">'
                + genresArray[i]
                + '</a>';
            if (i < genresArray.length - 1) {
                genresHTML += "<br>";
            }
        }
        rowHTML += "<th>" + genresHTML + "</th>";

        let starsArray = resultData[i]["movie_stars"].split(",");
        let starIdsArray = resultData[i]["movie_star_ids"].split(",");
        let starsHTML = "";
        for (let i = 0; i < starsArray.length; i++) {
            starsHTML += '<a href=' + starIdsArray[i] + '"single-star.html?id=">'
                + starsArray[i]
                + '</a>';
            if (i < starsArray.length - 1) {
                starsHTML += "<br>";
            }
        }
        rowHTML += "<th>" + starsHTML + "</th>";

        let rating = resultData[i]["movie_rating"];
        if (rating == null) {
            rating = "N/A";
        }

        rowHTML += "<th>" + rating + "</th>";

        let addCartButtonHTML = '<button class="btn btn-primary" onclick="addToCart(\'' + resultData[i]["movie_id"] + '\')">Add to Cart</button>';
        rowHTML += "<td>" + addCartButtonHTML + "</td>";

        rowHTML += "</tr>";
        movieTableBodyElement.append(rowHTML);
    }
}

$(document).ready(function () {
    let searchParams = new URLSearchParams(window.location.search);
    generateMovieList(searchParams);
    console.log(searchParams);

    let currentPage = parseInt(searchParams.get("pageNum"));

    let orderByOption = searchParams.get("orderBy");
    if (orderByOption) {
        jQuery("#order_by").val(orderByOption);
    }

    let perPageOption = searchParams.get("perPage");
    if (perPageOption) {
        jQuery("#per_page").val(perPageOption);
    }

    jQuery("#order_by").change(function () {
        let option = jQuery("#order_by").val();

        if (searchParams.has("orderBy")) {
            searchParams.set("orderBy", option);
        } else {
            searchParams.append("orderBy", option);
        }

        let searchUrl = "index.html?" + searchParams.toString();
        window.location.replace(searchUrl);
    });

    jQuery("#per_page").change(function () {
        let option = jQuery("#per_page").val();

        if (searchParams.has("perPage")) {
            searchParams.set("perPage", option);
        } else {
            searchParams.append("perPage", option);
        }

        let searchUrl = "index.html?" + searchParams.toString();
        window.location.replace(searchUrl);
    });

    jQuery("#nextButton").click(function() {
        searchParams.set("pageNum", (currentPage + 1).toString());
        let searchUrl = "index.html?" + searchParams.toString();
        window.location.replace(searchUrl);
    });

    jQuery("#prevButton").click(function() {
        searchParams.set("pageNum", (currentPage - 1).toString());
        let searchUrl = "index.html?" + searchParams.toString();
        window.location.replace(searchUrl);
    });


});
