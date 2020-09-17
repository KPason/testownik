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

public class QuestionsDataBase {
    private static QuestionsDataBase instance = new QuestionsDataBase();
    private final String fileName = "questionsItems.txt";
    private Path path = Paths.get(fileName);

    private ObservableList<Question> questionsList;

    private ArrayList<Question> wrongQuestionsList = wrongQuestionsList = new ArrayList<>();


    private QuestionsDataBase() {
    }

    public static QuestionsDataBase getInstance() {
        return instance;
    }

    public ObservableList<Question> getQuestionsList() {
        return questionsList;
    }

    public ArrayList<Question> getWrongQuestionsList() {
        return wrongQuestionsList;
    }


    public void loadQuestions() throws IOException {
        questionsList = FXCollections.observableArrayList();
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
                String answersNumber = questionPieces[6];
                int answersNumberInt = Integer.parseInt(answersNumber);
                Question addedQuestion = new Question(question, answer1, answer2, answer3, answer4, correctAnswer, answersNumberInt);
                questionsList.add(addedQuestion);

            }
        }
    }

    public void saveQuestions() throws IOException {

        //saving to the file
        BufferedWriter bw = Files.newBufferedWriter(path);
        try {

            for (Question question : questionsList) {
                bw.write(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s", question.getQuestion(), question.getFirstAnswer(), question.getSecondAnswer(),
                        question.getThirdAnswer(), question.getFourthAnswer(), question.getCorrectAnswer(),question.getAnswersNumber()));
                bw.newLine();
            }
        } finally {
            if (bw != null) {
                bw.close();
            }
        }
    }

    public void addWrongQuestion(Question question) {
        wrongQuestionsList.add(question);
    }

    public void deleteQuestion(Question question) {
        questionsList.remove(question);
    }


}



