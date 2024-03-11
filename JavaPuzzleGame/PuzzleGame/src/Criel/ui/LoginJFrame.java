package Criel.ui;

import javax.swing.*;

// 登录界面
// 逻辑跟游戏界面同理
public class LoginJFrame extends JFrame {

    // 构造方法
    public LoginJFrame(){
        // 设置大小
        this.setSize(488, 430);
        // 设置标题
        this.setTitle("Puzzle 登录");
        // 设置页面置顶
        this.setAlwaysOnTop(true);
        // 设置窗口初始位置在屏幕中间
        this.setLocationRelativeTo(null);
        // 设置关闭模式
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // 调用方法显示，因为默认是隐藏的(建议写在最后面)
        this.setVisible(true);
    }
}
