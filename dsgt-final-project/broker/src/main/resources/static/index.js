import { initializeApp } from "https://www.gstatic.com/firebasejs/9.9.4/firebase-app.js";
import {
    getFirestore,
    connectFirestoreEmulator,
    doc,
    setDoc,
    runTransaction
} from "https://www.gstatic.com/firebasejs/9.9.4/firebase-firestore.js";
import {
    getAuth,
    connectAuthEmulator,
    onAuthStateChanged,
    createUserWithEmailAndPassword,
    signInWithEmailAndPassword
} from "https://www.gstatic.com/firebasejs/9.9.4/firebase-auth.js";

let globalData;
let wines = [];
let liquors = [];
let basket = {};
let totalPrice = 0;
let token;
let isManager = false;

function setupFirebase() {
    const firebaseConfig = {
        apiKey: "AIzaSyBKeoT9A--aO7W9St_GkAv50u8K38FVpjA",
        authDomain: "distributed-liquor-store.firebaseapp.com",
        projectId: "distributed-liquor-store",
        storageBucket: "distributed-liquor-store.appspot.com",
        messagingSenderId: "246562931566",
        appId: "1:246562931566:web:62cf6478eaa7efe0a5b77f"
    };

    const firebaseApp = initializeApp(firebaseConfig);
    const auth = getAuth(firebaseApp);
    const db = getFirestore(firebaseApp);

    if (location.hostname === "localhost") {
        connectFirestoreEmulator(db, "localhost", 8084);
    }
    return { auth, db };
}

const { auth, db } = setupFirebase();

async function fetchData(url) {
    try {
        const response = await fetch(url);
        return await response.json();
    } catch (error) {
        console.error(`Error fetching data from ${url}:`, error);
        throw error;
    }
}

async function populateFirestore() {
    try {
        const liquorData = await fetchData("http://dappvm.eastus.cloudapp.azure.com:12000/api/liquor-info");
        const wineData = await fetchData("http://localhost:8080/wines");

        const inventoryData = {};

        await Promise.all([
            ...liquorData.map(item => saveToFirestore('liquors', item.brand, item)),
            ...wineData.map(item => saveToFirestore('wines', item.name, item)),
        ]);

        liquorData.forEach(item => inventoryData[item.brand] = { quantity: item.quantity });
        wineData.forEach(item => inventoryData[item.name] = { quantity: item.quantity });

        await setDoc(doc(db, "inventory", "items"), inventoryData);

        console.log("Data successfully populated to Firestore.");
    } catch (error) {
        console.error("Error populating Firestore:", error);
    }
}

async function saveToFirestore(collection, id, data) {
    const docRef = doc(db, collection, id);
    await setDoc(docRef, data);
}

populateFirestore();

function setupEventHandlers() {
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    const signInButton = document.getElementById("btnSignIn");
    const signUpButton = document.getElementById("btnSignUp");
    const logoutButton = document.getElementById("btnLogout");

    signInButton.addEventListener("click", () => handleAuth(emailInput.value, passwordInput.value, 'signIn'));
    signUpButton.addEventListener("click", () => handleAuth(emailInput.value, passwordInput.value, 'signUp'));
    logoutButton.addEventListener("click", () => getAuth().signOut().catch(err => console.error('Error signing out:', err)));
}

function handleAuthStateChanges() {
    onAuthStateChanged(auth, user => {
        if (!user) {
            showUnauthenticated();
            return;
        }
        user.getIdTokenResult()
            .then(idTokenResult => {
                showAuthenticated(user.email);
                token = idTokenResult.token;
                fetchDataOnAuth(token);
            })
            .catch(err => {
                console.error("Error getting ID token result:", err);
                showUnauthenticated();
            });
    });
}

function handleAuth(email, password, action) {
    const authMethod = action === 'signIn' ? signInWithEmailAndPassword : createUserWithEmailAndPassword;
    authMethod(auth, email, password)
        .then(() => {
            console.log(`${action === 'signIn' ? 'Signed in' : 'Account created'} successfully`);
            if (action === 'signIn') fetchDataOnAuth(token);
        })
        .catch(error => {
            console.error(`Error ${action === 'signIn' ? 'signing in' : 'creating account'}:`, error.message);
            alert(error.message);
        });
}

function fetchDataOnAuth(token) {
    fetchHello(token);
    fetchWhoAmI(token);
}

function fetchHello(token) {
    fetchWithToken(`/api/askDelivery?basketWines=${basketWines.join(',')}&basketLiquors=${basketLiquors.join(',')}`, token)
        .then(data => {
            globalData = data;
            addContent(data);
        })
        .catch(error => console.error("Error fetching /api/askDelivery:", error));
}

function fetchWhoAmI(token) {
    fetchWithToken('/api/whoami', token)
        .then(data => {
            console.log(data.email, data.role);
            isManager = data.role === 'manager';
            if (isManager) {
                displayManagerPage();
                addContent('You have manager access.');
            } else {
                displayOrderPage();
            }
            addContent(`Whoami at rest service: ${data.email} - ${data.role}`);
        })
        .catch(error => {
            console.error("Error fetching /api/whoami:", error);
            addContent(`Error fetching whoami: ${error.message}`);
            displayOrderPage();
        });
}

async function fetchWithToken(url, token) {
    const response = await fetch(url, {
        headers: { Authorization: 'Bearer ' + token }
    });
    if (!response.ok) {
        throw new Error(`Network response was not ok ${response.statusText}`);
    }
    return response.json();
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

function displayOrderPage() {
    loadCSS('../cssFiles/Order_page.css');
    document.body.innerHTML = `
        <header class="site-header">
            <h1>Bam <u>Booz</u>led</h1>
        </header>
        <div>
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
        </div>
        <button id="btnCheckout">Go to checkout</button>
    `;

    document.getElementById("orderForm").addEventListener("submit", (event) => {
        event.preventDefault();
        addToBasket('wine');
    });

    document.getElementById("liquorForm").addEventListener("submit", (event) => {
        event.preventDefault();
        addToBasket('liquor');
    });

    document.getElementById('btnCheckout').addEventListener('click', () => goToCheckout());

    fetchAndPopulate('wines', 'drink');
    fetchAndPopulate('liquors', 'liquor');
}

function loadCSS(href) {
    const newStylesheet = document.createElement('link');
    newStylesheet.rel = 'stylesheet';
    newStylesheet.href = href;
    document.head.appendChild(newStylesheet);
}

function fetchAndPopulate(type, selectId) {
    const url = type === 'wines' ? "http://localhost:8080/wines" : "http://dappvm.eastus.cloudapp.azure.com:12000/api/liquor-info";
    fetch(url)
        .then(response => response.json())
        .then(data => {
            if (!Array.isArray(data)) throw new Error("Expected an array of items");
            if (type === 'wines') wines = data;
            else liquors = data;
            populateSelect(selectId, data, type === 'wines' ? 'name' : 'brand');
        })
        .catch(error => console.error(`Error fetching ${type}:`, error));
}

function populateSelect(selectId, items, key) {
    const selectElement = document.getElementById(selectId);
    selectElement.innerHTML = '';
    items.forEach(item => {
        const option = document.createElement('option');
        option.value = item[key];
        option.text = item[key];
        selectElement.appendChild(option);
    });
}

function addToBasket(type) {
    const selectElement = document.getElementById(type === 'wine' ? "drink" : "liquor");
    const selectedItem = selectElement.value;
    const item = (type === 'wine' ? wines : liquors).find(i => i[type === 'wine' ? 'name' : 'brand'] === selectedItem);

    if (!item) {
        console.error(`Item not found: ${selectedItem}`);
        return;
    }

    if (!basket[selectedItem]) {
        basket[selectedItem] = { quantity: 0, price: item.price };
    }
    basket[selectedItem].quantity++;
    updateBasketDisplay();
}

function updateBasketDisplay() {
    totalPrice = Object.values(basket).reduce((sum, item) => sum + item.quantity * item.price, 0);
    const itemCount = Object.values(basket).reduce((count, item) => count + item.quantity, 0);
    document.getElementById("basket").textContent = `Basket: ${itemCount} items, Total price: $${totalPrice.toFixed(2)}`;
}

function goToCheckout() {
    displayCheckoutPage();
}

function displayCheckoutPage() {
    document.body.innerHTML = `
        <header class="site-header">
            <h1>Bam <u>Booz</u>led</h1>
        </header>
        <h1>Checkout</h1>
        <div>
            <form id="checkoutForm">
                <label for="name">Name:</label>
                <input type="text" id="name" name="name" required>
                <label for="address">Address:</label>
                <input type="text" id="address" name="address" required>
                <label for="phone">Phone:</label>
                <input type="tel" id="phone" name="phone" required>
                <label for="card">Card Number:</label>
                <input type="text" id="card" name="card" required>
                <button type="submit">Confirm Order</button>
            </form>
        </div>
    `;

    document.getElementById('checkoutForm').addEventListener('submit', async (event) => {
        event.preventDefault();
        const formData = new FormData(event.target);
        const orderDetails = {
            name: formData.get('name'),
            address: formData.get('address'),
            phone: formData.get('phone'),
            card: formData.get('card'),
            items: basket,
            totalPrice,
        };

        try {
            await saveOrderToFirestore(orderDetails);
            displayConfirmationPage(orderDetails);
        } catch (error) {
            console.error("Error saving order:", error);
            alert("There was an error processing your order. Please try again.");
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

async function saveOrderToFirestore(orderDetails) {
    await runTransaction(db, async (transaction) => {
        const inventoryRef = doc(db, "inventory", "items");
        const inventoryDoc = await transaction.get(inventoryRef);
        if (!inventoryDoc.exists()) throw "Inventory document does not exist!";

        const inventory = inventoryDoc.data();
        Object.entries(basket).forEach(([item, { quantity }]) => {
            if (inventory[item].quantity < quantity) throw `Not enough stock for ${item}`;
            inventory[item].quantity -= quantity;
        });

        transaction.update(inventoryRef, inventory);
        const orderRef = doc(db, "orders", `order_${Date.now()}`);
        transaction.set(orderRef, orderDetails);
    });
}

function displayConfirmationPage(orderDetails) {
    document.body.innerHTML = `
        <header class="site-header">
            <h1>Bam <u>Booz</u>led</h1>
        </header>
        <h1>Order Confirmation</h1>
        <p>Thank you for your order, ${orderDetails.name}!</p>
        <p>Your total is $${orderDetails.totalPrice.toFixed(2)}</p>
        <p>Your items will be shipped to ${orderDetails.address}.</p>
        <button id="btnNewOrder">Place another order</button>
    `;

    document.getElementById('btnNewOrder').addEventListener('click', () => {
        basket = {};
        totalPrice = 0;
        displayOrderPage();
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

    const orderNumber = generateOrderNumber();

    if (globalData) {
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
            basketWines.forEach(wine => {
                wineDetailsElement.innerHTML += `- ${wine.name} (Quantity: ${wine.quantity}, Price: $${(wine.price * wine.quantity).toFixed(2)})<br>`;
            });
        } else {
            wineDetailsElement.innerText = 'No wines in your order.';
        }

        // Display liquor details
        const liquorDetailsElement = document.getElementById('liquorDetails');
        if (basketLiquors.length > 0) {
            liquorDetailsElement.innerHTML = `<b>Liquor:</b>`;
            basketLiquors.forEach(liquor => {
                liquorDetailsElement.innerHTML += `- ${liquor.name} (Quantity: ${liquor.quantity}, Price: $${(liquor.price * liquor.quantity).toFixed(2)})<br>`;
            });
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
    const user = getAuth().currentUser;
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
    } catch (error) {
        console.error('Error confirming order:', error.message);
        throw error;
    }
}
