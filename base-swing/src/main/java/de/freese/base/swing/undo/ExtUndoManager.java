package de.freese.base.swing.undo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 * Erweiterter {@link UndoManager}.
 *
 * @author Thomas Freese
 */
public class ExtUndoManager extends UndoManager
{
    /**
     *
     */
    private static final long serialVersionUID = 8132103408414717090L;

    /**
     * Liefert eine unmodifiableList der vorhandenen {@link UndoableEdit}s.
     * 
     * @return {@link List}
     */
    public synchronized List<UndoableEdit> getEdits()
    {
        return Collections.unmodifiableList(this.edits);
    }

    /**
     * Liefert eine unmodifiableList der vorhandenen {@link UndoableEdit}s, welche noch ein Undo ausfuehren koennen.
     * 
     * @return {@link List}
     */
    public synchronized List<UndoableEdit> getUndoableEdits()
    {
        List<UndoableEdit> undoableEdits = new ArrayList<>();
        UndoableEdit nextRedoableEdit = editToBeRedone();

        for (UndoableEdit undoableEdit : this.edits)
        {
            if (undoableEdit == nextRedoableEdit)
            {
                break;
            }

            undoableEdits.add(undoableEdit);
        }

        return Collections.unmodifiableList(undoableEdits);
    }
}
