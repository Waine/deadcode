package com.aurea.deadcode.model.scitools;

/**
 * Created by ekonovalov on 17.03.2017.
 */
public class Reference {

    private com.scitools.understand.Reference r;

    private Entity ent;
    private Entity file;

    private int line;
    private int column;

    public Reference(com.scitools.understand.Reference r) {
        this.r = r;
        this.ent = new Entity(r.ent(), true);
        this.file = new Entity(r.file(), true);
        this.line = r.line();
        this.column = r.column();
    }

    public int line() {
        return line;
    }

    public int column() {
        return column;
    }

    public Entity ent() {
        return ent;
    }

    public Entity file() {
        return file;
    }

    public com.scitools.understand.Reference r() {
        return this.r;
    }

    @Override
    public String toString() {
        return ent.name() + " at " + r.line() + ":" + r.column();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reference reference = (Reference) o;

        if (line != reference.line) return false;
        if (column != reference.column) return false;
        if (ent != null ? !ent.equals(reference.ent) : reference.ent != null) return false;
        return (file != null ? !file.equals(reference.file) : reference.file != null);
    }

    @Override
    public int hashCode() {
        int result = ent != null ? ent.hashCode() : 0;
        result = 31 * result + line;
        result = 31 * result + column;
        return result;
    }

}
