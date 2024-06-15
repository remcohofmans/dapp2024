window.onload = function() {
    // Fetch delivery details from the server
    fetchDeliveryDetails()
        .then(function(data) {
            // Update the DOM with the fetched delivery details
            updateDeliveryDetails(data);
        })
        .catch(function(error) {
            console.error('Error fetching delivery details:', error);
        });
};

function fetchDeliveryDetails() {
    // Make a fetch request to the server to get the delivery details
    return fetch('https://example.com/api/delivery-details') // Replace 'https://example.com/api/delivery-details' with your API endpoint
        .then(function(response) {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        });
}

function updateDeliveryDetails(details) {
    // Update the DOM elements with the fetched delivery details
    document.querySelector('.details p:nth-child(2)').textContent += details.beer || '';
    document.querySelector('.details p:nth-child(3)').textContent += details.wine || '';
    document.querySelector('.details p:nth-child(4)').textContent += details.whiskey || '';
    document.querySelector('.details p:nth-child(5)').textContent += details.totalPrice || '';
    document.querySelector('.details p:nth-child(8)').textContent += details.expectedDeliveryDate || '';
    document.querySelector('.details p:nth-child(9)').textContent += details.deliveredBy || '';
    document.querySelector('.details p:nth-child(10)').textContent += details.contactPhoneNumber || '';
    document.querySelector('.details p:nth-child(11)').textContent += details.contactEmail || '';
}

