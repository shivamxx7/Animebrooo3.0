import requests
url = "https://i.postimg.cc/SQW8Mc40/1000106315-Photoroom.png"
response = requests.head(url)
print(response.status_code)
