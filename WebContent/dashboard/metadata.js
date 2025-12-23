
/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMetaDataResult(resultData) {
    console.log("handleMetaDataResult: populating metadata from resultData");
    let allTablesElement = jQuery("#metadata_tables");

    for (let i = 0; i < resultData.length; i++) {
        let tableHTML = "";
        tableHTML += "<table class='table table-striped'>";
        tableHTML += "<thead class='thead-dark'><tr><th colspan='2'>"
                    + resultData[i]["table_name"]
                    + "</th></tr></thead>";

        tableHTML += "<tr><th>Column Name</th><th>Column Type</th></tr>";

        let allColumns = resultData[i]["columns"];
        for (let j = 0; j < allColumns.length; j++) {
            tableHTML += "<tr>";
            tableHTML += "<td>" + allColumns[j]["col_name"] + "</td>";
            tableHTML += "<td>" + allColumns[j]["col_type"]+ "</td>";
            tableHTML += "</tr>";
        }

        tableHTML += '</table>';
        tableHTML += '<div></div>';
        allTablesElement.append(tableHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleMovieListResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "metadata", // Setting request url, which is mapped by MoviesServlet in Movies.java
    success: (resultData) => handleMetaDataResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});