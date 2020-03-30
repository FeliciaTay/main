package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.archival.InternshipApplicationViewType;
import seedu.address.model.internship.InternshipApplication;
import seedu.address.model.statistics.Statistics;

/**
 * Represents the in-memory model of the internship diary data.
 */
public class ModelManager implements Model, PropertyChangeListener {

    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final UserPrefs userPrefs;
    private final Statistics statistics;

    private InternshipDiary internshipDiary;
    private FilteredList<InternshipApplication> filteredInternshipApplications;
    private SortedList<InternshipApplication> sortedFilteredInternshipApplications;

    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

    /**
     * Initializes a ModelManager with the given internshipDiary and userPrefs.
     */
    public ModelManager(ReadOnlyInternshipDiary internshipDiary, ReadOnlyUserPrefs userPrefs) {
        super();
        requireAllNonNull(internshipDiary, userPrefs);

        logger.fine("Initializing with internship diary: " + internshipDiary + " and user prefs " + userPrefs);

        this.internshipDiary = new InternshipDiary(internshipDiary);
        // Model manager listens to any changes in displayedInternships in internshipdiary
        this.internshipDiary.addDisplayedInternshipsPropertyChangeListener(this);
        this.userPrefs = new UserPrefs(userPrefs);
        this.statistics = new Statistics();
        filteredInternshipApplications = new FilteredList<>(this.internshipDiary.getDisplayedInternshipList());
        sortedFilteredInternshipApplications = new SortedList<>(filteredInternshipApplications);
    }

    public ModelManager() {
        this(new InternshipDiary(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getInternshipDiaryFilePath() {
        return userPrefs.getInternshipDiaryFilePath();
    }

    @Override
    public void setInternshipDiaryFilePath(Path internshipDiaryFilePath) {
        requireNonNull(internshipDiaryFilePath);
        userPrefs.setInternshipDiaryFilePath(internshipDiaryFilePath);
    }

    //=========== InternshipDiary ============================================================================

    @Override
    public void setInternshipDiary(ReadOnlyInternshipDiary internshipDiary) {
        this.internshipDiary.resetData(internshipDiary);
    }

    @Override
    public ReadOnlyInternshipDiary getInternshipDiary() {
        return internshipDiary;
    }

    @Override
    public boolean hasInternshipApplication(InternshipApplication internshipApplication) {
        requireNonNull(internshipApplication);
        return internshipDiary.hasInternship(internshipApplication);
    }

    @Override
    public void archiveInternshipApplication(InternshipApplication target) {
        internshipDiary.archiveInternshipApplication(target);
    }

    @Override
    public void unarchiveInternshipApplication(InternshipApplication target) {
        internshipDiary.unarchiveInternshipApplication(target);
    }

    @Override
    public void deleteInternshipApplication(InternshipApplication target) {
        internshipDiary.removeInternship(target);
    }

    @Override
    public void addInternshipApplication(InternshipApplication internshipApplication) {
        internshipDiary.addInternshipApplication(internshipApplication);
    }

    @Override
    public void setInternshipApplication(InternshipApplication target, InternshipApplication editedInternship) {
        requireAllNonNull(target, editedInternship);

        internshipDiary.setInternship(target, editedInternship);
    }

    //=========== Internship Application List Accessors =============================================

    /**
     * Returns an unmodifiable view of the concatenated archived and unarchived list of {@code InternshipApplication}
     * backed by the internal list of {@code versionedInternshipDiary}
     */
    @Override
    public ObservableList<InternshipApplication> getAllInternshipApplicationList() {
        return internshipDiary.getAllInternshipList();
    }

    /**
     * Returns an unmodifiable view of the current list of {@code InternshipApplication}
     * backed by the internal list of {@code versionedInternshipDiary}
     */
    @Override
    public ObservableList<InternshipApplication> getFilteredInternshipApplicationList() {
        return sortedFilteredInternshipApplications;
    }

    @Override
    public void updateFilteredInternshipApplicationList(Predicate<InternshipApplication> predicate) {
        requireNonNull(predicate);
        filteredInternshipApplications.setPredicate(predicate);
        changes.firePropertyChange("predicate", null, predicate);
    }

    @Override
    public void updateFilteredInternshipApplicationList(Comparator<InternshipApplication> comparator) {
        requireNonNull(comparator);
        sortedFilteredInternshipApplications.setComparator(comparator);
        changes.firePropertyChange("comparator", null, comparator);
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return internshipDiary.equals(other.internshipDiary)
                && userPrefs.equals(other.userPrefs)
                && filteredInternshipApplications.equals(other.filteredInternshipApplications);
    }

    //=========== Archival view ==================================================================================

    @Override
    public void viewArchivedInternshipApplicationList() {
        internshipDiary.viewArchivedInternshipApplicationList();
    }

    @Override
    public void viewUnarchivedInternshipApplicationList() {
        internshipDiary.viewUnarchivedInternshipApplicationList();
    }

    @Override
    public InternshipApplicationViewType getCurrentView() {
        return internshipDiary.getCurrentView();
    }

    /**
     * Receives the latest changes in displayed internships from internship diary.
     * Updates the filtered and sorted internship applications accordingly
     * and fires property change event to its listeners.
     *
     * @param e event that describes the changes in the updated property.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        ObservableList<InternshipApplication> ia = (ObservableList<InternshipApplication>) e.getNewValue();
        filteredInternshipApplications = new FilteredList<>(ia);
        sortedFilteredInternshipApplications = new SortedList<>(filteredInternshipApplications);
        changes.firePropertyChange("filteredInternshipApplications", null,
                getFilteredInternshipApplicationList());
        changes.firePropertyChange("comparator", null, null);
        changes.firePropertyChange("predicate", null, null);
    }

    //=========== Statistics ==================================================================================

    @Override
    public Statistics getStatistics() {
        return statistics;
    }

    //=========== PropertyChangeListeners ======================================================================

    @Override
    public void addFilteredInternshipApplicationsPropertyChangeListener(PropertyChangeListener l) {
        changes.addPropertyChangeListener("filteredInternshipApplications", l);
    }

    @Override
    public void addComparatorPropertyChangeListener(PropertyChangeListener l) {
        changes.addPropertyChangeListener("comparator", l);
    }

    @Override
    public void addPredicatePropertyChangeListener(PropertyChangeListener l) {
        changes.addPropertyChangeListener("predicate", l);
    }

}
