import re

with open('src/main/resources/templates/admin/dashboard.html', 'r', encoding='utf-8') as f:
    content = f.read()

# Fix STF-
content = re.sub(r'<span>ID: <strong class="text-on-surface" th:text="\'STF-\' \+ \$\{session\.staffId\}">STF-001</strong></span>', '', content)

with open('src/main/resources/templates/admin/dashboard.html', 'w', encoding='utf-8') as f:
    f.write(content)
