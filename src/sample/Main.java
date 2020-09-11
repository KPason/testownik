package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.questionsDataBase.QuestionsDataBase;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainwindow.fxml"));
        primaryStage.setTitle("Test game");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
        primaryStage.setResizable(false);
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        try{
            QuestionsDataBase.getInstance().loadQuestions();

        }catch (IOException e){
            System.out.println("Couldn't load the questions");
        }
    }


}
