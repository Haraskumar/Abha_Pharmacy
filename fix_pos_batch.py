import re

with open('src/main/java/com/abhapharmacy/service/OrderService.java', 'r', encoding='utf-8') as f:
    content = f.read()

replacement = '''
        if ("DEFAULT".equals(batchNo)) {
            java.util.List<Batch> batches = batchRepository.findByMedicine_MedicineId(medicineId);
            if (batches == null || batches.isEmpty()) {
                throw new RuntimeException("No batches available for this medicine.");
            }
            Batch selected = null;
            for (Batch bItem : batches) {
                if (bItem.getQuantityInStock() >= quantity) {
                    selected = bItem;
                    break;
                }
            }
            if (selected == null) throw new RuntimeException("Insufficient stock in any single batch.");
            batchNo = selected.getBatchNo();
        }
        
        Batch b = batchRepository.findByMedicineIdAndBatchNo(medicineId, batchNo);
'''

content = content.replace('Batch b = batchRepository.findByMedicineIdAndBatchNo(medicineId, batchNo);', replacement)

with open('src/main/java/com/abhapharmacy/service/OrderService.java', 'w', encoding='utf-8') as f:
    f.write(content)
