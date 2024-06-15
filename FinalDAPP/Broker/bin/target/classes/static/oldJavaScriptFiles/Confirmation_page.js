window.onload = function() {
    // Simulated data for demonstration purposes
    var deliveryDetails = {
        orderNumber: '1111111111',
        beer: 'Beer: Guinness',
        wine: 'Wine: Merlot',
        whiskey: 'Whiskey: Jameson',
        totalPrice: 'Total price: 20 euro',
        expectedDeliveryDate: 'Expected delivery date: Friday, May 3, 4:00 pm – 4:30 pm Central Standard Time',
        deliveredBy: 'Delivered by: Del Ivery',
        contactDetail: 'Contact Detail:',
        phoneNumber: 'PhoneNumber : +32468164131',
        email: 'Email : bla@gmail.com'
    };

    // Update the DOM with the fetched delivery details
    updateDeliveryDetails(deliveryDetails);
};

function updateDeliveryDetails(details) {
    // Update the DOM elements with the fetched delivery details
    document.getElementById("orderNumber").textContent = details.orderNumber;
    document.getElementById("beer").textContent = details.beer;
    document.getElementById("wine").textContent = details.wine;
    document.getElementById("whiskey").textContent = details.whiskey;
    document.getElementById("totalPrice").textContent = details.totalPrice;
    document.getElementById("expectedDeliveryDate").textContent = details.expectedDeliveryDate;
    document.getElementById("deliveredBy").textContent = details.deliveredBy;
    document.getElementById("contactDetail").textContent = details.contactDetail;
    document.getElementById("phoneNumber").textContent = details.phoneNumber;
    document.getElementById("email").textContent = details.email;
}
