import React from "react";
import FavoriteBorderIcon from "@mui/icons-material/FavoriteBorder";
import FavoriteIcon from "@mui/icons-material/Favorite";
import { useNavigate } from "react-router-dom";

function ProductItem({
  id,
  image,
  name,
  price,
  onFavClick,
  isFavorited,
  isAdmin,
}) {
  const navigate = useNavigate();
  const handleEditClick = () => {
    navigate(`/EditProduct/${id}`);
  };
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
      {isAdmin && (
        <button className="editButton" onClick={handleEditClick}>
          Edit
        </button>
      )}
    </div>
  );
}

export default ProductItem;
