// Import the h and Component functions from the preact library
import { h, Component } from "https://esm.sh/preact@10.11.2";
// Import the htm library
import htm from "https://esm.sh/htm@3.1.1";
// Import the getAuth function from the state module
import { getAuth } from "./state.js";

// Bind the h function to the htm function
const html = htm.bind(h);

// Define the Account component
export class Account extends Component {
  constructor() {
    super();
    // Initialize the state of the component
    this.state = {
      orders: [],
      beverages: new Map(),
      quantities: new Map(),
    };
  }

  // This function is called after the component is mounted
  async componentDidMount() {
    // Fetch the orders from the API
    const response = await fetch("/api/getOrders", {
      headers: {
        Authorization: `Bearer ${await getAuth().currentUser.getIdToken(
          false
        )}`,
      },
    });
    if (!response.ok) {
      return html`${await response.text()}`;
    }
    const orders = await response.json();

    // Initialize maps for beverages and quantities
    const beverages = new Map();
    const quantities = new Map();
    // Loop through all orders
    for (const order of orders) {
      // Loop through all items in each order
      for (const item of order.items) {
        // If the beverage is not already in the map, fetch it from the API
        if (!beverages.has(item.beverageId)) {
          const response = await fetch(
            `/api/getBeverage?beverageId=${item.beverageId}`,
            {
              headers: {
                Authorization: `Bearer ${await getAuth().currentUser.getIdToken(
                  false
                )}`,
              },
            }
          );
          if (!response.ok) {
            return html`${await response.text()}`;
          }
          const beverage = await response.json();
          beverages.set(beverage.beverageId, beverage);
        }
        // If the quantity is not already in the map, fetch it from the API
        if (!quantities.has(item.quantityId)) {
          const response = await fetch(
            `/api/getQuantity?quantityId=${item.quantityId}`,
            {
              headers: {
                Authorization: `Bearer ${await getAuth().currentUser.getIdToken(
                  false
                )}`,
              },
            }
          );
          if (!response.ok) {
            return html`${await response.text()}`;
          }
          const quantity = await response.json();
          quantities.set(quantity.quantityId, quantity);
        }
      }
    }

    // Update the state of the component
    this.setState({ orders, beverages, quantities });
  }

  // This function is called to render the component
  render() {
    return html`
      <div class="page">
        <div>
          <h1>Orders</h1>
        </div>
        ${this.state.orders.length !== 0
          ? html`
              <div>
                ${this.state.orders.map(
                  (order) => html`
                    <div class="order">
                      <div class="order-header">
                        <div>Order reference: ${order.id}</div>
                        <div>
                          ${Intl.DateTimeFormat("en-gb", {
                            dateStyle: "long",
                            timeStyle: "short",
                          }).format(new Date(order.time))}
                        </div>
                      </div>
                      ${order.items.map(
                        (item) => html`
                          <div class="item">
                            <div>
                              ${this.state.beverages.get(item.beverageId).name}
                            </div>
                            <div>
                              ${this.state.quantities.get(item.quantityId).amount}
                            </div>
                            <div>
                              € ${this.state.beverages.get(item.beverageId).price}
                            </div>
                          </div>
                        `
                      )}
                    </div>
                  `
                )}
              </div>
            `
          : html` You have no orders yet `}
      </div>
    `;
  }
}
