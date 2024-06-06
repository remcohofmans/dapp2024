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
    return fetch('http://your-server-address/ws', {
        method: 'POST',
        headers: {
            'Content-Type': 'text/xml'
        },
        body: `
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:web="http://liquormenu.io/gt/webservice">
               <soapenv:Header/>
               <soapenv:Body>
                  <web:getLiquorsRequest/>
               </soapenv:Body>
            </soapenv:Envelope>
        `
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.text();
    })
    .then(str => new window.DOMParser().parseFromString(str, "text/xml"))
    .then(data => {
        const drinks = [];
        const items = data.getElementsByTagName('Liquor');
        for (let i = 0; i < items.length; i++) {
            const name = items[i].getElementsByTagName('name')[0].textContent;
            const price = parseFloat(items[i].getElementsByTagName('price')[0].textContent);
            drinks.push({ name, price });
        }
        return drinks;
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
    //Implement the check for availability
    window.location.href = "Checkout_page.html";
}
