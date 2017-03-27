package com.aurea.deadcode.task;

import com.aurea.deadcode.model.DeadCodeOccurrence;
import com.aurea.deadcode.model.GitHubRepository;
import com.scitools.understand.Entity;
import com.scitools.understand.Reference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ekonovalov on 17.03.2017.
 */
public class Converter {

    public static List<DeadCodeOccurrence> convert(List<Entity> entities, GitHubRepository repo, String repositoryPath) {
        String repoPath = new File(repositoryPath + "/" + repo.getId() + "/repository").getAbsolutePath();

        List<DeadCodeOccurrence> result = new ArrayList<>();
        for (Entity e : entities) {
            DeadCodeOccurrence o = new DeadCodeOccurrence();
            o.setName(e.name());
            o.setLongName(e.longname(true));
            o.setType(e.type());
            o.setKind(e.kind().name());
            Reference ref = e.refs(null, null, false)[0];
            o.setFile(ref.file().longname(true));
            o.setLine(ref.line());
            o.setColumn(ref.column());
            o.setRepository(repo);

            String f = new File(o.getFile()).getAbsolutePath().substring(repoPath.length());
            f = f.replaceAll("\\\\", "/");
            o.setFile(f);

            result.add(o);
        }

        return result;
    }

}
