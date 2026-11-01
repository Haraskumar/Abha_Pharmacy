import os
import glob
import re

customer_dir = 'src/main/resources/templates/customer'
for filepath in glob.glob(os.path.join(customer_dir, '*.html')):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Substitute the entire <header ...> ... </header> block
    new_content = re.sub(r'<header.*?</header>', '<header th:replace="~{customer/fragments/customer_header :: header}"></header>', content, count=1, flags=re.DOTALL)
    
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(new_content)
        
