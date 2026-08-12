package iaroslav.baranov.tracklog.reflection;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.Variable;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicate;
import iaroslav.baranov.tracklog.unification.Substitution;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Map;

@Service
public class ReflectionService {
    public <T> T createRecordFromSubstitution(Class<T> clazz, Substitution substitution) {
        try {
            Constructor<?> ctor = clazz.getDeclaredConstructors()[0];
            Parameter[] parameters = ctor.getParameters();
            Object[] values = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                String name = parameters[i].getName();
                Term substitutionValue = substitution.get(name);
                if (substitutionValue != null) {
                    values[i] = substitution.get(name);
                } else {
                    //Case where used name is the same as in the template and removed by unification alg
                    values[i] = new Variable(name);
                }
            }

            ctor.setAccessible(true);
            @SuppressWarnings("unchecked")
            T result = (T) ctor.newInstance(values);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create " + clazz.getName(), e);
        }
    }

    public <T> T createRecordFromMap(Class<T> clazz, Map<String, ?> map) {
        try {
            Constructor<?> ctor = clazz.getDeclaredConstructors()[0];
            Parameter[] parameters = ctor.getParameters();
            Object[] values = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                String name = parameters[i].getName();
                values[i] = map.get(name);
            }

            ctor.setAccessible(true);
            @SuppressWarnings("unchecked")
            T result = (T) ctor.newInstance(values);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create " + clazz.getName(), e);
        }
    }

    public Class<?> getClassOfFirstArgumentOfGeneric(Class<?> clazz, Class<?> baseType) {
        Class<?> resolved = org.springframework.core.ResolvableType
                .forClass(clazz)
                .as(baseType)
                .getGeneric(0)
                .resolve();

        if (resolved == null) {
            throw new IllegalStateException(
                    "Cannot resolve Args type for " + getClass().getName()
            );
        }

        return resolved;
    }
}
