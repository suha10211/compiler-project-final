package Classes.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
    public List<Row> rowList=new ArrayList<>();
    public void addRow(Row row){
        this.rowList.add(row);
    }
    public List<Row> getRows() {
        return this.rowList;
    }
    public void printTableWithoutErrors(List<String> errorVariables) {
        System.out.println("\nجدول الرموز (بدون المتغيرات التي تحتوي على أخطاء):");
        System.out.println("---------------------------------------------------------------");
        System.out.println("| النوع\t\t\t\t\t\t\t\t\t\t| القيمة");
        System.out.println("---------------------------------------------------------------");
        
        for(Row row : rowList) {
            if(row != null) {
                // التحقق مما إذا كان المتغير يحتوي على خطأ
                boolean hasError = false;
                for(String errorVar : errorVariables) {
                    if(row.value.contains(errorVar)) {
                        hasError = true;
                        break;
                    }
                }
                
                // طباعة الصف فقط إذا لم يكن يحتوي على خطأ
                if(!hasError) {
                    String type = String.format("%-20s", row.type);
                    String value = String.format("%-20s", row.value);
                    System.out.printf("| %s | %s%n", type, value);
                    System.out.println("---------------------------------------------------------------");
                }
            }
        }
    }
    public void printTable(){
        System.out.println("\nجدول الرموز الكامل:");
        System.out.println("---------------------------------------------------------------");
        System.out.println("| النوع\t\t\t\t\t\t\t\t\t\t| القيمة");
        System.out.println("---------------------------------------------------------------");
        for(int i=0;i<rowList.size();i++){
            if(rowList.get(i)!=null){
                String type=rowList.get(i).type;
                String value=rowList.get(i).value;
                type=String.format("%-20s",type);
                value=String.format("%-20s",value);
                System.out.printf("| %s | %s%n", type, value);
                System.out.println("---------------------------------------------------------------");
            }
        }
    }
}