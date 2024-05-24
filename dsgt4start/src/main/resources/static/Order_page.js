var basket = [];
var drinks = [];

window.onload = function() {
    var select = document.getElementById("drink");
    fetchDrinks()
        .then(function(data) {
            drinks = data;
            drinks.forEach(function(item) {
                var option = document.createElement("option");
                option.text = item.name + " - $" + item.price;
                option.value = JSON.stringify(item);
                select.add(option);
            });
        })
        .catch(function(error) {
            console.error('Error fetching drinks:', error);
        });
};

function fetchDrinks() {
    return fetch('https://example.com/api/drinks') // Replace 'https://example.com/api/drinks' with your API endpoint
        .then(function(response) {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        });
}

function addToBasket() {
    var select = document.getElementById("drink");
    var selectedOption = JSON.parse(select.options[select.selectedIndex].value);
    basket.push(selectedOption);
    document.getElementById("basket").innerText = "Basket size: " + basket.length + ", Total price: $" + calculateTotal();
}

function calculateTotal() {
    var total = 0;
    for (var i = 0; i < basket.length; i++) {
        total += basket[i].price;
    }
    return total.toFixed(2);
}

function checkout() {
    // Here you would typically send the basket data to your server
    // Then redirect the user to the checkout page
    window.location.href = 'Checkout_page.html';
}
