package com.aurea.deadcode.task.algo;

import com.aurea.deadcode.task.FindListener;
import com.scitools.understand.Database;
import com.scitools.understand.Entity;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ekonovalov on 17.03.2017.
 */
@Slf4j
public class DeadCode {

    private Database db;

    public DeadCode(Database db) {
        this.db = db;
    }

    public void find(FindListener l) {
        log.info("Start finding");

        int count = 0;

        List<Entity> batch = new ArrayList<>();
        for (Entity e : db.ents("Java Method Member Private ~Constructor ~unresolved ~unknown")) {
            if (e.refs("Callby", null, false).length == 0) {
                batch.add(e);
            }
            if (batch.size() >= 50) {
                l.acceptBatch(batch);
                batch.clear();
                log.info("Total processed = " + count);
            }
        }

        for (Entity e : db.ents("Java Variable Private Member")) {
            if (e.refs(null, null, true).length == 1) {
                batch.add(e);
            }
            if (batch.size() >= 50) {
                l.acceptBatch(batch);
                batch.clear();
                log.info("Total processed = " + count);
            }
        }

        for (Entity e : db.ents("Java Class Default")) {
            if (e.refs("Definein", "File", false).length > 0) {
                if (e.refs("Createby", null, false).length == 0 && e.refs("Java Coupleby", "~unresolved ~unknown", false).length == 0) {
                    batch.add(e);
                }
            }
            if (batch.size() >= 50) {
                l.acceptBatch(batch);
                batch.clear();
                log.info("Total processed = " + count);
            }
        }

        l.acceptBatch(batch);

        log.info("Finish finding");

    }
}
