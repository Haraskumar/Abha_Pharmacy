import re

with open('src/main/resources/templates/customer/dashboard.html', 'r', encoding='utf-8') as f:
    content = f.read()

# Make all cards consistent
content = content.replace('bg-surface-container-low rounded-xl p-space-xl shadow-sm flex flex-col justify-between', 'bg-surface-container-lowest rounded-xl p-space-xl shadow-sm flex flex-col justify-between transition-all duration-200 hover:shadow-md hover:-translate-y-0.5')
content = content.replace('text-tertiary', 'text-primary')

with open('src/main/resources/templates/customer/dashboard.html', 'w', encoding='utf-8') as f:
    f.write(content)
