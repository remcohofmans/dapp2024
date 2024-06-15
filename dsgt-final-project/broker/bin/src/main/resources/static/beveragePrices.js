// Import necessary modules
import { h, Component } from "https://esm.sh/preact@10.11.2";
import htm from "https://esm.sh/htm@3.1.1";
import { getAuth } from "./state.js";

// Bind htm to h
const html = htm.bind(h);

// Define the BeveragePrices component
export class BeveragePrices extends Component {
  constructor() {
    super();
    // Initialize state with beverage and prices
    this.state = {
      beverage: null,
      prices: [],
    };
  }

  async componentDidMount() {
    const [, , distributor, beverageId] = location.pathname.split("/");
    // Fetch beverage details
    const response1 = await fetch(
      `/api/getBeverage?distributor=${distributor}&beverageId=${beverageId}`,
      {
        headers: {
          Authorization: `Bearer ${await getAuth().currentUser.getIdToken(
            false
          )}`,
        },
      }
    );
    if (!response1.ok) {
      return html`${await response1.text()}`;
    }
    const beverage = await response1.json();

    // Fetch beverage prices
    const response2 = await fetch(
      `/api/getBeveragePrices?distributor=${distributor}&beverageId=${beverageId}`,
      {
        headers: {
          Authorization: `Bearer ${await getAuth().currentUser.getIdToken(
            false
          )}`,
        },
      }
    );
    if (!response2.ok) {
      return html`${await response2.text()}`;
    }
    const prices = await response2.json();

    // Update state with beverage and prices
    this.setState({ beverage, prices });
  }

  render() {
    // Render the beverage and its prices
    return html`
      <div class="page">
        <div class="beverages-item">
          ${this.state.beverage != null
            ? html`
                <img async src="${this.state.beverage.image}" />
                <div>
                  <div class="beverages-item-name">${this.state.beverage.name}</div>
                  <div class="beverages-item-location">
                    ${this.state.beverage.location}
                  </div>
                </div>
              `
            : ""}
        </div>
        <div>
          ${this.state.prices.map(
            (price) => html`
              <div class="beverage-price">
                <div>
                  ${Intl.NumberFormat("en-gb", {
                    style: "currency",
                    currency: "EUR",
                  }).format(price)}
                </div>
                <a
                  href="/beverages/${this.state.beverage.distributor}/${this.state
                    .beverage.beverageId}/${price}"
                >
                  <div class="beverage-prices-button-order">Order now</div>
                </a>
              </div>
            `
          )}
        </div>
      </div>
    `;
  }
}
