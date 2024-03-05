import React, { useState } from "react";
import { ProductList } from "../helpers/ProductList";
import ProductItem from "../Components/ProductItem";
import "../styles/Products.css";
import { useNavigate } from "react-router-dom";

function Products() {
  const [searchQuery, setSearchQuery] = useState("");
  const navigate = useNavigate();

  const handleSearchChange = (event) => {
    setSearchQuery(event.target.value);
  };
  const filteredProducts = ProductList.filter((productItem) =>
    productItem.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const isLoggedIn = !!localStorage.getItem("jwtToken");

  return (
    <div className="products">
      <h1 className="productsTitle">Pick your favourite products!</h1>

      {!isLoggedIn ? (
        <div className="loginPrompt">
          <p>Please, log in to pick your products!</p>
          <button onClick={() => navigate("/LogIn")}>Log In</button>
        </div>
      ) : (
        <>
          {/* Search bar */}
          <div className="searchBar">
            <input
              type="text"
              placeholder="Search products..."
              value={searchQuery}
              onChange={handleSearchChange}
            />
          </div>

          <div className="productList">
            {filteredProducts.map((productItem, key) => (
              <div key={key}>
                <ProductItem
                  image={productItem.image}
                  name={productItem.name}
                  price={productItem.price}
                />
              </div>
            ))}
          </div>
        </>
      )}
    </div>
  );
}

export default Products;
