import Angular.AngularLexer;
import Angular.AngularParser;
import Classes.ExpressionProcessor;
import Classes.Program;
import Classes.Visitor.AntlrToProgram;
import Classes.SemanticChecker;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import Classes.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        try {
            // تحليل الكود من ملف الاختبار
            AngularParser parser = getParser("tests/angular.txt");
            ParseTree antlrAST = parser.prog();

            // تحويل AST إلى برنامج
            AntlrToProgram progVisitor = new AntlrToProgram();
            Program prog = progVisitor.visit(antlrAST);

            // طباعة جدول الرموز الأساسي
            System.out.println("\nجدول الرموز الأساسي:");
            System.out.println("===============================================================");
            progVisitor.symbolTable.printTable();
            System.out.println("===============================================================");

            // التحقق الدلالي
            SemanticChecker semanticChecker = new SemanticChecker(progVisitor.symbolTable);
            semanticChecker.checkProgram(prog);

            // طباعة الأخطاء الدلالية
            System.out.println("\nالأخطاء الدلالية:");
            System.out.println("===============================================================");
            semanticChecker.printErrors();
            System.out.println("===============================================================");

            // استخراج المتغيرات التي تحتوي على أخطاء
            List<String> errorVariables = new ArrayList<>();
            for (String error : semanticChecker.getErrors()) {
                if (error.contains("'")) {
                    String varName = error.substring(error.indexOf("'") + 1, error.lastIndexOf("'"));
                    errorVariables.add(varName);
                }
            }

            // طباعة جدول الرموز بعد حذف المتغيرات التي تحتوي على أخطاء
            System.out.println("\nجدول الرموز (بعد حذف المتغيرات التي تحتوي على أخطاء):");
            System.out.println("===============================================================");
            progVisitor.symbolTable.printTableWithoutErrors(errorVariables);
            System.out.println("===============================================================");

            // معالجة التعبيرات وإنشاء الليبلات
            ExpressionProcessor processor = new ExpressionProcessor(prog.expressionList);
            List<String> results = processor.getEvaluationResults();

            // طباعة شجرة التحليل مع الليبلات
            System.out.println("\nشجرة التحليل مع الليبلات:");
            System.out.println("===============================================================");
            for (String result : results) {
                System.out.println(result);
            }

            // طباعة تقرير الليبلات
            System.out.println("\nتقرير الليبلات في المشروع:");
            System.out.println("===============================================================");
            for (String label : LabelManager.getInstance().getLabelsReport()) {
                System.out.println(label);
            }

        } catch (Exception e) {
            System.err.println("حدث خطأ أثناء تحليل الكود: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static AngularParser getParser(String fileName) {
        try {
            CharStream input = CharStreams.fromFileName(fileName);
            AngularLexer lexer = new AngularLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            return new AngularParser(tokens);
        } catch (IOException e) {
            throw new RuntimeException("خطأ في قراءة الملف: " + e.getMessage());
        }
    }
}
