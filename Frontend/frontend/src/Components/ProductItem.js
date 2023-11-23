import React from "react";
import FavoriteBorderIcon from "@mui/icons-material/FavoriteBorder";

function ProductItem({ image, name, price }) {
  return (
    <div className="productItem">
      <div style={{ backgroundImage: `url(${image})` }}></div>
      <h1>{name}</h1>
      <p>${price}</p>
      <span className="fav-icon" style={{ cursor: "pointer" }}>
        <FavoriteBorderIcon />
      </span>
    </div>
  );
}

export default ProductItem;
