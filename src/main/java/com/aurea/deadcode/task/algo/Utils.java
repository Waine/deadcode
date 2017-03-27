package com.aurea.deadcode.task.algo;

import com.scitools.understand.Reference;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by ekonovalov on 17.03.2017.
 */
public class Utils {

    public static Set<Reference> unique(Reference[] refs) {
        Set<String> unique = new HashSet<>();
        Set<Reference> result = new LinkedHashSet<>();
        for (Reference r : refs) {
            int size = unique.size();
            unique.add(r.ent().name() + r.line() + r.column());
            if (unique.size() > size) {
                result.add(r);
            }
        }

        return result;
    }

}
