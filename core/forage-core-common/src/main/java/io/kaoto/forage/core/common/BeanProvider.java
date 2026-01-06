package io.kaoto.forage.core.common;

public interface BeanProvider<T> {

    /**
     * Creates a new provided bean instance
     * @return the created bean
     */
    default T create() {
        return create(null);
    }

    /**
     * Creates a new provided bean instance
     * @param id a pre-existing ID that can be used by the provider to refer to itself (i.e.: in things such as configurations)
     * @return the created bean
     */
    T create(String id);
}
