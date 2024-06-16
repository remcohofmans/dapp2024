import { initializeApp } from "https://www.gstatic.com/firebasejs/9.9.4/firebase-app.js";
import { getFirestore, connectFirestoreEmulator, doc, setDoc, runTransaction } from "https://www.gstatic.com/firebasejs/9.9.4/firebase-firestore.js";
import {
    getAuth,
    connectAuthEmulator,
    onAuthStateChanged,
    createUserWithEmailAndPassword,
    signInWithEmailAndPassword
} from "https://www.gstatic.com/firebasejs/9.9.4/firebase-auth.js";

// Setup authentication, wire up events, and handle auth state changes
setupAuth();
setupEventHandlers();
handleAuthStateChanges();
let globalData;

let wines = [];
let liquors = [];
let basketWines = [];
let basketLiquors = [];
let basket = {};
let totalPrice = 0;
let token;
let isManager = false;

function setupAuth() {
    const firebaseConfig = {
        apiKey: "AIzaSyBoLKKR7OFL2ICE15Lc1-8czPtnbej0jWY",
        projectId: "demo-distributed-systems-kul",
    };

    const firebaseApp = initializeApp(firebaseConfig);
    const auth = getAuth(firebaseApp);
    const db = getFirestore(firebaseApp);

    // Connect to emulators if running locally
    if (location.hostname === "localhost") {
        connectAuthEmulator(auth, "http://localhost:8082", { disableWarnings: true });
        connectFirestoreEmulator(db, "localhost", 8084);
    }

    return { auth, db };
}

const { auth, db } = setupAuth();

// Function to fetch data from the first URL (liquor-info)
async function fetchLiquorData() {
    try {
        const response = await fetch("http://localhost:8090/api/liquor-info");
        const data = await response.json();
        return data; // Return the fetched data
    } catch (error) {
        console.error("Error fetching liquor data:", error);
        throw error; // Throw error to handle it outside
    }
}

// Function to fetch data from the second URL (wines)
async function fetchWineData() {
    try {
        const response = await fetch("http://localhost:8080/wines");
        const data = await response.json();
        return data; // Return the fetched data
    } catch (error) {
        console.error("Error fetching wine data:", error);
        throw error; // Throw error to handle it outside
    }
}

// Function to populate Firestore with fetched data
async function populateFirestore() {
    try {
        // Fetch data from both URLs
        const liquorData = await fetchLiquorData();
        const wineData = await fetchWineData();

        // Process and save liquor data to Firestore
        for (const item of liquorData) {
            const { brand, price, alcoholPercentage, volume, type, quantity } = item;
            const liquorDocRef = doc(db, "liquors", brand); // Assuming "brand" as document ID
            await setDoc(liquorDocRef, {
                brand,
                price,
                alcoholPercentage,
                volume,
                type,
                quantity
            });
        }

        // Process and save wine data to Firestore
        for (const item of wineData) {
            const { name, year, price, percentage, tastePallet, quantity } = item;
            const wineDocRef = doc(db, "wines", name); // Assuming "name" as document ID
            await setDoc(wineDocRef, {
                name,
                year,
                price,
                percentage,
                tastePallet,
                quantity
            });
        }

        // Initialize inventory collection with spirits and their quantities
        const inventoryData = {};

        // Add liquors to inventoryData
        for (const item of liquorData) {
            const { brand, quantity } = item;
            inventoryData[brand] = { quantity };
        }

        // Add wines to inventoryData
        for (const item of wineData) {
            const { name, quantity } = item;
            inventoryData[name] = { quantity };
        }

        // Create or update inventory collection
        const inventoryRef = doc(db, "inventory", "items");
        await setDoc(inventoryRef, inventoryData);

        console.log("Data successfully populated to Firestore.");
    } catch (error) {
        console.error("Error populating Firestore:", error);
    }
}
populateFirestore();




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
                //fetchWhoAmI(token);
                fetchData(token);
                //////////////////////////
            })
            .catch(error => {
                console.error("Error signing in:", error.message);
                alert(error.message);
            });
    });
    signUpButton.addEventListener("click", () => {
        createUserWithEmailAndPassword(getAuth(), emailInput.value, passwordInput.value)
            .then(() => {
                console.log("Account created successfully");
                displaySignedUpPage();
            })
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
            token = idTokenResult.token;
            console.log("Token: ", token);
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
    const basketWinesQueryParam = basketWines.join(',');
    const basketLiquorsQueryParam = basketLiquors.join(',');
    const url = `/api/askDelivery?basketWines=${basketWinesQueryParam}&basketLiquors=${basketLiquorsQueryParam}`;

    fetch(url, {
        headers: { Authorization: 'Bearer ' + token }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.text();
        })
        .then(data => {
            try {
                //globalData = JSON.parse(data);
                globalData = data;
                addContent(data);
            } catch (error) {
                console.error("Error parsing JSON:", error);
            }
        })
        .catch(error => console.error("Error fetching /api/askDelivery:", error));
}

function fetchWhoAmI(token) {
    fetch('/api/whoami', {
        headers: { Authorization: 'Bearer ' + token }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            console.log(data.email, data.role);

            if (data.role === 'manager') {
                isManager = true;
                console.log('User is a manager');
                displayManagerPage();
                addContent('You have manager access.');

            }
            else {
                displayOrderPage();
            }
            addContent(`Whoami at rest service: ${data.email} - ${data.role}`);
        })
        .catch(error => {
            console.error("Error fetching /api/whoami:", error);
            addContent(`Error fetching whoami: ${error.message}`);
            displayOrderPage();
        });

    console.log(isManager);
//    if (isManager){
//        displayManagerPage();
//    }
//    else {
//        console.log("not a manager")
//        displayOrderPage();
//    }
}

function displayOrderPage() {
    basketWines = [];
    basketLiquors = [];
    basket = {};
    totalPrice = 0;
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
    <form id="orderForm">
      <label for="drink">Choose your fine wine here:</label>
      <select id="drink" name="drink"></select>
      <input type="submit" value="Add to Basket">
    </form>
    <form id="liquorForm">
      <label for="liquor">Choose your fine liquor here:</label>
      <select id="liquor" name="liquor"></select>
      <input type="submit" value="Add to Basket">
    </form>
    <p id="basket">Basket: 0, Total price: $0.00</p>
  `;
    document.body.appendChild(newContent);

    document.getElementById("orderForm").addEventListener("submit", (event) => {
        event.preventDefault();
        addToBasket('wine');
    });

    document.getElementById("liquorForm").addEventListener("submit", (event) => {
        event.preventDefault();
        addToBasket('liquor');
    });

    fetchAndPopulateWines();
    fetchAndPopulateLiquors();
    document.addEventListener("DOMContentLoaded", () => {
        fetchAndPopulateWines();
        fetchAndPopulateLiquors();
    });

    function fetchAndPopulateWines() {
        fetch("http://localhost:8080/wines")
            .then(response => response.json())
            .then(data => {
                wines = data;
                const selectElement = document.getElementById("drink");
                selectElement.innerHTML = "";

                data.forEach(wine => {
                    const option = document.createElement("option");
                    option.value = wine.name;
                    option.textContent = `${wine.name} - $${wine.price}`;
                    selectElement.appendChild(option);
                });
            })
            .catch(error => console.error("Error fetching wines:", error));
    }

    function fetchAndPopulateLiquors() {
        fetch("http://localhost:8090/api/liquor-names")
            .then(response => response.json())
            .then(data => {

                if (!Array.isArray(data)) {
                    throw new Error("Expected an array of liquor strings");
                }

                liquors = data.map(item => {
                    const [name, price] = item.split(" - $");
                    return { name: name.trim(), price: parseFloat(price.trim()) };
                });

                const selectElement = document.getElementById("liquor");
                selectElement.innerHTML = "";

                liquors.forEach(liquor => {
                    const option = document.createElement("option");
                    option.value = liquor.name;
                    option.textContent = `${liquor.name} - $${liquor.price.toFixed(2)}`;
                    selectElement.appendChild(option);
                });
            })
            .catch(error => console.error("Error fetching liquors:", error));
    }


    function addToBasket(type) {
        const selectElement = document.getElementById(type === 'wine' ? 'drink' : 'liquor');
        const selectedName = selectElement.value;
        const selectedItem = type === 'wine' ? wines.find(wine => wine.name === selectedName) : liquors.find(liquor => liquor.name === selectedName);

        if (selectedItem) {
            if (basket[selectedName]) {
                basket[selectedName].quantity += 1;
            } else {
                basket[selectedName] = { ...selectedItem, quantity: 1 };
            }

            totalPrice += selectedItem.price;
            updateBasketDisplay();
        }
    }

    function updateBasketDisplay() {
        const basketElement = document.getElementById("basket");
        const basketItems = Object.values(basket);
        const totalItems = basketItems.reduce((sum, item) => sum + item.quantity, 0);
        basketElement.textContent = `Basket: ${totalItems}, Total price: $${totalPrice.toFixed(2)}`;
    }

    const checkoutButton = document.createElement('button');
    checkoutButton.id = 'btnCheckout';
    checkoutButton.innerText = 'Go to checkout';
    document.body.appendChild(checkoutButton);


    checkoutButton.addEventListener('click', async () => {
        let basketWines = [];
        let basketLiquors = [];
        let totalPrice = 0;

        Object.values(basket).forEach(item => {
            let individualItem = { name: item.name, quantity: item.quantity, price: item.price };
            if (wines.find(wine => wine.name === item.name)) {
                basketWines.push(individualItem);
            } else if (liquors.find(liquor => liquor.name === item.name)) {
                basketLiquors.push(individualItem);
            }
            totalPrice += item.price * item.quantity;
        });

        const orderDetails = {
            basketWines,
            basketLiquors,
            totalPrice
        };

        try {
            console.log("TESTINGGGG");
            await confirmOrder(orderDetails);

            displayCheckoutPage(orderDetails);

        } catch (error) {
            alert('Order confirmation failed: ' + error.message);
            displayOrderPage();
        }
    });
}
function saveOrderToFirestore(orderDetails) {
    const db = getFirestore();  // Assuming you already have a reference to Firestore

    // Generate a unique order ID
    const orderId = orderDetails.orderNumber; // Implement this function to generate a unique ID

    const orderRef = doc(db, 'orders', orderId);

    setDoc(orderRef, orderDetails)
        .then(() => {
            console.log('Order saved successfully to Firestore');
        })
        .catch((error) => {
            console.error('Error saving order to Firestore:', error);
        });
}

async function displayCheckoutPage(orderDetails) {
    // Clear the existing content
    document.body.innerHTML = '';

    // Load new CSS file
    const newStylesheet = document.createElement('link');
    newStylesheet.rel = 'stylesheet';
    newStylesheet.href = '../cssFiles/Checkout_page.css';
    document.head.appendChild(newStylesheet);

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
    <button id="confirmButton">Confirm Order</button>
  `;
    document.body.appendChild(checkoutContent);

    const confirmButton = document.getElementById('confirmButton');
    confirmButton.addEventListener('click', async () => {
        try {
            //await confirmOrder(orderDetails);
            displayConfirmationPage(orderDetails.basketWines, orderDetails.basketLiquors, orderDetails.totalPrice);
        } catch (error) {
            alert('Order confirmation failed: ' + error.message);
            basketWines = [];
            basketLiquors = [];
            basket = {};
            totalPrice = 0;
            displayOrderPage();
        }
    });

    const logoutButton = document.createElement('button');
    logoutButton.id = 'btnLogout';
    logoutButton.innerText = 'Logout';
    document.body.appendChild(logoutButton);

    logoutButton.addEventListener('click', () => {
        getAuth().signOut().catch(err => console.error('Error signing out:', err));
        location.reload();
    });
}



function checkInventory(orderDetails) {
    const { basketWines, basketLiquors } = orderDetails;

    return new Promise((resolve, reject) => {
        for (const wine of basketWines) {
            const remainingStock = getRemainingStock(wine.name);
            if (remainingStock < wine.quantity) {
                reject(new Error(`Insufficient stock for ${wine.name}.`));
                return; // Early exit if an item is missing
            }
        }

        for (const liquor of basketLiquors) {
            const remainingStock = getRemainingStock(liquor.name);
            if (remainingStock < liquor.quantity) {
                reject(new Error(`Insufficient stock for ${liquor.name}.`));
                return; // Early exit if an item is missing
            }
        }

        resolve(true); // All items have sufficient stock
    });
}
async function displayManagerPage() {
    const confirmationStyleSheet = document.createElement('link');
    confirmationStyleSheet.rel = 'stylesheet';
    confirmationStyleSheet.href = '../cssFiles/Manager_page.css';
    document.head.appendChild(confirmationStyleSheet);

    // Clear the existing content
    document.body.innerHTML = '';

    // Create new content
    const confirmationContent = document.createElement('div');
    confirmationContent.innerHTML = `
        <header class="site-header">
            <h1>Bam <u>Booz</u>led</h1>
        </header>
        <div class="confirmation-container">
            <div class="message">
                <p>Welcome to the manager page</p>
            </div>
            <div class="details">
                <h3>All deliveries</h3>
                <div id="orders"></div>
                <p id="totalDeliveries">Total number of deliveries: 0</p>
            </div>
        </div>
    `;
    document.body.appendChild(confirmationContent);

    // Fetch all orders from the API
    try {
        const response = await fetch('/api/getAllOrders', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        // Check if the response is OK (status code 200-299)
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const orders = await response.json();
        console.log('Orders:', orders); // Log the response to check its structure

        // Validate the response structure
        if (!Array.isArray(orders)) {
            throw new Error('Invalid data format: expected an array');
        }

        const ordersContainer = document.getElementById('orders');
        ordersContainer.innerHTML = '';

        orders.forEach((order, index) => {
            // Validate order structure
            if (!order.orderId || !order.customer || !order.items || !order.deliveryInfo) {
                console.error('Invalid order format:', order);
                return;
            }

            const orderDiv = document.createElement('div');
            orderDiv.classList.add('order');
            orderDiv.innerHTML = `
                <p><b>Delivery ${index + 1}</b></p>
                <p>Order ID: ${order.orderId}</p>
                <p>Customer: ${order.customer}</p>
                <p>Items: ${order.items.join(', ')}</p>
                <p>Delivery Address: ${order.deliveryInfo.address}</p>
                <p>Delivery Time: ${order.deliveryInfo.deliveryTime}</p>
                <p>Status: ${order.deliveryInfo.deliveryStatus}</p>
            `;
            ordersContainer.appendChild(orderDiv);
        });

        // Update total number of deliveries
        document.getElementById('totalDeliveries').innerHTML = `Total number of deliveries: <b>${orders.length}</b>`;

        const logoutButton = document.createElement('button');
        logoutButton.id = 'btnLogout';
        logoutButton.innerText = 'Logout';
        document.body.appendChild(logoutButton);

        const orderButton = document.createElement('orderButton');
        orderButton.id = 'orderButton';
        orderButton.innerText = 'Order';
        document.body.appendChild(orderButton);
        orderButton.addEventListener('click', () => {
            displayOrderPage();
        });
        logoutButton.addEventListener('click', () => {
            getAuth().signOut().catch(err => console.error('Error signing out:', err));
            location.reload();
        });
    } catch (error) {
        // Clear the existing content
        document.body.innerHTML = '';

        // Load new CSS file
        const newStylesheet = document.createElement('link');
        newStylesheet.rel = 'stylesheet';
        newStylesheet.href = '../cssFiles/Checkout_page.css';
        document.head.appendChild(newStylesheet);

        // Create new content
        const newHeader = document.createElement('header');
        newHeader.className = 'site-header';
        newHeader.innerHTML = `
            <h1>Bam <u>Booz</u>led</h1>
        `;
        document.body.appendChild(newHeader);

        const errorContent = document.createElement('div');
        errorContent.innerHTML = `
            <h1>Server Error</h1>
            <p>We are currently unable to contact the server. Please try again later.</p>
        `;
        document.body.appendChild(errorContent);

        const logoutButton = document.createElement('button');
        logoutButton.id = 'btnLogout';
        logoutButton.innerText = 'Logout';
        document.body.appendChild(logoutButton);

        logoutButton.addEventListener('click', () => {
            getAuth().signOut().catch(err => console.error('Error signing out:', err));
            location.reload();
        });

        console.error('Error fetching orders:', error);
    }
}
function displaySignedUpPage() {
    const SignUpPageStyleSheet = document.createElement('link');
    SignUpPageStyleSheet.rel = 'stylesheet';
    SignUpPageStyleSheet.href = '../cssFiles/SignUp_page.css';
    document.head.appendChild(SignUpPageStyleSheet);

    // Clear the existing content
    document.body.innerHTML = '';

    // Create new content
    const signupContent = document.createElement('div');
    signupContent.innerHTML = `
        <header class="site-header">
            <h1>Bam <u>Booz</u>led</h1>
        </header>
        <div class="confirmation-container">
            <div class="message">
                <p>Thanks for using our service</p>
                <p>You can know log in and start ordering your drinks</p>
            </div>
        </div>
    `;
    document.body.appendChild(signupContent);
    const logoutButton = document.createElement('button');
    logoutButton.id = 'btnLogout';
    logoutButton.innerText = 'Logout';
    document.body.appendChild(logoutButton);

    logoutButton.addEventListener('click', () => {
        getAuth().signOut().catch(err => console.error('Error signing out:', err));
        location.reload();
    });
    console.log("New user is registered");
}





function displayConfirmationPage(basketWines, basketLiquors, totalPrice) {
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
      <p id="wineDetails">Wine:</p>
      <p id="liquorDetails">Liquor:</p>
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

    // TESTING
    // let deliveryDate = "To be determined";
    // const deliveryPerson = {
    //     name: 'Delivery Service',
    //     phoneNumber: '+32123 56 78 90',
    //     email: 'delivery@example.com'
    // };
     let orderNumber = generateOrderNumber();

     if(globalData) {
         console.log("TESTING inside globalData");
         const data = JSON.parse(globalData);


         // Display order details
         document.getElementById('orderNumber').innerText = `Your order number: ${orderNumber}`;
         document.getElementById('totalPrice').innerText = `Total price: $${totalPrice.toFixed(2)}`;
         document.getElementById('expectedDeliveryDate').innerText = `Expected delivery date: ${data.deliveryDate}`;
         document.getElementById('deliveredBy').innerText = `Delivered by: ${data.deliveryPerson.name}`;
         document.getElementById('phoneNumber').innerText = `PhoneNumber : ${data.deliveryPerson.phoneNumber}`;
         document.getElementById('email').innerText = `Email : ${data.deliveryPerson.email}`;

         // Display wine details
         const wineDetailsElement = document.getElementById('wineDetails');
         if (basketWines.length > 0) {
             wineDetailsElement.innerHTML = `<b>Wine:</b>`;
             wineDetailsElement.appendChild(document.createElement('br'));
             for (const wine of basketWines) {
                 wineDetailsElement.innerHTML += `- ${wine.name} (Price: $${wine.price.toFixed(2)})<br>`;
             }
         } else {
             wineDetailsElement.innerText = 'No wines in your order.';
         }

         // Display liquor details
         const liquorDetailsElement = document.getElementById('liquorDetails');
         if (basketLiquors.length > 0) {
             liquorDetailsElement.innerHTML = `<b>Liquor:</b>`;
             liquorDetailsElement.appendChild(document.createElement('br'));
             for (const liquor of basketLiquors) {
                 liquorDetailsElement.innerHTML += `- ${liquor.name} (Price: $${liquor.price.toFixed(2)})<br>`;
             }
         } else {
             liquorDetailsElement.innerText = 'No liquors in your order.';
         }
         const orderDetails = {
             orderNumber: orderNumber,
             wines: basketWines,
             liquors: basketLiquors,
             totalPrice: totalPrice,
             deliveryDate: data.deliveryDate,
             deliveryPerson: data.deliveryPerson,
         };
         saveOrderToFirestore(orderDetails);
     }




    const logoutButton = document.createElement('button');
    logoutButton.id = 'btnLogout';
    logoutButton.innerText = 'Logout';
    document.body.appendChild(logoutButton);

    logoutButton.addEventListener('click', () => {
        getAuth().signOut().catch(err => console.error('Error signing out:', err));
        location.reload();
    });
}

function generateOrderNumber() {
    const timestamp = Date.now().toString(36).toUpperCase(); // Convert to base 36
    const randomString = Math.random().toString(36).substring(2, 7).toUpperCase(); // Generate random string
    return `${timestamp}-${randomString}`;
}

async function confirmOrder(orderDetails) {
    const user = auth.currentUser;
    if (!user) {
        throw new Error('No authenticated user found.');
    }

    const orderRef = doc(db, 'orders', user.uid + '-' + Date.now()); // Create a unique order ID
    const inventoryRef = doc(db, 'inventory', 'items'); // Assuming a single document for inventory

    try {
        await runTransaction(db, async (transaction) => {
            // Log the start of the transaction
            console.log('Starting transaction for order confirmation');

            // Get the inventory data
            const inventoryDoc = await transaction.get(inventoryRef);
            if (!inventoryDoc.exists()) {
                throw new Error("Inventory document does not exist!");
            }

            const inventoryData = inventoryDoc.data();
            const { basketWines, basketLiquors } = orderDetails;

            // Check and update inventory
            console.log('Checking and updating inventory');
            for (let wine of basketWines) {
                console.log(`Checking wine: ${wine.name}, quantity: ${wine.quantity}`);
                if (!inventoryData[wine.name] || inventoryData[wine.name].quantity < wine.quantity) {
                    throw new Error(`Not enough inventory for wine: ${wine.name}`);
                }
                inventoryData[wine.name].quantity -= wine.quantity;
                console.log(`Updated inventory for wine: ${wine.name}, new quantity: ${inventoryData[wine.name].quantity}`);
            }

            for (let liquor of basketLiquors) {
                console.log(`Checking liquor: ${liquor.name}, quantity: ${liquor.quantity}`);
                if (!inventoryData[liquor.name] || inventoryData[liquor.name].quantity < liquor.quantity) {
                    throw new Error(`Not enough inventory for liquor: ${liquor.name}`);
                }
                inventoryData[liquor.name].quantity -= liquor.quantity;
                console.log(`Updated inventory for liquor: ${liquor.name}, new quantity: ${inventoryData[liquor.name].quantity}`);
            }

            // Update the inventory in Firestore
            transaction.set(inventoryRef, inventoryData);
            console.log('Inventory updated successfully');

            // Simulate successful payment processing (replace with your actual payment logic)
            console.log('Payment processed successfully');

            // Save the order details in Firestore
            const newOrder = {
                userId: user.uid,
                orderDate: new Date(),
                items: [...basketWines, ...basketLiquors],
                totalPrice: orderDetails.totalPrice,
                status: 'confirmed'
            };
            transaction.set(orderRef, newOrder);
            console.log('Order saved successfully');
        });

        console.log('Order confirmed successfully');
        //const { basketWines, basketLiquors, totalPrice } = orderDetails;
        //displayCheckoutPage(basketWines, basketLiquors, totalPrice);
    } catch (error) {
        console.error('Error confirming order:', error.message);
        throw error; // Re-throw the error to be caught in displayCheckoutPage
    }
}
