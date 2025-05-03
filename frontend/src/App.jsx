import React from "react";
import LandingPage from "./pages/LandingPage"; // menu/cart page
import TrackOrder   from "./pages/TrackOrder"; // status page

function App() {
  const path = window.location.pathname;

  if (path.startsWith("/track")) return <TrackOrder />;
  if (path.startsWith("/menu"))  return <LandingPage />;
  // fallback (optional):
  return <p style={{textAlign:"center"}}>Unknown path</p>;
}

export default App;
