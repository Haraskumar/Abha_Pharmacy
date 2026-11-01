import re
with open('src/main/resources/templates/customer/dashboard.html', 'r', encoding='utf-8') as f:
    content = f.read()

content = re.sub(r'<span class="font-code-mono text-code-mono text-outline bg-surface-container px-space-sm py-0.5 rounded-lg" th:text="\'CUST-\' \+ \$\{session\.customerId\}">CUST-0000</span>', '', content)

with open('src/main/resources/templates/customer/dashboard.html', 'w', encoding='utf-8') as f:
    f.write(content)
