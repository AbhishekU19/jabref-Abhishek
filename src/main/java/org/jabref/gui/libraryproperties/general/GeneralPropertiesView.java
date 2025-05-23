package org.jabref.gui.libraryproperties.general;

import java.nio.charset.Charset;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import org.jabref.gui.libraryproperties.AbstractPropertiesTabView;
import org.jabref.gui.util.IconValidationDecorator;
import org.jabref.gui.util.ViewModelListCellFactory;
import org.jabref.logic.l10n.Localization;
import org.jabref.logic.preferences.CliPreferences;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.database.BibDatabaseMode;

import com.airhacks.afterburner.views.ViewLoader;
import de.saxsys.mvvmfx.utils.validation.visualization.ControlsFxVisualizer;
import jakarta.inject.Inject;

public class GeneralPropertiesView extends AbstractPropertiesTabView<GeneralPropertiesViewModel> {
    @FXML private ComboBox<Charset> encoding;
    @FXML private ComboBox<BibDatabaseMode> databaseMode;
    @FXML private TextField librarySpecificFileDirectory;
    @FXML private TextField userSpecificFileDirectory;
    @FXML private TextField laTexFileDirectory;

    private final ControlsFxVisualizer librarySpecificFileDirectoryValidationVisualizer = new ControlsFxVisualizer();
    private final ControlsFxVisualizer userSpecificFileDirectoryValidationVisualizer = new ControlsFxVisualizer();
    private final ControlsFxVisualizer laTexFileDirectoryValidationVisualizer = new ControlsFxVisualizer();

    @Inject private CliPreferences preferences;

    public GeneralPropertiesView(BibDatabaseContext databaseContext) {
        this.databaseContext = databaseContext;

        ViewLoader.view(this)
                  .root(this)
                  .load();
    }

    @Override
    public String getTabName() {
        return Localization.lang("General");
    }

    public void initialize() {
        this.viewModel = new GeneralPropertiesViewModel(databaseContext, dialogService, preferences);

        new ViewModelListCellFactory<Charset>()
                .withText(Charset::displayName)
                .install(encoding);
        encoding.disableProperty().bind(viewModel.encodingDisableProperty());
        encoding.itemsProperty().bind(viewModel.encodingsProperty());
        encoding.valueProperty().bindBidirectional(viewModel.selectedEncodingProperty());

        new ViewModelListCellFactory<BibDatabaseMode>()
                .withText(BibDatabaseMode::getFormattedName)
                .install(databaseMode);
        databaseMode.itemsProperty().bind(viewModel.databaseModesProperty());
        databaseMode.valueProperty().bindBidirectional(viewModel.selectedDatabaseModeProperty());

        librarySpecificFileDirectory.textProperty().bindBidirectional(viewModel.librarySpecificDirectoryPropertyProperty());
        userSpecificFileDirectory.textProperty().bindBidirectional(viewModel.userSpecificFileDirectoryProperty());
        laTexFileDirectory.textProperty().bindBidirectional(viewModel.laTexFileDirectoryProperty());

        librarySpecificFileDirectoryValidationVisualizer.setDecoration(new IconValidationDecorator());
        userSpecificFileDirectoryValidationVisualizer.setDecoration(new IconValidationDecorator());
        laTexFileDirectoryValidationVisualizer.setDecoration(new IconValidationDecorator());

        Platform.runLater(() -> {
            librarySpecificFileDirectoryValidationVisualizer.initVisualization(viewModel.librarySpecificFileDirectoryStatus(), librarySpecificFileDirectory);
            userSpecificFileDirectoryValidationVisualizer.initVisualization(viewModel.userSpecificFileDirectoryStatus(), userSpecificFileDirectory);
            laTexFileDirectoryValidationVisualizer.initVisualization(viewModel.laTexFileDirectoryStatus(), laTexFileDirectory);

            librarySpecificFileDirectory.requestFocus();
        });
    }

    @FXML
    public void browseLibrarySpecificFileDirectory() {
        viewModel.browseLibrarySpecificDir();
    }

    @FXML
    public void browseUserSpecificFileDirectory() {
        viewModel.browseUserDir();
    }

    @FXML
    void browseLatexFileDirectory() {
        viewModel.browseLatexDir();
    }
}
