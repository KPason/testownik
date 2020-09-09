package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import sample.questionsDataBase.Question;
import sample.questionsDataBase.QuestionsDataBase;

import java.io.IOException;
import java.util.Optional;


public class AddingQuestionController {

    @FXML
    private TextField dialogAddQuestionTextField;
    @FXML
    private TextField dialogAddFirstAnswerTextField;
    @FXML
    private TextField dialogAddSecondAnswerTextField;
    @FXML
    private TextField dialogAddThirdAnswerTextField;
    @FXML
    private TextField dialogAddFourthAnswerTextField;
    @FXML
    private TextField dialogAddCorrectAnswerTextField;

    public void processResults() throws IOException {
        String question = dialogAddQuestionTextField.getText().trim();
        String firstAnswer = dialogAddFirstAnswerTextField.getText().trim();
        String secondAnswer = dialogAddSecondAnswerTextField.getText().trim();
        String thirdAnswer = dialogAddThirdAnswerTextField.getText().trim();
        String fourthAnswer = dialogAddFourthAnswerTextField.getText().trim();
        String correctAnswer = dialogAddCorrectAnswerTextField.getText().trim();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm adding");
        alert.setHeaderText("Are you sure that you want to add question: " + question);
        alert.setContentText("Press ok to confirm, or cancel to avoid");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            QuestionsDataBase.getInstance().addQuestion(new Question(question, firstAnswer, secondAnswer, thirdAnswer, fourthAnswer, correctAnswer));
            QuestionsDataBase.getInstance().saveQuestions();
        }
    }
}
