let employee_login_form = jQuery("#employee_login_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataJson jsonObject
 */
function handleEmployeeLoginResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle employee login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    if (resultDataJson["status"] === "success") {
        window.location.replace("metadata.html");
    } else {
        console.log("show error message");
        console.log(resultDataJson["message"]);
        jQuery("#employee_login_error_message").text(resultDataJson["message"]);

        employee_login_form.trigger("reset");
        grecaptcha.reset();
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitEmployeeLoginForm(formSubmitEvent) {
    console.log("submit employee login form");
    formSubmitEvent.preventDefault();

    $.ajax(
        "dlogin", {
            method: "POST",
            data: employee_login_form.serialize(),
            success: handleEmployeeLoginResult
        }
    );
}
employee_login_form.submit(submitEmployeeLoginForm);