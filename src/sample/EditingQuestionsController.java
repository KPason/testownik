package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
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


    public void initialize() {
        questionsListView.setItems(QuestionsDataBase.getInstance().getQuestionsList()); //using Data Binding with ObservableArrayList via Collections.
        questionsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
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

      //  *************IT NEEDS A LOT OF VALIDATION*****************

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
            questionsListView.getSelectionModel().select(newQuestion);
        }
    }

    public void handleClickListView() {
        Question item = questionsListView.getSelectionModel().getSelectedItem();
        System.out.println("The selected item is " + item);
        setTextAreas(item);
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


}
