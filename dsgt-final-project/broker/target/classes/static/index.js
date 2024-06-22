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

    // Connect to emulators if running locally
    if (location.hostname === "localhost") {
        //connectAuthEmulator(auth, "http://localhost:8082", { disableWarnings: true });
        connectFirestoreEmulator(db, "localhost", 8084);
    }

    return { auth, db };
}

const { auth, db } = setupAuth();


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
            const remainingStock = getRemainingStock(liquor.brand);
            if (remainingStock < liquor.quantity) {
                reject(new Error(`Insufficient stock for ${liquor.brand}.`));
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
                 wineDetailsElement.innerHTML += `- ${wine.name} (Quantity: ${wine.quantity}, Price: $${(wine.price * wine.quantity).toFixed(2)})<br>`;             }
         } else {
             wineDetailsElement.innerText = 'No wines in your order.';
         }

         // Display liquor details
         const liquorDetailsElement = document.getElementById('liquorDetails');
         if (basketLiquors.length > 0) {
             liquorDetailsElement.innerHTML = `<b>Liquor:</b>`;
             liquorDetailsElement.appendChild(document.createElement('br'));
             for (const liquor of basketLiquors) {
                 liquorDetailsElement.innerHTML += `- ${liquor.name} (Quantity: ${liquor.quantity}, Price: $${(liquor.price * liquor.quantity).toFixed(2)})<br>`;             }
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