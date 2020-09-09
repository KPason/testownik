package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint;
import sample.questionsDataBase.QuestionsDataBase;

import java.io.IOException;
import java.util.Optional;


public class Controller {
    @FXML
    private RadioButton firstQuestionButton;
    @FXML
    private RadioButton secondQuestionButton;
    @FXML
    private RadioButton thirdQuestionButton;
    @FXML
    private RadioButton fourthQuestionButton;
    @FXML
    private Label questionLabel;
    @FXML
    private Button checkingButton;
    @FXML
    private Label resultLabel;
    @FXML
    private Button nextQuestionButton;
    @FXML
    private ToggleGroup answerToggleGroup;
    @FXML
    private BorderPane mainWindowBorderPane;


    private int questionsCounter = 0;
    private boolean isQuestionAnswered = false;

    public void initialize() {
        nextQuestionButton.setDisable(true);
        loadNextQuestion();

    }

    @FXML
    public void showRemovingQuestionDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainWindowBorderPane.getScene().getWindow());
        dialog.setTitle("Remove question");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("removeQuestion.fxml"));
            dialog.getDialogPane().setContent(root);
        } catch (IOException e) {
            System.out.println("Couldn't load remove question dialog");
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println("OK pressed");
        }
    }

    @FXML
    public void showAddingNewQuestionDialog() throws IOException {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainWindowBorderPane.getScene().getWindow());
        dialog.setTitle("Add question");
        dialog.setHeaderText("Create new to do item");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("addingQuestion.fxml"));

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
            AddingQuestionController controller = loader.getController();
            controller.processResults();
        }
    }

    public void checkingTheAnswer() {

        if (!firstQuestionButton.isSelected() && !secondQuestionButton.isSelected() && !thirdQuestionButton.isSelected()
                && !fourthQuestionButton.isSelected()) {
            resultLabel.setText("CHOOSE AN ANSWER");

        } else if (isCorrectAnswer()) {
            settingSceneAfterTheAnswer("green","Correct answer");
        } else {
            settingSceneAfterTheAnswer("red","Wrong answer");
        }

    }

    public void settingSceneAfterTheAnswer(String textColour, String labelText){
        resultLabel.setText(labelText);
        checkingButton.setTextFill(Paint.valueOf(textColour));
        questionsCounter++;

        // making question answered and disabling buttons
        isQuestionAnswered = true;
        nextQuestionButton.setDisable(false);
        checkingButton.setDisable(true);
    }

    public void loadNextQuestion() {

        if (questionsCounter < QuestionsDataBase.getInstance().getQuestionsList().size()) {
            // loading next question if there is any
            firstQuestionButton.setText(QuestionsDataBase.getInstance().getQuestionsList().get(questionsCounter).getFirstAnswer());
            secondQuestionButton.setText(QuestionsDataBase.getInstance().getQuestionsList().get(questionsCounter).getSecondAnswer());
            thirdQuestionButton.setText(QuestionsDataBase.getInstance().getQuestionsList().get(questionsCounter).getThirdAnswer());
            fourthQuestionButton.setText(QuestionsDataBase.getInstance().getQuestionsList().get(questionsCounter).getFourthAnswer());
            questionLabel.setText(QuestionsDataBase.getInstance().getQuestionsList().get(questionsCounter).getQuestion());
            checkingButton.setDisable(false);
            nextQuestionButton.setDisable(true);
        } else {
            // disabling buttons after all questions
            questionLabel.setText("ALL QUESTIONS CHECKED");
            firstQuestionButton.setDisable(true);
            secondQuestionButton.setDisable(true);
            thirdQuestionButton.setDisable(true);
            fourthQuestionButton.setDisable(true);
        }
    }

    public void goToTheNextQuestion() {
        if (isQuestionAnswered) {
            checkingButton.setTextFill(Paint.valueOf("black"));
            resultLabel.setText("");
            loadNextQuestion();
            isQuestionAnswered = false;
        }

    }

    public boolean isCorrectAnswer() {

        if (answerToggleGroup.selectedToggleProperty() != null) {
            String buttonValue = answerToggleGroup.selectedToggleProperty().getValue().toString();
            String correctAnswer = QuestionsDataBase.getInstance().getQuestionsList()
                    .get(questionsCounter).getCorrectAnswer();
            return buttonValue.substring(buttonValue.length() - (correctAnswer.length() + 1), buttonValue.length() - 1)
                    .equals(correctAnswer);
        } else return false;

    }
}
