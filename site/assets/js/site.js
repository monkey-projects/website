// Javascript functions for the website

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
	if (res.status < 400) {
	    // ok
	}
    }).catch((error) => {
	// TODO Handle error
    });
}

function notifySubmit(evt) {
    evt.preventDefault();
    var email = document.getElementById("subscribe-email").value;
    //alert("Thanks for registering " + email);
    postRegistration(email);
}

document.getElementById("register-form").onsubmit = notifySubmit;
