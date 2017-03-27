package com.aurea.deadcode.model.scitools;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by ekonovalov on 17.03.2017.
 */
public class Entity {

    private com.scitools.understand.Entity e;

    private int id;
    private String name;
    private String simplename;
    private String longname;
    private Set<Reference> refs;
    private String kind;
    private String type;

    public Entity(com.scitools.understand.Entity e) {
        this(e, false);
    }

    public Entity(com.scitools.understand.Entity e, boolean lazy) {
        this.e = e;
        this.id = e.id();
        this.name = e.name();
        this.simplename = e.simplename();
        this.longname = e.longname(true);

        if (!lazy) {
            com.scitools.understand.Reference[] refs = e.refs(null, null, false);
            this.refs = new LinkedHashSet<>();
            for (com.scitools.understand.Reference r : refs) {
                this.refs.add(new Reference(r));
            }
        }

        this.kind = e.kind().name();
        this.type = e.type();
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String simplename() {
        return simplename;
    }

    public String longname() {
        return longname;
    }

    public Set<Reference> refs() {
        return refs;
    }

    public Set<Reference> refs(String refKind, String entKind, boolean unique) {
        com.scitools.understand.Reference[] refs = e.refs(refKind, entKind, unique);
        Set<Reference> result = new LinkedHashSet<>();
        for (com.scitools.understand.Reference r : refs) {
            result.add(new Reference(r));
        }

        return result;
    }

    public String kind() {
        return kind;
    }

    public String[] metrics() {
        return e.metrics();
    }

    public Number metric(String var1) {
        return e.metric(var1);
    }

    public Map<String, Number> metric(String[] var1) {
        return e.metric(var1);
    }

    public String[] ib(String var1) {
        return e.ib(var1);
    }

    public String type() {
        return type;
    }

    public String freetext(String var1) {
        return e.freetext(var1);
    }

    public com.scitools.understand.Entity e() {
        return this.e;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;

        if (id != entity.id) return false;
        if (name != null ? !name.equals(entity.name) : entity.name != null) return false;
        if (simplename != null ? !simplename.equals(entity.simplename) : entity.simplename != null) return false;
        if (kind != null ? !kind.equals(entity.kind) : entity.kind != null) return false;
        return (type != null ? !type.equals(entity.type) : entity.type != null);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

}
