let addstar_form = jQuery("#addstar_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultData jsonObject
 */
function handleAddStar(resultData) {
    console.log("handle add response");
    console.log(resultData["status"]);

    console.log(resultData["message"]);
    jQuery("#addstar_message").text(resultData["message"]);

    if (resultData["status"] === "fail") {
        addstar_form.trigger("reset");
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitAddStarForm(formSubmitEvent) {
    console.log("submit add star form");
    formSubmitEvent.preventDefault();

    $.ajax(
        "addstar", {
            method: "POST",
            data: addstar_form.serialize(),
            success: (resultData) => handleAddStar(resultData)
        }
    );
}
addstar_form.submit(submitAddStarForm);