package Classes.Visitor;

import Angular.AngularParser;
import Angular.AngularParserBaseVisitor;
import Classes.GenericStatements.IfStatements.LogicalStatement;
import Classes.SymbolTable.Row;
import Classes.SymbolTable.SymbolTable;
import Classes.Values.Htmls.Tags.Attributes.Attribute;
import Classes.Values.Htmls.Tags.Attributes.NgFor;
import Classes.Values.Htmls.Tags.Attributes.NgIf;
import Classes.Values.Htmls.Tags.Attributes.QuotedAttribute;

public class AttributeVisitor extends AngularParserBaseVisitor<Attribute> {

    public SymbolTable symbolTable = new SymbolTable();
    public Attribute visitAttribute(AngularParser.AttributeContext ctx){
        if(ctx instanceof AngularParser.NgForContext){
            return this.visitNgFor((AngularParser.NgForContext) ctx);
        }else if(ctx instanceof AngularParser.NgIfContext){
            return this.visitNgIf((AngularParser.NgIfContext) ctx);
        }else if(ctx instanceof AngularParser.DoubleQuotedAttributeContext){
            return this.visitDoubleQuotedAttribute((AngularParser.DoubleQuotedAttributeContext) ctx);
        }else if(ctx instanceof AngularParser.OpenBracketAttributeContext){
            return this.visitOpenBracketAttribute((AngularParser.OpenBracketAttributeContext) ctx);
        }
        return this.visitOpenParenAttribute((AngularParser.OpenParenAttributeContext) ctx);
    }

    @Override
    public NgFor visitNgFor(AngularParser.NgForContext ctx) {
        Row row = new Row();
        row.type = "AngularAttribute";
        row.value = "NgFor";
        this.symbolTable.addRow(row);
        return this.visitNgForStatement(ctx.ngForStatement());
    }

    @Override
    public QuotedAttribute visitDoubleQuotedAttribute(AngularParser.DoubleQuotedAttributeContext ctx) {
        QuotedAttribute quotedAttribute = new QuotedAttribute();
        if(ctx.ID() != null){
            quotedAttribute.attributeName = ctx.ID().getText();
        }else {
            quotedAttribute.attributeName = ctx.Class().getText();
        }
        quotedAttribute.attributeValue = ctx.DoubleQuote().getText();
        return quotedAttribute;
    }

    @Override
    public QuotedAttribute visitOpenBracketAttribute(AngularParser.OpenBracketAttributeContext ctx) {
        QuotedAttribute quotedAttribute = new QuotedAttribute();
        if(ctx.ID() != null){
            quotedAttribute.attributeName = ctx.ID().getText();
        }else {
            quotedAttribute.attributeName = ctx.Class().getText();
        }
        quotedAttribute.attributeValue = ctx.DoubleQuote().getText();
        Row row = new Row();
        row.type = "AttributeType: " + quotedAttribute.attributeName;
        row.value = quotedAttribute.attributeValue;
        this.symbolTable.addRow(row);
        return quotedAttribute;
    }

    @Override
    public QuotedAttribute visitOpenParenAttribute(AngularParser.OpenParenAttributeContext ctx) {
        QuotedAttribute quotedAttribute = new QuotedAttribute();
        if(ctx.ID() != null){
            quotedAttribute.attributeName = ctx.ID().getText();
        }else {
            quotedAttribute.attributeName = ctx.Class().getText();
        }
        quotedAttribute.attributeValue = ctx.DoubleQuote().getText();
        Row row = new Row();
        row.type = "Event: " + quotedAttribute.attributeName;
        row.value = quotedAttribute.attributeValue;
        this.symbolTable.addRow(row);
        return quotedAttribute;
    }

    @Override
    public NgIf visitNgIf(AngularParser.NgIfContext ctx) {
        Row row = new Row();
        row.type = "AngularAttribute";
        row.value = "NgIf";
        this.symbolTable.addRow(row);
        return this.visitNgIfStatement(ctx.ngIfStatement());
    }

    @Override
    public NgFor visitNgForStatement(AngularParser.NgForStatementContext ctx) {
        NgFor ngFor = new NgFor();
        ngFor.singular = ctx.ID(0).getText();
        ngFor.plural = ctx.ID(1).getText();
        return ngFor;
    }

    @Override
    public NgIf visitNgIfStatement(AngularParser.NgIfStatementContext ctx) {
        NgIf ngIf = new NgIf();
        if(ctx.ID() != null){
            ngIf.conditionalVariable = ctx.ID().getText();
        }else{
            LogicalStatementVisitor logicalStatementVisitor=new LogicalStatementVisitor();
            ngIf.logicalStatement = logicalStatementVisitor.visitLogicalStatement(ctx.logicalStatement());
        }
        return ngIf;
    }
}
