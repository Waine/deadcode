package com.aurea.deadcode.jexl;

import com.aurea.deadcode.model.Occurrence;
import org.apache.commons.jexl3.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ekonovalov on 14.03.2017.
 */
public class TestExpression {

    @Test
    public void test() {

        String expr = "kind=='kind1'&name=='name1'";

        JexlEngine jexl = new JexlBuilder().create();
        JexlExpression e = jexl.createExpression(expr);

        Occurrence o = new Occurrence();
        o.setName("name1");
        o.setKind("kind1");

        JexlContext jc = new MapContext();
        jc.set("kind", o.getKind());
        jc.set("name", o.getName());

        Object b = e.evaluate(jc);

        Assert.assertTrue((Boolean) b);
    }

    @Test
    public void test2() {
        String expr = "kind.contains('kind1') && name.contains('name1')";

        JexlEngine jexl = new JexlBuilder().create();
        JexlExpression e = jexl.createExpression(expr);

        Occurrence o = new Occurrence();
        o.setName("name1_");
        o.setKind("kind1_");

        JexlContext jc = new MapContext();
        jc.set("kind", o.getKind());
        jc.set("name", o.getName());

        Object b = e.evaluate(jc);

        Assert.assertTrue((Boolean) b);
    }

    @Test
    public void test3() {
        String expr = "!name.contains('serialVersionUID') && !(referenceKind=='Abstract Method')";

        JexlEngine jexl = new JexlBuilder().create();
        JexlExpression e = jexl.createExpression(expr);

        Occurrence o = new Occurrence();
        o.setName("name1_");
        o.setKind("kind1_");

        JexlContext jc = new MapContext();
        jc.set("kind", "Unknown Variable");
        jc.set("name", "ABSTRACT");
        jc.set("referenceKind", "Method");
        jc.set("referenceName", "io.reactivex.OperatorsAreFinal.check");

        Object b = e.evaluate(jc);

        Assert.assertTrue((Boolean) b);
    }

    @Test(expected = JexlException.class)
    public void testBadExpression() {
        String expr = "!name.contains('serialVersionUID') && !(referenceKind=='Abstract Method'))";

        JexlEngine jexlEngine = new JexlBuilder().create();
        jexlEngine.createExpression(expr);

    }

}
