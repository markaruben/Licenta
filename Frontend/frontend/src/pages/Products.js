import React, { useState } from "react";
import { ProductList } from "../helpers/ProductList";
import ProductItem from "../Components/ProductItem";
import "../styles/Products.css";

function Products() {
  const [searchQuery, setSearchQuery] = useState("");

  const handleSearchChange = (event) => {
    setSearchQuery(event.target.value);
  };
  const filteredProducts = ProductList.filter((productItem) =>
    productItem.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="products">
      <h1 className="productsTitle">Pick your favourite products!</h1>

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
    </div>
  );
}

export default Products;
