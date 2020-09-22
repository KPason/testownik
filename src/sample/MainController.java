package sample;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import sample.questionsDataBase.Question;
import sample.questionsDataBase.QuestionsDataBase;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;


public class MainController {
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
    @FXML
    private Label answeredQuestionsCounterLabel;


    private ArrayList<Question> startedQuestionsList;
    private int attemptsCounter = 0;
    private int correctAnswersTotalNumber = 0;
    private int correctAnswersCounter = 0;
    private int wrongQuestionIndex = 0;
    private boolean isQuestionAnswered = false;

    private ArrayList<Question> savedWrongQuestions = new ArrayList<>();

    private Question actualWrongQuestion;

    private String lastQuestion;

    private Question actualQuestion;


    public void initialize() {
        disableAndDismissingTheButtons("WELCOME TO THE GAME", true, true);
        menuRestartButton.setDisable(true);
    }

    @FXML
    public void startTheGame() throws IOException {
        try {
            startedQuestionsList = new ArrayList<>();
            QuestionsDataBase.getInstance().loadQuestions();
            startedQuestionsList.addAll(QuestionsDataBase.getInstance().getQuestionsList());
        } catch (IOException e) {
            System.out.println("Couldn't load the questions");
        }
        correctAnswersTotalNumber = startedQuestionsList.size();
        disableAndDismissingTheButtons(false, false);
        setAnsweredQuestionsCounterLabel();
        loadNextQuestion(startedQuestionsList);
        nextQuestionButton.setDisable(true);
        menuRestartButton.setDisable(false);
        menuStartButton.setDisable(true);

        //wyswietlic jakis komunikat ze nie ma pytan w bazie czy coś
        if (startedQuestionsList.size() == 0) {
            System.out.println("Questions base is empty do sthg about it");
        }

    }

    @FXML
    public void restartTheGame() throws IOException {
        System.out.println("restarting the game");


        //resetting all main variables
        try {
            QuestionsDataBase.getInstance().saveQuestions();
        } catch (IOException e) {
            e.getMessage();
        }

        resettingMainVariables();
        disableAndDismissingTheButtons(false, false);
        nextQuestionButton.setDisable(true);

        startTheGame();

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
            if (startedQuestionsList.size() > 0) {
                QuestionsDataBase.getInstance().addWrongQuestion(actualQuestion);
            }
            settingSceneAfterTheAnswer("red", "Wrong answer");
        }


    }

    public void settingSceneAfterTheAnswer(String textColour, String labelText) {
        resultLabel.setText(labelText);
        resultLabel.setTextFill(Paint.valueOf(textColour));
        checkingButton.setTextFill(Paint.valueOf(textColour));
        setAnsweredQuestionsCounterLabel();
        attemptsCounter++;

        // making question answered and disabling buttons
        isQuestionAnswered = true;
        disableRadioButtons(true);
        nextQuestionButton.setDisable(false);
        checkingButton.setDisable(true);
    }


    public void goToTheNextQuestion() throws IOException {
        if (isQuestionAnswered) {
            checkingButton.setTextFill(Paint.valueOf("black"));
            resultLabel.setText("");
            startedQuestionsList.remove(actualQuestion);
            loadNextQuestion(startedQuestionsList);
            isQuestionAnswered = false;
        }

    }

    public void loadNextQuestion(List<Question> list) throws IOException {

        answerToggleGroup.selectToggle(null);
        disableRadioButtons(false);
        ArrayList<Question> wrongQuestionsList = QuestionsDataBase.getInstance().getWrongQuestionsList();


        if (list.size() > 0) {
            actualQuestion = getRandomElement(list);
            setRadioButtonsTextAndSetRestOfButtons(actualQuestion);

            // saving last question to not repeat the last one when redoing wrongly answered questions in the quiz
            lastQuestion = actualQuestion.getQuestion();
            omitEmptyAnswers();

            // loading WRONG question if there is any
        } else if (wrongQuestionsList.size() > 0 || !(savedWrongQuestions.isEmpty())) {

            //saving renewed wrongly answered questions to main wrong questions list
            if (wrongQuestionsList.size() == 0) {
                wrongQuestionsList.addAll(savedWrongQuestions);
                savedWrongQuestions.clear();
            }
            actualWrongQuestion = getRandomElement(wrongQuestionsList);

            // checking last displayed question to avoid repeating the same one
            while (lastQuestion.equals(actualWrongQuestion.getQuestion()) && wrongQuestionsList.size() > 1) {
                actualWrongQuestion = getRandomElement(wrongQuestionsList);
            }

            lastQuestion = actualWrongQuestion.getQuestion();
            setRadioButtonsTextAndSetRestOfButtons(actualWrongQuestion);
            omitEmptyAnswers();

        }else{
            disableAndDismissingTheButtons("",true,true);
            answeredQuestionsCounterLabel.setVisible(false);
            showScore();
        }
    }

    public boolean isCorrectAnswerChose() {

        //checking correct answers before ending the basic set
        if (answerToggleGroup.selectedToggleProperty() != null && 0 < startedQuestionsList.size()) {
            String buttonValue = answerToggleGroup.selectedToggleProperty().getValue().toString();
            String correctAnswer = actualQuestion.getCorrectAnswer();
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

    public void showEditingQuestionsDialog() throws IOException {

        QuestionsDataBase.getInstance().loadQuestions();

        //only show alert when the game is already started
        if (gameIsStarted()) {
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


        //loading questions if game is not started yet and user want to edit them
        if (!gameIsStarted()) {

            ButtonType save = new ButtonType("save changes", ButtonType.OK.getButtonData());
            ButtonType cancel = new ButtonType("don't save changes", ButtonType.CANCEL.getButtonData());
            dialog.getDialogPane().getButtonTypes().add(save);
            dialog.getDialogPane().getButtonTypes().add(cancel);
            Optional<ButtonType> result = dialog.showAndWait();

            if (result.isPresent() && result.get() == save) {
                QuestionsDataBase.getInstance().saveQuestions();
            }

        } else {

            ButtonType saveAndRestart = new ButtonType("save and restart", ButtonType.OK.getButtonData());
            ButtonType saveAndContinue = new ButtonType("save and continue", ButtonType.OK.getButtonData());
            ButtonType cancel = new ButtonType("cancel all actions", ButtonType.CLOSE.getButtonData());

            dialog.getDialogPane().getButtonTypes().add(saveAndRestart);
            dialog.getDialogPane().getButtonTypes().add(saveAndContinue);
            dialog.getDialogPane().getButtonTypes().add(cancel);
            Optional<ButtonType> result = dialog.showAndWait();

            if (result.isPresent() && result.get() == saveAndRestart) {

                restartTheGame();

            } else if (result.isPresent() && result.get() == saveAndContinue) {
                QuestionsDataBase.getInstance().saveQuestions();
                dialog.close();
            }
        }
    }

    public void showScore() throws IOException {

        double accuracyRatio = ((double)correctAnswersCounter/attemptsCounter)*100;
        Label title = new Label("YOUR RESULTS");
        Label first = new Label("TOTAL NUMBER OF QUESTIONS: " + correctAnswersTotalNumber);
        Label second = new Label("GOOD ANSWERS: " + correctAnswersCounter);
        Label third = new Label("WRONG ANSWERS: " + (attemptsCounter - correctAnswersCounter));
        Label fourth = new Label("ACCURACY RATIO: " + (new DecimalFormat("#.##").format(accuracyRatio)) + " %");
        Label fifth = new Label(setAccuracyResultText(accuracyRatio));
        VBox resultsBox = new VBox();
        HBox buttonsBox = new HBox();
        Button restart = new Button("RESTART");
        Parent mainSceneRoot = FXMLLoader.load(getClass().getResource("mainScene.fxml"));
        restart.setOnMouseClicked(e -> Main.getWindow().setScene(new Scene(mainSceneRoot,800,500)));
        
        Button exit = new Button("EXIT");
        exit.setOnMouseClicked(e -> Platform.exit());


        buttonsBox.getChildren().addAll(restart,exit);
        resultsBox.getChildren().addAll(title,first,second,third,fourth,fifth,buttonsBox);

        mainWindowBorderPane.setCenter(resultsBox);

        resultsBox.alignmentProperty().setValue(Pos.CENTER);
        resultsBox.setSpacing(5);
        buttonsBox.alignmentProperty().setValue(Pos.CENTER);
        buttonsBox.setSpacing(20);
        buttonsBox.setPadding(new Insets(20,0,0,0));

        FadeTransition fade = new FadeTransition(Duration.seconds(5),resultsBox);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

    }



    public String setAccuracyResultText(double accuracyValue){
        if(accuracyValue == 100.00){
            return "YOU MADE NO MISTAKES, GOOD JOB!";
        }else if(accuracyValue>80.00){
            return "YOU DID ALMOST PERFECT, NICE!";
        }else if(accuracyValue>50.00){
            return "YOU KNOW A LOT, BUT THERE IS STILL MUCH TO LEARN";
        }else if(accuracyValue>0.00){
            return "YOU HAVE A LOT TO LEARN";
        }else{
            return "Oh dude, we have a problem here";
        }
    }

    public void setRadioButtonsTextAndSetRestOfButtons(Question question){
        firstQuestionButton.setText(question.getFirstAnswer());
        secondQuestionButton.setText(question.getSecondAnswer());
        thirdQuestionButton.setText(question.getThirdAnswer());
        fourthQuestionButton.setText(question.getFourthAnswer());
        questionLabel.setText(question.getQuestion());
        checkingButton.setDisable(false);
        nextQuestionButton.setDisable(true);
    }

    public void setAnsweredQuestionsCounterLabel() {
        answeredQuestionsCounterLabel.setVisible(true);
        answeredQuestionsCounterLabel.setText(correctAnswersCounter + "/" + correctAnswersTotalNumber);
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

    public void omitEmptyAnswers() {
        //validated to always have at least 2 first answered not blank
        changeEmptyRadioButton(thirdQuestionButton);
        changeEmptyRadioButton(fourthQuestionButton);
    }

    public void changeEmptyRadioButton(RadioButton radioButton) {
        if (radioButton.getText().equals("**empty**")) {
            radioButton.setVisible(false);
        }
    }

    private boolean gameIsStarted() {
        return menuStartButton.isDisable();
    }

    public void resettingMainVariables() {
        attemptsCounter = 0;
        correctAnswersCounter = 0;
        QuestionsDataBase.getInstance().getWrongQuestionsList().clear();
        savedWrongQuestions.clear();
        actualWrongQuestion = null;
        wrongQuestionIndex = 0;
        isQuestionAnswered = false;
    }

    public Question getRandomElement(List<Question> list) {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

}
