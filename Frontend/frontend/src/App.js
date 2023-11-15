import "./App.css";
import Home from "./pages/Home";
import Navbar from "./Components/Navbar";
import LoginSignup from "./pages/LoginSignup";
import Footer from "./Components/Footer";
import Products from "./pages/Products";
import Contact from "./pages/Contact";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";

function App() {
  return (
    <div className="App">
      <Router>
        <Navbar />
        <Routes>
          <Route path="/" exact element={<Home />} />
          <Route path="/LogIn" element={<LoginSignup />} />
          <Route path="/Products" element={<Products />} />
          <Route path="/Contact" element={<Contact />} />
        </Routes>
        <Footer />
      </Router>
    </div>
  );
}

export default App;
