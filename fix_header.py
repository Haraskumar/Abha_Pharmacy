import re
with open('src/main/resources/templates/customer/fragments/customer_header.html', 'r', encoding='utf-8') as f:
    c = f.read()
c = re.sub(r'th:classappend="[^"]*request\.requestURI[^"]*"', '', c)
with open('src/main/resources/templates/customer/fragments/customer_header.html', 'w', encoding='utf-8') as f:
    f.write(c)
