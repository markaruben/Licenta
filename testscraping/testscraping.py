import re
import mysql.connector
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

import re

import re

import re

def extract_price(price_string, pattern):
    match = re.search(pattern, price_string)
    if match:
        return match.group()
    return None

patternNoComma = r'\d+[.,]?\d+'
patternComma = r'\d{1,3}(?:\.\d{3})*(?:,\d{1,2})?'




def get_product_info(url):
    driver = webdriver.Chrome()
    driver.get(url)
    
    try:
        # Look for elements with the "product-new-price" class
        price_element = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, 'product-new-price'))
        )

        # Find the title element by class name
        title_element = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, 'page-title'))
        )
        
        price = driver.execute_script("return arguments[0].innerText;", price_element)
        title = title_element.get_attribute('textContent')

        if ',' in price:
            print(price)
            price_numeric = extract_price(price,patternComma)
            print(price_numeric)
            print(title)
            return price_numeric, title, url

        # Otherwise, find the decimal separator element by class name
        decimal_element = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.TAG_NAME, 'sup'))
        )

        decimal = decimal_element.get_attribute('textContent')
        price_numeric = extract_price(price, patternNoComma)
        if price_numeric:
            if decimal:
                price_numeric += ',' + decimal
        else:
            price_numeric = None
        
        print(price_numeric)
        print(title)
        return price_numeric, title, url
    except Exception as e:
        print("An error occurred:", e)
    finally:
        driver.quit()

def add_to_database(title, price, url):
    # Truncate URL if it exceeds 255 characters
    if len(url) > 255:
        url = url[:255]

    connection = mysql.connector.connect(
        host="localhost",
        port="3306",  
        user="root",
        password="root",
        database="dbff"
    )
    cursor = connection.cursor()

    try:
        # Get the next ID from the sequence table
        cursor.execute("SELECT next_val FROM products_seq")
        next_id = cursor.fetchone()[0]

        # Increment the next value in the sequence table
        next_id += 1
        cursor.execute("UPDATE products_seq SET next_val = %s", (next_id,))

        # Check if the product already exists in the database
        cursor.execute("SELECT * FROM products WHERE title = %s", (title,))
        existing_product = cursor.fetchone()
        if existing_product:
            # Update the price of the existing product
            cursor.execute("UPDATE products SET price = %s WHERE title = %s", (price, title))
        else:
            # Insert the new product into the database with the next ID
            cursor.execute("INSERT INTO products (id, title, price, product_url) VALUES (%s, %s, %s, %s)", (next_id, title, price, url))

        # Commit changes and close connection
        connection.commit()
    except mysql.connector.Error as err:
        print("Error:", err)
    finally:
        connection.close()


product_url = "https://www.emag.ro/incarcator-compatibil-cu-iphone-14-13-12-11-pro-pro-max-ipad-airpods-type-c-cablu-de-date-lightning-fast-charge-20w-ambalaj-apple-alb-gri-incarcator-cablu-iphone1/pd/DKKBZ9MBM/?ref=profiled_categories_campaign_1_5&provider=rec&recid=rec_94_4d2c4d7ca0d945119c92e584787a5c5e0103cd769dd7e7cee60d78ae2eda3c2b_1712585490&scenario_ID=94"
product_price, product_title, product_url = get_product_info(product_url)
print("Product title:", product_title)
print("Product price:", product_price)

# Add or update the product information in the database
add_to_database(product_title, product_price, product_url)
