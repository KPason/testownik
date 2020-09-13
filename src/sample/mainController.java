package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint;
import sample.questionsDataBase.Question;
import sample.questionsDataBase.QuestionsDataBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;


public class mainController {
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
    @FXML
    private MenuItem menuStartButton;
    @FXML
    private MenuItem menuRestartButton;


    private int questionsCounter = 0;
    private int correctAnswersCounter = 0;
    private int correctAnswersTotalNumber; // do licznika odpowiedzi
    private int wrongQuestionIndex = 0;
    private boolean isQuestionAnswered = false;
    private ArrayList<Question> savedWrongQuestions = new ArrayList<>();
    private Question actualWrongQuestion;
    private String lastQuestion;


    public void initialize() {
        disableAndDismissingTheButtons("WELCOME TO THE GAME", true, true);
        menuRestartButton.setDisable(true);
    }

    @FXML
    public void startTheGame() {
        try {
            QuestionsDataBase.getInstance().loadQuestions();

        } catch (IOException e) {
            System.out.println("Couldn't load the questions");
        }

        disableAndDismissingTheButtons(false, false);
        loadNextQuestion();
        correctAnswersTotalNumber = QuestionsDataBase.getInstance().getQuestionsList().size();
        nextQuestionButton.setDisable(true);
        menuRestartButton.setDisable(false);
        menuStartButton.setDisable(true);

    }

    @FXML
    public void restartTheGame() {
        try {
            QuestionsDataBase.getInstance().saveQuestions();
        } catch (IOException e) {
            System.out.println("Couldn't save the questions when restarted the game");
        }
        try {
            QuestionsDataBase.getInstance().loadQuestions();

        } catch (IOException e) {
            System.out.println("Couldn't load the questions when restarted the game");
        }
        //resetting all main variables
        questionsCounter = 0;
        QuestionsDataBase.getInstance().getWrongQuestionsList().clear();
        savedWrongQuestions.clear();
        actualWrongQuestion = null;
        wrongQuestionIndex = 0;
        isQuestionAnswered = false;
        disableAndDismissingTheButtons(false, false);
        loadNextQuestion();
        correctAnswersTotalNumber = QuestionsDataBase.getInstance().getQuestionsList().size();
        nextQuestionButton.setDisable(true);
    }


    public void showEditingQuestionsDialog() {
        //loading questions if game is not started yet and user want to edit them
        if (!gameIsStarted()) {
            try {
                QuestionsDataBase.getInstance().loadQuestions();

            } catch (IOException e) {
                System.out.println("Couldn't load the questions");
            }
        }
        //only show alert when the game is already started
        if(gameIsStarted()){
            showEditingQuestionsAlert();
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainWindowBorderPane.getScene().getWindow());
        dialog.setTitle("Editing questions");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("editingQuestionsDialog.fxml"));
            dialog.getDialogPane().setContent(root);
        } catch (IOException e) {
            System.out.println("Couldn't load remove question dialog");
            e.printStackTrace();
        }


        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK && gameIsStarted()) {
            try {
                QuestionsDataBase.getInstance().saveQuestions();

            } catch (IOException e) {
                System.out.println("Couldn't load the questions");
            }

            System.out.println("OK pressed, questions saved.");
        }else if(result.isPresent() && result.get() == ButtonType.OK){

        }


    }

    public void checkingTheAnswer() {

        if (!firstQuestionButton.isSelected() && !secondQuestionButton.isSelected() && !thirdQuestionButton.isSelected()
                && !fourthQuestionButton.isSelected()) {
            resultLabel.setText("CHOOSE AN ANSWER");

        } else if (isCorrectAnswerChose()) {

            //incrementing good questions counter
            correctAnswersCounter++;
            settingSceneAfterTheAnswer("green", "Correct answer");
        } else {

            //adding failed question to wrong questions' list
            if (QuestionsDataBase.getInstance().getQuestionsList().size() > questionsCounter) {
                QuestionsDataBase.getInstance().addWrongQuestion(QuestionsDataBase.getInstance().getQuestionsList().get(questionsCounter));
            }
            settingSceneAfterTheAnswer("red", "Wrong answer");
        }


    }

    public void settingSceneAfterTheAnswer(String textColour, String labelText) {
        resultLabel.setText(labelText);
        resultLabel.setTextFill(Paint.valueOf(textColour));
        checkingButton.setTextFill(Paint.valueOf(textColour));
        questionsCounter++;

        // making question answered and disabling buttons
        isQuestionAnswered = true;
        disableRadioButtons(true);
        nextQuestionButton.setDisable(false);
        checkingButton.setDisable(true);
    }


    public void goToTheNextQuestion() {
        if (isQuestionAnswered) {
            checkingButton.setTextFill(Paint.valueOf("black"));
            resultLabel.setText("");
            loadNextQuestion();
            isQuestionAnswered = false;
        }

    }

    public void loadNextQuestion() {

        answerToggleGroup.selectToggle(null);
        disableRadioButtons(false);
        // loading NEXT question if there is any
        if (questionsCounter < QuestionsDataBase.getInstance().getQuestionsList().size()) {
            firstQuestionButton.setText(QuestionsDataBase.getInstance().getQuestionsList().get(questionsCounter).getFirstAnswer());
            secondQuestionButton.setText(QuestionsDataBase.getInstance().getQuestionsList().get(questionsCounter).getSecondAnswer());
            thirdQuestionButton.setText(QuestionsDataBase.getInstance().getQuestionsList().get(questionsCounter).getThirdAnswer());
            fourthQuestionButton.setText(QuestionsDataBase.getInstance().getQuestionsList().get(questionsCounter).getFourthAnswer());
            questionLabel.setText(QuestionsDataBase.getInstance().getQuestionsList().get(questionsCounter).getQuestion());
            checkingButton.setDisable(false);
            nextQuestionButton.setDisable(true);
            // saving last question to not repeat the last one when redoing wrong questions in quiz
            lastQuestion = QuestionsDataBase.getInstance().getQuestionsList().get(questionsCounter).getQuestion();


            // loading WRONG question if there is any
        } else if (QuestionsDataBase.getInstance().getWrongQuestionsList().size() > 0 || !(savedWrongQuestions.isEmpty())) {

            //copying saved again wrongly answered questions list to main wrong questions list
            if (QuestionsDataBase.getInstance().getWrongQuestionsList().size() == 0) {
                QuestionsDataBase.getInstance().getWrongQuestionsList().addAll(savedWrongQuestions);
                savedWrongQuestions.clear();
            }

            Random random = new Random();
            wrongQuestionIndex = random.nextInt(QuestionsDataBase.getInstance().getWrongQuestionsList().size());

            // checking last displayed question to avoid repeating the same one
            while (lastQuestion.equals(QuestionsDataBase.getInstance().getWrongQuestionsList().get(wrongQuestionIndex).getQuestion())
                    && QuestionsDataBase.getInstance().getWrongQuestionsList().size() > 1) {
                wrongQuestionIndex = random.nextInt(QuestionsDataBase.getInstance().getWrongQuestionsList().size());
            }
            actualWrongQuestion = QuestionsDataBase.getInstance().getWrongQuestionsList().get(wrongQuestionIndex);
            lastQuestion = actualWrongQuestion.getQuestion();


            firstQuestionButton.setText(actualWrongQuestion.getFirstAnswer());
            secondQuestionButton.setText(actualWrongQuestion.getSecondAnswer());
            thirdQuestionButton.setText(actualWrongQuestion.getThirdAnswer());
            fourthQuestionButton.setText(actualWrongQuestion.getFourthAnswer());
            questionLabel.setText(actualWrongQuestion.getQuestion());
            checkingButton.setDisable(false);
            nextQuestionButton.setDisable(true);

        } else {
            // disabling buttons after all questions
            disableAndDismissingTheButtons("", true, true);
        }
    }

    public boolean isCorrectAnswerChose() {

        //checking correct answers before ending the basic set
        if (answerToggleGroup.selectedToggleProperty() != null && questionsCounter < QuestionsDataBase.getInstance().getQuestionsList().size()) {
            String buttonValue = answerToggleGroup.selectedToggleProperty().getValue().toString();
            String correctAnswer = QuestionsDataBase.getInstance().getQuestionsList()
                    .get(questionsCounter).getCorrectAnswer();
            return buttonValue.substring(buttonValue.length() - (correctAnswer.length() + 1), buttonValue.length() - 1)
                    .equals(correctAnswer);

            //checking correct answers when wrong questions load
        } else if (answerToggleGroup.selectedToggleProperty() != null && QuestionsDataBase.getInstance().getWrongQuestionsList().size() > 0) {
            String buttonValue = answerToggleGroup.selectedToggleProperty().getValue().toString();
            String correctAnswer = QuestionsDataBase.getInstance().getWrongQuestionsList().get(wrongQuestionIndex).getCorrectAnswer();

            if (buttonValue.substring(buttonValue.length() - (correctAnswer.length() + 1), buttonValue.length() - 1)
                    .equals(correctAnswer)) {
                QuestionsDataBase.getInstance().getWrongQuestionsList().remove(wrongQuestionIndex);
                return true;
            } else {
                savedWrongQuestions.add(actualWrongQuestion);
                QuestionsDataBase.getInstance().getWrongQuestionsList().remove(wrongQuestionIndex);
                return false;
            }

        } else return false;

    }


    public void disableRadioButtons(boolean disableButtons) {
        firstQuestionButton.setDisable(disableButtons);
        secondQuestionButton.setDisable(disableButtons);
        thirdQuestionButton.setDisable(disableButtons);
        fourthQuestionButton.setDisable(disableButtons);
    }

    public void dismissButtons(boolean dismissButtons) {
        firstQuestionButton.setVisible(!dismissButtons);
        secondQuestionButton.setVisible(!dismissButtons);
        thirdQuestionButton.setVisible(!dismissButtons);
        fourthQuestionButton.setVisible(!dismissButtons);
        nextQuestionButton.setVisible(!dismissButtons);
        checkingButton.setVisible(!dismissButtons);
    }

    public void disableAndDismissingTheButtons(String questionAreaText, boolean disableButtons, boolean dismissButtons) {
        questionLabel.setText(questionAreaText);
        disableRadioButtons(disableButtons);
        dismissButtons(dismissButtons);
    }

    public void disableAndDismissingTheButtons(boolean disableButtons, boolean dismissButtons) {
        disableRadioButtons(disableButtons);
        dismissButtons(dismissButtons);
    }

    public void showEditingQuestionsAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("editing information");
        alert.setHeaderText(null);
        alert.setContentText("After adding or deleting any questions during the game you have to choose" +
                " if you want to continue playing with an old set of questions or restart the game with a new set!");
        alert.showAndWait();
    }

    private Boolean gameIsStarted(){
        return menuStartButton.isDisable();
    }

}
