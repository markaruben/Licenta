import React from "react";
import FavoriteBorderIcon from "@mui/icons-material/FavoriteBorder";
import FavoriteIcon from "@mui/icons-material/Favorite"; // import for colored icon

function ProductItem({ id, image, name, price, onFavClick, isFavorited }) {
  return (
    <div className="productItem">
      <div
        style={{ backgroundImage: `url(${image})` }}
        className="productImage"
      ></div>
      <h1>{name}</h1>
      <p>${price}</p>
      <span
        className="fav-icon"
        style={{ cursor: "pointer" }}
        onClick={() => onFavClick(id, isFavorited)}
      >
        {isFavorited ? <FavoriteIcon /> : <FavoriteBorderIcon />}
      </span>
    </div>
  );
}

export default ProductItem;
