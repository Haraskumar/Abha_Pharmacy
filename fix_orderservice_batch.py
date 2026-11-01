import re

with open('src/main/java/com/abhapharmacy/service/OrderService.java', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('batchRepository.findByMedicine_MedicineId(medicineId)', 'batchRepository.findByMedicineId(medicineId)')

with open('src/main/java/com/abhapharmacy/service/OrderService.java', 'w', encoding='utf-8') as f:
    f.write(content)
