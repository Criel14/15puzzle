package Criel.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

public class GameJFrame extends JFrame implements KeyListener, MouseListener{
// 游戏界面
// 直接继承JFrame，这样创建GameJFrame这个类的对象的时候，也会跟创建JFrame一样创建出一个窗口
// 以后关于游戏的界面全部写在这个类里面

    // 二维数组：用于图片打乱、记录图片序号位置
    private int [][] pictureNumber = new int[4][4];

    // 完成状态数组：用于判断是否完成
    private int [][] completeArr = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 10, 11, 12},
            {13, 14, 15, 0},
    };

    // 记录空白快0在数组中的索引
    private int zeroIndexI;
    private int zeroIndexJ;

    // 记录步数
    private int stepCount;

    // 用于计时
    private boolean isTiming;
    private Instant startTime;
    private Instant endTime;

    // 记录图片位置（默认为15p）
    private String path = "PuzzleGame\\image\\ClassicPuzzle\\15Puzzle\\15Puzzle_";

    // 创建下拉选项
    private JMenuItem instructionItem = new JMenuItem("查看"); // 查看游戏说明
    // 功能：
    private JMenuItem reStartItem = new JMenuItem("重新开始(R)");
    private JMenuItem closeItem = new JMenuItem("关闭游戏");
    // 关于：
    private JMenuItem weChatItem = new JMenuItem("微信号");
    private JMenuItem bilibiliItem = new JMenuItem("b站主页");
    // 更换内容:
    private JMenuItem picGenshinPaimon = new JMenuItem("原神_派蒙");
    private JMenuItem picGenshinAether = new JMenuItem("原神_空");
    private JMenuItem picSceneSunset = new JMenuItem("风景_日落");
    private JMenuItem picSceneYiQi = new JMenuItem("风景_一汽大众");
    private JMenuItem pic15Puzzle = new JMenuItem("数字华容道_15puzzle");

    // 构造方法
    public GameJFrame(){
        // 初始化界面
        // tip：如果在这里写了很多代码，想要把部分代码单独拎出一个函数，按ctrl + alt + m
        initJFrame();
        // 初始化菜单
        initMenuBar();
        // 初始化打乱
        initPictureNumer();
        // 初始化图片
        initImage();

        // 调用方法显示，因为默认是隐藏的(建议写在最后面)
        this.setVisible(true);
    }

    // 初始化界面
    private void initJFrame() {
        // 设置界面大小，单位是像素（这里直接按照教程给的像素去设置了）
        this.setSize(600, 580);
        // 设置标题
        this.setTitle("Puzzle小游戏 v1.0");
        // 设置页面置顶
        this.setAlwaysOnTop(true);
        // 设置窗口初始位置在屏幕中间
        this.setLocationRelativeTo(null);
        // 设置关闭模式
        // 参数是整形，这里调用了接口中的常量（这个参数含义是：关闭一个页面，则程序停止运行）
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // 取消默认布局方式，这样添加图片的时候就可以用xy坐标控制位置了，不然默认只会在正中间
        this.setLayout(null);

        // 添加键盘监听事件，传入的参数是KeyListener接口的实现
        this.addKeyListener(this);
    }

    // 初始化菜单
    private void initMenuBar() {
        //菜单（第一层）：JMenuBar
        //菜单里的选项（第二层）：JMenu
        //选项里的下拉选项（第三层）：JMenuItem

        // 创建菜单
        JMenuBar jMenuBar = new JMenuBar();
        // 创建选项
        JMenu functionJMenu = new JMenu("功能");
        JMenu aboutJMenu = new JMenu("关于");
        JMenu instructionMenu = new JMenu("游戏说明");
        JMenu changeMenu = new JMenu("更换内容");

        // 下拉选项添加进选项
        changeMenu.add(pic15Puzzle);
        changeMenu.add(picGenshinPaimon);
        changeMenu.add(picGenshinAether);
        changeMenu.add(picSceneSunset);
        changeMenu.add(picSceneYiQi);

        functionJMenu.add(reStartItem);
        functionJMenu.add(closeItem);
        functionJMenu.add(changeMenu);

        aboutJMenu.add(weChatItem);
        aboutJMenu.add(bilibiliItem);

        instructionMenu.add(instructionItem);

        // 选项添加进菜单
        jMenuBar.add(functionJMenu);
        jMenuBar.add(aboutJMenu);
        jMenuBar.add(instructionMenu);

        // 给选项添加鼠标监听
        reStartItem.addMouseListener(this);
        closeItem.addMouseListener(this);
        weChatItem.addMouseListener(this);
        bilibiliItem.addMouseListener(this);
        instructionItem.addMouseListener(this);
        pic15Puzzle.addMouseListener(this);
        picGenshinPaimon.addMouseListener(this);
        picGenshinAether.addMouseListener(this);
        picSceneSunset.addMouseListener(this);
        picSceneYiQi.addMouseListener(this);

        // 设置菜单（把菜单添加到窗口里）
        this.setJMenuBar(jMenuBar);
    }

    // 初始化打乱(打乱图片序号)
    private void initPictureNumer(){
        // 初始化步数（清零）
        stepCount = 0;

        // 创建一维数组并初始化值（先用一维数组来算，更加方便判断打乱有无解）
        int[] arr = new int[16];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }

        // 数字华容道NxN数字随机排列的阵列有解的充要条件是：（行号列号从0开始）
        //      （总逆序数 + 0的行号 + 0的列号）与 N 不同奇偶
        Random rd = new Random();
        while (true) {
            // 打乱：遍历，当前索引与随机索引交换
            for (int i = 0; i < arr.length; i++) {
                int index = rd.nextInt(arr.length);
                int tmp = arr[i];
                arr[i] = arr[index];
                arr[index] = tmp;
            }

            // 判断是否有解:
            // （1）计算逆序数
            //    tip：由于本程序N=4，数量级较小，直接暴力求逆序数（其实是不会写归并排序...）
            int sum = 0;
            for (int i = 1; i < arr.length; i++){
                for (int j = 0; j < i; j++){
                    if (arr[j] > arr[i]){
                        sum++;
                    }
                }
            }
            // （2）查找0的位置
            int index = 0;
            for (index = 0; index < arr.length; index++){
                if (arr[index] == 0){
                    break;
                }
            }
            // 判断最终结果是否为奇数，是则退出循环，否则重新打乱
            if ((sum + index/4 + index%4) % 2 != 0){
                // 顺便赋值给0初始坐标
                zeroIndexI = index / 4;
                zeroIndexJ = index % 4;
                break;
            }
        }

        // 结果赋值到二维数组里
        for (int i = 0; i < pictureNumber.length; i++){
            for (int j = 0; j < pictureNumber.length; j++){
                pictureNumber[i][j] = arr[i * 4 + j];
            }
        }

        // 控制台输出打乱
        System.out.println("当前打乱为：");
        for (int i=0; i< pictureNumber.length; i++){
            for (int j=0; j< pictureNumber.length; j++){
                System.out.print(pictureNumber[i][j] + "\t");
            }
            System.out.println();
        }
    }

    // 初始化图片(游戏内容)
    private void initImage(){
        // 先清空原本内容
        this.getContentPane().removeAll();

        // 如果完成，在最顶层展示完成图片，并且显示复原时间和TPS
        if (isComplete()){
            // 停止计时
            stopTimer();

            // 显示完成图片
            JLabel winJLabel = new JLabel(new ImageIcon("PuzzleGame\\image\\Others\\Win.png"));
            winJLabel.setBounds(60,20, 460, 460);
            this.getContentPane().add(winJLabel);

            // 显示复原时间
            JLabel timeJLabel = new JLabel("Time: " + getTimeElapsed());
            timeJLabel.setBounds(440, 480, 100, 20);
            timeJLabel.setFont(new Font("Arial", Font.BOLD, 14));
            this.getContentPane().add(timeJLabel);

            // 显示TPS(保留三位小数)
            DecimalFormat df = new DecimalFormat("#.###");
            String tps = df.format(stepCount/Double.parseDouble(getTimeElapsed()));
            JLabel tpsJLabel = new JLabel("TPS: " + tps);
            tpsJLabel.setBounds(240, 480, 120, 20);
            tpsJLabel.setFont(new Font("Arial", Font.BOLD, 14));
            this.getContentPane().add(tpsJLabel);
        } else {
            JLabel timeJLabel = new JLabel();
            // 如果在计时，显示：正在计算复原时间
            if (isTiming) {
                timeJLabel = new JLabel("Time calculating...");
            } else {
                timeJLabel = new JLabel("Waiting for start");
            }
            timeJLabel.setBounds(400, 480, 140, 20);
            timeJLabel.setFont(new Font("Arial", Font.BOLD, 14));
            this.getContentPane().add(timeJLabel);
        }

        // 显示步数
        JLabel stepJLabel = new JLabel("Step: " + stepCount);
        stepJLabel.setBounds(60, 480, 100, 20);
        stepJLabel.setFont(new Font("Arial", Font.BOLD, 14));
        this.getContentPane().add(stepJLabel);

        // 小细节：如果图片重叠，先加载的在顶层，后加载的在底层 —— 所以加载背景图片需要后加载
        for (int i=0; i<4; i++){
            for (int j=0; j<4; j++){
                // 根据二维数组pictureNumber来初始化图片位置
                int num = pictureNumber[i][j];
                // 创建ImageIcon对象来存储图片
                // tip：相对路径的出发位置是项目文件夹，所以从模块开始写路径就可以了
                ImageIcon imageIcon = new ImageIcon(path + num + ".jpg");
                // 创建JLabel对象（管理容器，作用类似html里的div吧）
                JLabel jLabel = new JLabel(imageIcon);
                // 设置图片位置，坐标是图片左上角的点：从左往右x，从上往下y
                jLabel.setBounds(105 * j + 80,105 * i + 40,105,105);

                // 在jLabel这里写鼠标控制的逻辑
                // 添加鼠标监听（参数用了匿名内部类，
                //      如果直接用GameJFrame去实现接口MouseListener，不知道怎么把jLabel的数据传给重写的方法）
                jLabel.addMouseListener(this);
                // 把管理容器添加到界面中
                this.getContentPane().add(jLabel); // 阿伟是这样写的，get获取的是JFrame里隐藏的容器
            }
        }

        // 加载背景图片
        JLabel jLabel = new JLabel(new ImageIcon("PuzzleGame\\image\\Others\\Background.jpg"));
        jLabel.setBounds(60,20, 460, 460);
        this.getContentPane().add(jLabel);

        // 刷新界面
        this.getContentPane().repaint();
    }

    // 判断是否完成游戏
    private boolean isComplete(){
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++) {
                if (completeArr[i][j] != pictureNumber[i][j]){
                    return false;
                }
            }
        }
        return true;
    }

    // 以下几个函数用于计时（newbing写的）
    // 获取开始时间
    public void startTimer() {
        startTime = Instant.now();
        isTiming = true;
    }
    // 获取结束时间
    public void stopTimer() {
        endTime = Instant.now();
        isTiming = false;
    }
    // 获取时间段
    public String getTimeElapsed() {
        if (startTime == null || endTime == null) {
            return "计时器尚未开始或尚未停止";
        }
        Duration duration = Duration.between(startTime, endTime);
        double seconds = duration.getSeconds() + (double) duration.getNano() / 1_000_000_000;
        return String.format("%.3f", seconds);
    }

    // 实现KeyListener接口
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {
        // 完成则不可操作
        if (isComplete()){
            return;
        }

        int code = e.getKeyCode();
//        System.out.println(code);
        switch (code){
            // 左
            case 37:
                // 判断越界
                if (zeroIndexJ == pictureNumber.length - 1){
                    return;
                }
                // 开始计时
                if (!isTiming){
                    startTimer();
                }
                // 空白格数据等于右边格子的数据
                pictureNumber[zeroIndexI][zeroIndexJ] = pictureNumber[zeroIndexI][zeroIndexJ + 1];
                pictureNumber[zeroIndexI][zeroIndexJ + 1] = 0;
                zeroIndexJ++;
                // 步数增加
                stepCount++;
                // 再次显示图片
                initImage();
                break;
            // 上
            case 38:
                // 判断越界
                if (zeroIndexI == pictureNumber.length - 1){
                    return;
                }
                // 开始计时
                if (!isTiming){
                    startTimer();
                }
                // 空白格数据等于下边格子的数据
                pictureNumber[zeroIndexI][zeroIndexJ] = pictureNumber[zeroIndexI + 1][zeroIndexJ];
                pictureNumber[zeroIndexI + 1][zeroIndexJ] = 0;
                zeroIndexI++;
                // 步数增加
                stepCount++;
                // 再次显示图片
                initImage();
                break;
            // 右
            case 39:
                // 判断越界
                if (zeroIndexJ == 0){
                    return;
                }
                // 开始计时
                if (!isTiming){
                    startTimer();
                }
                // 空白格数据等于左边格子的数据
                pictureNumber[zeroIndexI][zeroIndexJ] = pictureNumber[zeroIndexI][zeroIndexJ - 1];
                pictureNumber[zeroIndexI][zeroIndexJ - 1] = 0;
                zeroIndexJ--;
                // 步数增加
                stepCount++;
                // 再次显示图片
                initImage();
                break;
            // 下
            case 40:
                // 判断越界
                if (zeroIndexI == 0){
                    return;
                }
                // 开始计时
                if (!isTiming){
                    startTimer();
                }
                // 空白格数据等于上边格子的数据
                pictureNumber[zeroIndexI][zeroIndexJ] = pictureNumber[zeroIndexI - 1][zeroIndexJ];
                pictureNumber[zeroIndexI - 1][zeroIndexJ] = 0;
                zeroIndexI--;
                // 步数增加
                stepCount++;
                // 再次显示图片
                initImage();
                break;
            // 按下A键查看完整图片
            // 注意还需要在keyReleased中书写 送开后恢复拼图界面 的代码
            case 65:
                // 添加完整图片
                this.getContentPane().removeAll();
                JLabel jLabel = new JLabel(new ImageIcon(path + "Complete.jpg"));
                jLabel.setBounds(80,40,420,420);
                this.getContentPane().add(jLabel);
                // 添加背景
                JLabel bgJLabel = new JLabel(new ImageIcon("PuzzleGame\\image\\Others\\Background.jpg"));
                bgJLabel.setBounds(60,20, 460, 460);
                this.getContentPane().add(bgJLabel);
                // 刷新
                this.getContentPane().repaint();
            default:
                break;
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code){
            // 松开A键重新显示拼图游戏界面
            case 65:
                // 完成则不可操作
                if (isComplete()){
                    return;
                }
                initImage();
                break;
            // 按R重新游戏
            case 82:
                // 停止计时
                stopTimer();
                // 重新打乱
                initPictureNumer();
                // 重新显示
                initImage();
                break;
            default:
                break;
        }
    }

    //  实现MouseListener接口
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {
        // 不知道为什么如果下面的代码放在mouseClicked里面就不行了

        // 如果不是JMenuItem类型就退出
        Object obj = e.getSource();
        if (!(obj instanceof JMenuItem)){
            return;
        }

        // 判断点击的是什么对象
        if (obj == reStartItem){ // 重新开始
            // 停止计时
            stopTimer();
            // 重新打乱
            initPictureNumer();
            // 重新显示
            initImage();

        } else if (obj == closeItem) { // 退出游戏
            // 直接退出程序
            System.exit(0);

        } else if (obj == weChatItem) { // 微信号
            // 创建JDialog弹窗对象
            JDialog jDialog = new JDialog();
            jDialog.setSize(440, 540);

            // 创建JLabel对象，并放入图片
            // Tip: 如果把这段代码放在最后面，就无法显示了，很奇怪
            JLabel jLabel = new JLabel(new ImageIcon("PuzzleGame\\image\\About\\WeChat.jpg"));
            // 这里没有取消默认居中setLayout(null)，所以坐标其实无所谓
            jLabel.setBounds(0,0,400,481);
            // 添加进jDialog
            jDialog.getContentPane().add(jLabel);

            // 标题
            jDialog.setTitle("微信号");
            // 置顶和居中
            jDialog.setAlwaysOnTop(true);
            jDialog.setLocationRelativeTo(null);
            // 弹窗不关闭无法操作下面的界面
            jDialog.setModal(true);
            // 显示弹窗
            jDialog.setVisible(true);

        } else if (obj == bilibiliItem) { // b站主页
            // 创建JDialog弹窗对象
            JDialog jDialog = new JDialog();
            jDialog.setSize(440, 460);

            // 创建JLabel对象，并放入图片
            // Tip: 如果把这段代码放在最后面，就无法显示了，很奇怪
            JLabel jLabel = new JLabel(new ImageIcon("PuzzleGame\\image\\About\\Bilibili.jpg"));
            // 这里没有取消默认居中setLayout(null)，所以坐标其实无所谓
            jLabel.setBounds(0,0,400,400);
            // 添加进jDialog
            jDialog.getContentPane().add(jLabel);

            // 标题
            jDialog.setTitle("b站主页");
            // 置顶和居中
            jDialog.setAlwaysOnTop(true);
            jDialog.setLocationRelativeTo(null);
            // 弹窗不关闭无法操作下面的界面
            jDialog.setModal(true);
            // 显示弹窗
            jDialog.setVisible(true);

        } else if (obj == instructionItem) {    // 游戏说明
            // 创建JDialog弹窗对象
            JDialog jDialog = new JDialog();
            jDialog.setSize(720, 750);

            // 创建JLabel对象，并放入图片
            // Tip: 如果把这段代码放在最后面，就无法显示了，很奇怪
            JLabel jLabel = new JLabel(new ImageIcon("PuzzleGame\\image\\Others\\Instruction.png"));
            // 这里没有取消默认居中setLayout(null)，所以坐标其实无所谓
            jLabel.setBounds(0,0,700,713);
            // 添加进jDialog
            jDialog.getContentPane().add(jLabel);

            // 标题
            jDialog.setTitle("游戏说明");
            // 置顶和居中
            jDialog.setAlwaysOnTop(true);
            jDialog.setLocationRelativeTo(null);
            // 显示弹窗
            jDialog.setVisible(true);

        } else if (obj == pic15Puzzle) { // 切换为15p
            path = "PuzzleGame\\image\\ClassicPuzzle\\15Puzzle\\15puzzle_";
            // 然后重新开始
            // 停止计时
            stopTimer();
            // 重新打乱
            initPictureNumer();
            // 重新显示
            initImage();

        } else if (obj == picGenshinPaimon) { // 切换为派蒙
            path = "PuzzleGame\\image\\Genshin\\Paimon\\Paimon_";
            // 然后重新开始
            // 停止计时
            stopTimer();
            // 重新打乱
            initPictureNumer();
            // 重新显示
            initImage();

        } else if (obj == picGenshinAether) { // 切换为空
            path = "PuzzleGame\\image\\Genshin\\Aether\\Aether_";
            // 然后重新开始
            // 停止计时
            stopTimer();
            // 重新打乱
            initPictureNumer();
            // 重新显示
            initImage();

        } else if (obj == picSceneSunset) { // 切换为日落
            path = "PuzzleGame\\image\\Scene\\Sunset\\Sunset_";
            // 然后重新开始
            // 停止计时
            stopTimer();
            // 重新打乱
            initPictureNumer();
            // 重新显示
            initImage();

        } else if (obj == picSceneYiQi) { // 切换为一汽大众
            path = "PuzzleGame\\image\\Scene\\YiQi\\YiQi_";
            // 然后重新开始
            // 停止计时
            stopTimer();
            // 重新打乱
            initPictureNumer();
            // 重新显示
            initImage();

        }
    }
    @Override
    public void mouseEntered(MouseEvent e) {
        // 如果不是JLabel类型就退出
        Object obj = e.getSource();
        if (!(obj instanceof JLabel)){
            return;
        }

        // 完成则不可操作
        if (isComplete()){
            return;
        }

        // 获取当前鼠标悬停方块的数组索引（注意索引和坐标是相反的）
        JLabel nowJLabel = (JLabel) obj;
        int nowI = ((nowJLabel.getY() - 40) / 105);
        int nowJ = ((nowJLabel.getX() - 80) / 105);

        // 如果鼠标所在的方块不在空白格0的相邻位置
        if ( !((Math.abs(nowI - zeroIndexI) == 1 && nowJ == zeroIndexJ)
                || (Math.abs(nowJ - zeroIndexJ) == 1 && nowI == zeroIndexI)) ){
            return;
        }

        // 开始计时
        if (!isTiming){
            startTimer();
        }

        // tip：按照下面的逻辑写，应该不需要判断越界吧
        // 左移
        if ((nowI == zeroIndexI) && (nowJ - zeroIndexJ == 1)){
            // 空白格数据等于右边格子的数据
            pictureNumber[zeroIndexI][zeroIndexJ] = pictureNumber[zeroIndexI][zeroIndexJ + 1];
            pictureNumber[zeroIndexI][zeroIndexJ + 1] = 0;
            zeroIndexJ++;
            // 步数增加
            stepCount++;
            // 再次显示图片
            initImage();
        }
        // 右移
        if ((nowI == zeroIndexI) && (zeroIndexJ  - nowJ == 1)){
            // 空白格数据等于左边格子的数据
            pictureNumber[zeroIndexI][zeroIndexJ] = pictureNumber[zeroIndexI][zeroIndexJ - 1];
            pictureNumber[zeroIndexI][zeroIndexJ - 1] = 0;
            zeroIndexJ--;
            // 步数增加
            stepCount++;
            // 再次显示图片
            initImage();
        }
        // 上移
        if ((nowI - zeroIndexI == 1) && (zeroIndexJ  == nowJ)){
            // 空白格数据等于下边格子的数据
            pictureNumber[zeroIndexI][zeroIndexJ] = pictureNumber[zeroIndexI + 1][zeroIndexJ];
            pictureNumber[zeroIndexI + 1][zeroIndexJ] = 0;
            zeroIndexI++;
            // 步数增加
            stepCount++;
            // 再次显示图片
            initImage();
        }
        // 下移
        if ((zeroIndexI - nowI == 1) && (zeroIndexJ  == nowJ)){
            // 空白格数据等于上边格子的数据
            pictureNumber[zeroIndexI][zeroIndexJ] = pictureNumber[zeroIndexI - 1][zeroIndexJ];
            pictureNumber[zeroIndexI - 1][zeroIndexJ] = 0;
            zeroIndexI--;
            // 步数增加
            stepCount++;
            // 再次显示图片
            initImage();
        }
    }
    @Override
    public void mouseExited(MouseEvent e) {}

}
