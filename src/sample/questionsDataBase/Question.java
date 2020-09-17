package sample.questionsDataBase;

public class Question {
    private String question;
    private String firstAnswer;
    private String secondAnswer;
    private String thirdAnswer;
    private String fourthAnswer;
    private String correctAnswer;
    private int answersNumber;

    public Question(String question, String firstAnswer, String secondAnswer, String thirdAnswer, String fourthAnswer, String correctAnswer, int answersNumber) {
        this.question = question;
        this.firstAnswer = firstAnswer;
        this.secondAnswer = secondAnswer;
        this.thirdAnswer = thirdAnswer;
        this.fourthAnswer = fourthAnswer;
        this.correctAnswer = correctAnswer;
        this.answersNumber = answersNumber;
    }

    @Override
    public String toString() {
        return this.question;
    }

    public String getQuestion() {
        return question;
    }

    public int getAnswersNumber() {
        return answersNumber;
    }

    public String getFirstAnswer() {
        return firstAnswer;
    }


    public String getSecondAnswer() {
        return secondAnswer;
    }


    public String getThirdAnswer() {
        return thirdAnswer;
    }


    public String getFourthAnswer() {
        return fourthAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }


}
