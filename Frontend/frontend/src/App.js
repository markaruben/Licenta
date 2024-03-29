import "./App.css";
import Home from "./pages/Home";
import Navbar from "./Components/Navbar";
import LoginSignup from "./pages/LoginSignup";
import Footer from "./Components/Footer";
import Products from "./pages/Products";
import About from "./pages/About";
import Profile from "./pages/Profile";
import AddProduct from "./pages/AddProduct";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import { SnackbarProvider } from "notistack";

function App() {
  return (
    <SnackbarProvider maxSnack={3}>
      <div className="App">
        <Router>
          <Navbar />
          <Routes>
            <Route path="/" exact element={<Home />} />
            <Route path="/LogIn" element={<LoginSignup />} />
            <Route path="/Products" element={<Products />} />
            <Route path="/Profile" element={<Profile />} />
            <Route path="/AddProduct" element={<AddProduct />} />
          </Routes>
          <Footer />
        </Router>
      </div>
    </SnackbarProvider>
  );
}

export default App;
