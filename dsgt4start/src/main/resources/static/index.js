
import { h, Component, render } from "https://esm.sh/preact@10.11.2";
import htm from "https://esm.sh/htm@3.1.1";

// Import router library
import { createHashRouter } from "https://esm.sh/preact-router@4.1.0";

// Import state management functions
import { getQuotes, setQuotes, getIsManager, setIsManager, getAuth, setAuth } from "./state.js";

// Bind htm to h
const html = htm.bind(h);

// Import components
import { Login } from "./login.js";
import { Header } from "./header.js";
import { Manager } from "./manager.js";
import { Cart } from "./cart.js";
import { Beverages } from "./beverages.js";
import { BeveragePrices } from "./beveragePrices.js";
import { Account } from "./account.js";

// Check for Firebase configuration
if (process.env.NODE_ENV === 'production' && !firebaseConfig) {
  throw new Error('Firebase configuration is missing for production environment');
}

// Initialize authentication and manager state
let auth = {};
setIsManager(false);

// Router configuration
const router = createHashRouter();

// Root component
class App extends Component {
  constructor() {
    super();
    this.state = {
      currentUser: null,
    };
  }

  componentDidMount() {
    // Check for existing authentication on mount
    getAuth().onAuthStateChanged((user) => {
      this.setState({ currentUser: user });
      setIsManager(user?.claims?.manager || false);
      setAuth(user);
    });
  }

  render() {
    return html`
      <div>
        ${this.state.currentUser ? (
          html`
            <Header />
            ${router(
              [
                // Public routes
                ("/", () => html`<${Login} />`),

                // Protected routes (require authentication)
                (
                  "/manager",
                  () => (getIsManager() ? html`<${Manager} />` : html`Not authorized`),
                  []
                ),
                (
                  "/cart",
                  () => html`<${Cart} />`,
                  []
                ),
                (
                  "/account",
                  () => html`<${Account} />`,
                  []
                ),

                // Beverage routes

                ("/beverages/:distributor/:beverageId", () => html`<${Beverages} />`),
                (
                  "/beverages/:distributor/:beverageId/:priceId",
                  () => html`<${BeveragePrices} />`,
                  []
                ),

                // Catch-all route
                ("*", () => html`<h1>404 Not Found</h1>`),
              ],
              { initialUrl: "/" }
            )}
          `
        ) : (
          html`
            <${Login} />
          `
        )}
      </div>
    `;
  }
}

render(html`<${App} />`, document.body);
