// Import necessary modules
import { h, Component } from "https://esm.sh/preact@10.11.2";
import htm from "https://esm.sh/htm@3.1.1";
import { getAuth } from "./state.js";

// Bind htm to h
const html = htm.bind(h);

// Define the Beverages component
export class Beverages extends Component {
  constructor() {
    super();
    // Initialize state with beverages
    this.state = {
      beverages: [],
    };
  }

  async componentDidMount() {
    // Fetch beverage details
    const response = await fetch("/api/getBeverages", {
      headers: {
        Authorization: `Bearer ${await getAuth().currentUser.getIdToken(
          false
        )}`,
      },
    });
    if (!response.ok) {
      return html`${await response.text()}`;
    }
    const beverages = await response.json();

    // Update state with beverages
    this.setState({ beverages });
  }

  render() {
    // Render the beverages
    return html`
      <div class="page">
        <div>
          <h1>Beverages</h1>
        </div>
        <div class="beverages-grid">
          ${this.state.beverages.map(
            (beverage) => html`
              <a href="/beverages/${beverage.distributor}/${beverage.beverageId}">
                <div class="beverages-item">
                  <img async src="${beverage.image}" />
                  <div>
                    <div class="beverages-item-name">${beverage.name}</div>
                    <div class="beverages-item-location">${beverage.location}</div>
                  </div>
                </div>
              </a>
            `
          )}
        </div>
      </div>
    `;
  }
}
