import React from "react";
import LandingPage from "./pages/LandingPage"; // menu/cart page
import TrackOrder from "./pages/TrackOrder"; // status page
import Login from "./pages/Login";
import KitchenPanel from "./pages/KitchenPanel";
import WaiterPanel from "./pages/WaiterPanel";
import KitchenMessages from "./pages/KitchenMessages";
import ManagerPanel from "./pages/ManagerPanel";
import MenuEditor from "./pages/MenuEditor"; // adjust path if needed


function App() {
  const path = window.location.pathname;

  if (path.startsWith("/track")) return <TrackOrder />;
  if (path.startsWith("/menu")) return <LandingPage />;
  if (path.startsWith("/login")) return <Login />;
  if (path.startsWith("/kitchen-panel")) return <KitchenPanel />;
  if (path.startsWith("/waiter-panel")) return <WaiterPanel />;
  if (path.startsWith("/kitchen-messages")) return <KitchenMessages />;
  if (path.startsWith("/manager-panel")) return <ManagerPanel />;
  if (path.startsWith("/table")) return <LandingPage />
  if (path.startsWith("/manager-menu")) return <MenuEditor />;

  return <p style={{ textAlign: "center" }}>Unknown path</p>;
}

export default App;
