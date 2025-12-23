function generateGenreList() {
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/genres",
        success: (genreData) => handleGenreListResult(genreData)
    });
}

function handleGenreListResult(genreData) {
    console.log("handleGenreListResult: displaying genre list of all genres");

    let genreListElement = jQuery("#genre_list");

    for (let i = 0; i < genreData.length; i++) {
        let genreHTML = '<a href="index.html?pageNum=1&genre=' + genreData[i]['genre_name'] + '">'
            + genreData[i]['genre_name']
            + '</a><br>';

        genreListElement.append(genreHTML);
    }
}

function handleCharListResult() {
    console.log("handleCharListResult: displaying character list");

    let charListElement = jQuery("#char_list");
    let chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ*".split("");

    for (let i = 0; i < chars.length; i++) {
        let charHTML = '<a href="index.html?pageNum=1&firstChar=' + chars[i] + '">'
            + chars[i]
            + '</a><br>';

        charListElement.append(charHTML);
    }
}

$(document).ready(function () {
    generateGenreList();
    handleCharListResult();
});