package seedu.flashcard.model;

import static java.util.Objects.requireNonNull;
import static seedu.flashcard.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.flashcard.commons.core.GuiSettings;
import seedu.flashcard.commons.core.LogsCenter;
import seedu.flashcard.model.flashcard.Flashcard;
import seedu.flashcard.model.flashcard.Statistics;
import seedu.flashcard.model.tag.Tag;

/**
 * Represents the in memory model of the flashcard list data
 */
public class ModelManager implements Model {

    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);
    private final FlashcardList flashcardList;
    private final UserPrefs userPrefs;
    private final FilteredList<Flashcard> filteredFlashcards;
    private Flashcard viewedFlashcard;
    private Statistics desiredStats;

    /**
     * Default initializer
     */
    public ModelManager() {
        this(new FlashcardList(), new UserPrefs());
    }

    /**
     * Initializes a MOdelManager with the given flashcard list and userPrefs
     */
    public ModelManager(ReadOnlyFlashcardList flashcardList, ReadOnlyUserPrefs userPrefs) {
        super();
        requireAllNonNull(flashcardList, userPrefs);
        logger.fine("Initializing with flashcard list: " + flashcardList + " and user prefs " + userPrefs);
        this.flashcardList = new FlashcardList(flashcardList);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredFlashcards = new FilteredList<Flashcard>(this.flashcardList.getFlashcardList());
        this.viewedFlashcard = null;
    }

    @Override
    public Predicate<Flashcard> getHasTagPredicate(Set<Tag> tag) {
        requireNonNull(tag);
        return flashcard -> flashcard.hasAnyTag(tag);
    }

    @Override
    public Set<Tag> getAllSystemTags() {
        return flashcardList.getAllFlashcardTags();
    }

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
    public Path getFlashcardListFilePath() {
        return userPrefs.getFlashcardListFilePath();
    }

    @Override
    public void setFlashcardListFilePath(Path flashcardListFilePath) {
        requireNonNull(flashcardListFilePath);
        userPrefs.setFlashcardListFilePath(flashcardListFilePath);
    }

    @Override
    public void setFlashcardList(ReadOnlyFlashcardList flashcardList) {
        this.flashcardList.resetData(flashcardList);
    }

    @Override
    public ReadOnlyFlashcardList getFlashcardList() {
        return flashcardList;
    }

    @Override
    public boolean hasFlashcard(Flashcard flashcard) {
        requireNonNull(flashcard);
        return flashcardList.hasFlashcard(flashcard);
    }

    @Override
    public void deleteFlashcard(Flashcard flashcard) {
        flashcardList.removeFlashcard(flashcard);
    }

    @Override
    public void addFlashcard(Flashcard flashcard) {
        flashcardList.addFlashcard(flashcard);
        updateFilteredFlashcardList(PREDICATE_SHOW_ALL_FLASHCARDS);
    }

    @Override
    public void setFlashcard(Flashcard target, Flashcard editedFlashcard) {
        requireAllNonNull(target, editedFlashcard);
        flashcardList.setFlashcard(target, editedFlashcard);
    }

    @Override
    public ObservableList<Flashcard> getFilteredFlashcardList() {
        return filteredFlashcards;
    }

    @Override
    public boolean systemHasTag(Tag tag) {
        return flashcardList.flashcardsHasTag(tag);
    }

    @Override
    public void systemRemoveTag(Tag tag) {
        flashcardList.flashcardsRemoveTag(tag);
        updateFilteredFlashcardList(PREDICATE_SHOW_ALL_FLASHCARDS);
    }

    @Override
    public void updateFilteredFlashcardList(Predicate<Flashcard> predicate) {
        requireNonNull(predicate);
        filteredFlashcards.setPredicate(predicate);
    }

    @Override
    public void updateLastViewedFlashcard(Flashcard flashcard) {
        this.viewedFlashcard = flashcard;
    }

    @Override
    public Flashcard getLastViewedFlashcard() {
        return this.viewedFlashcard;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ModelManager)) {
            return false;
        }
        ModelManager obj = (ModelManager) other;
        return flashcardList.equals(obj.flashcardList)
                && userPrefs.equals(obj.userPrefs)
                && filteredFlashcards.equals(obj.filteredFlashcards);
    }

    @Override
    public String generateStatistics() {
        desiredStats = new Statistics();
        desiredStats.calculate(filteredFlashcards);
        return desiredStats.results();
    }

    @Override
    public Statistics getStatistics() {
        return desiredStats;
    }
}