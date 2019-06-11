package fi.vm.sade.koodisto.dto;

import static java.util.Objects.requireNonNull;

public class FindOrCreateWrapper<T> {

    private final Status status;
    private final T data;

    private FindOrCreateWrapper(Status status, T data) {
        this.status = requireNonNull(status);
        this.data = requireNonNull(data);
    }

    public static <T> FindOrCreateWrapper<T> found(T data) {
        return new FindOrCreateWrapper<>(Status.FOUND, data);
    }

    public static <T> FindOrCreateWrapper<T> created(T data) {
        return new FindOrCreateWrapper<>(Status.CREATED, data);
    }

    public Status getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public boolean isFound() {
        return Status.FOUND.equals(status);
    }

    public boolean isCreated() {
        return Status.CREATED.equals(status);
    }

    public enum Status {
        FOUND,
        CREATED,
    }

}
