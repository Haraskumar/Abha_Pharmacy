import os

os.makedirs('src/main/resources/templates/customer/fragments', exist_ok=True)

header_html = '''<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
<header th:fragment="header" class="fixed top-0 left-0 right-0 z-50 bg-surface-container-lowest shadow-[0_1px_8px_rgba(0,0,0,0.04)]">
<div class="h-16 w-full px-gutter-desktop flex items-center justify-between">
    <div class="flex items-center gap-space-lg">
        <div class="flex items-center gap-space-md">
            <div class="flex flex-col">
                <span class="font-headline-sm text-headline-sm text-primary leading-none">Abha Pharmacy</span>
                <span class="font-label-sm text-label-sm text-on-surface-variant leading-tight">Patient Care Portal</span>
            </div>
        </div>
        <div class="h-6 w-px bg-outline-variant/30 hidden lg:block"></div>
        <nav class="hidden lg:flex items-center gap-space-lg" data-active-classes="text-primary font-label-lg font-semibold">
            <a class="transition-colors text-primary font-label-lg font-semibold" data-path="patient-portal-hub" href="/customer/dashboard">Home Hub</a>
            <a class="font-label-lg text-label-lg text-on-surface-variant hover:text-on-surface transition-colors" data-path="medicine-discovery" href="/customer/catalog">Medicine Discovery</a>
            <a class="font-label-lg text-label-lg text-on-surface-variant hover:text-on-surface transition-colors" data-path="upload-prescription" href="/customer/prescription/upload">Upload Prescription</a>
            <a class="font-label-lg text-label-lg text-on-surface-variant hover:text-on-surface transition-colors" data-path="my-refills" href="/customer/refill">Refills</a>
            <a class="font-label-lg text-label-lg text-on-surface-variant hover:text-on-surface transition-colors" data-path="order-history" href="/customer/orders">Orders</a>
            <a class="font-label-lg text-label-lg text-on-surface-variant hover:text-on-surface transition-colors flex items-center gap-1" data-path="cart" href="/customer/cart">
                <span class="material-symbols-outlined text-[16px]">shopping_cart</span>Cart
            </a>
            <a class="font-label-lg text-label-lg text-on-surface-variant hover:text-on-surface transition-colors" data-path="patient-profile" href="/customer/profile">Profile</a>
        </nav>
    </div>
    <div class="flex items-center gap-space-lg">
        <div class="hidden sm:flex items-center gap-space-sm bg-surface-container-low px-space-md py-space-xs rounded-xl">
            <span class="material-symbols-outlined text-[16px] text-primary">verified_user</span>
            <div class="flex flex-col">
                <span class="font-label-sm text-label-sm text-on-surface-variant leading-none">Patient Record</span>
                <span class="font-code-mono text-code-mono text-on-surface font-semibold leading-none" th:text="">Active</span>
            </div>
        </div>
        <div class="flex items-center gap-space-md">
            <div class="w-8 h-8 rounded-full bg-primary flex items-center justify-center">
                <span class="material-symbols-outlined text-on-primary text-[18px]">person</span>
            </div>
            <a class="font-label-sm text-label-sm text-error hover:text-on-error-container transition-colors" href="/logout">Logout</a>
        </div>
    </div>
</div>
</header>
</body>
</html>
'''

with open('src/main/resources/templates/customer/fragments/customer_header.html', 'w', encoding='utf-8') as f:
    f.write(header_html)
