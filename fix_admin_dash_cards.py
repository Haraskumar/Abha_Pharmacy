import re

with open('src/main/resources/templates/admin/dashboard.html', 'r', encoding='utf-8') as f:
    content = f.read()

# I will replace any specific variant colors with a standard one if they exist
content = content.replace('bg-tertiary-container', 'bg-primary')
content = content.replace('text-tertiary', 'text-primary')
content = content.replace('bg-error-container', 'bg-primary')
content = content.replace('text-error', 'text-primary')

with open('src/main/resources/templates/admin/dashboard.html', 'w', encoding='utf-8') as f:
    f.write(content)
