package com.aurea.deadcode.task;

import com.aurea.deadcode.task.algo.DeadCode;
import com.scitools.understand.Database;
import com.scitools.understand.Entity;
import com.scitools.understand.Understand;
import com.scitools.understand.UnderstandException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * Created by ekonovalov on 17.03.2017.
 */
public class ScitoolsTest {

    @Test
    public void test() {
        Database db;
        try {
            db = Understand.open(new File("./src/test/resources/1.udb").getAbsolutePath());
        } catch (UnderstandException e) {
            return;
        }

        try {
            new DeadCode(db).find(new FindListener() {
                @Override
                public void acceptBatch(List<Entity> batch) {
                    for (Entity e : batch) {
                        System.out.println(e.name());
                    }
                    Assert.assertEquals(10, batch.size());
                }
            });
        } finally {
            db.close();
        }

    }

}
