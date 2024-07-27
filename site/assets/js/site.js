// Javascript functions for the website

function showElement(elName) {
    var el = document.getElementById(elName);
    el.classList.remove("d-none");
    el.classList.add("d-block");
}

function postRegistration(email) {
    // TODO Get base url from config
    fetch(apiUrl + "/email-registration", {
	method: 'post',
	body: JSON.stringify({ email: email }),
	headers: {
	    'Content-Type': 'application/json'
	}
    }).then((response) => {
	return response.json();
    }).then((res) => {
	if (res.email == email) {
	    // ok
	    showElement("registration-ok");
	}
	else {
	    // TODO Error details
	    showElement("registration-failed");
	}
    }).catch((error) => {
	// TODO Error details
	console.log("Error occurred: ", error);
	showElement("registration-failed");
    });
}

function notifySubmit(evt) {
    evt.preventDefault();
    var email = document.getElementById("subscribe-email").value;
    //alert("Thanks for registering " + email);
    postRegistration(email);
}

document.getElementById("register-form").onsubmit = notifySubmit;
