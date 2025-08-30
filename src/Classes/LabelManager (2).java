package Classes;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * مدير الليبلات (LabelManager)
 * هذه الكلاس مسؤولة عن إدارة الليبلات في البرنامج
 * يستخدم نمط Singleton لضمان وجود نسخة واحدة فقط من المدير
 */
public class LabelManager {
    private static LabelManager instance;
    private Map<String, LabelInfo> labels;
    private int counter;
    
    /**
     * المُنشئ الخاص - يتم استدعاؤه فقط من خلال getInstance
     * يقوم بتهيئة خريطة الليبلات وعداد التسلسل
     */
    private LabelManager() {
        labels = new HashMap<>();
        counter = 1;
    }
    
    /**
     * الحصول على نسخة وحيدة من مدير الليبلات
     * @return نسخة LabelManager
     */
    public static LabelManager getInstance() {
        if (instance == null) {
            instance = new LabelManager();
        }
        return instance;
    }
    
    /**
     * إنشاء ليبل جديد
     * @param type نوع الليبل
     * @param name اسم الليبل
     * @return الليبل المُنشأ
     */
    public String createLabel(String type, String name) {
        String label = String.format("L%d", counter++);
        labels.put(label, new LabelInfo(type, name));
        return label;
    }
    
    /**
     * إنشاء تقرير مفصل عن الليبلات
     * يقوم بتصنيف الليبلات حسب النوع وعرضها بشكل منظم
     * @return قائمة تحتوي على التقرير
     */
    public List<String> getLabelsReport() {
        List<String> report = new ArrayList<>();
        
        // تصنيف الليبلات حسب النوع
        Map<String, List<Map.Entry<String, LabelInfo>>> categorizedLabels = new TreeMap<>();
        
        // تجميع الليبلات حسب النوع
        for (Map.Entry<String, LabelInfo> entry : labels.entrySet()) {
            String type = entry.getValue().getType();
            categorizedLabels.computeIfAbsent(type, k -> new ArrayList<>()).add(entry);
        }
        
        // طباعة التقرير المصنف
        report.add("\nتقرير الليبلات في المشروع:");
        report.add("===============================================================");
        
        for (Map.Entry<String, List<Map.Entry<String, LabelInfo>>> category : categorizedLabels.entrySet()) {
            String type = category.getKey();
            List<Map.Entry<String, LabelInfo>> typeLabels = category.getValue();
            
            // طباعة عنوان الفئة
            report.add(String.format("\n[%s]", type));
            report.add("---------------------------------------------------------------");
            report.add("| الليبل | الاسم");
            report.add("---------------------------------------------------------------");
            
            // طباعة الليبلات في هذه الفئة
            for (Map.Entry<String, LabelInfo> label : typeLabels) {
                report.add(String.format("| %-6s | %s", 
                    label.getKey(), 
                    label.getValue().getName()));
            }
            report.add("---------------------------------------------------------------");
        }
        
        // طباعة ملخص
        report.add("\nملخص الليبلات:");
        report.add("---------------------------------------------------------------");
        report.add(String.format("إجمالي عدد الليبلات: %d", labels.size()));
        for (Map.Entry<String, List<Map.Entry<String, LabelInfo>>> category : categorizedLabels.entrySet()) {
            report.add(String.format("عدد %s: %d", 
                category.getKey(), 
                category.getValue().size()));
        }
        report.add("===============================================================");
        
        return report;
    }
    
    /**
     * كلاس داخلي لتخزين معلومات الليبل
     * يحتوي على نوع الليبل واسمه
     */
    private static class LabelInfo {
        private String type;
        private String name;
        
        public LabelInfo(String type, String name) {
            this.type = type;
            this.name = name;
        }
        
        public String getType() { return type; }
        public String getName() { return name; }
    }
} 