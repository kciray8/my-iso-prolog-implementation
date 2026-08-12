package iaroslav.baranov.tracklog.unification;

import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.Variable;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubstitutionTest {
    @Test
    public void compositionShouldWork(){
        Substitution theta = new Substitution();
        theta.addMapping("X", new CompoundTerm(new NamedAtom("f"), List.of(new Variable("Y"))));
        theta.addMapping("Y", new Variable("Z"));

        Substitution sigma = new Substitution();
        sigma.addMapping("X", new NamedAtom("a"));
        sigma.addMapping("Y", new NamedAtom("b"));
        sigma.addMapping("Z", new Variable("Y"));

        Substitution c = theta.compose(sigma);

        String expectedX = c.get("X").toCode();
        assertEquals("f(b)", expectedX);

        String expectedY = c.get("Z").toCode();
        assertEquals("Y", expectedY);
    }
}