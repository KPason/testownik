package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.questionsDataBase.QuestionsDataBase;

import java.io.IOException;

public class Main extends Application {

    private Stage window;

    @Override
    public void start(Stage primaryStage) throws Exception{

        window = primaryStage;
        Parent mainSceneRoot = FXMLLoader.load(getClass().getResource("mainScene.fxml"));
        window.setScene(new Scene(mainSceneRoot, 800, 500));
        window.setTitle("test game");
        window.show();
        window.setResizable(false);

    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {

    }

    @Override
    public void stop() throws Exception {
        try{
            QuestionsDataBase.getInstance().saveQuestions();
            System.out.println("saving questions when closing");

        }catch (IOException e){
            System.out.println("Couldn't save the questions when closing the app");
        }
        super.stop();
    }
}
