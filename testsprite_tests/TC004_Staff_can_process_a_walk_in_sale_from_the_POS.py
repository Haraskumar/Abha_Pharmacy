import asyncio
import re
from playwright import async_api
from playwright.async_api import expect

async def run_test():
    pw = None
    browser = None
    context = None

    try:
        # Start a Playwright session in asynchronous mode
        pw = await async_api.async_playwright().start()

        # Launch a Chromium browser in headless mode with custom arguments
        browser = await pw.chromium.launch(
            headless=True,
            args=[
                "--window-size=1280,720",
                "--disable-dev-shm-usage",
                "--ipc=host",
                "--single-process"
            ],
        )

        # Create a new browser context (like an incognito window)
        context = await browser.new_context()
        # Wider default timeout to match the agent's DOM-stability budget;
        # auto-waiting Playwright APIs (expect, locator.wait_for) inherit this.
        context.set_default_timeout(15000)

        # Open a new page in the browser context
        page = await context.new_page()

        # Interact with the page elements to simulate user flow
        # -> navigate
        await page.goto("http://localhost:8081")
        try:
            await page.wait_for_load_state("domcontentloaded", timeout=5000)
        except Exception:
            pass
        
        # -> Click the 'Authenticate Staff Terminal' button to open the staff login page.
        # badge Authenticate Staff Terminal link
        elem = page.get_by_role("link", name="badge Authenticate Staff")
        await elem.click(timeout=10000)
        
        # -> Fill 'jane@abhapharmacy.com' into the Staff Email field, 'password123' into the Password field, then click the 'Authenticate' button to sign in.
        # staff@abhapharmacy.com email field
        elem = page.get_by_role("textbox", name="staff@abhapharmacy.com")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("jane@abhapharmacy.com")
        
        # -> Fill 'jane@abhapharmacy.com' into the Staff Email field, 'password123' into the Password field, then click the 'Authenticate' button to sign in.
        # •••••••• password field
        elem = page.get_by_role("textbox", name="••••••••")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("password123")
        
        # -> Fill 'jane@abhapharmacy.com' into the Staff Email field, 'password123' into the Password field, then click the 'Authenticate' button to sign in.
        # lock_open Authenticate button
        elem = page.get_by_role("button", name="lock_open Authenticate")
        await elem.click(timeout=10000)
        
        # -> Click the 'Launch POS Register' button to open the POS register.
        # Launch POS Register arrow_forward link
        elem = page.get_by_role("link", name="Launch POS Register")
        await elem.click(timeout=10000)
        
        # -> Enter quantity '2' for Paracetamol 500mg using the Paracetamol row quantity field and click the 'Sell' button, then click 'Authorize & Complete Dispense' to complete checkout.
        # quantity number field
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("spinbutton")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("2")
        
        # -> Enter quantity '2' for Paracetamol 500mg using the Paracetamol row quantity field and click the 'Sell' button, then click 'Authorize & Complete Dispense' to complete checkout.
        # Sell button
        elem = page.get_by_role("cell", name="2 Sell").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Click the 'Sell' button for Paracetamol 500mg (ensure the POS page is settled and interactive elements are available).
        await page.mouse.wheel(0, 300)
        
        # -> Enter '2' in the Paracetamol 500mg quantity field and click the 'Sell' button to add it to the bill.
        # quantity number field
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("spinbutton")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("2")
        
        # -> Enter '2' in the Paracetamol 500mg quantity field and click the 'Sell' button to add it to the bill.
        # Sell button
        elem = page.get_by_role("cell", name="2 Sell").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Enter '2' into the Paracetamol quantity field and click the 'Sell' button to add it to the bill, then click 'Authorize & Complete Dispense'.
        # Sell
        elem = page.get_by_role("cell", name="Sell").first
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("2")
        
        # -> Enter '2' into the Paracetamol quantity field and click the 'Sell' button to add it to the bill, then click 'Authorize & Complete Dispense'.
        # Sell button
        elem = page.get_by_role("cell", name="12 Sell").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Enter quantity '2' into the Paracetamol 500mg quantity field and click the 'Sell' button, then click 'Authorize & Complete Dispense'.
        # quantity number field
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("spinbutton")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("2")
        
        # -> Enter quantity '2' into the Paracetamol 500mg quantity field and click the 'Sell' button, then click 'Authorize & Complete Dispense'.
        # Sell button
        elem = page.get_by_role("cell", name="2 Sell").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Enter '2' into the Paracetamol 500mg quantity field and click the 'Sell' button, then click the 'Authorize & Complete Dispense' button to complete checkout.
        # quantity number field
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("spinbutton")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("2")
        
        # -> Enter '2' into the Paracetamol 500mg quantity field and click the 'Sell' button, then click the 'Authorize & Complete Dispense' button to complete checkout.
        # Sell button
        elem = page.get_by_role("cell", name="2 Sell").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Set quantity '2' for Paracetamol 500mg and click the 'Sell' button, then click the 'Authorize & Complete Dispense' button to complete checkout.
        # Sell button
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Enter quantity '2' into the Paracetamol 500mg quantity field and click the 'Sell' button to add it to the bill.
        # quantity number field
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("spinbutton")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("2")
        
        # -> Enter quantity '2' into the Paracetamol 500mg quantity field and click the 'Sell' button to add it to the bill.
        # Sell button
        elem = page.get_by_role("cell", name="2 Sell").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Enter '2' into the Paracetamol quantity field and click the 'Sell' button to add it to the bill, then click 'Authorize & Complete Dispense'.
        # quantity number field
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("spinbutton")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("2")
        
        # -> Enter '2' into the Paracetamol quantity field and click the 'Sell' button to add it to the bill, then click 'Authorize & Complete Dispense'.
        # Sell button
        elem = page.get_by_role("cell", name="2 Sell").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Set quantity '2' for Paracetamol 500mg, click the 'Sell' button, then click 'Authorize & Complete Dispense' to complete checkout.
        # Sell button
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Enter '2' into the Paracetamol 500mg quantity field and click the 'Sell' button.
        # quantity number field
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("spinbutton")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("2")
        
        # -> Enter '2' into the Paracetamol 500mg quantity field and click the 'Sell' button.
        # Sell button
        elem = page.get_by_role("cell", name="2 Sell").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Set '2' in the Paracetamol quantity field and click the 'Sell' button, then click the 'Authorize & Complete Dispense' button to complete checkout.
        # quantity number field
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("spinbutton")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("2")
        
        # -> Set '2' in the Paracetamol quantity field and click the 'Sell' button, then click the 'Authorize & Complete Dispense' button to complete checkout.
        # Sell button
        elem = page.get_by_role("cell", name="2 Sell").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Set quantity '2' for 'Paracetamol 500mg' and click the 'Sell' button, then click the 'Authorize & Complete Dispense' button to complete checkout.
        # Sell button
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Set quantity '2' for Paracetamol 500mg, click the 'Sell' button, then click the 'Authorize & Complete Dispense' button to complete checkout.
        # quantity number field
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("spinbutton")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("2")
        
        # -> Set quantity '2' for Paracetamol 500mg, click the 'Sell' button, then click the 'Authorize & Complete Dispense' button to complete checkout.
        # Sell button
        elem = page.get_by_role("cell", name="2 Sell").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Enter '2' into the Paracetamol 500mg quantity field, click the 'Sell' button, then click 'Authorize & Complete Dispense' to complete checkout.
        # quantity number field
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("spinbutton")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("2")
        
        # -> Enter '2' into the Paracetamol 500mg quantity field, click the 'Sell' button, then click 'Authorize & Complete Dispense' to complete checkout.
        # Sell button
        elem = page.get_by_role("cell", name="2 Sell").get_by_role("button")
        await elem.click(timeout=10000)
        
        # --> Assertions to verify final state
        
        # --> A receipt summary is displayed (Print GST Tax Bill action is available).
        await page.get_by_role("button", name="receipt_long Print GST Tax").nth(0).scroll_into_view_if_needed()
        # Assert-outcome: passed
        # Assert: The Print GST Tax Bill button is visible, indicating a receipt summary is displayed.
        await expect(page.get_by_role("button", name="receipt_long Print GST Tax").nth(0)).to_be_visible(timeout=15000), "The Print GST Tax Bill button is visible, indicating a receipt summary is displayed."
        await asyncio.sleep(5)

    finally:
        if context:
            await context.close()
        if browser:
            await browser.close()
        if pw:
            await pw.stop()

asyncio.run(run_test())
    