/*import { initializeApp } from "https://www.gstatic.com/firebasejs/9.9.4/firebase-app.js";
import {
  getAuth,
  connectAuthEmulator,
  onAuthStateChanged,
  createUserWithEmailAndPassword,
  signInWithEmailAndPassword,
} from "https://www.gstatic.com/firebasejs/9.9.4/firebase-auth.js";

// Setup authentication and wire up events
setupAuth();
if (document.getElementById("btnSignIn")) {
  wireGuiUpEvents();
}
wireUpAuthChange();

// Setup authentication with local or cloud configuration
function setupAuth() {
  let firebaseConfig;
  if (location.hostname === "localhost") {
    firebaseConfig = {
      apiKey: "AIzaSyBoLKKR7OFL2ICE15Lc1-8czPtnbej0jWY",
      projectId: "demo-distributed-systems-kul",
    };
  } else {
    firebaseConfig = {
      // TODO: for level 2, paste your config here
    };
  }

  const firebaseApp = initializeApp(firebaseConfig);
  const auth = getAuth(firebaseApp);
  try {
    auth.signOut();
  } catch (err) { }

  if (location.hostname === "localhost") {
    connectAuthEmulator(auth, "http://localhost:8082", { disableWarnings: true });
  }
}

function wireGuiUpEvents() {
  var email = document.getElementById("email");
  email.textContent = "HAAAAKKK";
  var password = document.getElementById("password");
  var signInButton = document.getElementById("btnSignIn");
  var signUpButton = document.getElementById("btnSignUp");
  var logoutButton = document.getElementById("btnLogout");

  signInButton.addEventListener("click", function () {
    signInWithEmailAndPassword(getAuth(), email.value, password.value)
      .then(function () {
        console.log("signed in");
        window.location.href = 'Order_page.html';
      })
      .catch(function (error) {
        console.log("error signInWithEmailAndPassword:", error.message);
        alert(error.message);
      });
  });

  signUpButton.addEventListener("click", function () {
    createUserWithEmailAndPassword(getAuth(), email.value, password.value)
      .then(function () {
        console.log("account created");
      })
      .catch(function (error) {
        console.log("error createUserWithEmailAndPassword:", error.message);
        alert(error.message);
      });
  });

  logoutButton.addEventListener("click", function () {
    try {
      var auth = getAuth();
      auth.signOut();
    } catch (err) { }
  });
}

function wireUpAuthChange() {
  var auth = getAuth();
  onAuthStateChanged(auth, (user) => {
    console.log("onAuthStateChanged");
    if (!user) {
      console.log("user is null");
      if (document.getElementById("logindiv")) {
        showUnAuthenticated();
      }
      return;
    }
    if (!auth) {
      console.log("auth is null");
      if (document.getElementById("logindiv")) {
        showUnAuthenticated();
      }
      return;
    }
    if (!auth.currentUser) {
      console.log("currentUser is undefined or null");
      if (document.getElementById("logindiv")) {
        showUnAuthenticated();
      }
      return;
    }

    auth.currentUser.getIdTokenResult().then((idTokenResult) => {
      console.log("Hello " + auth.currentUser.email);
      if (document.getElementById("logindiv")) {
        showAuthenticated(auth.currentUser.email);
      }

      console.log("Token: " + idTokenResult.token);
      if (document.getElementById("contentdiv")) {
        fetchData(idTokenResult.token);

      }

    });
  });

}

function fetchData(token) {
  getHello(token);
  whoami(token);
}

function showAuthenticated(username) {
  document.getElementById("namediv").innerHTML = "Hello " + username;
  document.getElementById("logindiv").style.display = "none";
  document.getElementById("contentdiv").style.display = "block";
}

function showUnAuthenticated() {
  document.getElementById("namediv").innerHTML = "";
  //document.getElementById("email").value = "";
  document.getElementById("password").value = "";
  document.getElementById("logindiv").style.display = "block";
  document.getElementById("contentdiv").style.display = "none";
}

function addContent(text) {
  var newElement = document.createElement('div');
  newElement.className = 'content-style';
  newElement.innerHTML = text;
  document.getElementById("contentdiv").appendChild(newElement);
}

function getHello(token) {
  fetch('/api/hello', {
    headers: { Authorization: `Bearer ${token}` }
  })
    .then((response) => response.text())
    .then((data) => {
      console.log(data);
      addContent(data);
      document.getElementById("email").value = data;
    })
    .catch(function (error) {
      console.log(error);
    });
}

function whoami(token) {
  fetch('/api/whoami', {
    headers: { Authorization: 'Bearer ' + token }
  })
    .then((response) => response.json())
    .then((data) => {
      console.log(data.email + data.role);
      addContent("Whoami at rest service: " + data.email + " - " + data.role);
    })
    .catch(function (error) {
      console.log(error);
    });
}
*/
import {
  initializeApp
} from "https://www.gstatic.com/firebasejs/9.9.4/firebase-app.js";
import {
  getAuth,
  connectAuthEmulator,
  onAuthStateChanged,
  createUserWithEmailAndPassword,
  signInWithEmailAndPassword,
} from "https://www.gstatic.com/firebasejs/9.9.4/firebase-auth.js";

// we setup the authentication, and then wire up some key events to event handlers
setupAuth();
wireGuiUpEvents();
wireUpAuthChange();

//setup authentication with local or cloud configuration.
function setupAuth() {
  let firebaseConfig;
  if (location.hostname === "localhost") {
    firebaseConfig = {
      apiKey: "AIzaSyBoLKKR7OFL2ICE15Lc1-8czPtnbej0jWY",
      projectId: "demo-distributed-systems-kul",
    };
  } else {
    firebaseConfig = {
      // TODO: for level 2, paste your config here
    };
  }

  // signout any existing user. Removes any token still in the auth context
  const firebaseApp = initializeApp(firebaseConfig);
  const auth = getAuth(firebaseApp);
  try {
    auth.signOut();
  } catch (err) { }

  // connect to local emulator when running on localhost
  if (location.hostname === "localhost") {
    connectAuthEmulator(auth, "http://localhost:8082", { disableWarnings: true });
  }
}

function wireGuiUpEvents() {
  // Get references to the email and password inputs, and the sign in, sign out and sign up buttons
  var email = document.getElementById("email");
  var password = document.getElementById("password");
  var signInButton = document.getElementById("btnSignIn");
  var signUpButton = document.getElementById("btnSignUp");
  var logoutButton = document.getElementById("btnLogout");

  // Add event listeners to the sign in and sign up buttons
  signInButton.addEventListener("click", function () {
    // Sign in the user using Firebase's signInWithEmailAndPassword method

    signInWithEmailAndPassword(getAuth(), email.value, password.value)
        .then(function () {

          console.log("signedin");
        })
        .catch(function (error) {
          // Show an error message
          console.log("error signInWithEmailAndPassword:")
          console.log(error.message);
          alert(error.message);
        });
  });

  signUpButton.addEventListener("click", function () {
    // Sign up the user using Firebase's createUserWithEmailAndPassword method

    createUserWithEmailAndPassword(getAuth(), email.value, password.value)
        .then(function () {
          console.log("created");
        })
        .catch(function (error) {
          // Show an error message
          console.log("error createUserWithEmailAndPassword:");
          console.log(error.message);
          alert(error.message);
        });
  });

  logoutButton.addEventListener("click", function () {
    try {
      var auth = getAuth();
      auth.signOut();
    } catch (err) { }
  });

}

function wireUpAuthChange() {

  var auth = getAuth();
  onAuthStateChanged(auth, (user) => {
    console.log("onAuthStateChanged");
    if (user == null) {
      console.log("user is null");
      showUnAuthenticated();
      return;
    }
    if (auth == null) {
      console.log("auth is null");
      showUnAuthenticated();
      return;
    }
    if (auth.currentUser === undefined || auth.currentUser == null) {
      console.log("currentUser is undefined or null");
      showUnAuthenticated();
      return;
    }

    auth.currentUser.getIdTokenResult().then((idTokenResult) => {
      console.log("Hello " + auth.currentUser.email)

      //update GUI when user is authenticated
      showAuthenticated(auth.currentUser.email);

      console.log("Token: " + idTokenResult.token);

      //fetch data from server when authentication was successful.
      var token = idTokenResult.token;
      fetchData(token);

    });

  });
}

function fetchData(token) {
  getHello(token);
  whoami(token);
}
function showAuthenticated(username) {
  document.getElementById("namediv").innerHTML = "Hello " + username;
  document.getElementById("logindiv").style.display = "none";
  document.getElementById("contentdiv").style.display = "block";
}

function showUnAuthenticated() {
  document.getElementById("namediv").innerHTML = "";
  document.getElementById("email").value = "";
  document.getElementById("password").value = "";
  document.getElementById("logindiv").style.display = "block";
  document.getElementById("contentdiv").style.display = "none";
}

function addContent(text) {
  document.getElementById("contentdiv").innerHTML += (text + "<br/>");
}

// calling /api/hello on the rest service to illustrate text based data retrieval
function getHello(token) {

  fetch('/api/hello', {
    headers: { Authorization: 'Bearer ' + token }
  })
      .then((response) => {
        return response.text();
      })
      .then((data) => {

        console.log(data);
        addContent(data);
      })
      .catch(function (error) {
        console.log(error);
      });
}
// calling /api/whoami on the rest service to illustrate JSON based data retrieval
function whoami(token) {

  fetch('/api/whoami', {
    headers: { Authorization: 'Bearer ' + token }
  })
      .then((response) => {
        return response.json();
      })
      .then((data) => {
        console.log(data.email + data.role);
        addContent("Whoami at rest service: " + data.email + " - " + data.role);
        const newButton = document.createElement('button');


        newButton.textContent = 'Change Layout';
        newButton.onclick = changeLayout;
        document.body.appendChild(newButton);
      })
      .catch(function (error) {
        console.log(error);
      });
}

function changeLayout() {
  // Change the main style sheet
  const linkElement = document.getElementById('index.css');
  linkElement.href = 'Checkout.css';

  // Remove the old button
  const oldButton = document.querySelector('button');
  oldButton.parentNode.removeChild(oldButton);

  // Create a new element for the updated layout
  const newLayout = document.createElement('div');
  newLayout.innerHTML = '<h2>New Layout</h2><p>This is the new layout.</p>';

  // Append the new layout to the body
  document.body.appendChild(newLayout);

}
