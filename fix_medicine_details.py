import re

with open('src/main/resources/templates/customer/medicine_details.html', 'r', encoding='utf-8') as f:
    content = f.read()

# Change Order Formulation Now to Add to Cart
content = content.replace('Order Formulation Now', 'Add to Cart')

# Change button type to button and give it an id
content = content.replace('<button type="submit" class="bg-primary hover:bg-primary-container text-on-primary font-bold text-sm px-6 py-3 rounded-xl shadow-xs transition-all flex items-center justify-center gap-2 active:scale-[0.99]">', 
'<button type="button" id="add-to-cart-btn" class="bg-primary hover:bg-primary-container text-on-primary font-bold text-sm px-6 py-3 rounded-xl shadow-xs transition-all flex items-center justify-center gap-2 active:scale-[0.99]">')

# Inject script to show "Go to Cart" button
script = '''
            const addBtn = document.getElementById('add-to-cart-btn');
            if (addBtn) {
                addBtn.addEventListener('click', function(e) {
                    if (currentQty <= 10) {
                        e.preventDefault();
                        
                        // Change button to Go To Cart
                        addBtn.innerHTML = '<span class="material-symbols-outlined text-[20px]">shopping_cart_checkout</span><span>Go to Cart (' + currentQty + ' items)</span>';
                        
                        // We also need to let it redirect if they click it again
                        addBtn.onclick = function() {
                            window.location.href = '/customer/cart';
                        };
                        
                        // Show a tiny floating indicator if requested, but changing the button is clearer
                    }
                });
            }
'''
content = content.replace('const orderForm = document.getElementById(\'otc-order-form\');', script + '\n            const orderForm = document.getElementById(\'otc-order-form\');')

with open('src/main/resources/templates/customer/medicine_details.html', 'w', encoding='utf-8') as f:
    f.write(content)
