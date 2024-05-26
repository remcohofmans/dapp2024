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
//////////////////////////////////////::



import { initializeApp } from "https://www.gstatic.com/firebasejs/9.9.4/firebase-app.js";
import {
  getAuth,
  connectAuthEmulator,
  onAuthStateChanged,
  createUserWithEmailAndPassword,
  signInWithEmailAndPassword,
} from "https://www.gstatic.com/firebasejs/9.9.4/firebase-auth.js";

// Setup authentication, wire up events, and handle auth state changes
setupAuth();
setupEventHandlers();
handleAuthStateChanges();

function setupAuth() {
  const firebaseConfig = location.hostname === "localhost" ? {
    apiKey: "AIzaSyBoLKKR7OFL2ICE15Lc1-8czPtnbej0jWY",
    projectId: "demo-distributed-systems-kul",
  } : {
    // TODO: for level 2, paste your config here
  };

  const firebaseApp = initializeApp(firebaseConfig);
  const auth = getAuth(firebaseApp);
  auth.signOut().catch(err => console.error('Error signing out:', err));

  if (location.hostname === "localhost") {
    connectAuthEmulator(auth, "http://localhost:8082", { disableWarnings: true });
  }
}

function setupEventHandlers() {
  const emailInput = document.getElementById("email");
  const passwordInput = document.getElementById("password");
  const signInButton = document.getElementById("btnSignIn");
  const signUpButton = document.getElementById("btnSignUp");
  const logoutButton = document.getElementById("btnLogout");

  signInButton.addEventListener("click", () => {
    signInWithEmailAndPassword(getAuth(), emailInput.value, passwordInput.value)
        .then(() => {
          console.log("Signed in successfully");
          displayNewPage();
        })
        .catch(error => {
          console.error("Error signing in:", error.message);
          alert(error.message);
        });
  });

  signUpButton.addEventListener("click", () => {
    createUserWithEmailAndPassword(getAuth(), emailInput.value, passwordInput.value)
        .then(() => console.log("Account created successfully"))
        .catch(error => {
          console.error("Error creating account:", error.message);
          alert(error.message);
        });
  });

  logoutButton.addEventListener("click", () => {
    getAuth().signOut().catch(err => console.error('Error signing out:', err));
  });
}

function handleAuthStateChanges() {
  const auth = getAuth();
  onAuthStateChanged(auth, user => {
    if (!user) {
      showUnauthenticated();
      return;
    }

    user.getIdTokenResult().then(idTokenResult => {
      showAuthenticated(user.email);
      fetchData(idTokenResult.token);
    }).catch(err => {
      console.error("Error getting ID token result:", err);
      showUnauthenticated();
    });
  });
}

function fetchData(token) {
  fetchHello(token);
  fetchWhoAmI(token);
}

function showAuthenticated(username) {
  document.getElementById("namediv").innerHTML = "Hello " + username;
  document.getElementById("logindiv").style.display = "none";
  document.getElementById("contentdiv").style.display = "block";
}

function showUnauthenticated() {
  document.getElementById("namediv").innerHTML = "";
  document.getElementById("email").value = "";
  document.getElementById("password").value = "";
  document.getElementById("logindiv").style.display = "block";
  document.getElementById("contentdiv").style.display = "none";
}

function addContent(text) {
  document.getElementById("contentdiv").innerHTML += text + "<br/>";
}

function fetchHello(token) {
  fetch('/api/hello', {
    headers: { Authorization: 'Bearer ' + token }
  })
      .then(response => response.text())
      .then(data => {
        console.log(data);
        addContent(data);
      })
      .catch(error => console.error("Error fetching /api/hello:", error));
}

function fetchWhoAmI(token) {
  fetch('/api/whoami', {
    headers: { Authorization: 'Bearer ' + token }
  })
      .then(response => response.json())
      .then(data => {
        console.log(data.email, data.role);
        addContent(`Whoami at rest service: ${data.email} - ${data.role}`);
      })
      .catch(error => console.error("Error fetching /api/whoami:", error));
}

function displayNewPage() {
  // Clear the existing content
  document.body.innerHTML = '';

  // Load new CSS file
  const newStylesheet = document.createElement('link');
  newStylesheet.rel = 'stylesheet';
  newStylesheet.href = '../cssFiles/Order_page.css'; // Path to the new CSS file
  document.head.appendChild(newStylesheet);

  // Create new content
  const newHeader = document.createElement('header');
  newHeader.className = 'site-header';
  newHeader.innerHTML = `
    <h1>Bam <u>Booz</u>led</h1>
  `;
  document.body.appendChild(newHeader);

  const newContent = document.createElement('div');
  newContent.innerHTML = `
    <h1>Order Your Spirits</h1>
    <form onsubmit="event.preventDefault(); addToBasket();">
      <label for="drink">Choose your fine spirits here:</label>
      <select id="drink" name="drink">
        <!-- Options will be populated by JavaScript -->
      </select>
      <br>
      <input type="submit" value="Add to Basket">
    </form>
    <p id="basket">Basket : 0, Total price: $0.00</p>
    <button onclick="checkout()">Go to Checkout</button>
  `;
  document.body.appendChild(newContent);

  // Load the external JavaScript file
  const script = document.createElement('script');
  script.src = '../oldJavaScriptFiles/Order_page.js';
  document.body.appendChild(script);

  // Re-setup the logout button event handler
  const logoutButton = document.createElement('button');
  logoutButton.id = 'btnLogout';
  logoutButton.innerText = 'Logout';
  document.body.appendChild(logoutButton);

  logoutButton.addEventListener('click', () => {
    getAuth().signOut().catch(err => console.error('Error signing out:', err));
    location.reload(); // Refresh the page to show the login form again
  });
}
