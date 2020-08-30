package api;

public interface JsonMapper<E> {
    E mapToClass(Json json);
}
