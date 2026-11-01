import re

with open('src/main/resources/templates/admin/fragments/admin_head.html', 'r', encoding='utf-8') as f:
    content = f.read()

m3_colors = '''
            "background": "#faf8ff",
            "surface": "#faf8ff",
            "surface-container-lowest": "#ffffff",
            "surface-container-low": "#f2f3ff",
            "surface-container": "#eaedff",
            "surface-container-high": "#e2e7ff",
            "surface-container-highest": "#dae2fd",
            "on-surface": "#131b2e",
            "on-surface-variant": "#3e4944",
            "primary-container": "#0d7a5f",
            "on-primary": "#ffffff",
            "on-primary-container": "#a8ffdf",
            "secondary": "#206a54",
            "secondary-container": "#a6eed2",
            "on-secondary-container": "#266e58",
            "tertiary": "#005886",
            "tertiary-container": "#0071ac",
            "tertiary-fixed": "#cce5ff",
            "outline": "#6e7a74",
            "outline-variant": "#bdc9c2",
            "error": "#ba1a1a",
            "error-container": "#ffdad6",
            "on-error-container": "#93000a",
'''

# We need to insert m3_colors into the colors: { object in tailwind.config
content = content.replace('colors: {', 'colors: {' + m3_colors)

with open('src/main/resources/templates/admin/fragments/admin_head.html', 'w', encoding='utf-8') as f:
    f.write(content)
