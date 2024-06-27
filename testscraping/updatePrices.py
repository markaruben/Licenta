import re
import mysql.connector
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options
import schedule
import time
import requests

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

def update_product_price(product_id, new_price):
    try:
        connection = mysql.connector.connect(
            host="localhost",
            port="3306",
            user="root",
            password="root",
            database="dblic"
        )
        cursor = connection.cursor()

        cursor.execute("UPDATE products SET price = %s WHERE id = %s", (new_price, product_id))

        # Get next value for price_history id
        cursor.execute("SELECT next_val FROM price_history_seq")
        next_price_history_id = cursor.fetchone()[0]
        cursor.execute("UPDATE price_history_seq SET next_val = %s", (next_price_history_id + 1,))

        cursor.execute("INSERT INTO price_history (id, product_id, price, date) VALUES (%s, %s, %s, NOW())", (next_price_history_id, product_id, new_price))
        connection.commit()

        # Call backend endpoint to notify about price change
        notify_backend_price_change(product_id, new_price)

    except mysql.connector.Error as err:
        print("Error:", err)
    finally:
        if 'connection' in locals():
            connection.close()

def delete_product(product_id):
    backend_url = f'http://localhost:8000/products/{product_id}'
    try:
        response = requests.delete(backend_url)
        response.raise_for_status()
        print(f"Product ID {product_id} deleted successfully")
    except requests.RequestException as e:
        print(f"Error deleting product ID {product_id}:", e)

def notify_backend_price_change(product_id, new_price):
    backend_url = 'http://localhost:8000/products/notifyPriceChange'
    payload = {
        'productId': product_id,
        'price': new_price
    }

    try:
        response = requests.post(backend_url, json=payload)
        response.raise_for_status()
        print("Notification sent to backend successfully")
    except requests.RequestException as e:
        print("Error sending notification to backend:", e)

def scrape_price_from_url_emag(url):
    chrome_options = Options()
    chrome_options.add_argument("--headless")
    driver = webdriver.Chrome(options=chrome_options)
    driver.get(url)

    try:
        price_element = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, 'product-new-price'))
        )
        price = driver.execute_script("return arguments[0].innerText;", price_element)
        return price
    except Exception as e:
        print("An error occurred:", e)
    finally:
        driver.quit()

def scrape_price_from_url_amazonshop(url):
    chrome_options = Options()
    chrome_options.add_argument("--headless")
    driver = webdriver.Chrome(options=chrome_options)
    driver.get(url)

    try:
        price_element = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CLASS_NAME, 'current-price'))
        )
        price = price_element.get_attribute('textContent').strip()
        return price
    except Exception as e:
        print("An error occurred:", e)
    finally:
        driver.quit()

def update_prices_from_database():
    try:
        connection = mysql.connector.connect(
        host="localhost",
        port="3306",  
        user="root",
        password="root",
        database="dblic"
    )
        cursor = connection.cursor()

        cursor.execute("SELECT id, product_url, price FROM products")
        products = cursor.fetchall()
        for product in products:
            product_id, product_url, stored_price = product
            if 'emag.ro' in product_url:
                new_price = scrape_price_from_url_emag(product_url)
            elif 'amazonshop.ro' in product_url:
                new_price = scrape_price_from_url_amazonshop(product_url)
            else:
                print("Unsupported website:", product_url)
                continue

            if new_price:
                new_price_numeric = extract_price(new_price, r'\d{1,3}(?:\.\d{3})*(?:,\d{1,2})?')
                new_price_numeric = format_price(new_price_numeric)
                if new_price_numeric:
                    if new_price_numeric != stored_price:
                        update_product_price(product_id, new_price_numeric)
                    else:
                        print("New price is the same as the stored price for product ID:", product_id)
                else:
                    print("Price format not recognized for product ID:", product_id)
            else:
                print("Failed to scrape price for product ID:", product_id)
                delete_product(product_id)
    except mysql.connector.Error as err:
        print("Error:", err)
    finally:
        if 'connection' in locals():
            connection.close()

schedule.every(1).minutes.do(update_prices_from_database)

while True:
    schedule.run_pending()
    time.sleep(1)