package Classes;

import Classes.Program;
import Classes.SymbolTable.SymbolTable;
import Classes.GenericStatements.Variables.VariableDecl;
import Classes.Values.ValueType;
import Classes.Values.IntegerValue;
import Classes.Values.FloatValue;
import Classes.Values.StringValue;
import Classes.Values.BooleanValue;
import Classes.VariableNaming;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * فاحص الدلالات (SemanticChecker)
 * هذه الكلاس مسؤولة عن التحقق من صحة الكود من الناحية الدلالية
 * يقوم بفحص المتغيرات والتعريفات وأنواع البيانات
 */
public class SemanticChecker {
    private SymbolTable symbolTable;
    private List<String> errors;
    private List<SymbolTable> errorSymbolTables;

    /**
     * مُنشئ الفاحص الدلالي
     *
     * @param symbolTable جدول الرموز المراد فحصه
     */
    public SemanticChecker(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.errors = new ArrayList<>();
        this.errorSymbolTables = new ArrayList<>();
    }

    /**
     * فحص البرنامج بالكامل
     * يقوم بإجراء جميع عمليات الفحص الدلالي
     *
     * @param program البرنامج المراد فحصه
     * @return قائمة الأخطاء المكتشفة
     */
    public List<String> checkProgram(Program program) {
        System.out.println("\nبدء الفحص الدلالي للبرنامج...");
        errors.clear();
        errorSymbolTables.clear();

        // التحقق من المتغيرات غير المعرفة
        checkUndefinedVariables();

        // التحقق من أنواع المتغيرات
        checkVariableTypes();

        // التحقق من التعريفات المكررة
        checkDuplicateDeclarations();

        // التحقق من صحة التعابير
        checkExpressions();

        // التحقق من صحة الدوال
        checkFunctions();

        // التحقق من صحة الكومبوننت
        checkComponents();

        // التحقق من صحة الـ imports
        checkImports();

        System.out.println("\nانتهى الفحص الدلالي للبرنامج.");
        return errors;
    }

    /**
     * الحصول على قائمة الأخطاء
     *
     * @return قائمة الأخطاء المكتشفة
     */
    public List<String> getErrors() {
        return this.errors;
    }

    /**
     * طباعة الأخطاء المكتشفة بشكل منظم
     * يعرض تفاصيل كل خطأ مع جدول الرموز المرتبط به
     */
    public void printErrors() {
        if (errors.isEmpty()) {
            System.out.println("\nلا توجد أخطاء دلالية في الكود.");
            return;
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("تقرير الأخطاء الدلالية");
        System.out.println("=".repeat(50));

        for (int i = 0; i < errors.size(); i++) {
            String error = errors.get(i);
            String[] parts = error.split(": ", 2);
            String errorType = parts[0];
            String details = parts.length > 1 ? parts[1] : "";

            System.out.println("\n" + "-".repeat(50));
            System.out.println("الخطأ رقم " + (i + 1));
            System.out.println("نوع الخطأ: " + errorType);
            System.out.println("التفاصيل: " + details);
            System.out.println("-".repeat(50));

            // طباعة جدول الرموز الخاص بالخطأ
            if (i < errorSymbolTables.size()) {
                System.out.println("جدول الرموز الخاص بهذا الخطأ:");
                errorSymbolTables.get(i).printTable();
            }
        }
        System.out.println("\n" + "=".repeat(50));
    }

    /**
     * التحقق من المتغيرات غير المعرفة
     */
    private void checkUndefinedVariables() {
        System.out.println("\nفحص المتغيرات غير المعرفة...");
        SymbolTable errorTable = new SymbolTable();
        boolean hasError = false;

        for (var row : symbolTable.getRows()) {
            if (row.type.equals("VariableName")) {
                System.out.println("فحص المتغير: " + row.value);
                if (!isVariableDefined(row.value)) {
                    errors.add("خطأ: متغير غير معرف: '" + row.value + "'");
                    errorTable.addRow(row);
                    hasError = true;
                }
            }
        }

        if (hasError) {
            errorSymbolTables.add(errorTable);
        }
    }

    /**
     * التحقق من أنواع المتغيرات
     * يتحقق من توافق أنواع المتغيرات مع القيم المخصصة لها
     */
    private void checkVariableTypes() {
        System.out.println("\nفحص أنواع المتغيرات...");
        SymbolTable errorTable = new SymbolTable();
        boolean hasError = false;

        for (var row : symbolTable.getRows()) {
            if (row.type.equals("VariableName")) {
                System.out.println("فحص نوع المتغير: " + row.value);
                ValueType value = getVariableValue(row.value);
                if (value != null && !isTypeCompatible(value)) {
                    errors.add("خطأ: عدم توافق الأنواع: نوع المتغير '" + row.value + "' غير متوافق");
                    errorTable.addRow(row);
                    hasError = true;
                }
            }
        }

        if (hasError) {
            errorSymbolTables.add(errorTable);
        }
    }

    /**
     * التحقق من التعريفات المكررة
     * يبحث عن المتغيرات التي تم تعريفها أكثر من مرة
     */
    private void checkDuplicateDeclarations() {
        System.out.println("\nفحص التعريفات المكررة...");
        SymbolTable errorTable = new SymbolTable();
        List<String> declaredVars = new ArrayList<>();
        boolean hasError = false;

        for (var row : symbolTable.getRows()) {
            if (row.type.equals("VariableName")) {
                System.out.println("فحص تعريف المتغير: " + row.value);
                if (declaredVars.contains(row.value)) {
                    errors.add("خطأ: تعريف مكرر: المتغير '" + row.value + "' معرف أكثر من مرة");
                    errorTable.addRow(row);
                    hasError = true;
                } else {
                    declaredVars.add(row.value);
                }
            }
        }

        if (hasError) {
            errorSymbolTables.add(errorTable);
        }
    }

    /**
     * التحقق من صحة التعابير
     */
    private void checkExpressions() {
        System.out.println("\nفحص التعابير...");
        SymbolTable errorTable = new SymbolTable();
        boolean hasError = false;

        for (var row : symbolTable.getRows()) {
            if (row.type.equals("Expression")) {
                System.out.println("فحص التعبير: " + row.value);
                if (!validateExpression(row.value)) {
                    errors.add("خطأ: تعبير غير صحيح: '" + row.value + "'");
                    errorTable.addRow(row);
                    hasError = true;
                }
            }
        }

        if (hasError) {
            errorSymbolTables.add(errorTable);
        }
    }

    /**
     * التحقق من صحة الدوال
     */
    private void checkFunctions() {
        System.out.println("\nفحص الدوال...");
        SymbolTable errorTable = new SymbolTable();
        boolean hasError = false;

        for (var row : symbolTable.getRows()) {
            if (row.type.equals("FunctionCall")) {
                System.out.println("فحص استدعاء الدالة: " + row.value);
                String[] parts = row.value.split("\\(");
                if (parts.length != 2) {
                    errors.add("خطأ: استدعاء دالة غير صحيح: '" + row.value + "'");
                    errorTable.addRow(row);
                    hasError = true;
                    continue;
                }

                String functionName = parts[0];
                String params = parts[1].replace(")", "");
                List<String> parameters = new ArrayList<>();
                if (!params.isEmpty()) {
                    parameters = List.of(params.split(","));
                }

                if (!validateFunctionCall(functionName, parameters)) {
                    errors.add("خطأ: استدعاء دالة غير صحيح: '" + row.value + "'");
                    errorTable.addRow(row);
                    hasError = true;
                }
            }
        }

        if (hasError) {
            errorSymbolTables.add(errorTable);
        }
    }

    /**
     * التحقق من صحة الكومبوننت
     * يتحقق من وجود الكومبوننت وتوافق خصائصه
     */
    private void checkComponents() {
        System.out.println("\nفحص الكومبوننت...");
        SymbolTable errorTable = new SymbolTable();
        boolean hasError = false;
        boolean foundComponents = false;

        // التحقق من وجود أي كومبوننت في الكود
        for (var row : symbolTable.getRows()) {
            if (row.type.equals("ComponentDefinition")) {
                foundComponents = true;
                break;
            }
        }

        if (!foundComponents) {
            errors.add("خطأ: لم يتم العثور على أي كومبوننت في الكود");
            hasError = true;
            errorSymbolTables.add(errorTable);
            return; // نخرج من الدالة إذا لم نجد أي كومبوننت
        }

        // إذا وجدنا كومبوننت، نتحقق من استدعاءاتها
        for (var row : symbolTable.getRows()) {
            if (row.type.equals("ComponentCall")) {
                System.out.println("فحص استدعاء الكومبوننت: " + row.value);
                String[] parts = row.value.split("\\(");
                if (parts.length != 2) {
                    errors.add("خطأ: استدعاء كومبوننت غير صحيح: '" + row.value + "'");
                    errorTable.addRow(row);
                    hasError = true;
                    continue;
                }

                String componentName = parts[0];
                String params = parts[1].replace(")", "");
                List<String> parameters = new ArrayList<>();
                if (!params.isEmpty()) {
                    parameters = List.of(params.split(","));
                }

                if (!validateComponentCall(componentName, parameters)) {
                    errors.add("خطأ: استدعاء كومبوننت غير صحيح: '" + row.value + "'");
                    errorTable.addRow(row);
                    hasError = true;
                }
            }
        }

        if (hasError) {
            errorSymbolTables.add(errorTable);
        }
    }

    /**
     * التحقق من صحة الـ imports
     * يتحقق من وجود الملفات المستوردة وصحة مساراتها
     */
    private void checkImports() {
        System.out.println("\nفحص الـ imports...");
        SymbolTable errorTable = new SymbolTable();
        boolean hasError = false;

        // طباعة محتويات جدول الرموز للتصحيح
        System.out.println("\nمحتويات جدول الرموز:");
        for (var row : symbolTable.getRows()) {
            System.out.println("نوع: " + row.type + ", قيمة: " + row.value);
        }

        // قائمة الـ imports المطلوبة مع صيغها الصحيحة
        Map<String, String> requiredImports = new HashMap<>();
        requiredImports.put("Component", "import { Component } from '@angular/core';");
        requiredImports.put("NgModule", "import { NgModule } from '@angular/core';");
        requiredImports.put("BrowserModule", "import { BrowserModule } from '@angular/platform-browser';");
        requiredImports.put("FormsModule", "import { FormsModule } from '@angular/forms';");
        requiredImports.put("HttpClientModule", "import { HttpClientModule } from '@angular/common/http';");
        requiredImports.put("RouterModule", "import { RouterModule } from '@angular/router';");

        // جمع الـ imports الموجودة في الكود
        List<String> foundImports = new ArrayList<>();
        for (var row : symbolTable.getRows()) {
            if (row.type.equals("Import") || row.type.equals("Imported")) {
                String importText = row.value.trim();
                if (importText.startsWith("import")) {
                    foundImports.add(importText);
                    System.out.println("تم العثور على import: " + importText);
                }
            }
        }

        // التحقق من وجود أي imports
        if (foundImports.isEmpty()) {
            System.out.println("لم يتم العثور على أي imports في الكود");
            errors.add("خطأ: لا توجد imports في الكود");
            hasError = true;
        } else {
            // التحقق من الـ imports المطلوبة
            for (Map.Entry<String, String> required : requiredImports.entrySet()) {
                boolean found = false;
                for (String importText : foundImports) {
                    // التحقق من صيغة الـ import
                    if (isValidImportFormat(importText, required.getKey(), required.getValue())) {
                        found = true;
                        System.out.println("تم العثور على الـ import المطلوب: " + required.getKey());
                        break;
                    }
                }
                if (!found) {
                    errors.add("خطأ: import مفقود أو غير صحيح: '" + required.getValue() + "'");
                    hasError = true;
                }
            }

            // التحقق من صحة الـ imports الموجودة
            for (String importText : foundImports) {
                // التحقق من صيغة الـ import
                if (!isValidImportFormat(importText)) {
                    errors.add("خطأ: صيغة import غير صحيحة: '" + importText + "'");
                    hasError = true;
                    continue;
                }

                // التحقق من تكرار الـ import
                int count = 0;
                for (String otherImport : foundImports) {
                    if (otherImport.equals(importText)) {
                        count++;
                    }
                }
                if (count > 1) {
                    errors.add("خطأ: import مكرر: '" + importText + "'");
                    hasError = true;
                }
            }
        }

        // طباعة ملخص الـ imports
        System.out.println("\nملخص الـ imports:");
        System.out.println("الـ imports المطلوبة: " + requiredImports.size());
        System.out.println("الـ imports الموجودة: " + foundImports.size());
        System.out.println("الـ imports المفقودة: " + (requiredImports.size() - foundImports.size()));

        if (hasError) {
            errorSymbolTables.add(errorTable);
        }
    }

    /**
     * التحقق من صحة صيغة الـ import
     *
     * @param importText نص الـ import
     * @return true إذا كانت الصيغة صحيحة
     */
    private boolean isValidImportFormat(String importText) {
        // التحقق من الصيغة الأساسية
        if (!importText.startsWith("import") || !importText.endsWith(";")) {
            return false;
        }

        // التحقق من وجود الأقواس المتوازنة
        int curlyBrackets = 0;
        for (char c : importText.toCharArray()) {
            if (c == '{') curlyBrackets++;
            if (c == '}') curlyBrackets--;
            if (curlyBrackets < 0) return false;
        }
        if (curlyBrackets != 0) return false;

        // التحقق من وجود from
        if (!importText.contains("from")) {
            return false;
        }

        // التحقق من المسار
        String[] parts = importText.split("from");
        if (parts.length != 2) return false;

        String path = parts[1].trim();
        if (!path.startsWith("'") || !path.endsWith("';")) {
            return false;
        }

        return true;
    }

    /**
     * التحقق من صحة صيغة الـ import مع نوع محدد
     *
     * @param importText     نص الـ import
     * @param type           نوع الـ import
     * @param expectedFormat الصيغة المتوقعة
     * @return true إذا كانت الصيغة صحيحة
     */
    private boolean isValidImportFormat(String importText, String type, String expectedFormat) {
        if (!isValidImportFormat(importText)) {
            return false;
        }

        // التحقق من وجود النوع المطلوب
        if (!importText.contains(type)) {
            return false;
        }

        // التحقق من المسار الصحيح
        String expectedPath = expectedFormat.split("from")[1].trim();
        String actualPath = importText.split("from")[1].trim();

        return expectedPath.equals(actualPath);
    }

    /**
     * التحقق من تعريف المتغير
     *
     * @param varName اسم المتغير
     * @return true إذا كان المتغير معرفاً
     */
    private boolean isVariableDefined(String varName) {
        for (var row : symbolTable.getRows()) {
            if (row.type.equals("VariableDeclaration")) {
                String[] parts = row.value.split("=");
                if (parts.length == 2) {
                    String declaration = parts[0].trim();
                    // التحقق من أن اسم المتغير موجود في التعريف
                    if (declaration.endsWith(varName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * الحصول على قيمة المتغير
     *
     * @param varName اسم المتغير
     * @return نوع قيمة المتغير
     */
    private ValueType getVariableValue(String varName) {
        for (var row : symbolTable.getRows()) {
            if (row.type.equals("VariableDeclaration") && row.value.contains(varName)) {
                String[] parts = row.value.split("=");
                if (parts.length == 2) {
                    String value = parts[1].trim();
                    try {
                        if (value.matches("\\d+")) {
                            return new IntegerValue(Integer.parseInt(value));
                        }
                        if (value.matches("\\d+\\.\\d+")) {
                            return new FloatValue(Float.parseFloat(value));
                        }
                        if (value.startsWith("\"") && value.endsWith("\"")) {
                            return new StringValue(value.substring(1, value.length() - 1));
                        }
                        if (value.equals("true") || value.equals("false")) {
                            return new BooleanValue(Boolean.parseBoolean(value));
                        }
                    } catch (NumberFormatException e) {
                        // في حالة فشل التحويل
                        return null;
                    }
                }
            }
        }
        return null;
    }

    /**
     * التحقق من توافق نوع القيمة
     *
     * @param value نوع القيمة المراد التحقق منها
     * @return true إذا كان النوع متوافقاً
     */
    private boolean isTypeCompatible(ValueType value) {
        if (value == null) return false;

        // التحقق من توافق الأنواع
        for (var row : symbolTable.getRows()) {
            if (row.type.equals("VariableDeclaration")) {
                String[] parts = row.value.split("=");
                if (parts.length == 2) {
                    String type = parts[0].trim();

                    if (type.contains("int") && !(value instanceof IntegerValue)) return false;
                    if (type.contains("float") && !(value instanceof FloatValue)) return false;
                    if (type.contains("string") && !(value instanceof StringValue)) return false;
                    if (type.contains("bool") && !(value instanceof BooleanValue)) return false;
                }
            }
        }
        return true;
    }

    /**
     * التحقق من صحة التعبير
     *
     * @param expression التعبير المراد فحصه
     * @return true إذا كان التعبير صحيحاً
     */
    private boolean validateExpression(String expression) {
        // التحقق من صحة التعبير
        if (expression == null || expression.trim().isEmpty()) {
            return false;
        }

        // التحقق من الأقواس المتوازنة
        int brackets = 0;
        for (char c : expression.toCharArray()) {
            if (c == '(') brackets++;
            if (c == ')') brackets--;
            if (brackets < 0) return false;
        }
        if (brackets != 0) return false;

        // التحقق من العمليات الحسابية
        String[] operators = {"+", "-", "*", "/", "%", "=", "==", "!=", "<", ">", "<=", ">="};
        boolean hasOperator = false;
        for (String op : operators) {
            if (expression.contains(op)) {
                hasOperator = true;
                break;
            }
        }

        return hasOperator;
    }

    /**
     * التحقق من صحة استدعاء دالة
     *
     * @param functionName اسم الدالة
     * @param parameters   المعاملات
     * @return true إذا كان الاستدعاء صحيحاً
     */
    private boolean validateFunctionCall(String functionName, List<String> parameters) {
        // التحقق من وجود الدالة
        boolean functionExists = false;
        for (var row : symbolTable.getRows()) {
            if (row.type.equals("FunctionDeclaration") && row.value.startsWith(functionName)) {
                functionExists = true;
                break;
            }
        }

        if (!functionExists) {
            errors.add("خطأ: دالة غير معرفة: '" + functionName + "'");
            return false;
        }

        // التحقق من عدد المعاملات
        for (var row : symbolTable.getRows()) {
            if (row.type.equals("FunctionDeclaration") && row.value.startsWith(functionName)) {
                String[] parts = row.value.split("\\(");
                if (parts.length == 2) {
                    String params = parts[1].replace(")", "");
                    int expectedParams = params.isEmpty() ? 0 : params.split(",").length;
                    if (parameters.size() != expectedParams) {
                        errors.add("خطأ: عدد المعاملات غير صحيح في استدعاء الدالة '" + functionName + "'");
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * التحقق من صحة استدعاء كومبوننت
     *
     * @param componentName اسم الكومبوننت
     * @param parameters   المعاملات
     * @return true إذا كان الاستدعاء صحيحاً
     */
    private boolean validateComponentCall(String componentName, List<String> parameters) {
        // التحقق من وجود الكومبوننت
        boolean componentExists = false;
        for (var row : symbolTable.getRows()) {
            if (row.type.equals("ComponentDefinition") && row.value.startsWith(componentName)) {
                componentExists = true;
                break;
            }
        }

        if (!componentExists) {
            errors.add("خطأ: كومبوننت غير معرف: '" + componentName + "'");
            return false;
        }

        // التحقق من عدد المعاملات
        for (var row : symbolTable.getRows()) {
            if (row.type.equals("ComponentDefinition") && row.value.startsWith(componentName)) {
                String[] parts = row.value.split("\\(");
                if (parts.length == 2) {
                    String params = parts[1].replace(")", "");
                    int expectedParams = params.isEmpty() ? 0 : params.split(",").length;
                    if (parameters.size() != expectedParams) {
                        errors.add("خطأ: عدد المعاملات غير صحيح في استدعاء الكومبوننت '" + componentName + "'");
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * طباعة تقرير مفصل عن الفحص الدلالي
     *
     * @param program البرنامج المراد فحصه
     */
    public void printDetailedReport(Program program) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("تقرير الفحص الدلالي المفصل");
        System.out.println("=".repeat(60));

        // طباعة معلومات البرنامج
        System.out.println("\nمعلومات البرنامج:");
        System.out.println("-".repeat(40));
        System.out.println("عدد المتغيرات: " + countVariables());
        System.out.println("عدد الدوال: " + countFunctions());
        System.out.println("عدد الحلقات: " + countLoops());

        // طباعة الأخطاء
        if (errors.isEmpty()) {
            System.out.println("\n✅ لا توجد أخطاء دلالية في الكود");
        } else {
            System.out.println("\n❌ تم اكتشاف " + errors.size() + " أخطاء:");
            for (int i = 0; i < errors.size(); i++) {
                System.out.println("\n" + "-".repeat(40));
                System.out.println("الخطأ رقم " + (i + 1) + ":");
                System.out.println(errors.get(i));
            }
        }

        // طباعة ملخص
        System.out.println("\n" + "=".repeat(60));
        System.out.println("ملخص الفحص:");
        System.out.println("-".repeat(40));
        System.out.println("حالة الفحص: " + (errors.isEmpty() ? "✅ ناجح" : "❌ فشل"));
        System.out.println("عدد الأخطاء: " + errors.size());
        System.out.println("=".repeat(60));
    }

    /**
     * طباعة تقرير عن المتغيرات
     */
    public void printVariablesReport() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("تقرير المتغيرات");
        System.out.println("=".repeat(60));

        Map<String, List<String>> variablesByType = new HashMap<>();

        // تجميع المتغيرات حسب النوع
        for (var row : symbolTable.getRows()) {
            if (row.type.equals("VariableDeclaration")) {
                String type = getVariableType(row.value);
                variablesByType.computeIfAbsent(type, k -> new ArrayList<>()).add(row.value);
            }
        }

        // طباعة المتغيرات مصنفة حسب النوع
        for (Map.Entry<String, List<String>> entry : variablesByType.entrySet()) {
            System.out.println("\nنوع المتغيرات: " + entry.getKey());
            System.out.println("-".repeat(40));
            for (String var : entry.getValue()) {
                System.out.println("• " + var);
            }
        }

        System.out.println("\n" + "=".repeat(60));
    }

    /**
     * طباعة تقرير عن الدوال
     */
    public void printFunctionsReport() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("تقرير الدوال");
        System.out.println("=".repeat(60));

        for (var row : symbolTable.getRows()) {
            if (row.type.equals("FunctionDeclaration")) {
                System.out.println("\n" + "-".repeat(40));
                System.out.println("اسم الدالة: " + row.value);
                System.out.println("نوع الإرجاع: " + getFunctionReturnType(row.value));
                System.out.println("المعاملات: " + getFunctionParameters(row.value));
            }
        }

        System.out.println("\n" + "=".repeat(60));
    }

    /**
     * طباعة تقرير عن الأخطاء بتنسيق JSON
     */
    public void printErrorsAsJSON() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("تقرير الأخطاء (JSON)");
        System.out.println("=".repeat(60));

        System.out.println("{");
        System.out.println("  \"totalErrors\": " + errors.size() + ",");
        System.out.println("  \"errors\": [");

        for (int i = 0; i < errors.size(); i++) {
            String error = errors.get(i);
            System.out.println("    {");
            System.out.println("      \"id\": " + (i + 1) + ",");
            System.out.println("      \"message\": \"" + error + "\",");
            System.out.println("      \"type\": \"" + getErrorType(error) + "\"");
            System.out.print("    }" + (i < errors.size() - 1 ? "," : ""));
            System.out.println();
        }

        System.out.println("  ]");
        System.out.println("}");
        System.out.println("=".repeat(60));
    }

    // دوال مساعدة
    private int countVariables() {
        return (int) symbolTable.getRows().stream()
                .filter(row -> row.type.equals("VariableDeclaration"))
                .count();
    }

    private int countFunctions() {
        return (int) symbolTable.getRows().stream()
                .filter(row -> row.type.equals("FunctionDeclaration"))
                .count();
    }

    private int countLoops() {
        return (int) symbolTable.getRows().stream()
                .filter(row -> row.type.equals("Loop"))
                .count();
    }

    private String getFunctionReturnType(String functionName) {
        // مؤقتاً
        return "void";
    }

    private String getFunctionParameters(String functionName) {
        // مؤقتاً
        return "[]";
    }

    private String getErrorType(String error) {
        if (error.contains("غير معرف")) return "undefined_variable";
        if (error.contains("مكرر")) return "duplicate_declaration";
        if (error.contains("نوع")) return "type_mismatch";
        return "unknown";
    }

    /**
     * الحصول على نوع المتغير
     *
     * @param variable اسم المتغير
     * @return نوع المتغير
     */
    private String getVariableType(String variable) {
        for (var row : symbolTable.getRows()) {
            if (row.type.equals("VariableDeclaration") && row.value.contains(variable)) {
                String[] parts = row.value.split("\\s+");
                if (parts.length > 0) {
                    return parts[0].trim();
                }
            }
        }
        return "unknown";
    }

    /**
     * التحقق من وجود الكومبوننت
     *
     * @param componentName اسم الكومبوننت
     * @return true إذا كان الكومبوننت موجوداً
     */
    private boolean isComponentDefined(String componentName) {
        System.out.println("البحث عن تعريف للكومبوننت: " + componentName);
        for (var row : symbolTable.getRows()) {
            if (row.type.equals("ComponentDefinition")) {
                System.out.println("تم العثور على تعريف كومبوننت: " + row.value);
                if (row.value.contains(componentName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * التحقق من صحة خصائص الكومبوننت
     *
     * @param componentText نص تعريف الكومبوننت
     * @return true إذا كانت الخصائص صحيحة
     */
    private boolean validateComponentProperties(String componentText) {
        // التحقق من وجود الأقواس المتوازنة
        int brackets = 0;
        for (char c : componentText.toCharArray()) {
            if (c == '{') brackets++;
            if (c == '}') brackets--;
            if (brackets < 0) return false;
        }
        if (brackets != 0) return false;

        // التحقق من صحة الخصائص
        String[] properties = componentText.split("\\s+");
        for (String prop : properties) {
            if (prop.contains("=")) {
                String[] keyValue = prop.split("=");
                if (keyValue.length != 2) return false;
                if (keyValue[1].isEmpty()) return false;
            }
        }

        return true;
    }

    /**
     * التحقق من وجود الملف
     *
     * @param filePath مسار الملف
     * @return true إذا كان الملف موجوداً
     */
    private boolean isFileExists(String filePath) {
        // هنا يمكن إضافة منطق للتحقق من وجود الملف
        // مؤقتاً نرجع true
        return true;
    }

    /**
     * التحقق من تكرار الـ import
     *
     * @param filePath مسار الملف
     * @return true إذا كان الـ import مكرراً
     */
    private boolean isDuplicateImport(String filePath) {
        int count = 0;
        for (var row : symbolTable.getRows()) {
            if (row.type.equals("Import") && row.value.contains(filePath)) {
                count++;
                if (count > 1) return true;
            }
        }
        return false;
    }

    /**
     * التحقق من صحة اسم الكومبوننت
     *
     * @param componentName اسم الكومبوننت
     * @return true إذا كان الاسم صالحاً
     */
    private boolean isValidComponentName(String componentName) {
        // يجب أن يبدأ بحرف كبير
        if (!Character.isUpperCase(componentName.charAt(0))) {
            return false;
        }

        // يجب أن يحتوي على أحرف وأرقام فقط
        return componentName.matches("^[A-Za-z0-9]+$");
    }

    /**
     * التحقق من وجود الخصائص الإلزامية
     *
     * @param componentText نص تعريف الكومبوننت
     * @return true إذا كانت الخصائص الإلزامية موجودة
     */
    private boolean hasRequiredProperties(String componentText) {
        // قائمة الخصائص الإلزامية
        String[] requiredProps = {"id", "name"};

        for (String prop : requiredProps) {
            if (!componentText.contains(prop + "=")) {
                return false;
            }
        }
        return true;
    }

    /**
     * التحقق من صحة مسار الملف
     *
     * @param filePath مسار الملف
     * @return true إذا كان المسار صالحاً
     */
    private boolean isValidFilePath(String filePath) {
        // التحقق من عدم وجود أحرف غير مسموح بها
        if (filePath.contains("..") || filePath.contains("//")) {
            return false;
        }

        // التحقق من أن المسار يبدأ بـ ./
        return filePath.startsWith("./");
    }


} 