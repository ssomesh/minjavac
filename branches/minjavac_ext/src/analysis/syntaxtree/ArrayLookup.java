package analysis.syntaxtree;import analysis.visitors.TypeVisitor;import analysis.visitors.Visitor;public class ArrayLookup extends Exp {  public Exp arrayExpr, indexExpr;  public ArrayLookup(Exp ae1, Exp ae2) {    arrayExpr = ae1;    indexExpr = ae2;  }  public void accept(Visitor v) {    v.visit(this);  }  public Type accept(TypeVisitor v) {    return v.visit(this);  }}