package sample.questionsDataBase;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuestionsDataBase {
    private static QuestionsDataBase instance = new QuestionsDataBase();
    private final String fileName = "questionsItems.txt";
    private Path path = Paths.get(fileName);
    private ObservableList<Question> questionsList;
    private ArrayList<Question> correctQuestionsList; // wystarczy chyba tylko counter zwykły ogarnąć
    private ArrayList<Question> wrongQuestionsList;

    private QuestionsDataBase() {
    }

    public static QuestionsDataBase getInstance() {
        return instance;
    }

    public ObservableList<Question> getQuestionsList() {
        return questionsList;
    }

    public ArrayList<Question> getCorrectQuestionsList() {
        return correctQuestionsList;
    }

    public ArrayList<Question> getWrongQuestionsList() {
        return wrongQuestionsList;
    }

    public void loadQuestions() throws IOException {
        questionsList = FXCollections.observableArrayList();
        wrongQuestionsList = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String input;
            while ((input = br.readLine()) != null) {
                String[] questionPieces = input.split("\t");
                String question = questionPieces[0];
                String answer1 = questionPieces[1];
                String answer2 = questionPieces[2];
                String answer3 = questionPieces[3];
                String answer4 = questionPieces[4];
                String correctAnswer = questionPieces[5];
                Question addedQuestion = new Question(question, answer1, answer2, answer3, answer4, correctAnswer);
                questionsList.add(addedQuestion);

            }
        }
    }

    public void saveQuestions() throws IOException {

        BufferedWriter bw = Files.newBufferedWriter(path);
        try {

            for (Question question : questionsList) {
                bw.write(String.format("%s\t%s\t%s\t%s\t%s\t%s", question.getQuestion(), question.getFirstAnswer(), question.getSecondAnswer(),
                        question.getThirdAnswer(), question.getFourthAnswer(), question.getCorrectAnswer()));
                bw.newLine();
            }
        } finally {
            if (bw != null) {
                bw.close();
            }
        }
    }

    public void addQuestion(Question question) {
        questionsList.add(question);
    }

    public void addWrongQuestion(Question question){
        wrongQuestionsList.add(question);
    }

    public Question getWrongQuestion(int questionNumber){
        return wrongQuestionsList.get(questionNumber);
    }

}



