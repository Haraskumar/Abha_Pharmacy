import re

with open('src/main/resources/templates/customer/cart_checkout.html', 'r', encoding='utf-8') as f:
    content = f.read()

# I need to add an id to the payment section
content = content.replace('<!-- Payment Method Selector -->', '<!-- Payment Method Selector -->\n<div id="payment-section-container">')
# And close it after the Statutory Compliance notice? No, just close it before Statutory Compliance notice
content = content.replace('<!-- Statutory Compliance notice -->', '</div>\n<!-- Statutory Compliance notice -->')

# Update JS to hide/show
js_hide = '''
                    badgePickup.textContent = "SELECTED";
                    badgeDelivery.className = "font-mono text-xs text-outline font-semibold";
                    badgeDelivery.textContent = "FREE";
                    
                    const paymentSection = document.getElementById('payment-section-container');
                    if(paymentSection) paymentSection.style.display = 'none';
                }
'''
content = content.replace('''                    badgePickup.textContent = "SELECTED";
                    badgeDelivery.className = "font-mono text-xs text-outline font-semibold";
                    badgeDelivery.textContent = "FREE";
                }''', js_hide)

js_show = '''
                    badgeDelivery.textContent = "SELECTED";
                    badgePickup.className = "font-mono text-xs text-outline font-semibold";
                    badgePickup.textContent = "FREE";
                    
                    const paymentSection = document.getElementById('payment-section-container');
                    if(paymentSection) paymentSection.style.display = 'block';
                }
'''
content = content.replace('''                    badgeDelivery.textContent = "SELECTED";
                    badgePickup.className = "font-mono text-xs text-outline font-semibold";
                    badgePickup.textContent = "FREE";
                } else {''', js_show + ' else {')

with open('src/main/resources/templates/customer/cart_checkout.html', 'w', encoding='utf-8') as f:
    f.write(content)
