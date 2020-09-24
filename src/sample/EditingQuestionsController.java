package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.util.Callback;
import sample.questionsDataBase.Question;
import sample.questionsDataBase.QuestionsDataBase;

import java.io.IOException;
import java.util.Optional;

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


    public void initialize() {
        //populating list view
        labelAboveQuestionsTable.setText("QUESTIONS: " + QuestionsDataBase.getInstance().getQuestionsList().size());
        questionsListView.setItems(QuestionsDataBase.getInstance().getQuestionsList());
        questionsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        //setting context menu and menu item(s)
        editingQuestionsContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("delete");
        deleteMenuItem.setOnAction(actionEvent -> {
            Question item = questionsListView.getSelectionModel().getSelectedItem();
            deleteItem(item);

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


    }

    public void showAddingNewQuestionDialog() throws IOException {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(editingQuestionsDialogPane.getScene().getWindow());
        dialog.setTitle("Add question");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("addingQuestionDialog.fxml"));
        DialogPane addingDialog = dialog.getDialogPane();
        addingDialog.getStylesheets().add(getClass().getResource("/style/style1.css").toExternalForm());
        addingDialog.getStyleClass().add("editingDialog");



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

            while (newQuestion == null) {
                result = dialog.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                    break;
                }
                controller = loader.getController();
                newQuestion = controller.processResults();
            }
            labelAboveQuestionsTable.setText("QUESTIONS: " + QuestionsDataBase.getInstance().getQuestionsList().size());
            questionsListView.getSelectionModel().select(newQuestion);

        }

    }


    public void setTextAreas(Question item) {

        if(item.getFirstAnswer().equals("**empty**")){
            firstAnswerTextArea.setText("");
        }else{
            firstAnswerTextArea.setText(item.getFirstAnswer());
        }
        if(item.getSecondAnswer().equals("**empty**")){
            secondAnswerTextArea.setText("");
        }else{
            secondAnswerTextArea.setText(item.getSecondAnswer());
        }
        if(item.getThirdAnswer().equals("**empty**")){
            thirdAnswerTextArea.setText("");
        }else{
            thirdAnswerTextArea.setText(item.getThirdAnswer());
        }
        if(item.getFourthAnswer().equals("**empty**")){
            fourthAnswerTextArea.setText("");
        }else{
            fourthAnswerTextArea.setText(item.getFourthAnswer());
        }
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


    public void deleteItem(Question question) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("delete question");
        if(question.getQuestion().length()>50){
            alert.setHeaderText("Are you sure that you want to delete question: \n" + question.getQuestion().substring(0,question.getQuestion().length()/2) + "\n" + question.getQuestion().substring(question.getQuestion().length()/2));
        }else{
            alert.setHeaderText("Are you sure that you want to delete question: \n" + question.getQuestion());
        }
        alert.setContentText("Are you sure? Press OK to confirm, or cancel to go back");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            QuestionsDataBase.getInstance().deleteQuestion(question);

            labelAboveQuestionsTable.setText("QUESTIONS: " + QuestionsDataBase.getInstance().getQuestionsList().size());
            if (QuestionsDataBase.getInstance().getQuestionsList().size() == 0) {
                setTextAreas("");
            }
        }
    }
}
