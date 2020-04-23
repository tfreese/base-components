/**
 * Created: 29.07.2018
 */

package de.freese.base.core.codegen.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Basis-Implementierung eines Model-Objekts.
 *
 * @author Thomas Freese
 */
public abstract class AbstractModel
{
    /**
    *
    */
    private final List<String> annotations = new ArrayList<>();

    /**
    *
    */
    private final List<String> comments = new ArrayList<>();

    /**
    *
    */
    public final String name;

    /**
    *
    */
    public Object payload = null;

    /**
     * Erstellt ein neues {@link AbstractModel} Object.
     *
     * @param name String
     */
    public AbstractModel(final String name)
    {
        super();

        this.name = Objects.requireNonNull(name, "name required");
    }

    /**
     * @param annotation String
     */
    public void addAnnotation(final String annotation)
    {
        this.annotations.add(annotation);
    }

    /**
     * @param comment String
     */
    public void addComment(final String comment)
    {
        this.comments.add(comment);
    }

    /**
     * @return {@link List}
     */
    public List<String> getAnnotations()
    {
        return this.annotations;
    }

    /**
     * @return {@link List}
     */
    public List<String> getComments()
    {
        return this.comments;
    }

    /**
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return Object
     */
    @SuppressWarnings("unchecked")
    public <T> T getPayload()
    {
        return (T) this.payload;
    }

    /**
     * @param payload Object
     */
    public void setPayload(final Object payload)
    {
        this.payload = payload;
    }
}
