// Import necessary modules
import { h, Component } from "https://esm.sh/preact@10.11.2";
import htm from "https://esm.sh/htm@3.1.1";
import { effect } from "https://esm.sh/@preact/signals@1.1.2";
import { getAuth, getQuotes, setQuotes } from "./state.js";

// Bind htm to h
const html = htm.bind(h);

// Define the sorting order for taste pallets
const sortingOrder = ["Sweet", "Sour", "Bitter", "Salty", "Umami"];

// Define the BeverageTastePallets component
export class BeverageTastePallets extends Component {
  constructor() {
    super();
    // Initialize state with beverage, taste pallets, and time
    this.state = {
      beverage: null,
      tastePallets: {},
      time: "",
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
    const [, , distributor, beverageId, time] = location.pathname.split("/");
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

    // Fetch available taste pallets
    const response2 = await fetch(
      `/api/getAvailableTastePallets?distributor=${distributor}&beverageId=${beverageId}&time=${time}`,
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
    const tastePallets = await response2.json();

    // Update state with beverage, taste pallets, and time
    this.setState({ beverage, tastePallets, time });
  }

  render() {
    let quotes = this.state.quotes;
    const palletsInCart = new Set(quotes.map((quote) => quote.palletId));
    // Render the beverage and its taste pallets
    return html`
      <div class="page">
        <div class="beverages-item">
          ${this.state.beverage != null
            ? html`
                <img src="${this.state.beverage.image}" />
                <div>
                  <div class="beverages-item-name">${this.state.beverage.name}</div>
                  <div class="beverages-item-location">
                    ${this.state.beverage.location}
                  </div>
                  <div class="beverage-time">${this.state.time}</div>
                </div>
              `
            : ""}
        </div>
        <div>
          ${Object.entries(this.state.tastePallets)
            .sort(
              (a, b) => sortingOrder.indexOf(a[0]) - sortingOrder.indexOf(b[0])
            )
            .map(
              ([name, pallets]) => html`
                <div>
                  <div class="pallets-type">${name}</div>
                  <div class="pallets pallets-${name}">
                    ${pallets
                      .filter((pallet) => !palletsInCart.has(pallet.palletId))
                      .map(
                        (pallet) => html`
                          <div
                            class="pallet pallet-${pallet.name.slice(
                              pallet.name.length - 1
                            )}"
                          >
                            <button
                              class="pallets-button"
                              onClick="${() => {
                                quotes = [
                                  ...quotes,
                                  {
                                    distributor: pallet.distributor,
                                    beverageId: pallet.beverageId,
                                    palletId: pallet.palletId,
                                  },
                                ];
                                setQuotes(quotes);
                              }}"
                            >
                              ${pallet.name}
                            </button>
                          </div>
                        `
                      )}
                  </div>
                </div>
              `
            )}
        </div>
      </div>
    `;
  }
}
