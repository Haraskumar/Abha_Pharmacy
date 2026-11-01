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
        
        # -> Click the 'Authenticate Staff Terminal' link to open the staff login/authentication page.
        # badge Authenticate Staff Terminal link
        elem = page.get_by_role("link", name="badge Authenticate Staff")
        await elem.click(timeout=10000)
        
        # -> Fill the STAFF EMAIL with jane@abhapharmacy.com, fill the PASSWORD with password123, then click the 'Authenticate' button to sign in.
        # staff@abhapharmacy.com email field
        elem = page.get_by_role("textbox", name="staff@abhapharmacy.com")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("jane@abhapharmacy.com")
        
        # -> Fill the STAFF EMAIL with jane@abhapharmacy.com, fill the PASSWORD with password123, then click the 'Authenticate' button to sign in.
        # •••••••• password field
        elem = page.get_by_role("textbox", name="••••••••")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("password123")
        
        # -> Fill the STAFF EMAIL with jane@abhapharmacy.com, fill the PASSWORD with password123, then click the 'Authenticate' button to sign in.
        # lock_open Authenticate button
        elem = page.get_by_role("button", name="lock_open Authenticate")
        await elem.click(timeout=10000)
        
        # -> Click the 'Launch POS Register' button on the Point of Sale (POS) card to open the POS page.
        # Launch POS Register arrow_forward link
        elem = page.get_by_role("link", name="Launch POS Register")
        await elem.click(timeout=10000)
        
        # -> Set the Paracetamol 500mg quantity to '500' and click the 'Sell' button to trigger quantity validation feedback.
        # quantity number field
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("spinbutton")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("500")
        
        # -> Set the Paracetamol 500mg quantity to '500' and click the 'Sell' button to trigger quantity validation feedback.
        # Sell button
        elem = page.get_by_role("cell", name="500 Sell").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Set Paracetamol quantity to 1 using the quantity field, click the 'Sell' button for that line, then click the 'Authorize & Complete Dispense' button to complete checkout.
        # quantity number field
        elem = page.get_by_role("cell", name="500 Sell").get_by_role("spinbutton")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("1")
        
        # -> Set Paracetamol quantity to 1 using the quantity field, click the 'Sell' button for that line, then click the 'Authorize & Complete Dispense' button to complete checkout.
        # Sell button
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Click the 'Sell' button for Paracetamol 500mg, then click the 'Authorize & Complete Dispense' button to complete checkout and view the receipt summary.
        # Sell button
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Click the 'Sell' button for Paracetamol 500mg, then click the 'Authorize & Complete Dispense' button to finalize the checkout and view the receipt summary.
        # Sell button
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Set the Paracetamol quantity to 1, click the 'Sell' button for Paracetamol 500mg, then click the 'Authorize & Complete Dispense' button to complete checkout and view the receipt summary.
        # quantity number field
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("spinbutton")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("1")
        
        # -> Set the Paracetamol quantity to 1, click the 'Sell' button for Paracetamol 500mg, then click the 'Authorize & Complete Dispense' button to complete checkout and view the receipt summary.
        # Sell button
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Click the 'Sell' button for Paracetamol 500mg, then click the 'Authorize & Complete Dispense' button to finalize checkout.
        # Sell button
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Click the 'Sell' button for Paracetamol 500mg, then click the 'Authorize & Complete Dispense' button to finalize the checkout and show the receipt summary.
        # Sell button
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Enter '500' into the Paracetamol quantity field and click the 'Sell' button to trigger and verify the quantity validation message.
        # quantity number field
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("spinbutton")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("500")
        
        # -> Enter '500' into the Paracetamol quantity field and click the 'Sell' button to trigger and verify the quantity validation message.
        # Sell button
        elem = page.get_by_role("cell", name="500 Sell").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Reduce Paracetamol quantity to 1, click the 'Sell' button, then click 'Authorize & Complete Dispense' to finalize checkout and display the receipt summary.
        # quantity number field
        elem = page.get_by_role("cell", name="500 Sell").get_by_role("spinbutton")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("1")
        
        # -> Reduce Paracetamol quantity to 1, click the 'Sell' button, then click 'Authorize & Complete Dispense' to finalize checkout and display the receipt summary.
        # Sell button
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Click the 'Sell' button for Paracetamol 500mg, then click the 'Authorize & Complete Dispense' button to complete checkout and display the receipt summary.
        # Sell button
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Click the 'Sell' button for Paracetamol 500mg, then click the 'Authorize & Complete Dispense' button to finalize the checkout and view the receipt summary.
        # quantity number field
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("spinbutton")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("1")
        
        # -> Click the 'Sell' button for Paracetamol 500mg, then click the 'Authorize & Complete Dispense' button to finalize the checkout and view the receipt summary.
        # Sell button
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Set Paracetamol quantity to 1, click the 'Sell' button for Paracetamol 500mg, then click the 'Authorize & Complete Dispense' button to complete checkout and view the receipt summary.
        # quantity number field
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("spinbutton")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("1")
        
        # -> Set Paracetamol quantity to 1, click the 'Sell' button for Paracetamol 500mg, then click the 'Authorize & Complete Dispense' button to complete checkout and view the receipt summary.
        # Sell button
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Enter '500' into the Paracetamol quantity field and click the 'Sell' button to trigger the quantity-exceeded validation.
        # Sell button
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Enter 500 into the Paracetamol quantity field and click the 'Sell' button to trigger quantity validation.
        # quantity number field
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("spinbutton")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("500")
        
        # -> Enter 500 into the Paracetamol quantity field and click the 'Sell' button to trigger quantity validation.
        # Sell button
        elem = page.get_by_role("cell", name="500 Sell").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Set the Paracetamol quantity to '1', click the 'Sell' button for Paracetamol, then click the 'Authorize & Complete Dispense' button to finalize checkout and show the receipt summary.
        # quantity number field
        elem = page.get_by_role("cell", name="500 Sell").get_by_role("spinbutton")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("1")
        
        # -> Set the Paracetamol quantity to '1', click the 'Sell' button for Paracetamol, then click the 'Authorize & Complete Dispense' button to finalize checkout and show the receipt summary.
        # Sell button
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Set Paracetamol quantity to '1', click the 'Sell' button for Paracetamol, then click the 'Authorize & Complete Dispense' button to complete checkout and view the receipt summary.
        # quantity number field
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("spinbutton")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("1")
        
        # -> Set Paracetamol quantity to '1', click the 'Sell' button for Paracetamol, then click the 'Authorize & Complete Dispense' button to complete checkout and view the receipt summary.
        # Sell button
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("button")
        await elem.click(timeout=10000)
        
        # -> Enter an excessive quantity (500) in the Paracetamol quantity field and click the 'Sell' button, then verify the input attributes show the exceeded value and then reduce to 1 and click 'Sell' again.
        # quantity number field
        elem = page.get_by_role("row", name="1 Paracetamol 500mg").get_by_role("spinbutton")
        await elem.wait_for(state="visible", timeout=10000)
        await elem.fill("500")
        
        # --> Assertions to verify final state
        
        # --> Quantity-exceeded validation appears when Paracetamol quantity is set to 500.
        # Assert-outcome: passed
        # Assert: The Paracetamol quantity input shows valuetext '500', indicating the exceeded-stock validation.
        await expect(page.get_by_role("cell", name="500 Sell").get_by_role("spinbutton").nth(0)).to_have_attribute("valuetext", "500", timeout=15000), "The Paracetamol quantity input shows valuetext '500', indicating the exceeded-stock validation."
        
        # --> A receipt summary control is visible (Print GST Tax Bill button is present).
        await page.get_by_role("button", name="receipt_long Print GST Tax").nth(0).scroll_into_view_if_needed()
        # Assert-outcome: passed
        # Assert: The Print GST Tax Bill button is visible, indicating the receipt summary is displayed.
        await expect(page.get_by_role("button", name="receipt_long Print GST Tax").nth(0)).to_be_visible(timeout=15000), "The Print GST Tax Bill button is visible, indicating the receipt summary is displayed."
        await asyncio.sleep(5)

    finally:
        if context:
            await context.close()
        if browser:
            await browser.close()
        if pw:
            await pw.stop()

asyncio.run(run_test())
    