package Classes;
import java.util.ArrayList;
import java.util.List;

public class ExpressionProcessor {
    List<Expression> list;
    private LabelManager labelManager;
    
    public ExpressionProcessor(List<Expression> list){
        this.list = list;
        this.labelManager = LabelManager.getInstance();
    }

    public List<String> getEvaluationResults(){
        List<String> evaluations = new ArrayList<>();
        evaluations.add("\nشجرة التحليل المجردة (AST):");
        evaluations.add("---------------------------------------------------------------");
        
        for(Expression e : list){
            String label = labelManager.createLabel(e.getClass().getSimpleName(), e.toString());
            String ast = formatAST(e.toString(), 0, label);
            evaluations.add(ast);
        }
        
        evaluations.add("---------------------------------------------------------------");
        evaluations.addAll(labelManager.getLabelsReport());
        
        return evaluations;
    }

    private String formatAST(String ast, int level, String label) {
        StringBuilder formatted = new StringBuilder();
        String indent = "  ".repeat(level);
        
        String[] parts = ast.split("\\{");
        if (parts.length > 1) {
            formatted.append(indent).append("└─ ").append(parts[0].trim())
                   .append(" [").append(label).append("]").append("\n");
            
            String content = parts[1].replace("}", "").trim();
            String[] lines = content.split(",");
            for (String line : lines) {
                line = line.trim();
                if (!line.isEmpty()) {
                    formatted.append(indent).append("   ├─ ").append(line).append("\n");
                }
            }
        } else {
            formatted.append(indent).append("└─ ").append(ast.trim())
                   .append(" [").append(label).append("]").append("\n");
        }
        
        return formatted.toString();
    }

    private Expression getEvaResult(Expression e){
        return e;
    }
}
