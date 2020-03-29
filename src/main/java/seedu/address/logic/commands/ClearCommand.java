package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.InternshipDiary;
import seedu.address.model.Model;

/**
 * Clears the internship diary.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "Internship diary has been cleared!";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.setInternshipDiary(new InternshipDiary());
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
