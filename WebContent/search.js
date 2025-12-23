let search_form = jQuery("#search_form");

function handleSearchResult() {
    var title = jQuery('#search_form input[name="title"]').val();
    var year = jQuery('#search_form input[name="year"]').val();
    var director = jQuery('#search_form input[name="director"]').val();
    var star_name = jQuery('#search_form input[name="star_name"]').val();

    var searchUrl = "index.html?";

    if (title) {
        searchUrl += "title=" + title + "&";
    }
    if (year) {
        searchUrl += "year=" + year + "&";
    }
    if (director) {
        searchUrl += "director=" + director + "&";
    }
    if (star_name) {
        searchUrl += "star_name=" + star_name + "&";
    }
    searchUrl += "pageNum=1&";
    searchUrl = searchUrl.replace(/&$|\?$/, "");

    window.location.replace(searchUrl);
}

/**
 * Submit the form content with GET method
 * @param formSubmitEvent
 */
function submitSearchForm(formSubmitEvent) {
    console.log("submit search form");
    formSubmitEvent.preventDefault();
    handleSearchResult();
}

search_form.submit(submitSearchForm);


/*
 * This function is called by the library when it needs to lookup a query.
 *
 * The parameter query is the query string.
 * The doneCallback is a callback function provided by the library, after you get the
 *   suggestion list from AJAX, you need to call this function to let the library know.
 */
function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated")

    var cacheList = localStorage.getItem(query);
    if (cacheList) {
        console.log("Using cached results for query: " + query);
        var formattedData = JSON.parse(cacheList);

        console.log(formattedData);
        doneCallback({ suggestions: formattedData });
        return;
    }

    console.log("sending AJAX request to backend Java Servlet")
    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data
    jQuery.ajax({
        "method": "GET",
        // generate the request url from the query.
        // escape the query string to avoid errors caused by special characters
        "url": "api/movies?title=" + query,
        "success": function(data) {
            // pass the data, query, and doneCallback function into the success handler
            handleLookupAjaxSuccess(data, query, doneCallback)
        },
        "error": function(errorData) {
            console.log("lookup ajax error")
            console.log(errorData)
        }
    })
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 *
 * data is the JSON data string you get from your Java Servlet
 *
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful")

    // Filter out the object that contains the total count of movies
    var titles = data.filter(function(item) {
        return item.hasOwnProperty('movie_id');
    });

    var formattedData = titles.map(function(item) {
        return { value: item.movie_name, data: item.movie_id };
    });
    console.log(formattedData);

    localStorage.setItem(query, JSON.stringify(formattedData));

    // Call the callback function with the formatted suggestions
    doneCallback({ suggestions: formattedData });
}


/*
 * This function is the select suggestion handler function.
 * When a suggestion is selected, this function is called by the library.
 *
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
    var singleMoviePageUrl = "single-movie.html?id=" + suggestion["data"];
    window.location.href = singleMoviePageUrl;
}


/*
 * This statement binds the autocomplete library with the input box element and
 *   sets necessary parameters of the library.
 *
 * The library documentation can be find here:
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 *
 */
// $('#title') is to find element by the ID "autocomplete"
$('#title').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    minChars: 3
});