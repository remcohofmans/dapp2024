// Import necessary modules
import { h, Component } from "https://esm.sh/preact@10.11.2";
import { route } from "https://esm.sh/preact-router@4.1.0";
import htm from "https://esm.sh/htm@3.1.1";
import { effect } from "https://esm.sh/@preact/signals@1.1.2";
import { getAuth, getQuotes, setQuotes } from "./state.js";

// Bind htm to h
const html = htm.bind(h);

// Define the Cart component
export class Cart extends Component {
  constructor() {
    super();
    // Initialize state with beverages and distributors
    this.state = {
      beverages: new Map(),
      distributors: new Map(),
    };
    // Update state with quotes
    effect(() => {
      this.setState({
        ...this.state,
        quotes: getQuotes(),
      });
    });
  }

  async componentDidMount() {
    const quotes = this.state.quotes;
    const beverages = new Map();
    const distributors = new Map();
    for (const quote of quotes) {
      // Fetch beverage details
      if (!beverages.has(quote.beverageId)) {
        const response = await fetch(
          `/api/getBeverage?distributor=${quote.distributor}&beverageId=${quote.beverageId}`,
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
      // Fetch distributor details
      if (!distributors.has(quote.distributorId)) {
        const response = await fetch(
          `/api/getDistributor?distributor=${quote.distributor}&distributorId=${quote.distributorId}`,
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
        const distributor = await response.json();
        distributors.set(distributor.distributorId, distributor);
      }
    }

    // Update state with beverages and distributors
    this.setState({ beverages, distributors });
  }

  render() {
    // Render the cart
    return html`
      <div class="page">
        <div>
          <h1>Shopping cart</h1>
        </div>
        <div>
          ${this.state.quotes.map(
            (quote) => html`
              <div class="quote">
                <div>${this.state.beverages.get(quote.beverageId)?.name}</div>
                ${this.state.distributors.has(quote.distributorId)
                  ? html`
                      <div class="quote-distributor-name">
                        ${this.state.distributors.get(quote.distributorId).name}
                      </div>
                    `
                  : ""}
                <button
                  class="quote-remove-button"
                  onClick="${() => {
                    const quotes = this.state.quotes.filter(
                      (q) => q.distributorId !== quote.distributorId
                    );
                    setQuotes(quotes);
                  }}"
                >
                  Remove
                </button>
              </div>
            `
          )}
          ${this.state.quotes.length !== 0
            ? html`
                <button
                  class="quote-confirm-button"
                  onClick="${async () => {
                    const response = await fetch("/api/confirmQuotes", {
                      method: "POST",
                      body: JSON.stringify(this.state.quotes),
                      headers: {
                        Authorization: `Bearer ${await getAuth().currentUser.getIdToken(
                          false
                        )}`,
                        "Content-Type": "application/json",
                      },
                    });
                    if (response.ok) {
                      setQuotes([]);
                      route("/account");
                    }
                  }}"
                >
                  Order all
                </button>
              `
            : html` Your shopping cart is empty `}
        </div>
      </div>
    `;
  }
}
