package com.aurea.deadcode.task;

import com.aurea.deadcode.model.scitools.Entity;
import com.scitools.understand.Database;
import com.scitools.understand.Understand;
import com.scitools.understand.UnderstandException;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ekonovalov on 17.03.2017.
 */
public class ScitoolsInvestigateTest {

    String[] refTypes = {"Call", "Callby", "Cast", "Castby", "Contain", "Containin", "Couple", "Coupleby",
            "Create", "Createby", "Define", "Definein", "DotRef", "DotRefby", "End", "Endby", "Import", "Importby",
            "Modify", "Modifyby", "Override", "Overrideby", "Set", "Setby", "Typed", "Typedby", "Use", "Useby",
            "Throw", "Throwby"};

    @Test
    public void test() {
        Database db;
        try {
            //db = Understand.open(new File("./src/test/resources/1.udb").getAbsolutePath());
            //db = Understand.open(new File("/app/repo/1/db.udb").getAbsolutePath());
            db = Understand.open(new File("/app/repo/3/db.udb").getAbsolutePath());
        } catch (UnderstandException e) {
            return;
        }

        try {
            List<Entity> ents = new ArrayList<>();
/*
            for (com.scitools.understand.Entity ent : db.ents("Java Method Member Private ~Constructor ~unresolved ~unknown")) {
                Entity e = new Entity(ent);
                if(e.e().refs("Callby", null, false).length == 0 &&
                        !e.name().contains("(Anon_") && !e.name().contains("(lambda_expr_"))     {

                    ents.add(e);
                }
            }
*/
            for (com.scitools.understand.Entity ent : db.ents("Java Variable Private Member")) {
                Entity e = new Entity(ent);
                com.scitools.understand.Reference def = e.e().refs("Definein", null, true)[0];
                if (e.e().refs("Modifyby,Useby", null, true).length == 0 && !e.name().contains("serialVersionUID")) {
                    com.scitools.understand.Reference[] set = e.e().refs("Setby", null, true);
                    if (set.length == 0 || (set.length == 1 && set[0].equals(def))) {
                        printRefTypes(e);
                        ents.add(e);
                    }
                }

                /*
                if(e.e().refs("Setby,Modifyby", null, true).length == 0 &&
                        !e.name().contains("serialVersionUID")) {
                    printRefTypes(e);
                    ents.add(e);
                }
                */
            }
/*
            for (com.scitools.understand.Entity ent : db.ents("Java Class Default")) {
                Entity e = new Entity(ent);
                if(e.e().refs("Definein", "File", false).length > 0) {
                    if (e.e().refs("Createby", null, false).length == 0 && e.e().refs("Java Coupleby", "~unresolved ~unknown", false).length == 0) {
                        ents.add(e);
                    }
                }
            }
*/
            for (Entity e : ents) {
                System.out.println(e.name() + " " + e.kind() + " " + e.refs().iterator().next().line() + ":" + e.refs().iterator().next().column());
            }
        } finally {
            db.close();
        }

    }

    private void printRefTypes(Entity e) {
        System.out.println(e + " : total reference count = " + e.e().refs(null, null, false).length);
        for (String refType : refTypes) {
            com.scitools.understand.Reference[] refs = e.e().refs(refType, null, false);
            if (refs.length > 0) {
                for (com.scitools.understand.Reference r : refs) {
                    System.out.println(refType + " at " + r.line() + ":" + r.column());
                }
            }
        }
    }
}
