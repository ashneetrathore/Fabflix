let addmovie_form = jQuery("#addmovie_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultData jsonObject
 */
function handleAddMovie(resultData) {
    console.log("handle add response");
    console.log(resultData["status"]);

    console.log(resultData["message"]);
    jQuery("#addmovie_message").text(resultData["message"]);

    if (resultData["status"] === "fail") {
        addmovie_form.trigger("reset");
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitAddMovieForm(formSubmitEvent) {
    console.log("submit add movie form");
    formSubmitEvent.preventDefault();

    $.ajax(
        "addmovie", {
            method: "POST",
            data: addmovie_form.serialize(),
            success: (resultData) => handleAddMovie(resultData)
        }
    );
}
addmovie_form.submit(submitAddMovieForm);