package analysis.syntaxtree;import analysis.visitors.Visitor;public class ArrayLookup implements Exp {  public Exp e1, e2;  public ArrayLookup(Exp ae1, Exp ae2) {    e1 = ae1;    e2 = ae2;  }  public void accept(Visitor v) {    v.visit(this);  }}