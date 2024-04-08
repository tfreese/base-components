package de.freese.base.swing.undo;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 * @author Thomas Freese
 */
public class ExtUndoManager extends UndoManager {
    @Serial
    private static final long serialVersionUID = 8132103408414717090L;

    public synchronized List<UndoableEdit> getEdits() {
        return Collections.unmodifiableList(this.edits);
    }

    public synchronized List<UndoableEdit> getUndoableEdits() {
        final List<UndoableEdit> undoableEdits = new ArrayList<>();
        final UndoableEdit nextRedoableEdit = editToBeRedone();

        for (UndoableEdit undoableEdit : this.edits) {
            if (undoableEdit == nextRedoableEdit) {
                break;
            }

            undoableEdits.add(undoableEdit);
        }

        return Collections.unmodifiableList(undoableEdits);
    }
}
