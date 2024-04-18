import "./App.css";
import Home from "./pages/Home";
import Navbar from "./Components/Navbar";
import LoginSignup from "./pages/LoginSignup";
import Footer from "./Components/Footer";
import Profile from "./pages/Profile";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import { SnackbarProvider } from "notistack";
import FavoriteProduct from "./pages/FavoriteProduct";
import Product from "./pages/Product";
import { useEffect } from "react";

function App() {
  useEffect(() => {
    const handleBeforeUnload = (e) => {
      if (e.type === "beforeunload") {
        if (localStorage.getItem("jwtToken")) {
          localStorage.removeItem("jwtToken");
        }
        e.preventDefault();
        e.returnValue = "";
      }
    };

    window.addEventListener("beforeunload", handleBeforeUnload);
    return () => {
      window.removeEventListener("beforeunload", handleBeforeUnload);
    };
  }, []);

  return (
    <SnackbarProvider maxSnack={3}>
      <div className="App">
        <Router>
          <Navbar />
          <Routes>
            <Route path="/" exact element={<Home />} />
            <Route path="/LogIn" element={<LoginSignup />} />
            <Route path="/FavoriteProduct" element={<FavoriteProduct />} />
            <Route path="/Profile" element={<Profile />} />
            <Route path={`/product/:productId`} element={<Product />} />
          </Routes>
          <Footer />
        </Router>
      </div>
    </SnackbarProvider>
  );
}

export default App;
