package sample;

import fx.mycontrols.TextFieldLimited;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import sample.questionsDataBase.Question;
import sample.questionsDataBase.QuestionsDataBase;

import java.io.IOException;
import java.util.Optional;


public class AddingQuestionsController {

    @FXML
    private TextFieldLimited dialogAddQuestionTextField;
    @FXML
    private TextFieldLimited dialogAddFirstAnswerTextField;
    @FXML
    private TextFieldLimited dialogAddSecondAnswerTextField;
    @FXML
    private TextFieldLimited dialogAddThirdAnswerTextField;
    @FXML
    private TextFieldLimited dialogAddFourthAnswerTextField;
    @FXML
    private TextFieldLimited dialogAddCorrectAnswerTextField;

    private String question;
    private String firstAnswer;
    private String secondAnswer;
    private String thirdAnswer;
    private String fourthAnswer;
    private String correctAnswer;


    public void initialize() {

    }

    //*********************WILL NEED TO ADD CORRECT ANSWERS COUNTER WHEN IMPLEMENTED PROBABLY ************************
    public Question processResults() throws IOException {

        limitAllTextFieldsLength();

        question = dialogAddQuestionTextField.getText().trim();
        firstAnswer = dialogAddFirstAnswerTextField.getText().trim();
        secondAnswer = dialogAddSecondAnswerTextField.getText().trim();
        fourthAnswer = dialogAddFourthAnswerTextField.getText().trim();
        thirdAnswer = dialogAddThirdAnswerTextField.getText().trim();
        correctAnswer = dialogAddCorrectAnswerTextField.getText().trim();

        //check how many answers are empty and change the blanks to **empty**
        int numberOfAnswers = checkHowManyAnswers();

        Question newQuestion = new Question(question, firstAnswer, secondAnswer, thirdAnswer, fourthAnswer, correctAnswer, numberOfAnswers);


        if (numberOfAnswers < 2) {
            return showWarningAlert("Set the answers", "You passed " + numberOfAnswers + " answers", "Set at least 2 answers to continue");

        } else if (question.isBlank()) {
            return showWarningAlert("Set the question text", "Question text field is empty", "Set a question text to continue");

        } else if (!correctAnswer.equals(firstAnswer) && !correctAnswer.equals(secondAnswer) && !correctAnswer.equals(thirdAnswer) &&
                !correctAnswer.equals(fourthAnswer)) {
            return showWarningAlert("Set correct answer", "Correct answer does not match any of the answers", "Set correct answer");

        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm adding");
            alert.setHeaderText("Are you sure that you want to add question: " + question);
            alert.setContentText("Press ok to confirm, or cancel to avoid");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {

                QuestionsDataBase.getInstance().getQuestionsList().add(newQuestion);
            }
        }

        return newQuestion;
    }

    public int checkHowManyAnswers() {
        int numberOfAnswers = 0;
        String emptyString = "**empty**";
        if (!firstAnswer.isBlank()) {
            numberOfAnswers++;
        }else{
            firstAnswer = emptyString;
        }

        if (!secondAnswer.isBlank()) {
            numberOfAnswers++;
        }else{
            secondAnswer = emptyString;
        }

        if (!thirdAnswer.isBlank()) {
            numberOfAnswers++;
        }else{
            thirdAnswer = emptyString;
        }
        if (!fourthAnswer.isBlank()) {
            numberOfAnswers++;
        }else{
            fourthAnswer = emptyString;
        }
        return numberOfAnswers;
    }


    public Question showWarningAlert(String title, String headerText, String contextText) {
        ButtonType confirmationButton = new ButtonType("go back", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.WARNING, contextText, confirmationButton);
        alert.setHeaderText(headerText);
        alert.setTitle(title);
        alert.showAndWait();
        return null;
    }


    public void limitAllTextFieldsLength() {
        dialogAddQuestionTextField.setMaxLength(50);
        dialogAddFirstAnswerTextField.setMaxLength(30);
        dialogAddSecondAnswerTextField.setMaxLength(30);
        dialogAddThirdAnswerTextField.setMaxLength(30);
        dialogAddFourthAnswerTextField.setMaxLength(30);
        dialogAddCorrectAnswerTextField.setMaxLength(30);
    }


}
