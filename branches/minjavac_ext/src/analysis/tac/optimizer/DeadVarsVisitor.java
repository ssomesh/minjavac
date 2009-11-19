package analysis.tac.optimizer;

import analysis.tac.*;
import analysis.tac.instructions.*;
import analysis.tac.variables.TAArrayCellVar;
import analysis.tac.variables.TALocalVar;
import analysis.tac.variables.TAVariable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeadVarsVisitor implements TABasicBlockVisitor {
  private Set<TALocalVar> deadVars;
  private TAInstruction instruction;

  public void visit(TABasicBlock block) {
    deadVars = new HashSet<TALocalVar>(20);
    deadVars.addAll(block.readVars());
    deadVars.addAll(block.writeVars());
    deadVars.removeAll(block.liveVars());

    List<TAInstruction> code = block.instructions();

    for (int i = code.size()-1; i >= 0; --i) {
      instruction = code.get(i);
      attachDeadVars(instruction);
      instruction.accept(this);
    }
  }

  public void visit(Copy copy) {
    visitWrite(copy.getDestiny());
    visitRead(copy.getSource());
  }

  public void visit(Jump jump) {
    if (jump.isConditionalJump()) {
      ConditionalJump j = (ConditionalJump)jump;
      visitRead(j.getA());
      if (j.getB() != null) visitRead(j.getB());
    }
  }

  public void visit(Operation op) {
    visitWrite(op.getDestiny());
    visitRead(op.getA());
    if (op.getB() != null) visitRead(op.getB());
  }

  public void visit(ParameterSetup param) {
    visitRead(param.getParameter());
  }

  public void visit(ProcedureCall proc) {
    visitWrite(proc.getDestiny());
  }

  public void visit(Return ret) {
    visitRead(ret.getReturnVariable());
  }

  public void visit(Action action) { }

  public void visit(Label label) { }

  private void visitRead(TAVariable v) {
    if (v instanceof TAArrayCellVar) {
      visitRead(((TAArrayCellVar)v).getIndexVar());
      visitRead(((TAArrayCellVar)v).getArrayVar());
    } else if (v instanceof TALocalVar) {
      deadVars.remove((TALocalVar)v);
    }
  }

  private void visitWrite(TAVariable v) {
    if (v instanceof TAArrayCellVar) {
      visitRead(((TAArrayCellVar)v).getIndexVar());
      visitRead(((TAArrayCellVar)v).getArrayVar());
    } else if (v instanceof TALocalVar) {
      deadVars.add((TALocalVar)v);
    }
  }

  private void attachDeadVars(TAInstruction i) {
    for (TALocalVar v : deadVars)
      i.addDeadVar(v);
  }
}
