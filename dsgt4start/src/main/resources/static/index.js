import { initializeApp } from "https://www.gstatic.com/firebasejs/9.9.4/firebase-app.js";
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
  var password = document.getElementById("password");
  var signInButton = document.getElementById("btnSignIn");
  var signUpButton = document.getElementById("btnSignUp");
  var logoutButton = document.getElementById("btnLogout");

  signInButton.addEventListener("click", function () {
    signInWithEmailAndPassword(getAuth(), email.value, password.value)
      .then(function () {
        console.log("signed in");
        window.location.href = 'Order_page.html'; //
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
  document.getElementById("email").value = "";
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
