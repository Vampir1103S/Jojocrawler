package my_project.model.Entities;
/**
 * Einfacher Gegnertyp ohne eigene Speziallogik.
 * <p>
 * {@code Schläger} erbt vollständig das Verhalten aus der
 * {@link Enemy}-Klasse und dient als grundlegender oder
 * experimenteller Gegnertyp.
 * Die Klasse kann als Platzhalter, Test-Gegner oder Basis
 * für spätere Erweiterungen verwendet werden.
 */

public class Schläger extends Enemy{

    public Schläger() {
        super(0,0,0,0,0,0,"LEON",0,0);
    }
}
