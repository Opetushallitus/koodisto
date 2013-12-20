package fi.vm.sade.koodisto.model.constraint.fieldassert;

public interface Asserter<T> {

    public boolean assertTrue(T first, T second);
}
