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
let globalData;
let mostExpensive;

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
          ////////////////////////////  Add check for availability if not go to error page "service not available"
          displayOrderPage();
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
  fetchMostExpensive(token);
  console.log('------');
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
  fetch('/api/askDelivery', {
    headers: { Authorization: 'Bearer ' + token }
  })
      .then(response => response.text())
      .then(data => {
        console.log(data);

        globalData=data;
        addContent(data);
      })
      .catch(error => console.error("Error fetching /api/askDelivery:", error));
}


function fetchMostExpensive(token) {
  fetch('/api/askWine', {
    headers: { Authorization: 'Bearer ' + token }
  })
      .then(response => response.text())
      .then(data => {
        console.log(data);

        mostExpensive=data;
        addContent(data);
      })
      .catch(error => console.error("Error fetching /api/askWine:", error));
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

function displayOrderPage() {
  // Clear the existing content
  document.body.innerHTML = '';

  // Load new CSS file
  const newStylesheet = document.createElement('link');
  newStylesheet.rel = 'stylesheet';
  newStylesheet.href = '../cssFiles/Order_page.css';
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
        
      </select>
      <br>
      <input type="submit" value="Add to Basket">
    </form>
    <p id="basket">Basket : 0, Total price: $0.00</p>
    
  `;
  document.body.appendChild(newContent);
  // I still need to add the add to basket function!!!!
  const checkoutButton = document.createElement('button');
  checkoutButton.id = 'btnCheckout';
  checkoutButton.innerText = 'Go to checkout';
  document.body.appendChild(checkoutButton);

  checkoutButton.addEventListener('click', () => {
    displayCheckoutPage();
  });

  // Re-setup the logout button event handler
  //const logoutButton = document.createElement('button');
  //logoutButton.id = 'btnLogout';
  //logoutButton.innerText = 'Logout';
  //document.body.appendChild(logoutButton);

  //logoutButton.addEventListener('click', () => {
  //  getAuth().signOut().catch(err => console.error('Error signing out:', err));
  //  location.reload(); // Refresh the page to show the login form again
  //});
}

function displayCheckoutPage() {
  // Clear the existing content
  document.body.innerHTML = '';
  // Load new CSS file
  const newStylesheet = document.createElement('link');
  newStylesheet.rel = 'stylesheet';
  newStylesheet.href = '../cssFiles/Checkout_page.css';
  document.head.appendChild(newStylesheet);
  //callGetWineCardService();

// Create new content
  const newHeader = document.createElement('header');
  newHeader.className = 'site-header';
  newHeader.innerHTML = `
    <h1>Bam <u>Booz</u>led</h1>
  `;
  document.body.appendChild(newHeader);

  const checkoutContent = document.createElement('div');
  checkoutContent.innerHTML = `
    <h1>Checkout</h1>
    <p id="basket"></p>
    <p id="total"></p>
    <form onsubmit="event.preventDefault(); pay();">
        <label for="cardNumber">Card Number:</label>
        <input type="text" id="cardNumber" name="cardNumber">
            <br>
        <label for="expiryDate">Expiry Date:</label>
        <input type="text" id="expiryDate" name="expiryDate">
            <br>
        <label for="cvv">CVV:</label>
        <input type="text" id="cvv" name="cvv">
      
    </form>
  `;
  document.body.appendChild(checkoutContent);
  // I still need to add the add to basket function!!!!

  const confirmationButton = document.createElement('button');
  confirmationButton.id = 'btnCheckout';
  confirmationButton.innerText = "Go to checkout";
  document.body.appendChild(confirmationButton);
  confirmationButton.addEventListener('click', () => {
    ///////PAYMENT CHECK / ORDERING INFO NEEDS TO BE SENT (PAYMENT FUNCTION STILL NEEDS TO BE INTEGRATED)!!!!
    displayConfirmationPage();

  });
  const confirmationField = document.createElement('input');
  confirmationField.type = 'text'; // Set the input type to text
  confirmationField.id = 'txtCheckout'; // Set the ID
  confirmationField.value = mostExpensive; // Set the initial value

// You can optionally set the readonly attribute to prevent editing
// confirmationField.readOnly = true;

  document.body.appendChild(confirmationField)

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

function displayConfirmationPage() {
  // Clear the existing content
  document.body.innerHTML = '';

  // Load new CSS file
  const confirmationStyleSheet = document.createElement('link');
  confirmationStyleSheet.rel = 'stylesheet';
  confirmationStyleSheet.href = '../cssFiles/Confirmation_page.css';
  document.head.appendChild(confirmationStyleSheet);

  // Create new content
  const confirmationContent = document.createElement('div');
  confirmationContent.innerHTML = `
    <header class="site-header">
      <h1>Bam <u>Booz</u>led</h1>
    </header>
    <div class="confirmation-container">
      <div class="checkmark">✓</div>
      <div class="message">
        <p>Your delivery is being processed, and you will get a confirmation mail shortly.</p>
      </div>
      <div class="details">
        <h3>Delivery details</h3>
        <p id="orderNumber">Your order number:</p>
        <p id="beer">Beer:</p>
        <p id="wine">Wine:</p>
        <p id="whiskey">Whiskey:</p>
        <p id="totalPrice">Total price:</p>
      </div>
      <div class="details">
        <h3>Delivery info</h3>
        <p id="expectedDeliveryDate">Expected delivery date:</p>
        <p id="deliveredBy">Delivered by:</p>
        <p id="contactDetail">Contact Detail:</p>
        <p id="phoneNumber">PhoneNumber :</p>
        <p id="email">Email :</p>
      </div>
      <div class="options">
        <a href="mailto:koenraad.goddefroy@hotmail.com">Cancel order</a>
      </div>
    </div>
  `;
  document.body.appendChild(confirmationContent);

  // Populate the fields with data from globalData
  if (globalData) {
    const data = JSON.parse(globalData); // Assuming globalData is a JSON string

    document.getElementById('orderNumber').innerText = `Your order number: ${data.orderNumber}`;
    document.getElementById('beer').innerText = `Beer: ${data.beer}`;
    document.getElementById('wine').innerText = `Wine: ${data.wine}`;
    document.getElementById('whiskey').innerText = `Whiskey: ${data.whiskey}`;
    document.getElementById('totalPrice').innerText = `Delivery price: ${data.totalPrice}`;
    document.getElementById('expectedDeliveryDate').innerText = `Expected delivery date: ${data.deliveryDate}`;
    document.getElementById('deliveredBy').innerText = `Delivered by: ${data.deliveryPerson.name}`;
    document.getElementById('phoneNumber').innerText = `PhoneNumber : ${data.deliveryPerson.phoneNumber}`;
    document.getElementById('email').innerText = `Email : ${data.deliveryPerson.email}`;
  }

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

////////////////////////////////
/*
const axios = require('axios');
const xml2js = require('xml2js');

async function callGetWineCardService() {

  const xmlRequest = `
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tns="http://winemenu.io/gt/webservice">
            <soapenv:Header/>
            <soapenv:Body>
                <tns:getWineCardRequest/>
            </soapenv:Body>
        </soapenv:Envelope>`;

  try {

    const response = await axios.post('http://dapp.uksouth.cloudapp.azure.com:12000/ws/wines.wsdl', xmlRequest, {
      headers: { 'Content-Type': 'text/xml' }
    });

    // Parse XML response to JSON
    const jsonResponse = await xml2js.parseStringPromise(response.data, { explicitArray: false });

    // Extract and process the data from the response
    const wines = jsonResponse['soapenv:Envelope']['soapenv:Body']['tns:getWineCardResponse']['tns:wine'];

    console.log("Wines:", wines);

    // Process each wine
    if (Array.isArray(wines)) {
      wines.forEach(wine => {
        console.log(`Name: ${wine.name}`);
        console.log(`Year: ${wine.year}`);
        console.log(`Price: ${wine.price}`);
        console.log(`Percentage: ${wine.percentage}`);
        console.log(`Taste Palette: ${wine.tastePallet}`);
        console.log(`Quantity: ${wine.quantity}`);
        console.log('------');
      });
    } else if (wines) {
      // Single wine entry
      console.log(`Name: ${wines.name}`);
      console.log(`Year: ${wines.year}`);
      console.log(`Price: ${wines.price}`);
      console.log(`Percentage: ${wines.percentage}`);
      console.log(`Taste Palette: ${wines.tastePallet}`);
      console.log(`Quantity: ${wines.quantity}`);
    } else {
      console.log('No wines found.');
    }

  } catch (error) {
    console.error('Error calling SOAP service:', error);
  }
}

async function checkEndpoint(url) {
  try {
    const response = await axios.get(url);
    if (response.status === 200) {
      const confirmationButton = document.createElement('button');
      confirmationButton.id = 'btnCheckout';
      confirmationButton.innerText = '1';
      document.body.appendChild(confirmationButton);
      console.log("Endpoint is reachable:", url);
    } else {
      const confirmationButton = document.createElement('button');
      confirmationButton.id = 'btnCheckout';
      confirmationButton.innerText = '2';
      document.body.appendChild(confirmationButton);
      console.error("Unexpected response status:", response.status);
    }
  } catch (error) {
    const confirmationButton = document.createElement('button');
    confirmationButton.id = 'btnCheckout';
    confirmationButton.innerText = '3';
    document.body.appendChild(confirmationButton);
    console.error("Error reaching the endpoint:", error.message);
  }
}
*/
// Use this function before calling the SOAP service
