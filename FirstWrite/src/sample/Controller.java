package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class Controller {
    @FXML public TextField mac_1,mac_2,mac_3/*,tagmaskText*/;
    @FXML public Label powertext,taglentext;
    @FXML public Slider power_slider,tagmask_slider;
    @FXML public TextArea print;
    @FXML public ChoiceBox readername,porter;
    private String hostname,tagmask;
    double powernum;
    int taglen,porterno;
    //@FXML private TextField mac_1,mac_2,mac_3;

    @FXML public void handleSetting(ActionEvent actionEvent) {
        hostname = readername.getValue() + "-" + mac_1.getText() + "-" + mac_2.getText() + "-" + mac_3.getText() + ".local";
        powernum = power_slider.getValue();
        //tagmask = tagmaskText.getText();
        //taglen=((Double)tagmask_slider.getValue()).intValue();
        porterno = Integer.parseInt((String) porter.getValue());
        int flag = checkSettings();
        switch (flag){
            case 0:
                print.appendText("开始设置\n");
                print.appendText("主机名：" + hostname + "\n功率：" + powernum + "\n端口号：" + porterno+"\n");
                break;
            case 1:
                print.appendText("主机名格式错误！请重新输入！\n");break;
            /*case 2:
                print.setText("掩码格式错误！请重新输入！");break;*/
        }
        //print.appendText("主机名：" + hostname + "\n功率：" + powernum + "\n掩码：" + tagmask + "\n端口号：" + porterno);

    }

    public void handleChangePower(MouseEvent mouseEvent) {
        powertext.setText(power_slider.getValue()+"");
    }

    /**
     * 核查阅读器设置选项是否正确
     * @return 0 正确 1 主机名格式错误 2  掩码格式错误 3掩码位数错误
     */
    public int checkSettings(){
        if(!hostname.matches("speedwayr-[0-9a-f]{2}-[0-9a-f]{2}-[0-9a-f]{2}.local"))
            return 1;
        /*if(tagmask.length()!=taglen)
            return 3;
        else{
            if(!tagmask.matches("[0-9a-f]{"+taglen+"}"))
                return 2;
        }*/

        return 0;
    }

    /*public void handleChangeTaglen(MouseEvent mouseEvent) {
        taglentext.setText(tagmask_slider.getValue()+"");
    }*/

    /*public void tagmaskchange(KeyEvent keyEvent) {
        String s = tagmaskText.getText();

        *//*String key = keyEvent.getCode().getName();
        if(!key.matches("[0-9a-f]")) {
            print.setText(key);
            tagmaskText.setText(s.substring(0, s.length() - 1));
        }*//*
        if(s.length()>24){
            print.setText("掩码长度超出限制！");
            tagmaskText.setText(s.substring(0, s.length() - 1));
            tagmaskText.positionCaret(s.length()-1);
        }
        taglentext.setText(tagmaskText.getText().length()+"");
        tagmask_slider.setValue(tagmaskText.getText().length());
    }*/


}
