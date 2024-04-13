import re
import mysql.connector
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options
import schedule
import time

def extract_price(price_string, pattern):
    match = re.search(pattern, price_string)
    if match:
        price=match.group()
        price = price.replace('.', '')
        price = price.replace(',', '.')
        return price
    return None

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
        connection.commit()
    except mysql.connector.Error as err:
        print("Error:", err)
    finally:
        if 'connection' in locals():
            connection.close()

def scrape_price_from_url(url):
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

        cursor.execute("SELECT id, product_url FROM products")
        products = cursor.fetchall()
        for product in products:
            product_id, product_url = product
            new_price = scrape_price_from_url(product_url)
            if new_price:
                new_price_numeric = extract_price(new_price, r'\d{1,3}(?:\.\d{3})*(?:,\d{1,2})?')
                if new_price_numeric:
                    update_product_price(product_id, new_price_numeric)
                else:
                    print("Price format not recognized for product ID:", product_id)
            else:
                print("Failed to scrape price for product ID:", product_id)
    except mysql.connector.Error as err:
        print("Error:", err)
    finally:
        if 'connection' in locals():
            connection.close()

schedule.every(1).minutes.do(update_prices_from_database)

while True:
    schedule.run_pending()
    time.sleep(1)
