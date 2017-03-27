package com.aurea.deadcode.model;

/**
 * Created by ekonovalov on 15.03.2017.
 */
public enum Language {

    Ada("Ada"),
    Assembly("Assembly"),
    COBOL("COBOL"),
    Cpp("C++"),
    Csharp("C#"),
    Fortran("Fortran"),
    Java("Java"),
    Jovial("Jovial"),
    Pascal("Pascal"),
    Plm("Plm"),
    Python("Python"),
    VHDL("VHDL"),
    Web("Web");

    private String value;

    Language(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
