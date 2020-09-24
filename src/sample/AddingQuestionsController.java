package sample;

import fx.mycontrols.TextFieldLimited;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import sample.questionsDataBase.Question;
import sample.questionsDataBase.QuestionsDataBase;

import java.io.IOException;
import java.util.ArrayList;
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
    @FXML
    private Label questionCharactersLimit;
    @FXML
    private Label firstAnswerCharactersLimit;
    @FXML
    private Label secondAnswerCharactersLimit;
    @FXML
    private Label thirdAnswerCharactersLimit;
    @FXML
    private Label fourthAnswerCharactersLimit;
    @FXML
    private Label correctAnswerCharactersLimit;


    private String question;
    private String firstAnswer;
    private String secondAnswer;
    private String thirdAnswer;
    private String fourthAnswer;
    private String correctAnswer;


    public void initialize() {
        limitAllTextFieldsLength();
        showCharactersLimitForEveryTextField();
    }


    public Question processResults() throws IOException {

        question = dialogAddQuestionTextField.getText().trim();
        firstAnswer = dialogAddFirstAnswerTextField.getText().trim();
        secondAnswer = dialogAddSecondAnswerTextField.getText().trim();
        fourthAnswer = dialogAddFourthAnswerTextField.getText().trim();
        thirdAnswer = dialogAddThirdAnswerTextField.getText().trim();
        correctAnswer = dialogAddCorrectAnswerTextField.getText().trim();

        //check how many answers are empty and change the blanks to **empty**
        int numberOfAnswers = checkHowManyAnswersAndSetInOrder();
        Question newQuestion;

        if (numberOfAnswers < 2) {
            return showWarningAlert("Set the answers", "You passed " + numberOfAnswers + " answers", "Set at least 2 answers to continue");

        } else if (question.isBlank()) {
            return showWarningAlert("Set the question text", "Question text field is empty", "Set a question text to continue");

        } else if (isQuestionAlreadyInDataBase(question)) {
            return showWarningAlert("Already in database", "Question you have passed is already in a database", "Make a new question");

        } else if (!correctAnswer.equals(firstAnswer) && !correctAnswer.equals(secondAnswer) && !correctAnswer.equals(thirdAnswer) &&
                !correctAnswer.equals(fourthAnswer)) {
            return showWarningAlert("Set correct answer", "Correct answer does not match any of the answers", "Set correct answer");

        } else if (answersRepeat()) {
            return showWarningAlert("Answers are repeating", "Your answers shouldn't repeat","Make your answers unique");
        } else {
            newQuestion = new Question(question, firstAnswer, secondAnswer, thirdAnswer, fourthAnswer, correctAnswer, numberOfAnswers);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm adding");
            if(question.length()>50){
                alert.setHeaderText("Are you sure that you want to add question: \n" + question.substring(0,question.length()/2) + "\n" + question.substring(question.length()/2));
            }else{
                alert.setHeaderText("Are you sure that you want to add question: \n" + question);
            }
            alert.setContentText("Press ok to confirm, or cancel to avoid");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {

                QuestionsDataBase.getInstance().getQuestionsList().add(newQuestion);
            }
        }

        return newQuestion;
    }

    public int checkHowManyAnswersAndSetInOrder() {
        int answersCounter = 0;

        //checking how many answers and taking care of empty, not in order user's inputs
        String[] answers = {firstAnswer, secondAnswer, thirdAnswer, fourthAnswer};
        ArrayList<String> newAnswers = new ArrayList<>(4);
        for (String answer : answers) {
            if (!answer.isBlank()) {
                answersCounter++;
                newAnswers.add(answer);
            }
        }
        for (int i = 0; i < (4 - answersCounter); i++) {
            newAnswers.add("**empty**");
        }
        firstAnswer = newAnswers.get(0);
        secondAnswer = newAnswers.get(1);
        thirdAnswer = newAnswers.get(2);
        fourthAnswer = newAnswers.get(3);
        return answersCounter;
    }


    public Question showWarningAlert(String title, String headerText, String contextText) {
        ButtonType confirmationButton = new ButtonType("go back", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.WARNING, contextText, confirmationButton);
        alert.setHeaderText(headerText);
        alert.setTitle(title);
        DialogPane alertDialog = alert.getDialogPane();
        alertDialog.getStylesheets().add(getClass().getResource("/style/style1.css").toExternalForm());
        alertDialog.getStyleClass().add("alertDialog");
        alert.showAndWait();
        return null;
    }

    public boolean isQuestionAlreadyInDataBase(String question) {
        for (Question checkedQuestion : QuestionsDataBase.getInstance().getQuestionsList()) {
            if (question.equals(checkedQuestion.getQuestion())) {
                return true;
            }
        }
        return false;
    }

    public void limitAllTextFieldsLength() {
        dialogAddQuestionTextField.setMaxLength(250);
        dialogAddFirstAnswerTextField.setMaxLength(130);
        dialogAddSecondAnswerTextField.setMaxLength(130);
        dialogAddThirdAnswerTextField.setMaxLength(130);
        dialogAddFourthAnswerTextField.setMaxLength(130);
        dialogAddCorrectAnswerTextField.setMaxLength(130);
    }

    public void showCharactersLimitForOneTextField(TextField textField, Label labelAbove) {
        textField.setOnMouseEntered(event -> labelAbove.setVisible(true));
        textField.setOnMouseExited(event -> labelAbove.setVisible(false));
    }

    public void showCharactersLimitForEveryTextField() {
        firstAnswerCharactersLimit.setText("characters limit: " + dialogAddFirstAnswerTextField.getMaxLength());
        secondAnswerCharactersLimit.setText("characters limit: " + dialogAddSecondAnswerTextField.getMaxLength());
        thirdAnswerCharactersLimit.setText("characters limit: " + dialogAddThirdAnswerTextField.getMaxLength());
        fourthAnswerCharactersLimit.setText("characters limit: " + dialogAddFourthAnswerTextField.getMaxLength());
        correctAnswerCharactersLimit.setText("characters limit: " + dialogAddCorrectAnswerTextField.getMaxLength());
        questionCharactersLimit.setText("characters limit: " + dialogAddQuestionTextField.getMaxLength());
        showCharactersLimitForOneTextField(dialogAddFirstAnswerTextField, firstAnswerCharactersLimit);
        showCharactersLimitForOneTextField(dialogAddSecondAnswerTextField, secondAnswerCharactersLimit);
        showCharactersLimitForOneTextField(dialogAddThirdAnswerTextField, thirdAnswerCharactersLimit);
        showCharactersLimitForOneTextField(dialogAddFourthAnswerTextField, fourthAnswerCharactersLimit);
        showCharactersLimitForOneTextField(dialogAddQuestionTextField, questionCharactersLimit);
        showCharactersLimitForOneTextField(dialogAddCorrectAnswerTextField, correctAnswerCharactersLimit);
    }

    public boolean answersRepeat() {
        if (firstAnswer.equals(secondAnswer) || firstAnswer.equals(thirdAnswer) || firstAnswer.equals(fourthAnswer)) {
            return true;
        } else if (secondAnswer.equals(thirdAnswer) || secondAnswer.equals(fourthAnswer)) {
            return true;
        } else if (thirdAnswer.equals(fourthAnswer)) {
            return true;
        } else {
            return false;
        }
    }

}
