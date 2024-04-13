import re
import mysql.connector
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options

def extract_price(price_string, pattern):
    match = re.search(pattern, price_string)
    if match:
        return match.group()
    return None

patternNoComma = r'\d+[.,]?\d+'
patternComma = r'\d{1,3}(?:\.\d{3})*(?:,\d{1,2})?'

def format_price(price):
    price = price.replace('.', '')
    price = price.replace(',', '.')
    return price

def get_product_info(url):
    chrome_options = Options()
    chrome_options.add_argument("--headless")
    driver = webdriver.Chrome(options=chrome_options)
    driver.get(url)
    
    try:
        price_element = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, 'product-new-price'))
        )

        title_element = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, 'page-title'))
        )

        image_element = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.XPATH, '//a[@class="thumbnail product-gallery-image gtm_rp125918"]/img'))
        )


        price = driver.execute_script("return arguments[0].innerText;", price_element)
        title = title_element.get_attribute('textContent').strip()
        image_url = image_element.get_attribute('src')

        if ',' in price:
            price_numeric = extract_price(price, patternComma)
            return price_numeric, title, url, image_url

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
        return price_numeric, title, url, image_url
    except Exception as e:
        print("An error occurred:", e)
    finally:
        driver.quit()


def add_to_database(title, price, url, image_url):
    # Format the price string
    price = format_price(price)

    # Rest of your code remains the same
    if len(url) > 255:
        url = url[:255]
    if len(image_url) > 255:
        image_url = image_url[:255]

    connection = mysql.connector.connect(
        host="localhost",
        port="3306",  
        user="root",
        password="root",
        database="dblic"
    )
    cursor = connection.cursor()

    try:
        cursor.execute("SELECT next_val FROM products_seq")
        next_id = cursor.fetchone()[0]
        next_id += 1
        cursor.execute("UPDATE products_seq SET next_val = %s", (next_id,))

        cursor.execute("SELECT * FROM products WHERE title = %s", (title,))
        existing_product = cursor.fetchone()
        if existing_product:
            cursor.execute("UPDATE products SET price = %s, image_url = %s WHERE title = %s", (price, image_url, title))
        else:
            cursor.execute("INSERT INTO products (id, title, price, product_url, image_url) VALUES (%s, %s, %s, %s, %s)", (next_id, title, price, url, image_url))
        connection.commit()
    except mysql.connector.Error as err:
        print("Error:", err)
    finally:
        connection.close()

import sys
if len(sys.argv) != 2:
    print("Usage: python script.py <url>")
    sys.exit(1)

product_url = sys.argv[1]
product_price, product_title, product_url, image_url = get_product_info(product_url)
add_to_database(product_title, product_price, product_url, image_url)
