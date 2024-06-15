// Import the signal function from the @preact/signals library
import { signal } from "https://esm.sh/@preact/signals@1.1.2";

// Initialize an empty array for quotes
let initQuotes = [];
try {
  // Try to parse the quotes from localStorage, if it exists
  initQuotes = JSON.parse(localStorage.quotes ?? "[]");
} catch (e) {
  // If parsing fails, initQuotes remains an empty array
}

// Create a signal for quotes. This allows us to observe changes to quotes.
const quotes = signal(initQuotes);

// Create a signal for isManager. This allows us to observe changes to isManager.
const isManager = signal(false);

// Initialize an empty object for auth
let auth = {};

// Function to get the current value of quotes
export function getQuotes() {
  return quotes.value;
}

// Function to set a new value for quotes and save it to localStorage
export function setQuotes(q) {
  quotes.value = q;
  localStorage.quotes = JSON.stringify(q);
}

// Function to get the current value of isManager
export function getIsManager() {
  return isManager.value;
}

// Function to set a new value for isManager
export function setIsManager(b) {
  isManager.value = b;
}

// Function to get the current value of auth
export function getAuth() {
  return auth;
}

// Function to set a new value for auth
export function setAuth(a) {
  auth = a;
}
