package org.benf.cfr.reader.bytecode.analysis.parse.expression;

import org.benf.cfr.reader.bytecode.analysis.parse.Expression;
import org.benf.cfr.reader.bytecode.analysis.parse.LValue;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.LValueCollector;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.SSAIdentifiers;
import org.benf.cfr.reader.util.SetFactory;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: lee
 * Date: 16/03/2012
 * Time: 18:03
 * To change this template use File | Settings | File Templates.
 */
public class ComparisonOperation implements ConditionalExpression {
    private Expression lhs;
    private Expression rhs;
    private final CompOp op;

    public ComparisonOperation(Expression lhs, Expression rhs, CompOp op) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.op = op;
    }

    @Override
    public int getSize() {
        return 3;
    }

    private String brace(Expression e) {
        if (e instanceof ComparisonOperation) return "(" + e + ")";
        return e.toString();
    }

    @Override
    public String toString() {
        return brace(lhs) + " " + op.getShowAs() + " " + brace(rhs);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public Expression replaceSingleUsageLValues(LValueCollector lValueCollector, SSAIdentifiers ssaIdentifiers) {
        lhs = lhs.replaceSingleUsageLValues(lValueCollector, ssaIdentifiers);
        rhs = rhs.replaceSingleUsageLValues(lValueCollector, ssaIdentifiers);
        return this;
    }

    @Override
    public ConditionalExpression getNegated() {
        return new ComparisonOperation(lhs, rhs, op.getInverted());
    }

    @Override
    public ConditionalExpression getDemorganApplied(boolean amNegating) {
        if (!amNegating) return this;
        return getNegated();
    }

    private void addIfLValue(Expression expression, Set<LValue> res) {
        if (expression instanceof LValueExpression) {
            res.add(((LValueExpression) expression).getLValue());
        }
    }

    @Override
    public Set<LValue> getLoopLValues() {
        Set<LValue> res = SetFactory.newSet();
        addIfLValue(lhs, res);
        addIfLValue(rhs, res);
        return res;
    }
}
