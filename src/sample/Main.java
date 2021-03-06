package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.questionsDataBase.QuestionsDataBase;

import java.io.IOException;

public class Main extends Application {

    private static Stage window;

    public static Stage getWindow() {
        return window;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        window = primaryStage;
        Parent mainSceneRoot = FXMLLoader.load(getClass().getResource("mainScene.fxml"));
        window.setScene(new Scene(mainSceneRoot));
        window.setTitle("test game");
        window.show();
        window.setResizable(false);

    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        try {

            QuestionsDataBase.getInstance().loadQuestions();

        } catch (IOException e) {
            System.out.println("Couldn't load the questions when opening the app");
        }
    }

    @Override
    public void stop() throws Exception {

            try {
                QuestionsDataBase.getInstance().saveQuestions();

            } catch (IOException e) {
                System.out.println("Couldn't save the questions when closing the app");
            }


            super.stop();
    }
}
