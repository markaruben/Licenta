import re
import mysql.connector
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options
from urllib.parse import urlparse
import sys

pattern_no_comma = r'\d+[.,]?\d+'
pattern_comma = r'\d{1,3}(?:\.\d{3})*(?:,\d{1,2})?'

def extract_price(price_string, pattern):
    match = re.search(pattern, price_string)
    return match.group() if match else None

def format_price(price):
    return price.replace('.', '').replace(',', '.')

def get_product_info_amazonshop(url):
    chrome_options = Options()
    chrome_options.add_argument("--headless")
    driver = webdriver.Chrome(options=chrome_options)
    driver.get(url)
    
    try:
        price_element = WebDriverWait(driver, 1).until(
            EC.presence_of_element_located((By.CLASS_NAME, 'current-price'))
        )

        title_element = WebDriverWait(driver, 1).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, 'h1.h1.page-heading-product[itemprop="name"]'))
        )
        
        image_element = WebDriverWait(driver, 1).until(
            EC.presence_of_element_located((By.CLASS_NAME, 'zoomImg'))
        )

        price = price_element.get_attribute('textContent').strip()
        title = title_element.get_attribute('textContent').strip()
        image_url = image_element.get_attribute('src')

        price_numeric = extract_price(price, pattern_comma)
        
        return price_numeric, title, url, image_url
    except Exception as e:
        print("An error occurred:", e)
    finally:
        driver.quit()

def get_product_info(url):
    chrome_options = Options()
    chrome_options.add_argument("--headless")
    driver = webdriver.Chrome(options=chrome_options)
    driver.get(url)
    
    try:
        price_element = WebDriverWait(driver, 1).until(
            EC.presence_of_element_located((By.CLASS_NAME, 'product-new-price'))
        )

        title_element = WebDriverWait(driver, 1).until(
            EC.presence_of_element_located((By.CLASS_NAME, 'page-title'))
        )

        image_element = WebDriverWait(driver, 1).until(
            EC.presence_of_element_located((By.XPATH, '//a[@class="thumbnail product-gallery-image gtm_rp125918"]/img'))
        )

        price = price_element.get_attribute('textContent').strip()
        title = title_element.get_attribute('textContent').strip()
        image_url = image_element.get_attribute('src')

        if ',' in price:
            price_numeric = extract_price(price, pattern_comma)
            return price_numeric, title, url, image_url

        decimal_element = WebDriverWait(driver, 1).until(
            EC.presence_of_element_located((By.TAG_NAME, 'sup'))
        )

        decimal = decimal_element.get_attribute('textContent')
        price_numeric = extract_price(price, pattern_no_comma)
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

def add_to_database(products):
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

        for product in products:
            title, price, url, image_url = product
            price = format_price(price)
            
            # Shorten product_url to 255 characters
            url = url[:255]
            
            cursor.execute("UPDATE products_seq SET next_val = %s", (next_id,))
            cursor.execute("SELECT * FROM products WHERE title = %s", (title,))
            existing_product = cursor.fetchone()
            if existing_product:
                cursor.execute("UPDATE products SET price = %s, image_url = %s, product_url = %s WHERE title = %s", (price, image_url, url, title))
            else:
                cursor.execute("INSERT INTO products (id, title, price, product_url, image_url) VALUES (%s, %s, %s, %s, %s)", (next_id, title, price, url, image_url))
            next_id += 1
        connection.commit()
    except mysql.connector.Error as err:
        print("Error:", err)
    finally:
        connection.close()


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python script.py <url>")
        sys.exit(1)

    product_url = sys.argv[1]
    parsed_url = urlparse(product_url)

    if parsed_url.netloc == 'www.emag.ro':
        product_price, product_title, product_url, image_url = get_product_info(product_url)
    elif parsed_url.netloc == 'amazonshop.ro':
        product_price, product_title, product_url, image_url = get_product_info_amazonshop(product_url)
    else:
        print("Unsupported website")
        sys.exit(1)

    products = [(product_title, product_price, product_url, image_url)]
    add_to_database(products)
