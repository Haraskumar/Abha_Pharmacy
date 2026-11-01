import re

with open('src/main/resources/templates/admin/dashboard.html', 'r', encoding='utf-8') as f:
    content = f.read()

# Remove the entire aside block
content = re.sub(r'<aside.*?</aside>', '', content, flags=re.DOTALL)

# Remove the <div class="pl-64"> wrap
content = re.sub(r'<div class="pl-64">', '', content)
content = re.sub(r'</main></div></body>', '</main></body>', content)

# Replace the inner header with the fragment inclusion
content = re.sub(r'<header.*?<\/header>', '<header th:replace="~{admin/fragments/admin_header :: header}"></header>', content, count=1, flags=re.DOTALL)

with open('src/main/resources/templates/admin/dashboard.html', 'w', encoding='utf-8') as f:
    f.write(content)
