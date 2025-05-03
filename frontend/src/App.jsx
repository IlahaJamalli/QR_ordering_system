import React from "react";
import LandingPage from "./pages/LandingPage"; // menu/cart page
import TrackOrder from "./pages/TrackOrder"; // status page
import Login from "./pages/Login";
import KitchenPanel from "./pages/KitchenPanel";

function App() {
  const path = window.location.pathname;

  if (path.startsWith("/track")) return <TrackOrder />;
  if (path.startsWith("/menu")) return <LandingPage />;
  if (path.startsWith("/login")) return <Login />; // ✅ added
  if (path.startsWith("/kitchen-panel")) return <KitchenPanel />; // ✅ added

  // fallback (optional):
  return <p style={{ textAlign: "center" }}>Unknown path</p>;
}

export default App;
