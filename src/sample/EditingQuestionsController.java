package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.util.Callback;
import sample.questionsDataBase.Question;
import sample.questionsDataBase.QuestionsDataBase;

import java.io.IOException;
import java.util.Optional;
import java.util.Queue;

public class EditingQuestionsController {

    @FXML
    private ListView<Question> questionsListView;
    @FXML
    private TextArea firstAnswerTextArea;
    @FXML
    private TextArea secondAnswerTextArea;
    @FXML
    private TextArea thirdAnswerTextArea;
    @FXML
    private TextArea fourthAnswerTextArea;
    @FXML
    private TextArea correctAnswerTextArea;
    @FXML
    private DialogPane editingQuestionsDialogPane;
    @FXML
    private Label labelAboveQuestionsTable;
    @FXML
    private ContextMenu editingQuestionsContextMenu;

    private boolean isQuestionsListChanged;


    public void initialize() {
        //populating list view
        isQuestionsListChanged = false;
        labelAboveQuestionsTable.setText("QUESTIONS: " + QuestionsDataBase.getInstance().getQuestionsList().size());
        questionsListView.setItems(QuestionsDataBase.getInstance().getQuestionsList());
        questionsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        //setting context menu and menu item(s)
        editingQuestionsContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("delete");
        deleteMenuItem.setOnAction(actionEvent -> {
            Question item = questionsListView.getSelectionModel().getSelectedItem();
            deleteItem(item);
            System.out.println("IS LIST CHANGED: " + isQuestionsListChanged);
        });
        editingQuestionsContextMenu.getItems().addAll(deleteMenuItem);

        //associating context menu with list view's cell factory and setting cell's text
        questionsListView.setCellFactory(new Callback<ListView<Question>, ListCell<Question>>() {
            @Override
            public ListCell<Question> call(ListView<Question> questionListView) {
                ListCell<Question> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(Question item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item.getQuestion());
                        }
                    }
                };
                cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                    if (isNowEmpty) {
                        cell.setContextMenu(null);
                    } else {
                        cell.setContextMenu(editingQuestionsContextMenu);
                    }
                });

                return cell;
            }
        });

        //setting text areas
        questionsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Question>() {
            @Override
            public void changed(ObservableValue<? extends Question> observableValue, Question question, Question newValue) {
                if (newValue != null) {
                    Question item = questionsListView.getSelectionModel().getSelectedItem();
                    setTextAreas(item);
                }
            }
        });

        System.out.println("IS LIST CHANGED: " + isQuestionsListChanged);
    }

    public void showAddingNewQuestionDialog() throws IOException {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(editingQuestionsDialogPane.getScene().getWindow());
        dialog.setTitle("Add question");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("addingQuestionDialog.fxml"));


        try {
            dialog.getDialogPane().setContent(loader.load());

        } catch (IOException e) {
            System.out.println("Couldn't load dialog");
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            AddingQuestionsController controller = loader.getController();
            Question newQuestion = controller.processResults();
            labelAboveQuestionsTable.setText("QUESTIONS: " + QuestionsDataBase.getInstance().getQuestionsList().size());
            questionsListView.getSelectionModel().select(newQuestion);
            isQuestionsListChanged = true;
        }
        System.out.println("IS LIST CHANGED: " + isQuestionsListChanged);
    }


    public void setTextAreas(Question item) {
        firstAnswerTextArea.setText(item.getFirstAnswer());
        secondAnswerTextArea.setText(item.getSecondAnswer());
        thirdAnswerTextArea.setText(item.getThirdAnswer());
        fourthAnswerTextArea.setText(item.getFourthAnswer());
        correctAnswerTextArea.setText(item.getCorrectAnswer());
        firstAnswerTextArea.setEditable(false);
        secondAnswerTextArea.setEditable(false);
        thirdAnswerTextArea.setEditable(false);
        fourthAnswerTextArea.setEditable(false);
        correctAnswerTextArea.setEditable(false);
    }

    public void setTextAreas(String string) {
        firstAnswerTextArea.setText(string);
        secondAnswerTextArea.setText(string);
        thirdAnswerTextArea.setText(string);
        fourthAnswerTextArea.setText(string);
        correctAnswerTextArea.setText(string);
        firstAnswerTextArea.setEditable(false);
        secondAnswerTextArea.setEditable(false);
        thirdAnswerTextArea.setEditable(false);
        fourthAnswerTextArea.setEditable(false);
        correctAnswerTextArea.setEditable(false);
    }

    public void limitLength(int maxLength, TextField t) {
        t.lengthProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    // Check if the new character is greater than LIMIT
                    if (t.getText().length() >= maxLength) {

                        t.setText(t.getText().substring(0, maxLength));
                    }
                }
            }
        });
    }

    public void deleteItem(Question question) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("delete question");
        alert.setHeaderText("Deleting question \"" + question.getQuestion() + "\"");
        alert.setContentText("Are you sure? Press OK to confirm, or cancel to go back");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            QuestionsDataBase.getInstance().deleteQuestion(question);

            labelAboveQuestionsTable.setText("QUESTIONS: " + QuestionsDataBase.getInstance().getQuestionsList().size());
            isQuestionsListChanged = true;
            if (QuestionsDataBase.getInstance().getQuestionsList().size() == 0) {
                setTextAreas("");
            }
        }
    }
}
