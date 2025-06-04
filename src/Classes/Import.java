package Classes;

public class Import extends Expression {
    public String type;
    public String fullImport;

    public Import() {
    }

    public Import(String importText) {
        this.fullImport = importText;
        // استخراج نوع الـ import من النص الكامل
        if (importText.contains("Component")) {
            this.type = "Component";
        } else if (importText.contains("NgModule")) {
            this.type = "NgModule";
        } else if (importText.contains("BrowserModule")) {
            this.type = "BrowserModule";
        } else if (importText.contains("FormsModule")) {
            this.type = "FormsModule";
        } else if (importText.contains("HttpClientModule")) {
            this.type = "HttpClientModule";
        } else if (importText.contains("RouterModule")) {
            this.type = "RouterModule";
        } else {
            this.type = "Unknown";
        }
    }

    @Override
    public String toString() {
        return "Import: " + this.fullImport;
    }
}
